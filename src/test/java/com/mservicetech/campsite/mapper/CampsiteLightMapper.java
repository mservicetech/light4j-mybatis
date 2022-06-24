package com.mservicetech.campsite.mapper;

import com.mservicetech.campsite.model.Client;
import com.mservicetech.campsite.model.Reservation;
import org.apache.ibatis.annotations.*;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

@Mapper
public interface CampsiteLightMapper {

    @Select("SELECT reserved_date from reserved")
    List<Date> getReservedDates();

    @Insert("INSERT INTO CLIENT (FULL_NAME, EMAIL) VALUES (#{name}, #{email})")
    @Options(useGeneratedKeys = true, keyColumn = "id")
    int insertClient(Client client);

    @Select("SELECT ID, FULL_NAME, EMAIL FROM CLIENT WHERE EMAIL = #{email}")
    @ResultMap("clientResult")
    Client selectClientByEmail(String email);

    @Select("SELECT ID, FULL_NAME, EMAIL FROM CLIENT WHERE id = #{id}")
    @ResultMap("clientResult")
    Client selectClientById(Long id);

    @SelectProvider
    List<LocalDate> verifyReserveDates(@Param("items") List<LocalDate> dates);

    @Insert("INSERT INTO reserved(reserved_date) VALUES (#{date})")
    void insertReservedDate(LocalDate date);

    @DeleteProvider
    int deleteReservedDates(@Param("items") List<LocalDate> dates);

    @InsertProvider
    int insertReservation(@Param("id") String id,  @Param("client_id") Long client_id, @Param("arrivalDate") LocalDate arrivalDate, @Param("departureDate") LocalDate departureDate);

    @SelectProvider
    @ResultMap("reservationResult")
    Reservation selectReservation(@Param("id") String reservationId);

    @DeleteProvider
    int deleteReservation(String reservationId);

    @UpdateProvider
    int updateReservation(Reservation reservation);




}
