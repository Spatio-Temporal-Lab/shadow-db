package com.sphereex.jmh.jdbc;

import com.sphereex.jmh.config.BenchmarkParameters;
import com.sphereex.jmh.util.Strings;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.ThreadLocalRandom;

@BenchmarkMode(Mode.SingleShotTime)
@State(Scope.Thread)
public abstract class UnpooledBatchInsertBenchmarkBase implements JDBCConnectionProvider {
    
    private static final int OFFSET = 100000000;
    
    private final ThreadLocalRandom random = ThreadLocalRandom.current();
    
    private final PreparedStatement[] inserts = new PreparedStatement[BenchmarkParameters.TABLES];
    
    private Connection connection;
    
    @Setup(Level.Trial)
    public void setup() throws Exception {
        connection = getConnection();
        cleanup();
        for (int i = 0; i < inserts.length; i++) {
            inserts[i] = connection.prepareStatement(String.format("insert into sbtest%d (id,k,c,pad) values (?,?,?,?)", i + 1));
        }
        setupBatch();
    }
    
    private void setupBatch() throws SQLException {
        for (PreparedStatement each : inserts) {
            for (int i = 0; i < BenchmarkParameters.TABLE_SIZE; i++) {
                each.setInt(1, i + 1 + OFFSET);
                each.setInt(2, random.nextInt(Integer.MAX_VALUE));
                each.setString(3, Strings.randomString(120));
                each.setString(4, Strings.randomString(60));
                each.addBatch();
            }
        }
    }
    
    @Benchmark
    public void batchInserts() throws Exception {
        for (PreparedStatement each : inserts) {
            each.executeBatch();
        }
    }
    
    @TearDown(Level.Trial)
    public void tearDown() throws Exception {
        for (PreparedStatement each : inserts) {
            each.close();
        }
        cleanup();
        connection.close();
    }
    
    private void cleanup() throws SQLException {
        try (Statement statement = connection.createStatement()) {
            for (int i = 0; i < inserts.length; i++) {
                statement.executeUpdate(String.format("delete from sbtest%d where id between %d and %d", i + 1, OFFSET + 1, OFFSET + 1 + BenchmarkParameters.TABLE_SIZE));
            }
        }
    }
}
