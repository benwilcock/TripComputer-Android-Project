package com.soagrowers.android.tripcomputer.data;

import android.content.Context;
import android.test.InstrumentationTestCase;

import com.soagrowers.android.tripcomputer.data.Constants;
import com.soagrowers.android.tripcomputer.data.Journey;
import com.soagrowers.android.tripcomputer.data.JourneyConverter;
import com.soagrowers.android.tripcomputer.data.MapBasedJourney;

import java.util.Date;
import java.util.UUID;

import static com.soagrowers.android.tripcomputer.data.MapBasedJourneyKeys.CURRENT_PLACE_TXT;
import static com.soagrowers.android.tripcomputer.data.MapBasedJourneyKeys.FIRST_DATE_DATE;
import static com.soagrowers.android.tripcomputer.data.MapBasedJourneyKeys.FIRST_PLACE_TXT;
import static com.soagrowers.android.tripcomputer.data.MapBasedJourneyKeys.ID;
import static com.soagrowers.android.tripcomputer.data.MapBasedJourneyKeys.IS_FINISHED_BOOL;
import static com.soagrowers.android.tripcomputer.data.MapBasedJourneyKeys.IS_STARTED_BOOL;
import static com.soagrowers.android.tripcomputer.data.MapBasedJourneyKeys.LAST_DATE_DATE;
import static com.soagrowers.android.tripcomputer.data.MapBasedJourneyKeys.LAST_PLACE_TXT;

/**
 * Created by Ben on 22/08/2014.
 */
public class TestJourneyConverter extends InstrumentationTestCase {

    private Context mContext;
    private Journey journey;
    private String place = "A PLACE";

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mContext = getInstrumentation().getTargetContext();

        journey = new Journey(); // Sets UUID
        journey.start(); //sets isStarted, StartedDate and ChronoBase
        journey.stop(); //sets isFinished, FinishedDate and ChronoStop

        journey.setAccuracyThreshold(Constants.ZERO_FLOAT);
        journey.setChargeValue(Constants.ZERO_FLOAT);
        journey.setDistanceUnits(Constants.ZERO_INT);

        journey.setFirstPlace(place);
        journey.setCurrentPlace(place);
        journey.setLastPlace(place);

    }

    public void testBuildMapBasedJourneyFromJourney() {

        MapBasedJourney map = JourneyConverter.buildMapBasedJourneyFromJourney(journey, mContext);

        assertEquals(journey.getId(), (UUID) map.get(ID).get());
        assertEquals((Boolean) journey.isStarted(), (Boolean) map.get(IS_STARTED_BOOL).get());
        assertEquals((Boolean) journey.isFinished(), (Boolean) map.get(IS_FINISHED_BOOL).get());
        assertEquals(journey.getFirstPlace(), (String) map.get(FIRST_PLACE_TXT).get());
        assertEquals(journey.getLastPlace(), (String) map.get(LAST_PLACE_TXT).get());
        assertEquals(journey.getCurrentPlace(), (String) map.get(CURRENT_PLACE_TXT).get());
        assertEquals(journey.getStartedDate(), (Date) map.get(FIRST_DATE_DATE).get());
        assertEquals(journey.getStoppedDate(), (Date) map.get(LAST_DATE_DATE).get());

        /*
        map.get( CURRENT_PLACE_TXT, journey.getCurrentPlace());
        map.get( FIRST_PLACE_TXT, journey.getFirstPlace());
        map.get( LAST_PLACE_TXT, journey.getLastPlace());
        map.get( TOTAL_DISTANCE_FLT, journey.getTotalDistance());
        map.get( FIRST_DATE_DATE, journey.getStartedDate());
        map.get( LAST_DATE_DATE, journey.getStoppedDate());
        map.get( FIRST_CHRONO_LONG, journey.getChronometerBase());
        map.get( LAST_CHRONO_LONG, journey.getChronometerStop());
        map.get( TOTAL_TIME_LONG, journey.getTotalTime());
        map.get( CURRENT_LATITUDE_DBL, journey.getCurrentLatitude());
        map.get( CURRENT_LONGITUDE_DBL, journey.getCurrentLongitude());
        map.get( TOTAL_LOCATIONS_IGNORED_INT, journey.getUnusedLocationCount());
        map.get( TOTAL_LOCATIONS_USED_INT, journey.getUsedLocationCount());
        map.get( TOTAL_LOCATIONS_INT, journey.getTotalLocationCount());
        map.get( SETTING_ACCURACY_THRESHOLD_FLT, journey.getAccuracyThreshold());
        map.get( CURRENT_LOCATION_ACCURACY_FLT, journey.getCurrentAccuracy());
        map.get( TOTAL_LOCATION_ACCURACY_FLT, journey.getTotalAccuracy());
        map.get( SETTING_DISTANCE_UNITS_INT, journey.getDistanceUnits());
        map.get( SETTING_CHARGE_VALUE_FLT, journey.getChargeValue());
        map.get( CURRENT_HEADING_FLT, journey.getCurrentHeading());
        map.get( CURRENT_ALTITUDE_DBL, journey.getCurrentAltitude());
        map.get( CURRENT_ACTIVITY_TYPE_INT, journey.getCurrentActivityType());
        map.get( CURRENT_ACTIVITY_CONFIDENCE_INT, journey.getCurrentActivityConfidence());
        */
    }

}
