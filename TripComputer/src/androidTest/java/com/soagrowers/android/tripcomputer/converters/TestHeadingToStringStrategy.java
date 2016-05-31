package com.soagrowers.android.tripcomputer.converters;

import android.content.Context;
import android.content.res.Resources;
import android.test.InstrumentationTestCase;

import com.soagrowers.android.tripcomputer.data.MapBasedJourney;

import static com.soagrowers.android.tripcomputer.data.MapBasedJourneyKeys.CURRENT_ALTITUDE_DBL;
import static com.soagrowers.android.tripcomputer.data.MapBasedJourneyKeys.CURRENT_HEADING_FLT;

/**
 * Created by Ben on 08/09/2014.
 */
public class TestHeadingToStringStrategy extends InstrumentationTestCase {

  private Context mContext;
  private Resources mRes;
  private HeadingToStringStrategy converter;
  private MapBasedJourney testJourney;

  @Override
  public void setUp() throws Exception {
    super.setUp();
    mContext = getInstrumentation().getTargetContext();
    mRes = getInstrumentation().getTargetContext().getResources();
    converter = new HeadingToStringStrategy(mContext);
    testJourney = new MapBasedJourney();
  }

  @Override
  public void tearDown() throws Exception {
    super.tearDown();
  }

  public void testHeadingIsEmpty() throws Exception {

    testJourney.start();
    assertFalse(converter.toString(testJourney).isPresent());
  }

  public void testHeadingIsNotEmpty() throws Exception {

    testJourney.start();
    testJourney.put(CURRENT_HEADING_FLT, Float.valueOf(100.3f));
    assertTrue(converter.toString(testJourney).isPresent());
    assertEquals("E (100°)", converter.toString(testJourney).get());
  }
}
