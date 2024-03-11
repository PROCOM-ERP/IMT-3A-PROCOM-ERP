package com.example.authenticationservice.utils;

import org.springframework.stereotype.Component;

@Component
public class MessageUtils {

    private static final long BASE_DELAY = 1000;

    /**
     * Calculates the delay for exponential backoff.
     * 
     * @param retryCount the current retry attempt count.
     * @return the delay in milliseconds before the next retry attempt.
     */
    public long calculateExponentialBackoff(int retryCount) {
        return (long) (Math.pow(2, retryCount) * BASE_DELAY);
    }
}
