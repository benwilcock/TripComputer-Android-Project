package com.soagrowers.android.tripcomputer.events;

/**
 * Created by Ben on 08/07/2014.
 */
public class LocalityUpdatedEvent extends AbstractEvent {

    private String locality;

    public LocalityUpdatedEvent(String locality) {
        super();
        this.locality = locality;
    }

    public String getLocality() {
        return locality;
    }
}
