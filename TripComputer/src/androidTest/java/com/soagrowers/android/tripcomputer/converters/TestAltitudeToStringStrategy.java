package com.soagrowers.android.tripcomputer.converters;

import android.content.Context;
import android.content.res.Resources;
import android.test.InstrumentationTestCase;

import com.soagrowers.android.tripcomputer.data.Constants;
import com.soagrowers.android.tripcomputer.data.MapBasedJourney;

import static com.soagrowers.android.tripcomputer.data.Constants.MILLISECONDS_PER_SECOND;
import static com.soagrowers.android.tripcomputer.data.Constants.SECONDS_PER_MINUTE;
import static com.soagrowers.android.tripcomputer.data.MapBasedJourneyKeys.CURRENT_ALTITUDE_DBL;
import static com.soagrowers.android.tripcomputer.data.MapBasedJourneyKeys.SETTING_DISTANCE_UNITS_INT;
import static com.soagrowers.android.tripcomputer.data.MapBasedJourneyKeys.TOTAL_DISTANCE_FLT;
import static com.soagrowers.android.tripcomputer.data.MapBasedJourneyKeys.TOTAL_TIME_LONG;

/**
 * Created by Ben on 08/09/2014.
 */
public class TestAltitudeToStringStrategy extends InstrumentationTestCase {

  private Context mContext;
  private Resources mRes;
  private AltitudeToStringStrategy converter;
  private MapBasedJourney testJourney;

  @Override
  public void setUp() throws Exception {
    super.setUp();
    mContext = getInstrumentation().getTargetContext();
    mRes = getInstrumentation().getTargetContext().getResources();
    converter = new AltitudeToStringStrategy(mContext);
    testJourney = new MapBasedJourney();
  }

  @Override
  public void tearDown() throws Exception {
    super.tearDown();
  }

  public void testAltitudeIsEmpty() throws Exception {

    testJourney.start();
    assertFalse(converter.toString(testJourney).isPresent());
  }

  public void testAltitudeIsNotEmpty() throws Exception {

    testJourney.start();
    testJourney.put(CURRENT_ALTITUDE_DBL, Double.valueOf(100.3d));
    assertTrue(converter.toString(testJourney).isPresent());
    assertEquals("100.3 meters", converter.toString(testJourney).get());
  }
}
