package com.mservicetech.mybatis;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.builder.annotation.ProviderContext;
import org.apache.ibatis.builder.annotation.ProviderMethodResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

public class DefaultSqlProviderMethodResolver implements ProviderMethodResolver {

    private static final Logger logger = LoggerFactory.getLogger(DefaultSqlProviderMethodResolver.class);


    @Override
    public Method resolveMethod(ProviderContext context) {
        return null;
    }

    public String provideSql(ProviderContext context) {
        MybatisConfig config = MybatisSessionManager.mybatisConfig;
        String name = context.getMapperMethod().getName();
        String dbId = context.getDatabaseId();
        name = StringUtils.isNotBlank(dbId) ? dbId.trim() + "." + name : name;
        String sql = config.getSqlSource().get(name);
        if (sql != null ) {
            return sql.replace("@{", "${");
        } else {
            logger.info("Cannot get sql statement from [{}] in default namespace." , name);
            return null;
        }

    }
}
