package com.syfen.redis.exceptions;

/**
 * User: ToneD
 * Created: 18/09/13 11:07 PM
 */
public class RedisAlreadyInitializedException extends Exception {

    public RedisAlreadyInitializedException() {}

    public String getMessage() {

        return "Redis can only be initialized one time.";
    }
}
