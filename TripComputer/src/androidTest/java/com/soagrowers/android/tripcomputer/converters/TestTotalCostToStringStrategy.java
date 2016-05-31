package com.soagrowers.android.tripcomputer.converters;

import android.content.Context;
import android.content.res.Resources;
import android.test.InstrumentationTestCase;

import com.google.common.base.Optional;
import com.soagrowers.android.tripcomputer.data.Constants;
import com.soagrowers.android.tripcomputer.data.MapBasedJourney;
import com.soagrowers.android.tripcomputer.data.MapBasedJourneyKeys;

import static com.soagrowers.android.tripcomputer.data.MapBasedJourneyKeys.SETTING_CHARGE_VALUE_FLT;
import static com.soagrowers.android.tripcomputer.data.MapBasedJourneyKeys.SETTING_DISTANCE_UNITS_INT;
import static com.soagrowers.android.tripcomputer.data.MapBasedJourneyKeys.TOTAL_DISTANCE_FLT;

/**
 * Created by Ben on 08/09/2014.
 */
public class TestTotalCostToStringStrategy extends InstrumentationTestCase {

  private Context mContext;
  private Resources mRes;
  private CostToStringStrategy converter;
  private MapBasedJourney testJourney;

  @Override
  public void setUp() throws Exception {
    super.setUp();
    mContext = getInstrumentation().getTargetContext();
    mRes = getInstrumentation().getTargetContext().getResources();
    converter = new CostToStringStrategy(mContext);
    testJourney = new MapBasedJourney();
  }

  @Override
  public void tearDown() throws Exception {
    super.tearDown();
  }

  public void testCostIsEmpty() throws Exception {

    // Check that no settings gives an absent
    assertTrue(!converter.toString(testJourney).isPresent());
    assertEquals(Optional.absent(), converter.toString(testJourney));

    // Start the Journey so that values can be updated
    testJourney.start();

    testJourney.put(MapBasedJourneyKeys.TOTAL_DISTANCE_FLT, Constants.ZERO_FLOAT);
    assertTrue(!converter.toString(testJourney).isPresent());
    assertEquals(Optional.absent(), converter.toString(testJourney));

    testJourney.put(MapBasedJourneyKeys.SETTING_CHARGE_VALUE_FLT, Constants.ZERO_FLOAT);
    assertTrue(!converter.toString(testJourney).isPresent());
    assertEquals(Optional.absent(), converter.toString(testJourney));
  }

  public void testCostIsNotEmpty() throws Exception {

    // Start the Journey so that values can be updated
    testJourney.start();
    testJourney.put(MapBasedJourneyKeys.SETTING_DISTANCE_UNITS_INT, Constants.MILES);
    assertTrue(!converter.toString(testJourney).isPresent());
    assertEquals(Optional.absent(), converter.toString(testJourney));

    // Set distance to 1600 meters (<1 Mile)
    testJourney.put(TOTAL_DISTANCE_FLT, Float.valueOf(1600f));
    testJourney.put(SETTING_CHARGE_VALUE_FLT, Float.valueOf(0.00f));
    assertEquals(Optional.absent(), converter.toString(testJourney));

    // Set distance to 1600 meters (<1 Mile)
    testJourney.put(TOTAL_DISTANCE_FLT, Float.valueOf(1600f));
    testJourney.put(SETTING_CHARGE_VALUE_FLT, Float.valueOf(0.45f));
    assertEquals("£0.00", converter.toString(testJourney).get());

    // Set distance to 1610 meters (>1 Mile)
    testJourney.put(TOTAL_DISTANCE_FLT, Float.valueOf(1610f));
    testJourney.put(SETTING_DISTANCE_UNITS_INT, Constants.MILES);
    assertEquals("£0.45", converter.toString(testJourney).get());

    // Set units to 1 KM
    testJourney.put(TOTAL_DISTANCE_FLT, Float.valueOf(1000f));
    testJourney.put(SETTING_DISTANCE_UNITS_INT, Constants.KILOMETERS);
    assertEquals("£0.45", converter.toString(testJourney).get());

    // Set units to 1000 meters
    testJourney.put(TOTAL_DISTANCE_FLT, Float.valueOf(1000f));
    testJourney.put(SETTING_DISTANCE_UNITS_INT, Constants.METERS);
    assertEquals("£450.00", converter.toString(testJourney).get());
  }
}
