package com.mservicetech.mybatis;

import org.apache.ibatis.session.SqlSession;

public interface SessionTask<T> {

    T execute(SqlSession session) throws Exception;
}
