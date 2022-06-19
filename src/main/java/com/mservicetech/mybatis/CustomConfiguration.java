package com.mservicetech.mybatis;

import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.session.Configuration;

public class CustomConfiguration extends Configuration {

    public CustomConfiguration(Environment env) {
        super(env);
    }

    public void addResultMapAlias(String aliasId, ResultMap resultMap) {
        resultMaps.put(aliasId, resultMap);
        checkLocallyForDiscriminatedNestedResultMaps(resultMap);
        checkGloballyForDiscriminatedNestedResultMaps(resultMap);
    }
}
