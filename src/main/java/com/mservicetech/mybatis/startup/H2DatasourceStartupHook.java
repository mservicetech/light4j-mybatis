package com.mservicetech.mybatis.startup;

import com.networknt.db.H2DataSource;
import com.networknt.server.StartupHookProvider;
import com.networknt.service.SingletonServiceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class H2DatasourceStartupHook implements StartupHookProvider {
    private static Logger logger = LoggerFactory.getLogger(H2DatasourceStartupHook.class);
    public static DataSource dataSource;
    @Override
    public void onStartup() {
        logger.info("H2WebServerStartupHook begins");
        try {
            org.h2.tools.Server.createWebServer().start();
            H2DataSource h2DataSource = SingletonServiceFactory.getBean(H2DataSource.class);
            dataSource = h2DataSource.getDataSource();
            try(Connection connection = H2DatasourceStartupHook.dataSource.getConnection()){
                logger.debug("h2 Datasource connection verified;");
            }
        } catch (SQLException e) {
            logger.error("Cannot start H2 web server!");
        }

        logger.info("H2WebServerStartupHook ends");
    }
}
