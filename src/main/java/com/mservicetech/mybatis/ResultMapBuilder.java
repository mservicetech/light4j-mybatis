package com.mservicetech.mybatis;

import com.networknt.utility.StringUtils;
import org.apache.ibatis.builder.BaseBuilder;
import org.apache.ibatis.builder.BuilderException;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.apache.ibatis.builder.ResultMapResolver;
import org.apache.ibatis.builder.xml.XMLMapperEntityResolver;
import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.mapping.Discriminator;
import org.apache.ibatis.mapping.ResultFlag;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.parsing.XNode;
import org.apache.ibatis.parsing.XPathParser;
import org.apache.ibatis.reflection.MetaClass;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Supplier;

/**
 * ResultMap Builder
 * It build resultMap from extended yaml config file (mybatis/values yaml)
 *
 */
public class ResultMapBuilder extends BaseBuilder {

    private final MapperBuilderAssistant builderAssistant;
    private final CustomConfiguration config;
    static final String CONFIG_NAME = "mybatis.yml";
    private static final Logger logger = LoggerFactory.getLogger(ResultMapBuilder.class);


    private  Field typeHandlerField;


    public ResultMapBuilder(CustomConfiguration configuration) {
        super(configuration);
        this.config = configuration;
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
        try {
            this.typeHandlerField = ResultMapping.class.getDeclaredField("typeHandler");
            this.typeHandlerField.setAccessible(true);
        }catch (Throwable e) {
            logger.warn("There is no typeHanlder field for the ResultMapping");
        }
        this.builderAssistant.setCurrentNamespace("default");
    }

    protected void setTrimTypeHandler(ResultMapping resultMapping, XNode context) {
        if (resultMapping == null || context == null) {
            return;
        }
        String trim = getTrim(context);
        if (trim==null || "false".equalsIgnoreCase(trim)) {
            return;
        }
        TypeHandler<?> typeHandler = TrimDelegateTypeHandler.createTrimTypeHandler(resultMapping.getTypeHandler(), "null".equalsIgnoreCase(trim));
        try {
          typeHandlerField.set(resultMapping, typeHandler);
        } catch (Throwable e) {
            logger.warn("Cannot set trim handler for ResultMapping: {}" , resultMapping.getProperty());
        }
    }

    private String getTrim(XNode context) {
        if (context == null) {
            return null;
        }
        String value = context.getStringAttribute("trim");
        if (StringUtils.isNotBlank(value)) {
            return value.trim();
        }
        return getTrim(context.getParent());
    }

    public void addResultMapping(Map<String, String> mappings, Set<Class<? extends Class<?>>> mappers) {
        mappings.entrySet().forEach(entry -> {
            ResultMap resultMap = resultMapElement(entry.getKey(), entry.getValue(), Collections.emptyList(), null);
            String mId = resultMap.getId();
            if (mId.startsWith("default.")) {
                String shortName = mId.substring("default.".length());
                mappers.forEach(c -> this.config.addResultMapAlias(c.getName() + "." +  shortName, resultMap));
            }
        });
    }

    public ResultMap resultMapElement(String id, String resultMapXml, List<ResultMapping> extraResultMappings, Class<?> enclosingType ) {
        XPathParser parser = new XPathParser(resultMapXml, false, configuration.getVariables(), new XMLMapperEntityResolver());
        XNode resultMapNode = parser.evalNode("/resultMap");
        ResultMap resultMap = processResultMapElement(()->id, resultMapNode, extraResultMappings, enclosingType);
        return resultMap;
    }

    private ResultMap processResultMapElement(Supplier<String> idProducer, XNode resultMapNode, List<ResultMapping> additionalResultMappings, Class<?> enclosingType ) {
        ErrorContext.instance().activity("processing " + resultMapNode.getValueBasedIdentifier());
        String type = resultMapNode.getStringAttribute("type", resultMapNode.getStringAttribute("ofType",
                resultMapNode.getStringAttribute("resultType", resultMapNode.getStringAttribute("javaType"))));
        Class<?> typeClass = resolveClass(type);
        //TODO hanlder typeClass == null
        Discriminator discriminator = null;
        List<ResultMapping> resultMappings = new ArrayList<>(additionalResultMappings);
        List<XNode> resultChildrenNode = resultMapNode.getChildren();
        for (XNode node: resultChildrenNode) {
            if ("constructor".equalsIgnoreCase(node.getName())) {
                processConstructorElement(idProducer.get(), node, typeClass, resultMappings);
            } else if ("discriminator".equalsIgnoreCase(node.getName())) {
                discriminator = processDiscriminatorElement(idProducer.get(), node, typeClass, resultMappings);
            } else {
                List<ResultFlag> flags = new ArrayList<>();
                if ("io".equalsIgnoreCase(node.getName())) {
                    flags.add(ResultFlag.ID);
                }
                resultMappings.add(buildResultMappingFromContext(idProducer.get(), node, typeClass, flags));
            }
        }
        String extend = resultMapNode.getStringAttribute("extends");
        Boolean autoMapping = resultMapNode.getBooleanAttribute("autoMapping");
        ResultMapResolver resultMapResolver = new ResultMapResolver(builderAssistant, idProducer.get(), typeClass, extend, discriminator, resultMappings, autoMapping);
        try {
            return resultMapResolver.resolve();
        } catch (Exception e) {
            configuration.addIncompleteResultMap(resultMapResolver);
            throw  e;
        }
    }

