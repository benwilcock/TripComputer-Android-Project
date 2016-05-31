package com.soagrowers.android.tripcomputer.controllers.strategies;

import android.location.Location;

import com.soagrowers.android.tripcomputer.data.Journey;
import com.soagrowers.android.utils.Log;
import com.soagrowers.android.utils.StringUtils;

import java.text.DecimalFormat;


/**
 * Created by Ben on 29/01/14.
 */
public class CalculateHeadingStrategy implements JourneyUpdateHandlerStrategy {

    public static final String TAG = CalculateHeadingStrategy.class.getCanonicalName();
    private static final Log log = Log.getInstance();
    private static final float MIN_DISTANCE_IN_METERS = 25.0f;
    private Location oldLocation = null;


    public CalculateHeadingStrategy() {
    }

    @Override
    public void reset() {
        oldLocation = null;
    }

    /**
     * @param journey
     * @param newLocation
     * @return boolean - True if the location can be considered 'Good'.
     */

    @Override
    public void execute(Journey journey, Location newLocation) {

        //is this a FIRST-PASS Scenario?
        if (null == oldLocation) {

            // No GOOD location has been previously stored
            oldLocation = newLocation;

        } else {
            //establish the distance between locations
            float distanceInMetersBetweenLocations = oldLocation.distanceTo(newLocation);

            //should we class this as movement?
            if (distanceInMetersBetweenLocations > MIN_DISTANCE_IN_METERS) {

                //decide the heading
                float currentHeading = oldLocation.bearingTo(newLocation);
                if (currentHeading < 0) {
                    currentHeading = currentHeading + 360;
                }
                ;
                DecimalFormat headingFormat = new DecimalFormat("##0");
                journey.setCurrentHeading(currentHeading);

                StringBuilder sb = new StringBuilder();
                sb.append(headingFormat.format(journey.getCurrentHeading()));
                sb.append(StringUtils.DEGREES);
                sb.append(StringUtils.SPACE);
                sb.append(StringUtils.bracket(StringUtils.convertToCompassString(journey.getCurrentHeading())));

                log.v(TAG, "Current Heading is: " + sb.toString());

                //remember to store this location
                oldLocation = newLocation;
            }
        }
        return;
    }
}
