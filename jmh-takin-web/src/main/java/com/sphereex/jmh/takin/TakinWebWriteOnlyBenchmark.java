package com.sphereex.jmh.takin;

import com.sphereex.jmh.jdbc.UnpooledWriteOnlyBenchmarkBase;
import com.sphereex.jmh.takin.util.CurlUtil;

import java.sql.Connection;

public class TakinWebWriteOnlyBenchmark extends UnpooledWriteOnlyBenchmarkBase {
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
    public void oltpWriteOnly() throws Exception {
        String curl = "curl --location --request POST 'http://" + url + "/write_only' --header 'Content-Type: application/json'";
        CurlUtil.execCmd(useShadow? curl + " -H 'User-Agent:PerfomanceTest'": curl);
    }

}
