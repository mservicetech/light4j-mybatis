package com.mservicetech.mybatis.startup;

import com.networknt.server.ShutdownHookProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MybatisShutdownHookProvider implements ShutdownHookProvider {

    protected static Logger logger = LoggerFactory.getLogger(MybatisShutdownHookProvider.class);

    @Override
    public void onShutdown() {
        if(MybatisStartupHookProvider.sessionManager != null) {
            MybatisStartupHookProvider.sessionManager = null;
        }
        logger.info("MybatisShutdownHookProvider is called");
    }
}
