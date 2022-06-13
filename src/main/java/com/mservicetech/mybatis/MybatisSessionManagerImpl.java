package com.mservicetech.mybatis;

import com.mservicetech.mybatis.exception.MyBatisExecuteException;
import com.networknt.db.GenericDataSource;
import org.apache.ibatis.cursor.Cursor;
import org.apache.ibatis.executor.BatchResult;
import org.apache.ibatis.io.ResolverUtil;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.*;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.util.*;

public class MybatisSessionManagerImpl implements MybatisSessionManager{

    private static final Logger logger = LoggerFactory.getLogger(SqlSessionFactoryBean.class);

    public SqlSessionFactory sqlSessionFactory;

    private ThreadLocal<List<SessionWrapper>> threadLocal = new ThreadLocal<>() {
        @Override
        protected  List<SessionWrapper> initialValue() {
               return new ArrayList<>();
        }
    };

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
        Optional.ofNullable(mybatisConfig.getResultMappings()).ifPresent(m->{
            // add result mapping to default namespace
            //new
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
        try {
            if (SessionMode.NEW.equals(mode)) {
                return this.executeWithNewSession(task);
            } else if (SessionMode.CURRENT.equals(mode)) {
                return this.executeWithCurrentSession(task);
            } else {
                return this.executeWithExistingSession(task);
            }
        } catch (RuntimeException e) {
            throw  e;
        } catch (Exception e) {
            throw  new MyBatisExecuteException("Mybatis call execution failed", e);
        }

    }

    private <T> T executeWithNewSession(SessionTask<T> task) throws Exception {
        SqlSession session = this.sqlSessionFactory.openSession(false);
        List<SessionWrapper> sessionWrappers = this.threadLocal.get();
        SessionWrapper sessionWrapper = new SessionWrapper(session);
        sessionWrappers.add(sessionWrapper);
        boolean rb = false;
        try {
            T v = task.execute(session);
            if (! sessionWrapper.isRollback()) {
                session.commit();
            } else  {
             session.rollback();
             rb = true;
             throw new IllegalArgumentException("Session is marked as rollback session.");
            }
            return v;
        } catch (RuntimeException e) {
            if (!rb) {
                session.rollback();
            }
            throw  e;
        } finally {
            sessionWrappers.remove(sessionWrapper);
            session.close();
        }
    }

    private  <T> T executeWithCurrentSession(SessionTask<T> task) throws Exception {
        SqlSession session = this.getCurrentSession();
        if (session != null) {
            try {
                return task.execute(session);
            } catch (Exception e) {
                session.rollback();
                throw e;
            }
        } else {
            return this.executeWithNewSession(task);
        }
    }

    private  <T> T executeWithExistingSession(SessionTask<T> task) throws Exception {
        SqlSession session = this.getCurrentSession();
        if (session != null) {
            try {
                return task.execute(session);
            } catch (Exception e) {
                session.rollback();
                throw e;
            }
        } else {
            throw new IllegalArgumentException("There is not existing session in the current thread local. ");
        }
    }

    private SqlSession getCurrentSession() {
       List<SessionWrapper> sessionWrappers = this.threadLocal.get();
       return sessionWrappers.isEmpty()? null : sessionWrappers.get(0);
    }

    public Set<Class<? extends Class<?>>> getMapperClasses(String packageName) {
        ResolverUtil<Class<?>> resolverUtil = new ResolverUtil<>();
        resolverUtil.find(new ResolverUtil.IsA(Object.class), packageName);
        return resolverUtil.getClasses();
    }

    private static class SessionWrapper implements SqlSession {

        public final SqlSession nested;
        private boolean rollback = false;

        SessionWrapper(SqlSession nested) {
            this.nested = nested;
        }

        @Override
        public <T> T selectOne(String s) {
            return this.nested.selectOne(s);
        }

        @Override
        public <T> T selectOne(String s, Object o) {
            return this.nested.selectOne(s, o);
        }

        @Override
        public <E> List<E> selectList(String s) {
            return this.nested.selectList(s);
        }

        @Override
        public <E> List<E> selectList(String s, Object o) {
            return this.nested.selectList(s, o);
        }

        @Override
        public <E> List<E> selectList(String s, Object o, RowBounds rowBounds) {
            return this.nested.selectList(s, o, rowBounds);
        }

        @Override
        public <K, V> Map<K, V> selectMap(String s, String s1) {
            return this.nested.selectMap(s,s1);
        }

        @Override
        public <K, V> Map<K, V> selectMap(String s, Object o, String s1) {
            return this.nested.selectMap(s,o, s1);
        }

        @Override
        public <K, V> Map<K, V> selectMap(String s, Object o, String s1, RowBounds rowBounds) {
            return this.nested.selectMap(s,o, s1, rowBounds);
        }

        @Override
        public <T> Cursor<T> selectCursor(String s) {
            return this.nested.selectCursor(s);
        }

        @Override
        public <T> Cursor<T> selectCursor(String s, Object o) {
            return this.nested.selectCursor(s, o);
        }

        @Override
        public <T> Cursor<T> selectCursor(String s, Object o, RowBounds rowBounds) {
            return this.nested.selectCursor(s, o, rowBounds);
        }

        @Override
        public void select(String s, Object o, ResultHandler resultHandler) {
            this.nested.select(s, o, resultHandler);
        }

        @Override
        public void select(String s, ResultHandler resultHandler) {
            this.nested.select(s, resultHandler);
        }

        @Override
        public void select(String s, Object o, RowBounds rowBounds, ResultHandler resultHandler) {
            this.nested.select(s, o, rowBounds, resultHandler);
        }

        @Override
        public int insert(String s) {
            return this.nested.insert(s);
        }

        @Override
        public int insert(String s, Object o) {
            return this.nested.insert(s, o);
        }

        @Override
        public int update(String s) {
            return this.nested.update(s);
        }

        @Override
        public int update(String s, Object o) {
            return this.nested.update(s, o);
        }

        @Override
        public int delete(String s) {
            return this.nested.delete(s);
        }

        @Override
        public int delete(String s, Object o) {
            return this.nested.delete(s, o);
        }

        @Override
        public void commit() {
            if (this.rollback) {
                throw  new IllegalArgumentException("The session has been marked as rollback, cannot commit the session");
            }
        }

        @Override
        public void commit(boolean force) {
            if (this.rollback) {
                throw  new IllegalArgumentException("The session has been marked as rollback, cannot commit the session");
            }
            this.nested.commit(force);
        }

        @Override
        public void rollback() {
           this.rollback = true;
        }

        @Override
        public void rollback(boolean force) {
            this.nested.rollback(force);
            this.rollback = true;
        }

        @Override
        public List<BatchResult> flushStatements() {
            return this.nested.flushStatements();
        }

        @Override
        public void close() {
            this.nested.close();
        }

        @Override
        public void clearCache() {
            this.nested.clearCache();
        }

        @Override
        public Configuration getConfiguration() {
            return this.nested.getConfiguration();
        }

        @Override
        public <T> T getMapper(Class<T> aClass) {
            return this.nested.getMapper(aClass);
        }

        @Override
        public Connection getConnection() {
            return this.nested.getConnection();
        }

        public boolean isRollback() {
            return this.rollback;
        }
    }
}
