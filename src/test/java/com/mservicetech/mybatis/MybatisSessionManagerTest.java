package com.mservicetech.mybatis;

import com.mservicetech.campsite.model.Client;
import com.mservicetech.campsite.model.Reservation;
import com.mservicetech.campsite.repository.CampsiteRepository;
import com.networknt.service.SingletonServiceFactory;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MybatisSessionManagerTest {

    private static CampsiteRepository campsiteRepository = SingletonServiceFactory.getBean(CampsiteRepository.class);
    private static MybatisSessionManager sessionManager = SingletonServiceFactory.getBean(MybatisSessionManager.class);

    private static Client client;
    private static Reservation reservation;


    @BeforeAll
    public static void setUp() throws SQLException, IOException {
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
    public void testFindReserved() throws SQLException {
        List<LocalDate> reservedList =  campsiteRepository.findReserved();
        assertTrue(reservedList.size()>0);
    }

}
