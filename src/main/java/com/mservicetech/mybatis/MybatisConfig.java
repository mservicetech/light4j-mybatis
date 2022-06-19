package com.mservicetech.mybatis;

import com.networknt.config.Config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MybatisConfig {
    public static final String CONFIG_NAME = "mybatis";

    private String registerAliases;
    private String mapperPackage;
    private Map<String, String> nameAliases;
    private Map<String, String> resultMappings;
    private Map<String, String> sqlSource;
    private final Map<String, Object> mappedConfig;
    private Config config;


    public MybatisConfig() {
        config = Config.getInstance();
        mappedConfig = config.getJsonMapConfigNoCache(CONFIG_NAME);
        setConfigMap();
        setConfigData();
    }

    public static MybatisConfig load() {
        return new MybatisConfig();
    }

    public String getRegisterAliases() {
        return registerAliases;
    }

    public void setRegisterAliases(String registerAliases) {
        this.registerAliases = registerAliases;
    }

    public String getMapperPackage() {
        return mapperPackage;
    }

    public void setMapperPackage(String mapperPackage) {
        this.mapperPackage = mapperPackage;
    }

    public Map<String, String> getNameAliases() {
        return nameAliases;
    }

    public void setNameAliases(Map<String, String> nameAliases) {
        this.nameAliases = nameAliases;
    }

    public void setConfigMap() {
        if (mappedConfig.get("nameAliases") !=null && mappedConfig.get("nameAliases") instanceof Map) {
            this.nameAliases = new HashMap<>();
            nameAliases = (Map<String, String>) mappedConfig.get("nameAliases");
        }
        if (mappedConfig.get("resultMappings") !=null && mappedConfig.get("resultMappings") instanceof Map) {
            this.resultMappings = new HashMap<>();
            resultMappings = (Map<String, String>) mappedConfig.get("resultMappings");
        }
        if (mappedConfig.get("sqlSource") !=null && mappedConfig.get("sqlSource") instanceof Map) {
            this.sqlSource = new HashMap<>();
            sqlSource = (Map<String, String>) mappedConfig.get("sqlSource");
        }
    }

    public Map<String, String> getResultMappings() {
        return resultMappings;
    }

    public void setResultMappings(Map<String, String> resultMappings) {
        this.resultMappings = resultMappings;
    }


    public Map<String, String> getSqlSource() {
        return sqlSource;
    }

    public void setSqlSource(Map<String, String> sqlSource) {
        this.sqlSource = sqlSource;
    }


    public Map<String, Object> getMappedConfig() {
        return mappedConfig;
    }


    public void setConfigData() {
        Object object = getMappedConfig().get("registerAliases");
        if(object != null ) {
            registerAliases = (String)object;
        }
        object = getMappedConfig().get("mapperPackage");
        if(object != null ) {
            mapperPackage = (String)object;
        }
    }
}
