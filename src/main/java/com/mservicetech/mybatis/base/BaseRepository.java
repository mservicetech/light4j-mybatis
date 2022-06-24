package com.mservicetech.mybatis.base;

import com.mservicetech.mybatis.MybatisSessionManager;
import com.mservicetech.mybatis.SessionTask;
import com.networknt.service.SingletonServiceFactory;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.Objects;

/**
 * Base Repository(DAO) class which provide execution entry point for executing mybatis session
 *
 * @author Gavin Chen
 */
public abstract class BaseRepository {

    protected MybatisSessionManager sessionManager;

    public BaseRepository() {
        this.sessionManager = Objects.requireNonNull(SingletonServiceFactory.getBean(MybatisSessionManager.class), "A Define for MybatisSessionManager is required");
    }

    /**
     * Gets the SqlSessionFactory, then use it as application needed for mybatis process
     *
     * @return mybatis SqlSessionFactory
     */
    protected SqlSessionFactory getSessionFactory() {
       return sessionManager.getSessionFactory();
    }

    /**
     * execute DB query task with current mode
     *
     * @return
     */
    protected <T> T executeWithSession(SessionTask<T> task) {
        return sessionManager.executeWithSession(task);
    }

    /**
     * execute DB query task with specified mode
     *
     * @return
     */
    protected <T> T executeWithSession(SessionTask<T> task, MybatisSessionManager.SessionMode mode) {
        return sessionManager.executeWithSession(task, mode);
    }
}
