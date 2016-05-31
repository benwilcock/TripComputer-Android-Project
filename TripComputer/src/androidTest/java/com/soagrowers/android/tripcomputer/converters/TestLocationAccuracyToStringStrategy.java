package com.soagrowers.android.tripcomputer.converters;

import android.content.Context;
import android.content.res.Resources;
import android.test.InstrumentationTestCase;

import com.soagrowers.android.tripcomputer.data.MapBasedJourney;

import static com.soagrowers.android.tripcomputer.data.MapBasedJourneyKeys.CURRENT_HEADING_FLT;
import static com.soagrowers.android.tripcomputer.data.MapBasedJourneyKeys.CURRENT_LOCATION_ACCURACY_FLT;
import static com.soagrowers.android.tripcomputer.data.MapBasedJourneyKeys.TOTAL_LOCATIONS_INT;
import static com.soagrowers.android.tripcomputer.data.MapBasedJourneyKeys.TOTAL_LOCATION_ACCURACY_FLT;

/**
 * Created by Ben on 08/09/2014.
 */
public class TestLocationAccuracyToStringStrategy extends InstrumentationTestCase {

  private Context mContext;
  private Resources mRes;
  private LocationAccuracyToStringStrategy converter;
  private MapBasedJourney testJourney;

  @Override
  public void setUp() throws Exception {
    super.setUp();
    mContext = getInstrumentation().getTargetContext();
    mRes = getInstrumentation().getTargetContext().getResources();
    converter = new LocationAccuracyToStringStrategy(mContext);
    testJourney = new MapBasedJourney();
  }

  @Override
  public void tearDown() throws Exception {
    super.tearDown();
  }

  public void testAccuracyEmpty() throws Exception {
    testJourney.start();
    assertFalse(converter.toString(testJourney).isPresent());
    testJourney.put(TOTAL_LOCATION_ACCURACY_FLT, 0.0f);
    assertFalse(converter.toString(testJourney).isPresent());
    testJourney.put(TOTAL_LOCATIONS_INT, 0);
    assertFalse(converter.toString(testJourney).isPresent());
    testJourney.put(CURRENT_LOCATION_ACCURACY_FLT, 0.0f);
    assertFalse(converter.toString(testJourney).isPresent());
  }

  public void testHeadingIsNotEmpty() throws Exception {

    testJourney.start();
    testJourney.put(TOTAL_LOCATION_ACCURACY_FLT, 40.0f);
    testJourney.put(TOTAL_LOCATIONS_INT, 4);
    testJourney.put(CURRENT_LOCATION_ACCURACY_FLT, 80.0f);

    assertTrue(converter.toString(testJourney).isPresent());
    assertEquals("80.0m (10.0m)", converter.toString(testJourney).get());
  }
}
