package com.sphereex.jmh.takin;

import com.sphereex.jmh.jdbc.UnpooledPointSelectBenchmarkBase;
import com.sphereex.jmh.jdbc.UnpooledReadOnlyBenchmarkBase;
import com.sphereex.jmh.takin.util.CurlUtil;

import java.sql.Connection;

public class TakinWebReadOnlyBenchmark extends UnpooledReadOnlyBenchmarkBase {
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
    public void oltpReadOnly() throws Exception {
        String curl = "curl --location --request POST 'http://" + url + "/read_only' --header 'Content-Type: application/json'";
        CurlUtil.execCmd(useShadow? curl + " -H 'User-Agent:PerfomanceTest'": curl);
    }

}
