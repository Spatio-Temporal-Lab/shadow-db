package com.sphereex.jmh.takin;

import com.sphereex.jmh.jdbc.UnpooledNoneIndexUpdateOnlyBenchmarkBase;
import com.sphereex.jmh.jdbc.UnpooledPointSelectBenchmarkBase;
import com.sphereex.jmh.takin.util.CurlUtil;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;

import java.sql.Connection;

public class TakinWebUpdateNonIndexBenchmark extends UnpooledNoneIndexUpdateOnlyBenchmarkBase {
    @Override
    public Connection getConnection() {
        return null;
    }

    @Override
    public void setup() throws Exception {

    }

    @Override
    public void tearDown() throws Exception {

    }

    public static String url = System.getProperty("demoUrl");
    public static boolean useShadow = Boolean.parseBoolean(System.getProperty("useShadow"));


    @Override
    public void oltpUpdateOnly() throws Exception {
        String curl = "curl --location --request POST 'http://" + url + "/update_non_index' --header 'Content-Type: application/json'";
        CurlUtil.execCmd(useShadow? curl + " -H 'User-Agent:PerfomanceTest'": curl);
    }

}
