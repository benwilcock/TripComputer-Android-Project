package com.soagrowers.android.tripcomputer.services;

/**
 * Created by Ben on 15/04/2014.
 */
public class ServiceConnectionException extends RuntimeException {

    public ServiceConnectionException(String message) {
        super(message);
    }

    public ServiceConnectionException(Throwable throwable) {
        super(throwable);
    }
}
