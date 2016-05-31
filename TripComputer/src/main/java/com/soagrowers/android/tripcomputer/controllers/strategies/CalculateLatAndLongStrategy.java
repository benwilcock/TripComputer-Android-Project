package com.soagrowers.android.tripcomputer.controllers.strategies;

import android.location.Location;

import com.soagrowers.android.tripcomputer.data.Journey;
import com.soagrowers.android.utils.Log;


/**
 * Created by Ben on 29/01/14.
 */
public class CalculateLatAndLongStrategy implements JourneyUpdateHandlerStrategy {

    public static final String TAG = CalculateLatAndLongStrategy.class.getCanonicalName();
    private static final Log log = Log.getInstance();

    public CalculateLatAndLongStrategy() {
    }

    @Override
    public void reset() {
        return;
    }

    @Override
    public void execute(Journey journey, Location newLocation) {

        //set the lat and long of the Journey
        journey.setCurrentLatitude(newLocation.getLatitude());
        journey.setCurrentLongitude(newLocation.getLongitude());
    }
}
