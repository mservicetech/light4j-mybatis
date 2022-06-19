package com.mservicetech.mybatis.startup;

import com.mservicetech.mybatis.MybatisSessionManager;
import com.networknt.server.StartupHookProvider;
import com.networknt.service.SingletonServiceFactory;

import java.util.Objects;

/**
 * StartupHookProvider for mybatis usage.
 * This is an option for using mybatis as db process framework.
 * The StartupHookProvider will initial the mybatis config load at service startup time.
 *
 * @author Gavin Chen
 */
public class MybatisStartupHookProvider implements StartupHookProvider {

    public static MybatisSessionManager sessionManager;

    @Override
    @SuppressWarnings("unchecked")
    public void onStartup() {
        sessionManager = Objects.requireNonNull(SingletonServiceFactory.getBean(MybatisSessionManager.class), "A Define for MybatisSessionManager is required");
    }
}
