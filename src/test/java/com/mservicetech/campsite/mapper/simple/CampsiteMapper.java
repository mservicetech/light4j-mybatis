package com.mservicetech.campsite.mapper.simple;

import com.mservicetech.campsite.model.Client;
import com.mservicetech.campsite.model.Reservation;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface CampsiteMapper {

    List<LocalDate> findReserved();

    void insertClient(Client client);

    Client getClientByEmail(String email);

}
