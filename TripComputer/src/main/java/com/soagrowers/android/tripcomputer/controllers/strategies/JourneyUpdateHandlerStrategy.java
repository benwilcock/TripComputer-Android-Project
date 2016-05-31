package com.soagrowers.android.tripcomputer.controllers.strategies;

import android.location.Location;

import com.soagrowers.android.tripcomputer.data.Journey;

/**
 * Created by Ben on 29/01/14.
 */
public interface JourneyUpdateHandlerStrategy {

    public void execute(Journey journey, Location newLocation);

    public void reset();
}
