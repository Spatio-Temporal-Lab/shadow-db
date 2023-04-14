package com.sphereex.jmh.jdbc;

import com.sphereex.jmh.config.BenchmarkParameters;
import com.sphereex.jmh.takin.Takins;
import com.sphereex.jmh.util.Strings;
import org.openjdk.jmh.annotations.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;

@State(Scope.Thread)
public abstract class UnpooledReadWriteBenchmarkBase implements JDBCConnectionProvider {

    private AtomicLong count = new AtomicLong();
    
    private final ThreadLocalRandom random = ThreadLocalRandom.current();
    
    private final PreparedStatement[] reads = new PreparedStatement[BenchmarkParameters.TABLES];
    
    private final PreparedStatement[] indexUpdates = new PreparedStatement[BenchmarkParameters.TABLES];
    
    private final PreparedStatement[] nonIndexUpdates = new PreparedStatement[BenchmarkParameters.TABLES];
    
    private final PreparedStatement[] deletes = new PreparedStatement[BenchmarkParameters.TABLES];
    
    private final PreparedStatement[] inserts = new PreparedStatement[BenchmarkParameters.TABLES];

    private Connection connection;
    
    @Setup(Level.Iteration)
    public void setup() throws Exception {
        connection = getConnection();
        for (int i = 0; i < reads.length; i++) {
            reads[i] = connection.prepareStatement(Takins.replaceTable(String.format("select c from sbtest%d where id=?", i + 1)));
        }
        for (int i = 0; i < indexUpdates.length; i++) {
            indexUpdates[i] = connection.prepareStatement(Takins.replaceTable(String.format("update sbtest%d set k=k+1 where id=?", i + 1)));
        }
        for (int i = 0; i < nonIndexUpdates.length; i++) {
            nonIndexUpdates[i] = connection.prepareStatement(Takins.replaceTable(String.format("update sbtest%d set c=? where id=?", i + 1)));
        }
        for (int i = 0; i < deletes.length; i++) {
            deletes[i] = connection.prepareStatement(Takins.replaceTable(String.format("delete from sbtest%d where id=?", i + 1)));
            inserts[i] = connection.prepareStatement(Takins.replaceTable(String.format("insert into sbtest%d (id,k,c,pad) values (?,?,?,?)", i + 1)));
        }
    }
    
    @Benchmark
    @BenchmarkMode({Mode.Throughput, Mode.AverageTime, Mode.SampleTime})
    public void oltpReadWrite() throws Exception {
        try {
            connection.setAutoCommit(false);
            for (PreparedStatement each : reads) {
                each.setInt(1, random.nextInt(BenchmarkParameters.TABLE_SIZE));
                each.execute();
            }
            PreparedStatement indexUpdate = indexUpdates[random.nextInt(BenchmarkParameters.TABLES)];
            indexUpdate.setInt(1, random.nextInt(BenchmarkParameters.TABLE_SIZE));
            indexUpdate.execute();

            PreparedStatement nonIndexUpdate = nonIndexUpdates[random.nextInt(BenchmarkParameters.TABLES)];
            nonIndexUpdate.setString(1, Strings.randomString(120));
            nonIndexUpdate.setInt(2, random.nextInt(BenchmarkParameters.TABLE_SIZE));
            nonIndexUpdate.execute();

            int table = random.nextInt(BenchmarkParameters.TABLES);
            int id = random.nextInt(BenchmarkParameters.TABLE_SIZE);

            PreparedStatement delete = deletes[table];
            delete.setInt(1, id);

            PreparedStatement insert = inserts[table];
            insert.setInt(1, id);
            insert.setInt(2, random.nextInt(Integer.MAX_VALUE));
            insert.setString(3, Strings.randomString(120));
            insert.setString(4, Strings.randomString(60));

            delete.execute();
            insert.execute();

            count.addAndGet(1);

            connection.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @TearDown(Level.Iteration)
    public void tearDown() throws Exception {
        for (PreparedStatement each : reads) {
            each.close();
        }
        for (PreparedStatement each : indexUpdates) {
            each.close();
        }
        for (PreparedStatement each : nonIndexUpdates) {
            each.close();
        }
        for (PreparedStatement each : deletes) {
            each.close();
        }
        for (PreparedStatement each : inserts) {
            each.close();
        }

        connection.close();
    }
}
