package com.mservicetech.mybatis;

import com.networknt.config.Config;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class MybatisConfigTest {

    private static MybatisConfig mybatisConfig;

    @BeforeAll
    public static void setUp() throws SQLException, IOException {
        mybatisConfig = MybatisConfig.load();
    }

    @Test
    public void testConfigValues() {
        assertNotNull(mybatisConfig.getMapperPackage());
        Map<String, String> nameAliases = mybatisConfig.getNameAliases();
        assertNotNull(nameAliases);
        Map<String, String> resultMappings = mybatisConfig.getResultMappings();
        assertNotNull(resultMappings);
        Map<String, String> sqlSource = mybatisConfig.getSqlSource();
        assertNotNull(sqlSource);
    }
}
