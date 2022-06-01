package com.mservicetech.mybatis.startup;

import com.mservicetech.mybatis.MybatisConfig;
import com.networknt.config.Config;
import com.networknt.db.GenericDataSource;
import com.networknt.db.H2DataSource;
import com.networknt.server.Server;
import com.networknt.server.ServerConfig;
import com.networknt.server.StartupHookProvider;
import com.networknt.service.SingletonServiceFactory;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;

import javax.sql.DataSource;

public class MybatisStartupHookProvider implements StartupHookProvider {

    public static DataSource dataSource;
    public static SqlSessionFactory sqlSessionFactory;
    public static final ServerConfig serverConfig = Server.getServerConfig();
    public static final MybatisConfig mybatisConfig = (MybatisConfig)Config.getInstance().getJsonObjectConfig(MybatisConfig.CONFIG_NAME, MybatisConfig.class);

    @Override
    @SuppressWarnings("unchecked")
    public void onStartup() {
        GenericDataSource genericDataSource = SingletonServiceFactory.getBean(GenericDataSource.class);
        dataSource = genericDataSource.getDataSource();
        TransactionFactory transactionFactory = new JdbcTransactionFactory();
        Environment env = new Environment(serverConfig.getEnvironment(), transactionFactory, dataSource);
        Configuration configuration = new Configuration(env);

        sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);
        sqlSessionFactory.getConfiguration().getTypeAliasRegistry().registerAliases(mybatisConfig.getRegisterAliases());
        sqlSessionFactory.getConfiguration().addMappers(mybatisConfig.getMapperPackage());
    }
}
