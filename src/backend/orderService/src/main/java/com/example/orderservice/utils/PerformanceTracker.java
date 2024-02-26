package com.example.orderservice.utils;

import org.springframework.stereotype.Component;

@Component
public class PerformanceTracker {

    public long getCurrentTime() {
        return System.nanoTime();
    }

    public long getElapsedTimeMillis(long startTimeNano) {
        return getElapsedTimeNano(startTimeNano) / 1_000_000;
    }

    private long getElapsedTimeNano(long startTimeNano) {
        return System.nanoTime() - startTimeNano;
    }
}
