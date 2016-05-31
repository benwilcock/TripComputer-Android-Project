package com.soagrowers.android.tripcomputer.services;

/**
 * Created by Ben on 29/11/13.
 */
public class JourneyStartException extends Exception {

    public JourneyStartException(String message) {
        super(message);
    }

    public JourneyStartException(Throwable throwable) {
        super(throwable);
    }
}
