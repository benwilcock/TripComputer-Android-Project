package com.soagrowers.android.tripcomputer.data;

import android.content.Context;
import android.content.res.Resources;
import android.test.InstrumentationTestCase;
import android.text.format.DateFormat;

import com.google.android.gms.location.DetectedActivity;
import com.google.common.base.Optional;
import com.soagrowers.android.tripcomputer.R;
import com.soagrowers.android.utils.StringUtils;

import java.text.DecimalFormat;
import java.util.Date;

import static android.test.MoreAsserts.assertContainsRegex;
import static com.soagrowers.android.tripcomputer.data.MapBasedJourneyKeys.CURRENT_ALTITUDE_DBL;
import static com.soagrowers.android.tripcomputer.data.MapBasedJourneyKeys.CURRENT_HEADING_FLT;
import static com.soagrowers.android.tripcomputer.data.MapBasedJourneyKeys.CURRENT_LOCATION_ACCURACY_FLT;
import static com.soagrowers.android.tripcomputer.data.MapBasedJourneyKeys.FIRST_DATE_DATE;
import static com.soagrowers.android.tripcomputer.data.MapBasedJourneyKeys.LAST_DATE_DATE;
import static com.soagrowers.android.tripcomputer.data.MapBasedJourneyKeys.SETTING_CHARGE_VALUE_FLT;
import static com.soagrowers.android.tripcomputer.data.MapBasedJourneyKeys.SETTING_DISTANCE_UNITS_INT;
import static com.soagrowers.android.tripcomputer.data.MapBasedJourneyKeys.TOTAL_DISTANCE_FLT;
import static com.soagrowers.android.tripcomputer.data.MapBasedJourneyKeys.TOTAL_LOCATIONS_INT;
import static com.soagrowers.android.tripcomputer.data.MapBasedJourneyKeys.TOTAL_LOCATIONS_USED_INT;
import static com.soagrowers.android.tripcomputer.data.MapBasedJourneyKeys.TOTAL_LOCATION_ACCURACY_FLT;
import static com.soagrowers.android.tripcomputer.data.MapBasedJourneyKeys.TOTAL_TIME_LONG;


/**
 * Created by Ben on 15/08/2014.
 */
public class TestMapBasedJourneyUiWrapper extends InstrumentationTestCase {

  public static final String TAG = TestMapBasedJourneyUiWrapper.class.getSimpleName();

  MapBasedJourneyUiWrapper testWrapper;
  MapBasedJourney testJourney;
  Context mContext;
  Resources mRes;
  //Activity mActivity;

