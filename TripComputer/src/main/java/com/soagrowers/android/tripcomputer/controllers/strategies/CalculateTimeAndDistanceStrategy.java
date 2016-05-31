package com.soagrowers.android.tripcomputer.controllers.strategies;

import android.location.Location;
import android.os.SystemClock;

import com.soagrowers.android.tripcomputer.R;
import com.soagrowers.android.tripcomputer.data.Constants;
import com.soagrowers.android.tripcomputer.data.Journey;
import com.soagrowers.android.utils.AndroidUtils;
import com.soagrowers.android.utils.Log;


/**
 * Created by Ben on 29/01/14.
 */
public class CalculateTimeAndDistanceStrategy implements JourneyUpdateHandlerStrategy {

    public static final String TAG = CalculateTimeAndDistanceStrategy.class.getCanonicalName();
    private static final Log log = Log.getInstance();
    public static final float MIN_DISTANCE_BETWEEN_LOCATIONS_IN_METERS = 30.0f;

    private Location oldLocation = null;
    private long oldLocationTime = Constants.ZERO_LONG;


    public CalculateTimeAndDistanceStrategy() {
    }

    @Override
    public void reset() {
        oldLocation = null;
        oldLocationTime = Constants.ZERO_LONG;
    }

    /**
     * @param journey
     * @param newLocation
     * @return boolean - True if the location can be considered 'Good'.
     */

    @Override
    public void execute(Journey journey, Location newLocation) {

        //Location ACCURACY is HIGH enough
        // Get the time NOW
        long newLocationTime = SystemClock.elapsedRealtime();

        //Get the "remember waypoints" config
        AndroidUtils h = AndroidUtils.getInstance();
        boolean rememberWaypoints = h.getBoolean(R.bool.feature_rememberWaypoints);

        //is this a FIRST-PASS Scenario?
        if (null == oldLocation) {

            oldLocationTime = newLocationTime;
            oldLocation = newLocation;

            // No GOOD location has been previously stored
            journey.addUsedLocation(newLocation, rememberWaypoints);

        } else {
            //establish the distance between locations
            float distanceInMetersBetweenLocations = oldLocation.distanceTo(newLocation);

            //should we class this as movement?
            if (distanceInMetersBetweenLocations > MIN_DISTANCE_BETWEEN_LOCATIONS_IN_METERS) {

                //add the distance travelled to the total
                log.v(TAG, "Distance between Locations (meters): " + distanceInMetersBetweenLocations);
                journey.setTotalDistance(journey.getTotalDistance() + distanceInMetersBetweenLocations);

                //calculate the time taken between these locations
                long timeInMillisBetweenLocations = newLocationTime - oldLocationTime;

                //add the time taken to the total
                log.v(TAG, "Time between Locations (millis): " + timeInMillisBetweenLocations);
                journey.setTotalTime(journey.getTotalTime() + timeInMillisBetweenLocations);

                //remember when we last calculated
                oldLocationTime = newLocationTime;

                //remember to store this location
                oldLocation = newLocation;

                log.v(TAG, "USING the Location. [ACCURACY & DISTANCE are OK]");
                journey.addUsedLocation(newLocation, rememberWaypoints);
            } else {

                //the Location is not far enough from the previous fix to be considered as moving...
                journey.addUnusedLocation(newLocation);
                log.v(TAG, "IGNORING the Location. [DISTANCE Failed].");
            }
        }

        log.v(TAG, "Total Distance (meters): " + journey.getTotalDistance());
        log.v(TAG, "Total Time (millis): " + journey.getTotalTime());
        return;
    }
}
