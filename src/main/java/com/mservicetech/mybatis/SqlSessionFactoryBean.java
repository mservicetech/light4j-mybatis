package com.mservicetech.mybatis;

import com.networknt.utility.ObjectUtils;
import com.networknt.utility.StringUtils;
import org.apache.ibatis.builder.xml.XMLConfigBuilder;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.cache.Cache;
import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.io.VFS;
import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.reflection.factory.ObjectFactory;
import org.apache.ibatis.reflection.wrapper.ObjectWrapperFactory;
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.apache.ibatis.type.TypeHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Modifier;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SqlSessionFactoryBean {

    private static final Logger logger = LoggerFactory.getLogger(SqlSessionFactoryBean.class);

    String CONFIG_LOCATION_DELIMITERS = ",; \t\n";

    private Configuration configuration;

    private InputStream configLocation;

    private InputStream[] mapperLocations;


    private DataSource dataSource;

    private TransactionFactory transactionFactory;

    private Properties configurationProperties;

    private SqlSessionFactoryBuilder sqlSessionFactoryBuilder = new SqlSessionFactoryBuilder();

    private SqlSessionFactory sqlSessionFactory;

    // EnvironmentAware requires spring 3.1
    private String environment = SqlSessionFactoryBean.class.getSimpleName();

    private boolean failFast;

    private Interceptor[] plugins;

    private TypeHandler<?>[] typeHandlers;

    private String typeHandlersPackage;

    @SuppressWarnings("rawtypes")
    private Class<? extends TypeHandler> defaultEnumTypeHandler;

    private Class<?>[] typeAliases;

    private String typeAliasesPackage;

    private Class<?> typeAliasesSuperType;

    private LanguageDriver[] scriptingLanguageDrivers;

    private Class<? extends LanguageDriver> defaultScriptingLanguageDriver;

    // issue #19. No default provider.
    private DatabaseIdProvider databaseIdProvider;

    private Class<? extends VFS> vfs;

    private Cache cache;

    private ObjectFactory objectFactory;

    private ObjectWrapperFactory objectWrapperFactory;

    /**
     * Sets the ObjectFactory.
     *
     * @since 1.1.2
     * @param objectFactory
     *          a custom ObjectFactory
     */
    public void setObjectFactory(ObjectFactory objectFactory) {
        this.objectFactory = objectFactory;
    }

    /**
     * Sets the ObjectWrapperFactory.
     *
     * @since 1.1.2
     * @param objectWrapperFactory
     *          a specified ObjectWrapperFactory
     */
    public void setObjectWrapperFactory(ObjectWrapperFactory objectWrapperFactory) {
        this.objectWrapperFactory = objectWrapperFactory;
    }

    /**
     * Set locations of MyBatis mapper files that are going to be merged into the {@code SqlSessionFactory} configuration
     * at runtime.
     *
     * This is an alternative to specifying "&lt;sqlmapper&gt;" entries in an MyBatis config file. This property being
     * based on Spring's resource abstraction also allows for specifying resource patterns here: e.g.
     * "classpath*:sqlmap/*-mapper.xml".
     *
     * @param mapperLocations
     *          location of MyBatis mapper files
     */
    public void setMapperLocations(InputStream... mapperLocations) {
        this.mapperLocations = mapperLocations;
    }

    /**
     * Gets the DatabaseIdProvider
     *
     * @since 1.1.0
     * @return a specified DatabaseIdProvider
     */
    public DatabaseIdProvider getDatabaseIdProvider() {
        return databaseIdProvider;
    }

    /**
     * Gets the VFS.
     *
     * @return a specified VFS
     */
    public Class<? extends VFS> getVfs() {
        return this.vfs;
    }

    /**
     * Sets the VFS.
     *
     * @param vfs
     *          a VFS
     */
    public void setVfs(Class<? extends VFS> vfs) {
        this.vfs = vfs;
    }

    /**
     * Gets the Cache.
     *
     * @return a specified Cache
     */
    public Cache getCache() {
        return this.cache;
    }

    /**
     * Sets the Cache.
     *
     * @param cache
     *          a Cache
     */
    public void setCache(Cache cache) {
        this.cache = cache;
    }

    /**
     * Mybatis plugin list.
     *
     * @since 1.0.1
     *
     * @param plugins
     *          list of plugins
     *
     */
    public void setPlugins(Interceptor... plugins) {
        this.plugins = plugins;
    }

    /**
     * Packages to search for type aliases.
     *
     * <p>
     * Since 2.0.1, allow to specify a wildcard such as {@code com.example.*.model}.
     *
     * @since 1.0.1
     *
     * @param typeAliasesPackage
     *          package to scan for domain objects
     *
     */
    public void setTypeAliasesPackage(String typeAliasesPackage) {
        this.typeAliasesPackage = typeAliasesPackage;
    }

    /**
     * Super class which domain objects have to extend to have a type alias created. No effect if there is no package to
     * scan configured.
     *
     * @since 1.1.2
     *
     * @param typeAliasesSuperType
     *          super class for domain objects
     *
     */
    public void setTypeAliasesSuperType(Class<?> typeAliasesSuperType) {
        this.typeAliasesSuperType = typeAliasesSuperType;
    }

    /**
     * Packages to search for type handlers.
     *
     * <p>
     * Since 2.0.1, allow to specify a wildcard such as {@code com.example.*.typehandler}.
     *
     * @since 1.0.1
     *
     * @param typeHandlersPackage
     *          package to scan for type handlers
     *
     */
    public void setTypeHandlersPackage(String typeHandlersPackage) {
        this.typeHandlersPackage = typeHandlersPackage;
    }

    /**
     * Set type handlers. They must be annotated with {@code MappedTypes} and optionally with {@code MappedJdbcTypes}
     *
     * @since 1.0.1
     *
     * @param typeHandlers
     *          Type handler list
     */
    public void setTypeHandlers(TypeHandler<?>... typeHandlers) {
        this.typeHandlers = typeHandlers;
    }

    /**
     * Set the default type handler class for enum.
     *
     * @since 2.0.5
     * @param defaultEnumTypeHandler
     *          The default type handler class for enum
     */
    public void setDefaultEnumTypeHandler(
            @SuppressWarnings("rawtypes") Class<? extends TypeHandler> defaultEnumTypeHandler) {
        this.defaultEnumTypeHandler = defaultEnumTypeHandler;
    }

    /**
     * List of type aliases to register. They can be annotated with {@code Alias}
     *
     * @since 1.0.1
     *
     * @param typeAliases
     *          Type aliases list
     */
    public void setTypeAliases(Class<?>... typeAliases) {
        this.typeAliases = typeAliases;
    }

    /**
     * If true, a final check is done on Configuration to assure that all mapped statements are fully loaded and there is
     * no one still pending to resolve includes. Defaults to false.
     *
     * @since 1.0.1
     *
     * @param failFast
     *          enable failFast
     */
    public void setFailFast(boolean failFast) {
        this.failFast = failFast;
    }


    /**
     * Set a customized MyBatis configuration.
     *
     * @param configuration
     *          MyBatis configuration
     * @since 1.3.0
     */
    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    public InputStream getConfigLocation() {
        return this.configLocation;
    }

    public void setConfigLocation(InputStream configLocation) {
        this.configLocation = configLocation;
    }

    /**
     * Set optional properties to be passed into the SqlSession configuration, as alternative to a
     * {@code &lt;properties&gt;} tag in the configuration xml file. This will be used to resolve placeholders in the
     * config file.
     *
     * @param sqlSessionFactoryProperties
     *          optional properties for the SqlSessionFactory
     */
    public void setConfigurationProperties(Properties sqlSessionFactoryProperties) {
        this.configurationProperties = sqlSessionFactoryProperties;
    }

    /**
     * Set the JDBC {@code DataSource} that this instance should manage transactions for. The {@code DataSource} should
     * match the one used by the {@code SqlSessionFactory}: for example, you could specify the same JNDI DataSource for
     * both.
     *
     * @param dataSource
     *          a JDBC {@code DataSource}
     *
     */
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }


    /**
     * Sets the {@code SqlSessionFactoryBuilder} to use when creating the {@code SqlSessionFactory}.
     *
     * This is mainly meant for testing so that mock SqlSessionFactory classes can be injected. By default,
     * {@code SqlSessionFactoryBuilder} creates {@code DefaultSqlSessionFactory} instances.
     *
     * @param sqlSessionFactoryBuilder
     *          a SqlSessionFactoryBuilder
     *
     */
    public void setSqlSessionFactoryBuilder(SqlSessionFactoryBuilder sqlSessionFactoryBuilder) {
        this.sqlSessionFactoryBuilder = sqlSessionFactoryBuilder;
    }

    /**
     * Set the MyBatis TransactionFactory to use. Default is {@code SpringManagedTransactionFactory}
     *
     * @param transactionFactory
     *          the MyBatis TransactionFactory
     */
    public void setTransactionFactory(TransactionFactory transactionFactory) {
        this.transactionFactory = transactionFactory;
    }

    /**
     * <b>NOTE:</b> This class <em>overrides</em> any {@code Environment} you have set in the MyBatis config file. This is
     * used only as a placeholder name. The default value is {@code SqlSessionFactoryBean.class.getSimpleName()}.
     *
     * @param environment
     *          the environment name
     */
    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    /**
     * Set scripting language drivers.
     *
     * @param scriptingLanguageDrivers
     *          scripting language drivers
     * @since 2.0.2
     */
    public void setScriptingLanguageDrivers(LanguageDriver... scriptingLanguageDrivers) {
        this.scriptingLanguageDrivers = scriptingLanguageDrivers;
    }

    /**
     * Set a default scripting language driver class.
     *
     * @param defaultScriptingLanguageDriver
     *          A default scripting language driver class
     * @since 2.0.2
     */
    public void setDefaultScriptingLanguageDriver(Class<? extends LanguageDriver> defaultScriptingLanguageDriver) {
        this.defaultScriptingLanguageDriver = defaultScriptingLanguageDriver;
    }


    public SqlSessionFactory getSqlSessionFactory() throws Exception {

        return this.sqlSessionFactory;
    }


    public Class<? extends SqlSessionFactory> getObjectType() {
        return this.sqlSessionFactory == null ? SqlSessionFactory.class : this.sqlSessionFactory.getClass();
    }

    /**
     * Build a {@code SqlSessionFactory} instance.
     *
     * The default implementation uses the standard MyBatis {@code XMLConfigBuilder} API to build a
     * {@code SqlSessionFactory} instance based on a Reader. Since 1.3.0, it can be specified a {@link Configuration}
     * instance directly(without config file).
     *
     * @return SqlSessionFactory
     * @throws Exception
     *           if configuration is failed
     */
    protected SqlSessionFactory buildSqlSessionFactory() throws Exception {

        final Configuration targetConfiguration;

        XMLConfigBuilder xmlConfigBuilder = null;
        if (this.configuration != null) {
            targetConfiguration = this.configuration;
            if (targetConfiguration.getVariables() == null) {
                targetConfiguration.setVariables(this.configurationProperties);
            } else if (this.configurationProperties != null) {
                targetConfiguration.getVariables().putAll(this.configurationProperties);
            }
        } else if (this.configLocation != null) {
            xmlConfigBuilder = new XMLConfigBuilder(this.configLocation, null, this.configurationProperties);
            targetConfiguration = xmlConfigBuilder.getConfiguration();
        } else {
            logger.debug("Property 'configuration' or 'configLocation' not specified, using default MyBatis Configuration");
            targetConfiguration = new Configuration();
            Optional.ofNullable(this.configurationProperties).ifPresent(targetConfiguration::setVariables);
        }

        Optional.ofNullable(this.objectFactory).ifPresent(targetConfiguration::setObjectFactory);
        Optional.ofNullable(this.objectWrapperFactory).ifPresent(targetConfiguration::setObjectWrapperFactory);
        Optional.ofNullable(this.vfs).ifPresent(targetConfiguration::setVfsImpl);

        if (StringUtils.hasLength(this.typeAliasesPackage)) {
            scanClasses(this.typeAliasesPackage, this.typeAliasesSuperType).stream()
                    .filter(clazz -> !clazz.isAnonymousClass()).filter(clazz -> !clazz.isInterface())
                    .filter(clazz -> !clazz.isMemberClass()).forEach(targetConfiguration.getTypeAliasRegistry()::registerAlias);
        }

        if (!ObjectUtils.isEmpty(this.typeAliases)) {
            Stream.of(this.typeAliases).forEach(typeAlias -> {
                targetConfiguration.getTypeAliasRegistry().registerAlias(typeAlias);
                logger.debug( "Registered type alias: '" + typeAlias + "'");
            });
        }

        if (!ObjectUtils.isEmpty(this.plugins)) {
            Stream.of(this.plugins).forEach(plugin -> {
                targetConfiguration.addInterceptor(plugin);
                logger.debug( "Registered plugin: '" + plugin + "'");
            });
        }

        if (StringUtils.hasLength(this.typeHandlersPackage)) {
            scanClasses(this.typeHandlersPackage, TypeHandler.class).stream().filter(clazz -> !clazz.isAnonymousClass())
                    .filter(clazz -> !clazz.isInterface()).filter(clazz -> !Modifier.isAbstract(clazz.getModifiers()))
                    .forEach(targetConfiguration.getTypeHandlerRegistry()::register);
        }

        if (!ObjectUtils.isEmpty(this.typeHandlers)) {
            Stream.of(this.typeHandlers).forEach(typeHandler -> {
                targetConfiguration.getTypeHandlerRegistry().register(typeHandler);
                logger.debug("Registered type handler: '" + typeHandler + "'");
            });
        }

        targetConfiguration.setDefaultEnumTypeHandler(defaultEnumTypeHandler);

        if (!ObjectUtils.isEmpty(this.scriptingLanguageDrivers)) {
            Stream.of(this.scriptingLanguageDrivers).forEach(languageDriver -> {
                targetConfiguration.getLanguageRegistry().register(languageDriver);
                logger.debug("Registered scripting language driver: '" + languageDriver + "'");
            });
        }
        Optional.ofNullable(this.defaultScriptingLanguageDriver)
                .ifPresent(targetConfiguration::setDefaultScriptingLanguage);

        if (this.databaseIdProvider != null) {// fix #64 set databaseId before parse mapper xmls
            try {
                targetConfiguration.setDatabaseId(this.databaseIdProvider.getDatabaseId(this.dataSource));
            } catch (SQLException e) {
                throw new IOException("Failed getting a databaseId", e);
            }
        }

        Optional.ofNullable(this.cache).ifPresent(targetConfiguration::addCache);

        if (xmlConfigBuilder != null) {
            try {
                xmlConfigBuilder.parse();
                logger.debug("Parsed configuration file: '" + this.configLocation + "'");
            } catch (Exception ex) {
                throw new IOException("Failed to parse config resource: " + this.configLocation, ex);
            } finally {
                ErrorContext.instance().reset();
            }
        }

        targetConfiguration.setEnvironment(new Environment(this.environment,
                this.transactionFactory == null ? new JdbcTransactionFactory() : this.transactionFactory,
                this.dataSource));

        if (this.mapperLocations != null) {
            if (this.mapperLocations.length == 0) {
                logger.warn("Property 'mapperLocations' was specified but matching resources are not found.");
            } else {
                for (InputStream mapperLocation : this.mapperLocations) {
                    if (mapperLocation == null) {
                        continue;
                    }
                    try {
                        XMLMapperBuilder xmlMapperBuilder = new XMLMapperBuilder(mapperLocation,
                                targetConfiguration, mapperLocation.toString(), targetConfiguration.getSqlFragments());
                        xmlMapperBuilder.parse();
                    } catch (Exception e) {
                        throw new IOException("Failed to parse mapping resource: '" + mapperLocation + "'", e);
                    } finally {
                        ErrorContext.instance().reset();
                    }
                    logger.debug( "Parsed mapper file: '" + mapperLocation + "'");
                }
            }
        } else {
            logger.debug("Property 'mapperLocations' was not specified.");
        }

        return this.sqlSessionFactoryBuilder.build(targetConfiguration);
    }


    private Set<Class<?>> scanClasses(String packageNames, Class<?> assignableType) throws IOException {
        Set<Class<?>> classes = new HashSet<>();
        String[] packageNameArray = StringUtils.tokenizeToStringArray(packageNames, CONFIG_LOCATION_DELIMITERS);
        for (String packageName : packageNameArray) {

            InputStream stream = ClassLoader.getSystemClassLoader()
                    .getResourceAsStream(packageName.replaceAll("[.]", "/"));
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            classes.addAll(reader.lines()
                    .filter(line -> line.endsWith(".class"))
                    .map(line -> getClass(line, packageName))
                    .collect(Collectors.toSet()));
        }
        return classes;
    }

    private Class<?> getClass(String className, String packageName) {
        String classForName = null;
        try {
            classForName = packageName + "." + className.substring(0, className.lastIndexOf('.'));
            return  Resources.classForName(classForName);
        } catch (ClassNotFoundException e) {
            logger.debug("Java  reflection error on class name:" + classForName);
        }
        return null;
    }
}
