package com.sphereex.jmh.takin;

import com.sphereex.jmh.jdbc.UnpooledReadWriteBenchmarkBase;
import lombok.SneakyThrows;

import javax.sql.DataSource;
import java.sql.Connection;

public class TakinReadWriteBenchmark extends UnpooledReadWriteBenchmarkBase {

    private static DataSource DATA_SOURCE = null;

    static {
        try {
            DATA_SOURCE = Takins.createDataSource();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SneakyThrows
    @Override
    public Connection getConnection() {
        return DATA_SOURCE.getConnection();
    }
}
