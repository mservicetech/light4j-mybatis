package com.mservicetech.mybatis.base;

import com.networknt.server.Server;
import com.networknt.server.ServerConfig;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;

import javax.sql.DataSource;

/**
 * This is a simple Base Repository(DAO) class which provide general config and setting for mybatis
 * User can use this base class to use mybatis as traditional way
 *
 * @author Gavin Chen
 */
public abstract class SimpleBaseRepository {
    protected SqlSessionFactory sqlSessionFactory;
    public static final ServerConfig serverConfig = Server.getServerConfig();

    protected void SqlSessionFactoryConfig(DataSource dataSource) {
        TransactionFactory transactionFactory = new JdbcTransactionFactory();
        Environment env = new Environment(serverConfig.getEnvironment()==null ? "test" : serverConfig.getEnvironment(), transactionFactory, dataSource);
        Configuration configuration = new Configuration(env);

        sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);

    }
}
