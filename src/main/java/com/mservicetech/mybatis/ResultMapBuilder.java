package com.mservicetech.mybatis;

import org.apache.ibatis.builder.BaseBuilder;
import org.apache.ibatis.builder.MapperBuilderAssistant;

public class ResultMapBuilder extends BaseBuilder {

    private final MapperBuilderAssistant builderAssistant;
    private final CustomConfiguration configuration;
    static final String CONFIG_NAME = "mybatis.yml";

    public ResultMapBuilder(CustomConfiguration configuration) {
        super(configuration);
        this.configuration = configuration;
        this.builderAssistant = new MapperBuilderAssistant(configuration, CONFIG_NAME) {
            @Override
            public String applyCurrentNamespace(String base, boolean isReference) {
                if (base==null) {
                    return null;
                }
                String alias = MybatisSessionManager.mybatisConfig.getNameAliases()==null? null: MybatisSessionManager.mybatisConfig.getNameAliases().get(base);
                return super.applyCurrentNamespace(alias!=null? alias: base, isReference);
            }
        };
        this.builderAssistant.setCurrentNamespace("default");
    }
}