  public TestMapBasedJourneyUiWrapper() {
    super();
  }

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    mContext = getInstrumentation().getTargetContext();
    mRes = getInstrumentation().getTargetContext().getResources();
    //mActivity = (Activity)getInstrumentation().getTargetContext();
    testJourney = new MapBasedJourney();
    testWrapper = new MapBasedJourneyUiWrapper(testJourney, mContext);
  }

  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
  }

  public void testGetCurrentActivityTypeAsString() {
    assertNotNull(mContext);
    assertNotNull(mRes);
    assertFalse(testJourney.get(MapBasedJourneyKeys.CURRENT_ACTIVITY_TYPE_INT).isPresent());
    assertFalse(testJourney.get(MapBasedJourneyKeys.CURRENT_ACTIVITY_CONFIDENCE_INT).isPresent());
    assertEquals(Optional.<String>absent(), testWrapper.getCurrentActivityTypeAsString());

    testJourney.start();

    testJourney.put(MapBasedJourneyKeys.CURRENT_ACTIVITY_TYPE_INT, DetectedActivity.IN_VEHICLE);
    testJourney.put(MapBasedJourneyKeys.CURRENT_ACTIVITY_CONFIDENCE_INT, 99);
    assertTrue(testJourney.get(MapBasedJourneyKeys.CURRENT_ACTIVITY_TYPE_INT).isPresent());
    assertContainsRegex(mRes.getString(R.string.txt_driving), testWrapper.getCurrentActivityTypeAsString().get());

    testJourney.put(MapBasedJourneyKeys.CURRENT_ACTIVITY_TYPE_INT, DetectedActivity.ON_BICYCLE);
    assertTrue(testJourney.get(MapBasedJourneyKeys.CURRENT_ACTIVITY_TYPE_INT).isPresent());
    assertContainsRegex(mRes.getString(R.string.txt_cycling), testWrapper.getCurrentActivityTypeAsString().get());

    testJourney.put(MapBasedJourneyKeys.CURRENT_ACTIVITY_TYPE_INT, DetectedActivity.ON_FOOT);
    assertTrue(testJourney.get(MapBasedJourneyKeys.CURRENT_ACTIVITY_TYPE_INT).isPresent());
    assertContainsRegex(mRes.getString(R.string.txt_walking), testWrapper.getCurrentActivityTypeAsString().get());

    testJourney.put(MapBasedJourneyKeys.CURRENT_ACTIVITY_TYPE_INT, DetectedActivity.RUNNING);
    assertTrue(testJourney.get(MapBasedJourneyKeys.CURRENT_ACTIVITY_TYPE_INT).isPresent());
    assertContainsRegex(mRes.getString(R.string.txt_running), testWrapper.getCurrentActivityTypeAsString().get());

    testJourney.put(MapBasedJourneyKeys.CURRENT_ACTIVITY_TYPE_INT, DetectedActivity.STILL);
    assertTrue(testJourney.get(MapBasedJourneyKeys.CURRENT_ACTIVITY_TYPE_INT).isPresent());
    assertContainsRegex(mRes.getString(R.string.txt_still), testWrapper.getCurrentActivityTypeAsString().get());

    testJourney.put(MapBasedJourneyKeys.CURRENT_ACTIVITY_TYPE_INT, DetectedActivity.TILTING);
    assertTrue(testJourney.get(MapBasedJourneyKeys.CURRENT_ACTIVITY_TYPE_INT).isPresent());
    assertContainsRegex(mRes.getString(R.string.txt_tilting), testWrapper.getCurrentActivityTypeAsString().get());

    testJourney.put(MapBasedJourneyKeys.CURRENT_ACTIVITY_TYPE_INT, DetectedActivity.UNKNOWN);
    assertTrue(testJourney.get(MapBasedJourneyKeys.CURRENT_ACTIVITY_TYPE_INT).isPresent());
    assertContainsRegex(mRes.getString(R.string.txt_unknown), testWrapper.getCurrentActivityTypeAsString().get());

    // Now test confidence level gets added...
    testJourney.put(MapBasedJourneyKeys.CURRENT_ACTIVITY_TYPE_INT, DetectedActivity.UNKNOWN);
    assertTrue(testJourney.get(MapBasedJourneyKeys.CURRENT_ACTIVITY_TYPE_INT).isPresent());
    assertContainsRegex("[99%]", testWrapper.getCurrentActivityTypeAsString().get());

    testJourney.stop();
  }

  public void testGetDatesAsFormattedStrings() {

    // Get the correct format for the strings
    String format = mRes.getString(R.string.format_dateandtime);

    // Set up the Journey status
    testJourney.start();
    testJourney.stop();

    assertTrue(testJourney.get(FIRST_DATE_DATE).isPresent());
    assertTrue(testJourney.get(LAST_DATE_DATE).isPresent());

    Date first = (Date) testJourney.get(FIRST_DATE_DATE).get();
    String first_string = DateFormat.format(format, first).toString();
    assertEquals(first_string, testWrapper.getStartTimeAsString().get());

    Date last = (Date) testJourney.get(LAST_DATE_DATE).get();
    String last_string = DateFormat.format(format, last).toString();
    assertEquals(last_string, testWrapper.getStopTimeAsString().get());
  }

  public void testUnStartedJourney() {

    // Before Start, none of these things have been set...
    assertEquals(Optional.<String>absent(), testWrapper.getStartTimeAsString());
    assertEquals(Optional.<String>absent(), testWrapper.getStopTimeAsString());

    // Before Location recirding starts, none of these things have been set...
    assertEquals(Optional.<String>absent(), testWrapper.getCurrentLatitudeAsString());
    assertEquals(Optional.<String>absent(), testWrapper.getCurrentLongitudeAsString());
    assertEquals(Optional.<String>absent(), testWrapper.getCurrentActivityTypeAsString());
  }

  public void testGetTotalDistanceAsString() {

    // Start the Journey so that values can be updated
    testJourney.start();

    // Set distance to 1600 meters (<1 Mile)
    testJourney.put(TOTAL_DISTANCE_FLT, Float.valueOf(1600f));
    testJourney.putSetting(SETTING_DISTANCE_UNITS_INT, Constants.MILES);
    assertEquals("0 miles", testWrapper.getTotalDistanceAsString().get());

    // Set distance to 1610 meters (>1 Mile)
    testJourney.put(TOTAL_DISTANCE_FLT, Float.valueOf(1610f));
    testJourney.putSetting(SETTING_DISTANCE_UNITS_INT, Constants.MILES);
    assertEquals("1 miles", testWrapper.getTotalDistanceAsString().get());

    // Set units to 1 KM
    testJourney.put(TOTAL_DISTANCE_FLT, Float.valueOf(1000f));
    testJourney.putSetting(SETTING_DISTANCE_UNITS_INT, Constants.KILOMETERS);
    assertEquals("1 kilometers", testWrapper.getTotalDistanceAsString().get());

    // Set units to 1000 meters
    testJourney.put(TOTAL_DISTANCE_FLT, Float.valueOf(1000f));
    testJourney.putSetting(SETTING_DISTANCE_UNITS_INT, Constants.METERS);
    assertEquals("1000 meters", testWrapper.getTotalDistanceAsString().get());

    // Test ZERO meters
    testJourney.put(TOTAL_DISTANCE_FLT, Float.valueOf(0f));
    testJourney.putSetting(SETTING_DISTANCE_UNITS_INT, Constants.METERS);
    assertEquals(Optional.<String>absent(), testWrapper.getTotalDistanceAsString());
  }

  public void testGetAverageSpeedAsString() {

    // Start the Journey so that values can be updated
    testJourney.start();

    // Test ZERO distance
    testJourney.put(TOTAL_DISTANCE_FLT, Float.valueOf(0.0f));
    assertEquals(Optional.absent(), testWrapper.getAverageSpeedAsString());

    // Set time elapsed to 1hr
    testJourney.put(TOTAL_TIME_LONG, Long.valueOf(3600 * Constants.MILLISECONDS_PER_SECOND));

    // Set distance to 1600 meters (1 Mile)
    testJourney.put(TOTAL_DISTANCE_FLT, Float.valueOf(16100f));
    testJourney.putSetting(SETTING_DISTANCE_UNITS_INT, Constants.MILES);
    assertEquals("10.0 mph", testWrapper.getAverageSpeedAsString().get());

    // Set units to KM
    testJourney.put(TOTAL_DISTANCE_FLT, Float.valueOf(10000f));
    testJourney.putSetting(SETTING_DISTANCE_UNITS_INT, Constants.KILOMETERS);
    assertEquals("10.0 kph", testWrapper.getAverageSpeedAsString().get());

    // Set units to meters
    testJourney.put(TOTAL_DISTANCE_FLT, Float.valueOf(36000f));
    testJourney.putSetting(SETTING_DISTANCE_UNITS_INT, Constants.METERS);
    assertEquals("10.0 msec", testWrapper.getAverageSpeedAsString().get());

    // Test ZERO time
    testJourney.put(TOTAL_TIME_LONG, Constants.ZERO_LONG);
    assertEquals(Optional.absent(), testWrapper.getAverageSpeedAsString());
  }

  public void testGetCurrentCostAsString() {
    // Start the Journey so that values can be updated
    testJourney.start();

    // Test ZERO distance
    testJourney.put(TOTAL_DISTANCE_FLT, Float.valueOf(0.0f));
    assertEquals(Optional.absent(), testWrapper.getCostAsString());

    // Set distance to 16100 meters (10 Miles)
    testJourney.put(TOTAL_DISTANCE_FLT, Float.valueOf(16100f));
    testJourney.putSetting(SETTING_DISTANCE_UNITS_INT, Constants.MILES);
    testJourney.putSetting(SETTING_CHARGE_VALUE_FLT, Float.valueOf(1.00f));
    String currency = mRes.getString(R.string.currency);
    assertEquals(currency + "10.00", testWrapper.getCostAsString().get());
  }

  public void testGetLocationAccuracyAsString() {

    DecimalFormat accuracyFormat = new DecimalFormat(mRes.getString(R.string.format_location_accuracy));

    //Should be blank if no travel has occurred...
    assertEquals(Optional.<String>absent(), testWrapper.getLocationAccuracyAsString());

    // Start the journey so that values can be updated.
    testJourney.start();

    // Set some test accuracy numbers
    Integer locations = Integer.valueOf(10);
    Float accuracy = Float.valueOf(300.00f);
    Float current = Float.valueOf(20.0f);

    testJourney.put(TOTAL_LOCATIONS_INT, locations);
    testJourney.put(TOTAL_LOCATION_ACCURACY_FLT, accuracy);
    testJourney.put(CURRENT_LOCATION_ACCURACY_FLT, current);

    String average_string = accuracyFormat.format(accuracy / locations);
    String curr_string = accuracyFormat.format(current);
    String measure = mRes.getString(R.string.txt_m);

    // Now try with a current accuracy as well...
    String test = curr_string + measure + " (" + average_string + measure + ")";
    assertEquals(test, testWrapper.getLocationAccuracyAsString().get());
  }

  public void testGetLocationQuantityAsString() {
    DecimalFormat qtyFormat = new DecimalFormat(mRes.getString(R.string.format_location_qty));

    // Should be blank initially...
    assertEquals(Optional.<String>absent(), testWrapper.getLocationQuantityAsString());

    //Should be blank if no travel has occurred...
    Integer total_locations = Integer.valueOf(Constants.ZERO_INT);
    Integer total_locations_used = Integer.valueOf(Constants.ZERO_INT);
    //String expected = qtyFormat.format(total_locations_used) + " (" + qtyFormat.format(total_locations) + ")";
    assertEquals(Optional.<String>absent(), testWrapper.getLocationQuantityAsString());

    // Start the journey so that values can be updated.
    testJourney.start();

    testJourney.put(TOTAL_LOCATIONS_INT, Integer.valueOf(100));
    assertEquals("100", testWrapper.getLocationQuantityAsString().get());
    testJourney.put(TOTAL_LOCATIONS_USED_INT, Integer.valueOf(10));
    assertEquals("100 (10)", testWrapper.getLocationQuantityAsString().get());
  }

  public void testGetHeadingAsString() {
    DecimalFormat degreesFormat = new DecimalFormat(mRes.getString(R.string.format_degrees));

    // Should be empty initially...
    assertEquals(Optional.<String>absent(), testWrapper.getHeadingAsString());

    // Start the journey...
    testJourney.start();
    assertEquals(Optional.<String>absent(), testWrapper.getHeadingAsString());

    Float heading = Float.valueOf(90f);
    testJourney.put(CURRENT_HEADING_FLT, heading);

    String expected = "E (" + degreesFormat.format(heading) + StringUtils.DEGREES + ")";
    assertEquals(expected, testWrapper.getHeadingAsString().get());
  }

  public void testGetCurrentAltitudeAsString() {

    // Should be empty initially...
    assertEquals(Optional.<String>absent(), testWrapper.getCurrentAltitudeAsString());

    // Start the Journey
    testJourney.start();

    String suffix = StringUtils.SPACE + mRes.getString(R.string.txt_meters);

    // Set to ZERO
    testJourney.put(CURRENT_ALTITUDE_DBL, Double.valueOf(Constants.ZERO_DOUBLE));
    String expected = String.valueOf(Constants.ZERO_DOUBLE) + suffix;
    assertEquals(expected, testWrapper.getCurrentAltitudeAsString().get());

    // Set to 100m
    testJourney.put(CURRENT_ALTITUDE_DBL, Double.valueOf(100d));
    expected = String.valueOf(100d) + suffix;
    assertEquals(expected, testWrapper.getCurrentAltitudeAsString().get());
  }
}
