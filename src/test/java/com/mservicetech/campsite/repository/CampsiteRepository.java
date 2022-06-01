package com.mservicetech.campsite.repository;

import com.mservicetech.campsite.model.Client;
import com.mservicetech.mybatis.MybatisConfig;
import com.networknt.config.Config;
import org.apache.ibatis.session.SqlSession;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public interface CampsiteRepository {
    MybatisConfig mybatisConfig = (MybatisConfig) Config.getInstance().getJsonObjectConfig(MybatisConfig.CONFIG_NAME, MybatisConfig.class);

    List<LocalDate> findReserved();

    Client checkClientExisting(Client client);

    long insertClient  (Client client);
}
