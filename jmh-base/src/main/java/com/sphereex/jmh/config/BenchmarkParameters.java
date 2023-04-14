package com.sphereex.jmh.config;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BenchmarkParameters {
    
    public static final int TABLES = Integer.getInteger("tables", 10);
    
    public static final int TABLE_SIZE = Integer.getInteger("tableSize", 1000000);
}
