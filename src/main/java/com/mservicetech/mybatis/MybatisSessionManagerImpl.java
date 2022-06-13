package com.mservicetech.mybatis;

import com.networknt.db.GenericDataSource;
import org.apache.ibatis.io.ResolverUtil;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class MybatisSessionManagerImpl implements MybatisSessionManager{

    private static final Logger logger = LoggerFactory.getLogger(SqlSessionFactoryBean.class);

    public SqlSessionFactory sqlSessionFactory;

    public MybatisSessionManagerImpl(GenericDataSource genericDataSource) {
        TransactionFactory transactionFactory = new JdbcTransactionFactory();
        Environment env = new Environment(Optional.ofNullable(serverConfig.getEnvironment()).orElse("dev"), transactionFactory, genericDataSource.getDataSource());
        CustomConfiguration configuration = new CustomConfiguration(env);

        Optional.ofNullable(mybatisConfig.getRegisterAliases()).ifPresent(a-> {
           logger.info("Register type alias: {}" , a);
           configuration.getTypeAliasRegistry().registerAliases(a);
        });

        Set<Class<? extends Class<?>>> mappers = new HashSet<>();
        Optional.ofNullable(mybatisConfig.getMapperPackage()).ifPresent(p-> {
            logger.info("Retrieve mapper classes from package: {}" , p);
            mappers.addAll(getMapperClasses(p));
        });
        mappers.forEach(c->{
            configuration.addMapper(c);
        });

        sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);
        logger.info("Mybatis session manager created.");
    }

    @Override
    public SqlSessionFactory getSessionFactory() {
        return this.sqlSessionFactory;
    }

    /**
     * This method will make sure all database process in the method call will use same sql session.
     * And the session will be committed or rollbacked in same transaction.
     *
     * @param <T>
     * @param mode SessionMode
     * @return <T>
     */
    @Override
    public <T> T executeWithSession(SessionTask<T> task, SessionMode mode) {
        return null;
    }

    private <T> T executeWithNewSession(SessionTask<T> task) throws Exception {

        SqlSession session = this.sqlSessionFactory.openSession(false);

        return task.execute(session);
    }

    public Set<Class<? extends Class<?>>> getMapperClasses(String packageName) {
        ResolverUtil<Class<?>> resolverUtil = new ResolverUtil<>();
        resolverUtil.find(new ResolverUtil.IsA(Object.class), packageName);
        return resolverUtil.getClasses();
    }
}
