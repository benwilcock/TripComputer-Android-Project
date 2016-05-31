package com.soagrowers.android.tripcomputer.services;

/**
 * Created by Ben on 15/04/2014.
 */
public interface ServiceManager {

    public void connect() throws ServiceConnectionException;

    public void disconnect();
}
