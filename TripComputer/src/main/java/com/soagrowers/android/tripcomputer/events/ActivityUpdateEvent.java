package com.soagrowers.android.tripcomputer.events;

import com.google.android.gms.location.DetectedActivity;

/**
 * Created by Ben on 30/05/2014.
 */
public class ActivityUpdateEvent extends AbstractEvent {

    private final DetectedActivity activity;

    public ActivityUpdateEvent(DetectedActivity activity) {
        super();
        this.activity = activity;
    }

    public DetectedActivity getActivity() {
        return activity;
    }
}
