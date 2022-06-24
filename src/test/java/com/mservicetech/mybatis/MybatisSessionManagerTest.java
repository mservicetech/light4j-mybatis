package com.mservicetech.mybatis;

import com.mservicetech.campsite.model.Client;
import com.mservicetech.campsite.model.Reservation;
import com.mservicetech.campsite.repository.CampsiteRepository;
import com.networknt.service.SingletonServiceFactory;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.jupiter.api.*;

import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MybatisSessionManagerTest {

    private static CampsiteRepository campsiteRepository = SingletonServiceFactory.getBean(CampsiteRepository.class);
    private static MybatisSessionManager sessionManager = SingletonServiceFactory.getBean(MybatisSessionManager.class);

    private static Client client;
    private static Reservation reservation;


    @BeforeAll
    public static void setUp() throws SQLException {
        client = new Client();
        client.setEmail("volcano.admin@gmail.com");
        reservation = new Reservation();
        reservation.setArrival(LocalDate.now());
        reservation.setDeparture(LocalDate.now().plusDays(3));
        clean();
    }

    private static  void clean() throws SQLException {
        try (SqlSession session = sessionManager.getSessionFactory().openSession(); Statement statement = session.getConnection().createStatement()) {
            statement.execute("DELETE FROM reserved");
            statement.execute("DELETE FROM reservation");
            statement.execute("DELETE FROM client");
            statement.execute("INSERT INTO reserved(reserved_date ) VALUES('2025-11-05')");
            statement.execute("INSERT INTO client(full_name, email ) VALUES('Admin', 'volcano.admin@gmail.com')");
        }
    }

    @Test
    @Order(1)
    public void testFindReserved() throws SQLException {
        List<LocalDate> reservedList =  campsiteRepository.findReserved();
        assertTrue(reservedList.size()>0);
    }

    @Test
    @Order(2)
    public void testVerifyDates() throws SQLException {
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(LocalDate.now());
        dateList.add(LocalDate.now().plusDays(1));
        dateList.add(LocalDate.now().plusDays(2));
        List<LocalDate> reservedList =  campsiteRepository.verifyDates(dateList);

        assertEquals(reservedList.size(), 0);
    }

    @Test
    @Order(3)
    public void testCheckUserExisting() {
        Client existing =  campsiteRepository.checkClientExisting(client);
        assertNotNull(existing);
    }

//    @Test
//    @Order(4)
//    public void testInsertClient() throws SQLException{
//        Client client = new Client();
//        client.setName("Test Test");
//        client.setEmail("Test.Test@volcano.com");
//        long newClient =  campsiteRepository.insertClient( client);
//        assertNotNull(newClient);
//    }

    @Test
    @Order(5)
    public void testReservedDates() throws SQLException{
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(LocalDate.now());
        dateList.add(LocalDate.now().plusDays(1));
        campsiteRepository.deleteDates(dateList);
        int records =  campsiteRepository.reserveDates( dateList);
        assertEquals(records, 2);

    }
}
