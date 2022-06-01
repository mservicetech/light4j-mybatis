package com.mservicetech.mybatis;

import java.util.HashMap;
import java.util.Map;

public class MybatisConfig {
    public static final String CONFIG_NAME = "mybatis";

    private String registerAliases;
    private String mapperPackage;

    public MybatisConfig() {
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
}
