package com.mservicetech.mybatis;

import com.networknt.config.Config;
import com.networknt.server.Server;
import com.networknt.server.ServerConfig;
import org.apache.ibatis.session.SqlSessionFactory;

public interface MybatisSessionManager {

     ServerConfig serverConfig = Server.getServerConfig();
     MybatisConfig mybatisConfig = (MybatisConfig) Config.getInstance().getJsonObjectConfig(MybatisConfig.CONFIG_NAME, MybatisConfig.class);


     enum SessionMode {
         NEW,                   //new session for executing task
         CURRENT,              // use current session if there is an existing session, if no session in the thread, create a new one
         EXISTING              // use current session if there is an existing session, otherwise throw exception
     }

     SqlSessionFactory getSessionFactory();

     <T> T executeWithSession(SessionTask<T> task, SessionMode mode);

     default <T> T executeWithSession(SessionTask<T> task) {
         return  this.executeWithSession(task, SessionMode.CURRENT);
     }
}
