package com.soagrowers.android.tripcomputer.converters;

import android.content.Context;
import android.content.res.Resources;
import android.test.AndroidTestCase;
import android.test.InstrumentationTestCase;
import android.test.MoreAsserts;

import com.google.common.base.Optional;
import com.soagrowers.android.tripcomputer.data.Constants;
import com.soagrowers.android.tripcomputer.data.MapBasedJourney;
import com.soagrowers.android.tripcomputer.data.MapBasedJourneyKeys;

import java.util.Calendar;
import java.util.Date;

import static com.soagrowers.android.tripcomputer.data.MapBasedJourneyKeys.SETTING_CHARGE_VALUE_FLT;
import static com.soagrowers.android.tripcomputer.data.MapBasedJourneyKeys.SETTING_DISTANCE_UNITS_INT;
import static com.soagrowers.android.tripcomputer.data.MapBasedJourneyKeys.TOTAL_DISTANCE_FLT;

/**
 * Created by Ben on 08/09/2014.
 */
public class TestBasicToStringStrategy extends InstrumentationTestCase {

  private Context mContext;
  private Resources mRes;
  private BasicToStringStrategy converter;
  private MapBasedJourney testJourney;
  private static final String TEST_KEY = "TEST_KEY";
  private static final Calendar TEST_DATE = Calendar.getInstance();

  @Override
  public void setUp() throws Exception {
    super.setUp();
    mContext = getInstrumentation().getTargetContext();
    mRes = getInstrumentation().getTargetContext().getResources();
    converter = new BasicToStringStrategy(mContext, TEST_KEY);
    testJourney = new MapBasedJourney();
  }

  @Override
  public void tearDown() throws Exception {
    super.tearDown();
  }

  public void testWhenEmpty() throws Exception {
    // Check that no settings gives an absent
    assertFalse(converter.toString(testJourney).isPresent());
    assertEquals(Optional.absent(), converter.toString(testJourney));
  }

  public void testWhenNotEmpty() throws Exception {

    // Start the Journey so that values can be updated
    testJourney.start();
    testJourney.put(TEST_KEY, TEST_DATE);
    assertTrue(converter.toString(testJourney).isPresent());
    assertEquals(TEST_DATE.toString(), converter.toString(testJourney).get());
    MoreAsserts.assertContainsRegex(String.valueOf(TEST_DATE.get(Calendar.DAY_OF_MONTH)), converter.toString(testJourney).get());
  }
}
