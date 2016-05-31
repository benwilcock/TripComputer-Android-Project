package com.soagrowers.android.tripcomputer.converters;

import android.content.Context;
import android.content.res.Resources;
import android.test.InstrumentationTestCase;

import com.soagrowers.android.tripcomputer.data.Constants;
import com.soagrowers.android.tripcomputer.data.MapBasedJourney;

import static com.soagrowers.android.tripcomputer.data.Constants.MILLISECONDS_PER_SECOND;
import static com.soagrowers.android.tripcomputer.data.Constants.SECONDS_PER_MINUTE;
import static com.soagrowers.android.tripcomputer.data.MapBasedJourneyKeys.SETTING_DISTANCE_UNITS_INT;
import static com.soagrowers.android.tripcomputer.data.MapBasedJourneyKeys.TOTAL_DISTANCE_FLT;
import static com.soagrowers.android.tripcomputer.data.MapBasedJourneyKeys.TOTAL_TIME_LONG;

/**
 * Created by Ben on 08/09/2014.
 */
public class TestAverageSpeedToStringStrategy extends InstrumentationTestCase {

  private Context mContext;
  private Resources mRes;
  private AverageSpeedToStringStrategy converter;
  private MapBasedJourney testJourney;

  @Override
  public void setUp() throws Exception {
    super.setUp();
    mContext = getInstrumentation().getTargetContext();
    mRes = getInstrumentation().getTargetContext().getResources();
    converter = new AverageSpeedToStringStrategy(mContext);
    testJourney = new MapBasedJourney();
  }

  @Override
  public void tearDown() throws Exception {
    super.tearDown();
  }

  public void testAverageSpeedIsEmpty() throws Exception {

    testJourney.start();
    testJourney.put(TOTAL_DISTANCE_FLT, Float.valueOf(1600f));
    testJourney.put(SETTING_DISTANCE_UNITS_INT, Constants.MILES);
    // Total Time is missing!!!

    assertFalse(converter.toString(testJourney).isPresent());
  }

  public void testAverageSpeedIsNotEmpty() throws Exception {

    testJourney.start();
    testJourney.put(TOTAL_DISTANCE_FLT, Float.valueOf(1610f));
    testJourney.put(SETTING_DISTANCE_UNITS_INT, Constants.MILES);
    testJourney.put(TOTAL_TIME_LONG, Long.valueOf(1 * SECONDS_PER_MINUTE * MILLISECONDS_PER_SECOND));

    assertTrue(converter.toString(testJourney).isPresent());
    assertEquals("60.0 mph", converter.toString(testJourney).get());
  }

  /*
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
    */
}
