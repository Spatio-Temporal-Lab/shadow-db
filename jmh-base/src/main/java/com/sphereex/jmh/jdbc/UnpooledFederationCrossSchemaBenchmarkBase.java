/*
 * Copyright (c) 2014, Oracle America, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 *  * Neither the name of Oracle nor the names of its contributors may be used
 *    to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.sphereex.jmh.jdbc;

import com.sphereex.jmh.config.BenchmarkParameters;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.concurrent.ThreadLocalRandom;

@State(Scope.Thread)
public abstract class UnpooledFederationCrossSchemaBenchmarkBase implements JDBCConnectionProvider {
    
    private PreparedStatement preparedStatement;
    
    private Connection connection;
    
    @Setup(Level.Trial)
    public void setup() throws Exception {
        connection = getConnection();
        preparedStatement = connection.prepareStatement("select * from sbtest1 a inner join sbtest3 b on a.k = b.k WHERE a.id=? and b.id < 1000");
    }

    @Benchmark
    public void oltpFederationSelect() throws Exception {
        preparedStatement.setInt(1,ThreadLocalRandom.current().nextInt(BenchmarkParameters.TABLE_SIZE));
        preparedStatement.execute();
    }
    
    @TearDown(Level.Trial)
    public void tearDown() throws Exception {
        preparedStatement.close();
        connection.close();
    }
}
