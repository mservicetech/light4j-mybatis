## light4j-mybatis

Common components for using mybatis in light-4j framework. It provides generate mybatis session management and multiple transactions support.

With light4j-mybatis, user can select use normal mybatis query/mapping config or simple add the query/mapping config to light-4j style yaml config file (mybatis.yml or values.yml). 

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

### Usage by using mybatis mappng annotation

```
    @Insert("INSERT INTO CLIENT (FULL_NAME, EMAIL) VALUES (#{name}, #{email})")
    @Options(useGeneratedKeys = true, keyColumn = "id")
    int insertClient(Client client);

    @Select("SELECT ID, FULL_NAME, EMAIL FROM CLIENT WHERE EMAIL = #{email}")
    @ResultMap("clientResult")
    Client selectClientByEmail(String email);
```

### Usage by using light4j yaml format config

User can use light4j yaml config file (mybatis.yml or values.yml) to config the query/result mapping, for example:

```
mybatis.resultMappings:
  clientResult: |
      <resultMap type="Client" trim="true">
       <id property="id" column="ID"/>
       <result property="email" column="EMAIL"/>
       <result property="name" column="NAME"/>
      </resultMap>
  reservationResult: |
      <resultMap type="Reservation">
       <id property="id" column="ID"/>
       <association property="client" column="client_id" javaType="Client" select="selectClientById">
       </association>
       <result property="arrival" column="arrival_date"/>
       <result property="departure" column="departure_date"/>
      </resultMap>

mybatis.sqlSource:
    updateReservation: UPDATE reservation SET arrival_date=@{arrival}, departure_date='@{departure}' WHERE id = '@{id}'
    deleteReservation: UPDATE reservation SET status='Inactive'  WHERE id = '@{reservationId}'
    selectReservation: SELECT id, client_id, arrival_date, departure_date FROM reservation WHERE id = '@{id}' and status = 'Active'
    insertReservation: INSERT INTO reservation(id, client_id, arrival_date,  departure_date) VALUES ('@{id}', @{CLIENT_id}, '@{arrivalDate}', '@{departureDate}')
    deleteReservedDates:  |
        <script>
          DELETE FROM reserved WHERE reserved_date IN 
          <foreach collection="items" item="item" separator="," open="(" close=")">'@{item}'></foreach>
        </script>
    verifyReserveDates:  |
        <script>
          SELECT reserved_date FROM reserved WHERE reserved_date IN 
          <foreach collection="items" item="item" separator="," open="(" close=")">'@{item}'></foreach>
        </script>
```

For the detail, please refer the code in the test package.