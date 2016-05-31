package com.soagrowers.android.tripcomputer.events;

import android.location.Location;

/**
 * Created by Ben on 30/05/2014.
 */
public class LocationUpdatedEvent extends AbstractEvent {

    private final Location location;

    public LocationUpdatedEvent(Location theLocation) {
        super();
        location = theLocation;
    }

    public Location getLocation() {
        return location;
    }
}