    private Discriminator processDiscriminatorElement (String id, XNode context, Class<?> resyltType, List<ResultMapping> resultMappings) {
        String column = context.getStringAttribute("column");
        String javaType = context.getStringAttribute("javaType");
        String jdbcType = context.getStringAttribute("jdbcType");
        String typeHandler = context.getStringAttribute("typeHandler");
        Class<?> javaTypeClass = resolveClass(javaType);
        Class<? extends TypeHandler<?>> typeHandlerClass = resolveClass(typeHandler);
        JdbcType jdbcTypeEnum = resolveJdbcType(jdbcType);
        Map<String, String> discriminatorMap = new HashMap<>();
        for (XNode node : context.getChildren()) {
            String value = node.getStringAttribute("value");
            String resultMap = node.getStringAttribute("resultMap", processNestedResultMappings(id, node, resultMappings, resyltType));
            discriminatorMap.put(value, resultMap);
        }

        return builderAssistant.buildDiscriminator(resyltType, column, javaTypeClass, jdbcTypeEnum, typeHandlerClass, discriminatorMap);
    }

    private void processConstructorElement(String id, XNode node, Class<?> resulType, List<ResultMapping> resultMappings) {
        List<XNode> argNodes = node.getChildren();
        for (XNode argNode: argNodes) {
            List<ResultFlag> flags = new ArrayList<>();
            flags.add(ResultFlag.CONSTRUCTOR);
            if ("idArg".equalsIgnoreCase(argNode.getName())) {
                flags.add(ResultFlag.ID);
            }
            resultMappings.add(buildResultMappingFromContext(id, argNode, resulType, flags));
        }
    }

    private ResultMapping buildResultMappingFromContext(String id, XNode context, Class<?> resulType, List<ResultFlag> flags) {
        String property;
        if (flags.contains(ResultFlag.CONSTRUCTOR)) {
            property = context.getStringAttribute("name");
        } else {
            property = context.getStringAttribute("property");
        }
        String column = context.getStringAttribute("column");
        String javaType = context.getStringAttribute("javaType");
        String jdbcType = context.getStringAttribute("jdbcType");
        String nestedSelect = context.getStringAttribute("select");
        String nestedResultMap = context.getStringAttribute("resultMap", ()->processNestedResultMappings(id, context, Collections.emptyList(), resulType));
        String notNullColumn = context.getStringAttribute("notNullColumn");
        String columnPrefix = context.getStringAttribute("columnPrefix");
        String typeHandler = context.getStringAttribute("typeHandler");
        String resultSet = context.getStringAttribute("resultSet");
        String foreignColumn = context.getStringAttribute("foreignColumn");
        boolean lazy = "lazy".equals(context.getStringAttribute("fetchType", configuration.isLazyLoadingEnabled()? "lazy" : "eager"));
        Class<?> javaTypeClass = resolveClass(javaType);
        Class<? extends TypeHandler<?>> typeHandlerClass = resolveClass(typeHandler);
        JdbcType jdbcTypeEnum = resolveJdbcType(jdbcType);
        ResultMapping resultMapping = builderAssistant.buildResultMapping(resulType, property, column, javaTypeClass, jdbcTypeEnum,
                                nestedSelect, nestedResultMap, notNullColumn, columnPrefix, typeHandlerClass, flags, resultSet,foreignColumn, lazy);

        setTrimTypeHandler(resultMapping, context);
        return resultMapping;
    }

    private  String processNestedResultMappings(String id, XNode context, List<ResultMapping> resultMappings, Class<?> enclosingType) {
        if (Arrays.asList("association", "collection", "case").contains(context.getName()) && context.getStringAttribute("select") ==null) {
            validateCollection(context, enclosingType);
            ResultMap resultMap = processResultMapElement(()->getNestedMapping(id, context), context, resultMappings, enclosingType);
            return resultMap.getId();
        }
        return null;
    }

    private String getNestedMapping (String rootId, XNode node) {
        String id = node.getStringAttribute("id", node.getValueBasedIdentifier());
        if (id.startsWith("resultMap_")) {
            id = new StringBuilder("resultMap[").append(rootId).append("]_").append(id.substring(10)).toString();
        }
        return id;
    }

    protected  void validateCollection(XNode context, Class<?> enclosingType) {
        if ("collection".equalsIgnoreCase(context.getName()) && context.getStringAttribute("resultMap") == null
            && context.getStringAttribute("javaType") == null) {
            MetaClass metaResultType = MetaClass.forClass(enclosingType, configuration.getReflectorFactory());
            String property = context.getStringAttribute("property");
            if (!metaResultType.hasSetter(property)) {
                throw new BuilderException("Ambiguous collection type for property " + property + ". you must specify 'javaType' or 'resultMap'");
            }
        }
    }
}
