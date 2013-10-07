package com.syfen.redis.exceptions;

/**
 * User: ToneD
 * Created: 19/09/13 12:00 AM
 */
public class RedisNotInitializedException extends Exception {

    public RedisNotInitializedException(String message) {}

    public String getMessage() {

        return "Redis has not been initialized.";
    }
}
