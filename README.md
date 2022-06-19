## light4j-mybatis

Common components for using mybatis db access in light-4j framework. 

### Usage by using mybatis XML base config

There three solution for the light4j integration:

- Define Repository class and extend the SimpleBaseRepository class (Please refer the sample and test cases in the test package)

```
  - com.networknt.db.GenericDataSource:
      - com.networknt.db.H2DataSource:
          - java.lang.String: H2DataSource
  - com.mservicetech.campsite.repository.CampsiteRepository:
      - com.mservicetech.campsite.repository.CampsiteRepositoryImpl
```

- Using StartupHookProvider:

```
  - com.networknt.server.StartupHookProvider:
    - com.mservicetech.mybatis.startup.MybatisStartupHookProvider
```

The MybatisStartupHookProvider will initial the SqlSessionFactory at service startup time, and it can be use in the DAO/Service class directly (MybatisStartupHookProvider.sqlSessionFactory)

- Using MybatisSessionManager by extend BaseRepository class:

define the MybatisSessionManager in the service.yml (or values.yml)

```
  - com.mservicetech.mybatis.MybatisSessionManager:
      - com.mservicetech.mybatis.MybatisSessionManagerImpl
  - com.mservicetech.campsite.repository.CampsiteRepository:
      - com.mservicetech.campsite.repository.CampsiteRepositoryImpl
```


- Using SqlSessionFactoryBean

SqlSessionFactoryBean is similar as Spring-Mybatis SqlSessionFactoryBean class. It has full setting for the Mybatis. 

### Config file

Config file mybatis.yml defines config values for model package and mapper package:

```
mybatis.registerAliases: com.mservicetech.campsite.model
mybatis.mapperPackage: com.mservicetech.campsite.mapper
```


### Usage by using light4j yaml format config

//TODO