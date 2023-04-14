package com.sphereex.jmh.takin;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.profile.StackProfiler;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.sql.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@State(Scope.Thread)
@Warmup(iterations = 5, time = 3)
@Measurement(iterations = 5, time = 3)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class PgPreparedStatementBenchmark {
    
    private final Connection connection;
    
    private final ThreadLocalRandom random = ThreadLocalRandom.current();
    
    private PreparedStatement preparedStatement;
    
    public PgPreparedStatementBenchmark() {
        Connection connection;
        try {
            DataSource dataSource = Takins.createDataSource();
            connection = dataSource.getConnection();
        } catch (Exception e) {
            connection = null;
            e.printStackTrace();
        }
        this.connection = connection;
    }
    
    @Setup(Level.Invocation)
    public void setup() {
        try {
            preparedStatement = connection.prepareStatement("select ?");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    @Benchmark
    public void benchSetTimestamp() throws SQLException {
        preparedStatement.setTimestamp(1, new Timestamp(random.nextLong(Long.MAX_VALUE)));
    }
    
    @TearDown(Level.Invocation)
    public void tearDown() {
        try {
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) throws RunnerException {
        new Runner(new OptionsBuilder()
                .include(PgPreparedStatementBenchmark.class.getName())
                .threads(Runtime.getRuntime().availableProcessors())
                .forks(3)
                .addProfiler(StackProfiler.class)
                .build()).run();
    }
}
