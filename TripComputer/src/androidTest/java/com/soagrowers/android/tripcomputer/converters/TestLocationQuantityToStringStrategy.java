package com.soagrowers.android.tripcomputer.converters;

import android.content.Context;
import android.content.res.Resources;
import android.test.InstrumentationTestCase;

import com.google.common.base.Optional;
import com.soagrowers.android.tripcomputer.data.MapBasedJourney;

import static com.soagrowers.android.tripcomputer.data.MapBasedJourneyKeys.CURRENT_LOCATION_ACCURACY_FLT;
import static com.soagrowers.android.tripcomputer.data.MapBasedJourneyKeys.TOTAL_LOCATIONS_INT;
import static com.soagrowers.android.tripcomputer.data.MapBasedJourneyKeys.TOTAL_LOCATIONS_USED_INT;
import static com.soagrowers.android.tripcomputer.data.MapBasedJourneyKeys.TOTAL_LOCATION_ACCURACY_FLT;

/**
 * Created by Ben on 08/09/2014.
 */
public class TestLocationQuantityToStringStrategy extends InstrumentationTestCase {

  private Context mContext;
  private Resources mRes;
  private LocationQuantityToStringStrategy converter;
  private MapBasedJourney testJourney;

  @Override
  public void setUp() throws Exception {
    super.setUp();
    mContext = getInstrumentation().getTargetContext();
    mRes = getInstrumentation().getTargetContext().getResources();
    converter = new LocationQuantityToStringStrategy(mContext);
    testJourney = new MapBasedJourney();
  }

  @Override
  public void tearDown() throws Exception {
    super.tearDown();
  }

  public void testLocationQuantityWhenEmpty() throws Exception {
    testJourney.start();
    assertFalse(converter.toString(testJourney).isPresent());
    testJourney.put(TOTAL_LOCATIONS_INT, 0);
    assertTrue(converter.toString(testJourney).isPresent());
    testJourney.put(TOTAL_LOCATIONS_USED_INT, 0);
    assertTrue(converter.toString(testJourney).isPresent());
  }

  public void testLocationQuantityWhenNotEmpty() throws Exception {

    testJourney.start();
    testJourney.put(TOTAL_LOCATIONS_INT, 0);
    assertTrue(converter.toString(testJourney).isPresent());
    assertEquals("0", converter.toString(testJourney).get());

    testJourney.put(TOTAL_LOCATIONS_INT, 100);
    assertEquals("100", converter.toString(testJourney).get());

    testJourney.put(TOTAL_LOCATIONS_USED_INT, 10);
    assertEquals("100 (10)", converter.toString(testJourney).get());

  }
}
