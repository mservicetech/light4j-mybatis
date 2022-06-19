package com.mservicetech.campsite.repository;

import com.mservicetech.campsite.model.Client;
import com.mservicetech.mybatis.MybatisConfig;

import java.time.LocalDate;
import java.util.List;

public interface CampsiteRepository {
    MybatisConfig mybatisConfig = MybatisConfig.load();

    List<LocalDate> findReserved();

    Client checkClientExisting(Client client);

    long insertClient  (Client client);
}
