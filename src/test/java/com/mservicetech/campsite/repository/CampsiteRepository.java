package com.mservicetech.campsite.repository;

import com.mservicetech.campsite.model.Client;
import com.mservicetech.campsite.model.Reservation;
import com.mservicetech.mybatis.MybatisConfig;
import org.apache.ibatis.session.SqlSession;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public interface CampsiteRepository {
    MybatisConfig mybatisConfig = MybatisConfig.load();

    List<LocalDate> findReserved();


    int reserveDates( List<LocalDate> dateList) ;

    int deleteDates(List<LocalDate> dateList) ;

    List<LocalDate> verifyDates(List<LocalDate> dateList);

    Client checkClientExisting(Client client)  ;

    long insertClient  (Client client) ;

    String createReservation(Reservation reservation) ;

    Reservation getReservation(String reservationId) ;

    int deleteReservation(Reservation reservation) ;

    int updateReservation(Reservation oldReservation, Reservation reservation) ;
}
