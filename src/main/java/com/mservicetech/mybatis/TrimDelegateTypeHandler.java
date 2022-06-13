package com.mservicetech.mybatis;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TrimDelegateTypeHandler<T> implements TypeHandler<T> {


    public static <R> TypeHandler<R> createTrimTypeHandler(TypeHandler<R> delegate, boolean trimToNull) {
        return new TrimDelegateTypeHandler<>(delegate, trimToNull);
    }

    private final TypeHandler<T> delegate;
    private final boolean trimToNull;

    private TrimDelegateTypeHandler(TypeHandler<T> delegate, boolean trimToNull) {
        this.delegate = delegate;
        this.trimToNull = trimToNull;
    }

    @Override
    public void setParameter(PreparedStatement preparedStatement, int i, T p, JdbcType jdbcType) throws SQLException {
        delegate.setParameter(preparedStatement, i, p, jdbcType);
    }

    @Override
    public T getResult(ResultSet resultSet, String s) throws SQLException {
        return doTrim(delegate.getResult(resultSet, s));
    }

    @Override
    public T getResult(ResultSet resultSet, int i) throws SQLException {
        return doTrim(delegate.getResult(resultSet, i));
    }

    @Override
    public T getResult(CallableStatement callableStatement, int i) throws SQLException {
        return doTrim(delegate.getResult(callableStatement, i));
    }

    private T doTrim(T val) {
        if (val instanceof String) {
            return (T) (this.trimToNull ? StringUtils.trimToNull((String) val): ((String) val).trim());
        }
        return val;
    }
}
