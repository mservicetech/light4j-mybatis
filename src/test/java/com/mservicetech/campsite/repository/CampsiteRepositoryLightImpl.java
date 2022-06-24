package com.mservicetech.campsite.repository;

import com.mservicetech.campsite.mapper.CampsiteLightMapper;
import com.mservicetech.campsite.model.Client;
import com.mservicetech.campsite.model.Reservation;
import com.mservicetech.mybatis.base.BaseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

public class CampsiteRepositoryLightImpl extends BaseRepository implements CampsiteRepository {

    private static final Logger logger = LoggerFactory.getLogger(BaseRepository.class);


    @Override
    public List<LocalDate> findReserved()  {
       // return executeWithSession(session -> session.getMapper(CampsiteLightMapper.class).getReservedDates());
        return executeWithSession(session -> session.getMapper(CampsiteLightMapper.class).getReservedDates().stream()
                .map(v->v.toLocalDate()).collect(Collectors.toList())
        );
    }

    @Override
    public int reserveDates(List<LocalDate> dateList) {
        return executeWithSession(session -> {
            CampsiteLightMapper mapper = session.getMapper(CampsiteLightMapper.class);
            dateList.forEach(i -> mapper.insertReservedDate(i));
            return dateList.size();
        });
    }

    @Override
    public int deleteDates(List<LocalDate> dateList) {
        return executeWithSession(session -> {
            CampsiteLightMapper mapper = session.getMapper(CampsiteLightMapper.class);
            return  mapper.deleteReservedDates(dateList);
        });
    }

    @Override
    public List<LocalDate> verifyDates(List<LocalDate> dateList) {
        return executeWithSession(session -> {
            CampsiteLightMapper mapper = session.getMapper(CampsiteLightMapper.class);
            return  mapper.verifyReserveDates(dateList);
        });
    }

    @Override
    public Client checkClientExisting(Client client) {
        return executeWithSession(session -> {
            CampsiteLightMapper mapper = session.getMapper(CampsiteLightMapper.class);
            return  mapper.selectClientByEmail(client.getEmail());
        });
    }

    @Override
    public long insertClient(Client client) {
        return executeWithSession(session -> {
            CampsiteLightMapper mapper = session.getMapper(CampsiteLightMapper.class);
            mapper.insertClient(client);
            return client.getId();
        });
    }

    @Override
    public String createReservation(Reservation reservation) {
        try {
            return executeWithSession(session -> {
                CampsiteLightMapper mapper = session.getMapper(CampsiteLightMapper.class);
                List<LocalDate> dateList = new ArrayList<>();
                long days = Duration.between(reservation.getArrival().atStartOfDay(), reservation.getDeparture().atStartOfDay()).toDays();
                LongStream.range(0, days).forEach(l->dateList.add(reservation.getArrival().plusDays(l)));
                List<LocalDate> bookedList = this.verifyDates(dateList);
                if (bookedList.size()==0) {
                    Client client = mapper.selectClientByEmail(reservation.getClient().getEmail());
                    if (client==null) {
                        mapper.insertClient(reservation.getClient());
                    } else {
                        reservation.setClient(client);
                    }
                    int records = reserveDates(dateList);
                    if (logger.isDebugEnabled()) logger.debug("Total days:" + records + " reserved for client" + reservation.getClient().getName());
                    String reservationId = UUID.randomUUID().toString();
                    mapper.insertReservation(reservationId, reservation.getClient().getId(), reservation.getArrival(), reservation.getDeparture());
                    return reservationId;
                } else {
                    throw new RuntimeException("error on reservation, campsite is not available;");
                }

            });
        } catch (Exception e) {
            throw new RuntimeException("Cannot create resevation");
        }
    }

    @Override
    public Reservation getReservation(String reservationId) {
        return executeWithSession(session -> {
            CampsiteLightMapper mapper = session.getMapper(CampsiteLightMapper.class);
            return mapper.selectReservation(reservationId);
        });
    }

    @Override
    public int deleteReservation(Reservation reservation) {
        return executeWithSession(session -> {
            CampsiteLightMapper mapper = session.getMapper(CampsiteLightMapper.class);
            List<LocalDate> dateList = new ArrayList<>();
            long days = Duration.between(reservation.getArrival().atStartOfDay(), reservation.getDeparture().atStartOfDay()).toDays();
            LongStream.range(0, days).forEach(l->dateList.add(reservation.getArrival().plusDays(l)));
            this.deleteDates(dateList);
            return mapper.deleteReservation(reservation.getId());
        });
    }

    @Override
    public int updateReservation(Reservation oldReservation, Reservation reservation) {

        if (reservation.getId() == null ) {
            reservation.setId(oldReservation.getId());
        }
        if (!reservation.getId().equals(oldReservation.getId())) {
            throw  new IllegalArgumentException("Update Reservation Id not match.");
        }
        return executeWithSession(session -> {
            return session.getMapper(CampsiteLightMapper.class).updateReservation(reservation);
        });
    }
}
