package com.sphereex.jmh.takin;

import com.pamirs.attach.plugin.apache.tomcatjdbc.obj.TomcatJdbcMediatorDataSource;
import com.pamirs.attach.plugin.apache.tomcatjdbc.util.TomcatJdbcDatasourceUtils;
import com.pamirs.pradar.internal.config.ShadowDatabaseConfig;
import com.pamirs.pradar.pressurement.agent.shared.service.GlobalConfig;
import com.pamirs.pradar.pressurement.datasource.SqlParser;
import com.pamirs.pradar.pressurement.datasource.util.DbUrlUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.apache.tomcat.jdbc.pool.DataSource;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Takins {

    private static Properties properties = null;

    public final static String URL = "url";
    public final static String SHADOW_URL = "shadowUrl";
    public final static String SCHEMA = "ndbc";
    public final static String SHADOW_SCHEMA = "shadowSchema";
    public final static String USER_NAME = "userName";
    public final static String PASSWORD = "password";
    public final static String APPLICATION_NAME = "applicationName";
    public final static String SHADOW_MAPPING = "shadow-mapping";
    public final static String DS_TYPE = "dsType";
    public final static String SHADOW_DRIVER = "shadowDriver";
    public final static String SHADOW_USER_NAME = "shadowUserName";
    public final static String SHADOW_PASSWORD = "shadowPassword";
    public final static String USE_SHADOW = "useShadow";

    public static DataSource createDataSource() throws Exception {
        ShadowDatabaseConfig config = getConfig();
        GlobalConfig.getInstance().getShadowDatasourceConfigs()
                .put(DbUrlUtils.getKey(config.getUrl(), config.getUsername()), config);

        TomcatJdbcMediatorDataSource dbMediatorDataSource = new TomcatJdbcMediatorDataSource();
        DataSource dataSource = new DataSource();
        dataSource.setUrl(getProperty(URL));
        dataSource.setUsername(getProperty(USER_NAME));
        dataSource.setDriverClassName(getProperty(SHADOW_DRIVER));
        dataSource.setPassword(getProperty(PASSWORD));
        dbMediatorDataSource.setDataSourceBusiness(dataSource);
        dataSource.setMaxWait(10000);
        dataSource.setMaxActive(200);
        Map<String, ShadowDatabaseConfig> configMap =
                Collections.singletonMap(DbUrlUtils.getKey(config.getUrl(), config.getUsername()), getConfig());
        if(Boolean.parseBoolean(getProperty(USE_SHADOW))) {
            return TomcatJdbcDatasourceUtils.generateDatasourceFromConfiguration(dataSource, configMap);
        } else {
            return dbMediatorDataSource.getDataSourceBusiness();
        }
    }

    @SneakyThrows
    public static ShadowDatabaseConfig getConfig() {
        ShadowDatabaseConfig config = new ShadowDatabaseConfig();
        config.setApplicationName(getProperty(APPLICATION_NAME));

        // 配置影子表的映射关系
        HashMap<String, String> shadowMapping = new HashMap<>();
        Arrays.stream(getProperty(SHADOW_MAPPING).split(","))
                .forEach(i -> shadowMapping.put(i.split(":")[0], i.split(":")[1]));
        config.setBusinessShadowTables(shadowMapping);

        // 数据源类型 0:影子库 1:影子表 2:影子库+影子表,
        config.setDsType(Integer.parseInt(getProperty(DS_TYPE)));

        config.setShadowDriverClassName(getProperty(SHADOW_DRIVER));
        config.setShadowUsername(getProperty(SHADOW_USER_NAME));
        config.setShadowPassword(getProperty(SHADOW_PASSWORD));
        config.setUrl(getProperty(URL));
        config.setShadowUrl(getProperty(SHADOW_URL));
        config.setSchema(getProperty(SCHEMA));
        config.setShadowSchema(getProperty(SHADOW_SCHEMA));

        return config;
    }

    public static String getProperty(String key) throws Exception {
        if (properties == null) {
            properties = new Properties();
//            BufferedReader bufferedReader = new BufferedReader(new FileReader("C:\\Users\\Administrator\\Desktop\\shadow.properties"));
            BufferedReader bufferedReader = new BufferedReader(new FileReader(System.getProperty("shadowConfig")));
            properties.load(bufferedReader);
        }
        return properties.getProperty(key);
    }

    @SneakyThrows
    public static String replaceTable(String sql) {
        return Boolean.parseBoolean(Takins.getProperty(Takins.USE_SHADOW))? SqlParser.parseAndReplaceTableNames(sql,
                DbUrlUtils.getKey(Takins.getProperty(Takins.URL), Takins.getProperty(Takins.USER_NAME)), "mysql", "other"):
                sql;
    }
}
