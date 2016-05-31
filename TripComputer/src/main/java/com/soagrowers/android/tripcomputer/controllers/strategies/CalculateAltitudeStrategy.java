package com.soagrowers.android.tripcomputer.controllers.strategies;

import android.location.Location;

import com.soagrowers.android.tripcomputer.data.Journey;
import com.soagrowers.android.utils.Assert;
import com.soagrowers.android.utils.Log;


/**
 * Created by Ben on 29/01/14.
 */
public class CalculateAltitudeStrategy implements JourneyUpdateHandlerStrategy {

    public static final String TAG = CalculateAltitudeStrategy.class.getCanonicalName();
    private static final Log log = Log.getInstance();

    public CalculateAltitudeStrategy() {
    }


    @Override
    public void execute(Journey journey, Location newLocation) {
        Assert.notNull(journey);
        Assert.notNull(newLocation);

        //set the Altitude of the Journey
        journey.setCurrentAltitude(newLocation.getAltitude());
    }

    @Override
    public void reset() {
        return;
    }
}
