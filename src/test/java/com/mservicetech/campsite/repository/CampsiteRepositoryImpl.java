package com.mservicetech.campsite.repository;

import com.mservicetech.campsite.mapper.CampsiteMapper;
import com.mservicetech.campsite.model.Client;
import com.mservicetech.mybatis.base.SimpleaBaseRepository;
import com.networknt.db.GenericDataSource;
import org.apache.ibatis.session.SqlSession;

import java.time.LocalDate;
import java.util.List;

public class CampsiteRepositoryImplSimplea extends SimpleaBaseRepository implements CampsiteRepository{

    public CampsiteRepositoryImplSimplea(GenericDataSource genericDataSource) {
        SqlSessionFactoryConfig(genericDataSource.getDataSource());
        sqlSessionFactory.getConfiguration().getTypeAliasRegistry().registerAliases(mybatisConfig.getRegisterAliases());
        sqlSessionFactory.getConfiguration().addMappers(mybatisConfig.getMapperPackage());
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
}
