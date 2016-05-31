package com.soagrowers.android.tripcomputer.events;

import com.google.android.gms.location.DetectedActivity;

/**
 * Created by Ben on 30/05/2014.
 */
public class JourneyAutoStopEvent extends AbstractEvent {


    private DetectedActivity physicalActivity;

    public JourneyAutoStopEvent(DetectedActivity physicalActivity) {
        super();
        this.physicalActivity = physicalActivity;
    }

    public DetectedActivity getPhysicalActivity() {
        return physicalActivity;
    }
}
