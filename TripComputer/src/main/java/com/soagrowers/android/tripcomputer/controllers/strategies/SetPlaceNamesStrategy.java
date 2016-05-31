package com.soagrowers.android.tripcomputer.controllers.strategies;

import com.soagrowers.android.tripcomputer.data.Journey;
import com.soagrowers.android.utils.AndroidUtils;
import com.soagrowers.android.utils.Assert;
import com.soagrowers.android.utils.Log;

/**
 * Created by Ben on 08/07/2014.
 */
public class SetPlaceNamesStrategy {

    private static final String TAG = SetPlaceNamesStrategy.class.getSimpleName();
    private Log log = Log.getInstance();
    private Journey journey = null;

    public void execute(Journey journey, String place) {
        Assert.notNull(journey);
        Assert.notNull(place);
        this.journey = journey;

        if (this.journey.isRunning()) {
            this.setCurrentRecordedPlace(place);
            this.setFirstRecordedPlace(place);
            this.setLastRecordedPlace(place);
        }
    }


    private void setCurrentRecordedPlace(String place) {

        AndroidUtils utils = AndroidUtils.getInstance();

        //Check the String for validity...
        if (utils.isPlaceValid(place)) {
            log.d(TAG, "Setting current place to: " + place);
            this.journey.setCurrentPlace(new String(place));
        }
    }


    private void setLastRecordedPlace(String place) {

        AndroidUtils utils = AndroidUtils.getInstance();

        //Check the String for validity...
        if (utils.isPlaceValid(place)) {
            log.d(TAG, "Setting last recorded: " + place);
            this.journey.setLastPlace(new String(place));
        }
    }


    private void setFirstRecordedPlace(String place) {

        AndroidUtils utils = AndroidUtils.getInstance();

        //Check the String for validity...
        if (utils.isPlaceValid(place)) {
            log.d(TAG, "Setting first recorded: " + place);
            this.journey.setFirstPlace(new String(place));
        }
    }
}
