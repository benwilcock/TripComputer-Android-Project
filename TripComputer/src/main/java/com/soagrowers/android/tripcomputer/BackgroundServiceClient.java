package com.soagrowers.android.tripcomputer;

import com.soagrowers.android.utils.Enums;

/**
 * Created by Ben on 11/09/13.
 */
public interface BackgroundServiceClient {

    public void onServiceConnected(Enums.SERVICE_TYPE name);

    public void onServiceDisconnected(Enums.SERVICE_TYPE name);
}
