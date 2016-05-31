package com.soagrowers.android.tripcomputer.data;

import android.content.Context;

import java.util.Date;
import java.util.Map;

import static com.soagrowers.android.tripcomputer.data.MapBasedJourneyKeys.CURRENT_ACTIVITY_CONFIDENCE_INT;
import static com.soagrowers.android.tripcomputer.data.MapBasedJourneyKeys.CURRENT_ACTIVITY_TYPE_INT;
import static com.soagrowers.android.tripcomputer.data.MapBasedJourneyKeys.CURRENT_ALTITUDE_DBL;
import static com.soagrowers.android.tripcomputer.data.MapBasedJourneyKeys.CURRENT_HEADING_FLT;
import static com.soagrowers.android.tripcomputer.data.MapBasedJourneyKeys.CURRENT_LATITUDE_DBL;
import static com.soagrowers.android.tripcomputer.data.MapBasedJourneyKeys.CURRENT_LOCATION_ACCURACY_FLT;
import static com.soagrowers.android.tripcomputer.data.MapBasedJourneyKeys.CURRENT_LONGITUDE_DBL;
import static com.soagrowers.android.tripcomputer.data.MapBasedJourneyKeys.CURRENT_PLACE_TXT;
import static com.soagrowers.android.tripcomputer.data.MapBasedJourneyKeys.FIRST_CHRONO_LONG;
import static com.soagrowers.android.tripcomputer.data.MapBasedJourneyKeys.FIRST_DATE_DATE;
import static com.soagrowers.android.tripcomputer.data.MapBasedJourneyKeys.FIRST_PLACE_TXT;
import static com.soagrowers.android.tripcomputer.data.MapBasedJourneyKeys.ID;
import static com.soagrowers.android.tripcomputer.data.MapBasedJourneyKeys.IS_FINISHED_BOOL;
import static com.soagrowers.android.tripcomputer.data.MapBasedJourneyKeys.IS_STARTED_BOOL;
import static com.soagrowers.android.tripcomputer.data.MapBasedJourneyKeys.LAST_CHRONO_LONG;
import static com.soagrowers.android.tripcomputer.data.MapBasedJourneyKeys.LAST_DATE_DATE;
import static com.soagrowers.android.tripcomputer.data.MapBasedJourneyKeys.LAST_PLACE_TXT;
import static com.soagrowers.android.tripcomputer.data.MapBasedJourneyKeys.SETTING_ACCURACY_THRESHOLD_FLT;
import static com.soagrowers.android.tripcomputer.data.MapBasedJourneyKeys.SETTING_CHARGE_VALUE_FLT;
import static com.soagrowers.android.tripcomputer.data.MapBasedJourneyKeys.SETTING_DISTANCE_UNITS_INT;
import static com.soagrowers.android.tripcomputer.data.MapBasedJourneyKeys.TOTAL_DISTANCE_FLT;
import static com.soagrowers.android.tripcomputer.data.MapBasedJourneyKeys.TOTAL_LOCATIONS_IGNORED_INT;
import static com.soagrowers.android.tripcomputer.data.MapBasedJourneyKeys.TOTAL_LOCATIONS_INT;
import static com.soagrowers.android.tripcomputer.data.MapBasedJourneyKeys.TOTAL_LOCATIONS_USED_INT;
import static com.soagrowers.android.tripcomputer.data.MapBasedJourneyKeys.TOTAL_LOCATION_ACCURACY_FLT;
import static com.soagrowers.android.tripcomputer.data.MapBasedJourneyKeys.TOTAL_TIME_LONG;

/**
 * Created by Ben on 22/08/2014.
 */
public final class JourneyConverter {

    public static MapBasedJourney buildMapBasedJourneyFromJourney(Journey journey, Context context) {
        final MapBasedJourney mapJourney = MapBasedJourneyBuilder.build(context);
        final Map<String, Object> map = mapJourney.delegate();

        // Convert a Journey to a MapBasedJourney
        map.put(ID, journey.getId());
        map.put(IS_STARTED_BOOL, journey.isStarted());
        map.put(IS_FINISHED_BOOL, journey.isFinished());
        map.put(CURRENT_PLACE_TXT, String.valueOf(journey.getCurrentPlace()));
        map.put(FIRST_PLACE_TXT, String.valueOf(journey.getFirstPlace()));
        map.put(LAST_PLACE_TXT, String.valueOf(journey.getLastPlace()));
        map.put(TOTAL_DISTANCE_FLT, journey.getTotalDistance());
        map.put(FIRST_DATE_DATE, new Date(journey.getStartedDate().getTime()));
        map.put(LAST_DATE_DATE, new Date(journey.getStoppedDate().getTime()));
        map.put(FIRST_CHRONO_LONG, journey.getChronometerBase());
        map.put(LAST_CHRONO_LONG, journey.getChronometerStop());
        map.put(TOTAL_TIME_LONG, journey.getTotalTime());
        map.put(CURRENT_LATITUDE_DBL, journey.getCurrentLatitude());
        map.put(CURRENT_LONGITUDE_DBL, journey.getCurrentLongitude());
        map.put(TOTAL_LOCATIONS_IGNORED_INT, journey.getUnusedLocationCount());
        map.put(TOTAL_LOCATIONS_USED_INT, journey.getUsedLocationCount());
        map.put(TOTAL_LOCATIONS_INT, journey.getTotalLocationCount());
        map.put(SETTING_ACCURACY_THRESHOLD_FLT, journey.getAccuracyThreshold());
        map.put(CURRENT_LOCATION_ACCURACY_FLT, journey.getCurrentAccuracy());
        map.put(TOTAL_LOCATION_ACCURACY_FLT, journey.getTotalAccuracy());
        map.put(SETTING_DISTANCE_UNITS_INT, journey.getDistanceUnits());
        map.put(SETTING_CHARGE_VALUE_FLT, journey.getChargeValue());
        map.put(CURRENT_HEADING_FLT, journey.getCurrentHeading());
        map.put(CURRENT_ALTITUDE_DBL, journey.getCurrentAltitude());
        map.put(CURRENT_ACTIVITY_TYPE_INT, journey.getCurrentActivityType());
        map.put(CURRENT_ACTIVITY_CONFIDENCE_INT, journey.getCurrentActivityConfidence());

        return mapJourney;
    }
}
