package com.sphereex.jmh.jdbc;

import java.sql.Connection;

public interface JDBCConnectionProvider {
    
    Connection getConnection();
}
