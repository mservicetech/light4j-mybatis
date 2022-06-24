package com.mservicetech.campsite.repository;

import com.mservicetech.campsite.mapper.simple.CampsiteMapper;
import com.mservicetech.campsite.model.Client;
import com.mservicetech.campsite.model.Reservation;
import com.mservicetech.mybatis.base.SimpleBaseRepository;
import com.networknt.db.GenericDataSource;
import org.apache.ibatis.session.SqlSession;

import java.time.LocalDate;
import java.util.List;

public class CampsiteRepositoryImpl extends SimpleBaseRepository implements CampsiteRepository{

    public CampsiteRepositoryImpl(GenericDataSource genericDataSource) {
        SqlSessionFactoryConfig(genericDataSource.getDataSource());
        sqlSessionFactory.getConfiguration().getTypeAliasRegistry().registerAliases(mybatisConfig.getRegisterAliases());
        //For test purpose change to a new folder
//        sqlSessionFactory.getConfiguration().addMappers(mybatisConfig.getMapperPackage());
        sqlSessionFactory.getConfiguration().addMappers("com.mservicetech.campsite.mapper.simple");
    }

    @Override
    public List<LocalDate> findReserved() {
        List<LocalDate> reservedDates;
        try (SqlSession session = sqlSessionFactory.openSession()) {
            CampsiteMapper campsiteMapper = session.getMapper(CampsiteMapper.class);
            reservedDates = campsiteMapper.findReserved();
        }
        return reservedDates;
    }

    @Override
    public Client checkClientExisting(Client client) {
        Client existingClient;
        try (SqlSession session = sqlSessionFactory.openSession()) {
            CampsiteMapper campsiteMapper = session.getMapper(CampsiteMapper.class);
            existingClient = campsiteMapper.getClientByEmail(client.getEmail());
        }
        return existingClient;
    }

    @Override
    public long insertClient(Client client) {
        try (SqlSession session = sqlSessionFactory.openSession()) {
            CampsiteMapper campsiteMapper = session.getMapper(CampsiteMapper.class);
            campsiteMapper.insertClient(client);
        }
        return client.getId();
    }

    @Override
    public int reserveDates(List<LocalDate> dateList) {
        return 0;
    }

    @Override
    public int deleteDates(List<LocalDate> dateList) {
        return 0;
    }

    @Override
    public List<LocalDate> verifyDates(List<LocalDate> dateList) {
        return null;
    }

    @Override
    public String createReservation(Reservation reservation) {
        return null;
    }

    @Override
    public Reservation getReservation(String reservationId) {
        return null;
    }

    @Override
    public int deleteReservation(Reservation reservation) {
        return 0;
    }

    @Override
    public int updateReservation(Reservation oldReservation, Reservation reservation) {
        return 0;
    }
}
