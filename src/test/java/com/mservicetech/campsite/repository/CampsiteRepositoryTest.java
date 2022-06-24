package com.mservicetech.campsite.repository;

import com.mservicetech.campsite.model.Client;
import com.mservicetech.campsite.model.Reservation;
import com.networknt.db.GenericDataSource;
import com.networknt.service.SingletonServiceFactory;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class CampsiteRepositoryTest {

    private static GenericDataSource genericDataSource = SingletonServiceFactory.getBean(GenericDataSource.class);
    private static CampsiteRepository campsiteRepository = new CampsiteRepositoryImpl(genericDataSource);

    private static  Client client;
    private static Reservation reservation;

    private static SqlSessionFactory sqlSessionFactory;


    @BeforeAll
    public static void setUp() throws SQLException, IOException {
        client = new Client();
        client.setEmail("volcano.admin@gmail.com");
        reservation = new Reservation();
        reservation.setArrival(LocalDate.now());
        reservation.setDeparture(LocalDate.now().plusDays(3));
    }

    @Test
    public void testCheckUserExisting() {
        Client existing =  campsiteRepository.checkClientExisting(client);
        assertNotNull(existing);
    }


    @Test
    public void testInsertClient() throws SQLException{
        Client client = new Client();
        client.setName("Test Test");
        client.setEmail("Test.Test@volcano.com");
        long newClient =  campsiteRepository.insertClient( client);
        assertNotNull(newClient);
    }


}
