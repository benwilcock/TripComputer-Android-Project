package com.soagrowers.android.tripcomputer.converters;

import android.content.Context;
import android.content.res.Resources;
import android.test.InstrumentationTestCase;

import com.google.common.base.Optional;
import com.soagrowers.android.tripcomputer.data.Constants;
import com.soagrowers.android.tripcomputer.data.MapBasedJourney;
import com.soagrowers.android.tripcomputer.data.MapBasedJourneyKeys;

import static com.soagrowers.android.tripcomputer.data.MapBasedJourneyKeys.*;

/**
 * Created by Ben on 08/09/2014.
 */
public class TestTotalDistanceToStringStrategy extends InstrumentationTestCase {

    private Context mContext;
    private Resources mRes;
    private DistanceToStringStrategy converter;
    private MapBasedJourney testJourney;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        mContext = getInstrumentation().getTargetContext();
        mRes = getInstrumentation().getTargetContext().getResources();
        converter = new DistanceToStringStrategy(mContext, TOTAL_DISTANCE_FLT);
        testJourney = new MapBasedJourney();
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }

    public void testTotalDistanceIsEmpty() throws Exception {

        // Check that no setting gives an absent
        assertTrue(!converter.toString(testJourney).isPresent());
        assertEquals(Optional.absent(), converter.toString(testJourney));

        // Start the Journey so that values can be updated
        testJourney.start();
        testJourney.put(MapBasedJourneyKeys.TOTAL_DISTANCE_FLT, Constants.ZERO_FLOAT);
        testJourney.put(MapBasedJourneyKeys.SETTING_DISTANCE_UNITS_INT, Constants.MILES);

        // Check that zero setting gives an absent
        assertTrue(!converter.toString(testJourney).isPresent());
        assertEquals(Optional.absent(), converter.toString(testJourney));
    }

    public void testTotalDistanceIsNotEmpty() throws Exception {

        // Start the Journey so that values can be updated
        testJourney.start();

        // Set distance to 1600 meters (<1 Mile)
        testJourney.put(TOTAL_DISTANCE_FLT, Float.valueOf(1600f));
        testJourney.put(SETTING_DISTANCE_UNITS_INT, Constants.MILES);
        assertEquals("0 miles", converter.toString(testJourney).get());

        // Set distance to 1610 meters (>1 Mile)
        testJourney.put(TOTAL_DISTANCE_FLT, Float.valueOf(1610f));
        testJourney.put(SETTING_DISTANCE_UNITS_INT, Constants.MILES);
        assertEquals("1 miles", converter.toString(testJourney).get());

        // Set units to 1 KM
        testJourney.put(TOTAL_DISTANCE_FLT, Float.valueOf(1000f));
        testJourney.put(SETTING_DISTANCE_UNITS_INT, Constants.KILOMETERS);
        assertEquals("1 kilometers", converter.toString(testJourney).get());

        // Set units to 1000 meters
        testJourney.put(TOTAL_DISTANCE_FLT, Float.valueOf(1000f));
        testJourney.put(SETTING_DISTANCE_UNITS_INT, Constants.METERS);
        assertEquals("1000 meters", converter.toString(testJourney).get());

    }
}
