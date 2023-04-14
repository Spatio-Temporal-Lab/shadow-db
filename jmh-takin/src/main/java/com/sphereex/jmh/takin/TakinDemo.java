package com.sphereex.jmh.takin;

import com.pamirs.pradar.pressurement.datasource.SqlParser;
import com.pamirs.pradar.pressurement.datasource.util.DbUrlUtils;
import lombok.SneakyThrows;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TakinDemo {

    public static void main(String[] args) throws Exception {
        DataSource dataSource = Takins.createDataSource();
        String sql = "select * from sbtest1";
        String executeSql = Boolean.parseBoolean(Takins.getProperty(Takins.USE_SHADOW))? replaceTable(sql) : sql;
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement =
                     connection.prepareStatement(executeSql);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                System.out.println(resultSet.getObject(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @SneakyThrows
    public static String replaceTable(String sql) {
        return SqlParser.parseAndReplaceTableNames(sql,
                DbUrlUtils.getKey(Takins.getProperty(Takins.URL), Takins.getProperty(Takins.USER_NAME)), "mysql", "other");
    }
}
