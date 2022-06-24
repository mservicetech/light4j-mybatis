package com.mservicetech.mybatis;

import org.apache.ibatis.session.SqlSession;

/**
 * Wapper mybatis session as session task for execute
 *
 */
public interface SessionTask<T> {

    T execute(SqlSession session) throws Exception;
}
