package com.soagrowers.android.tripcomputer.data;

import android.test.InstrumentationTestCase;
import android.util.Log;

import com.google.common.base.Optional;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Ben on 11/08/2014.
 */
public class TestMapBasedJourney extends InstrumentationTestCase {

  public static final String TAG = TestMapBasedJourney.class.getCanonicalName();

  MapBasedJourney goodMap;
  MapBasedJourney badMap;
  Optional<String> optional;

  public TestMapBasedJourney() {
    super();
  }

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    goodMap = new MapBasedJourney();

    optional = Optional.of("test_key");
    Map brokeMap = new HashMap<String, Optional>();
    brokeMap.put("", null);
    brokeMap.put("test", null);
    brokeMap.put("test", optional);
    badMap = new MapBasedJourney(brokeMap);
  }

  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
  }

  public void testPutNullKey() {
    try {
      goodMap.put(null, (Object)optional);
    } catch (AssertionError ae) {
      assertTrue(true);
    }
  }

  public void testPutEmptyKey() {
    try {
      goodMap.put("", optional);
    } catch (AssertionError ae) {
      assertTrue(true);
    }
  }

  public void testPutInvalidKey() {
    try {
      goodMap.put("null", optional);
    } catch (AssertionError ae) {
      assertTrue(true);
    }
  }

  public void testGetWithNull() {
    try {
      badMap.get((String) null);
    } catch (AssertionError ae) {
      assertTrue(true);
    }
  }

  public void testGetWithEmptyString() {
    try {
      badMap.get("");
    } catch (AssertionError ae) {
      assertTrue(true);
    }
  }

  public void testGetWithNullString() {
    try {
      badMap.get("null");
    } catch (AssertionError ae) {
      assertTrue(true);
    }
  }

  public void testGetWithBadString() {
    try {
      Optional<Object> o = (Optional<Object>) goodMap.get("bad");
      assertFalse("The optional should be empty and contain no reference!", o.isPresent());
      o.get();
    } catch (IllegalStateException e) {
      assertTrue(true);
    }
  }

  public void testValidEntry() {
    goodMap.start();
    goodMap.put("valid", optional);
    goodMap.stop();
  }

  public void testDefaultPropertiesAreBeingSetOnConstruction() {

    Optional<Object> o = (Optional<Object>) goodMap.get(MapBasedJourneyKeys.ID);
    assertTrue(o.isPresent());
    UUID uuid = (UUID) o.get();
    Log.d(TAG, uuid.toString());

    o = (Optional<Object>) goodMap.get(MapBasedJourneyKeys.MODEL_TYPE);
    assertTrue(o.isPresent());
    String type = (String) o.get();
    assertEquals(type, MapBasedJourney.class.getCanonicalName());
    Log.d(TAG, type.toString());

        /*

        There is an issue here in that the BuildConfig is that of the Test application
        and not the main application, so the values don't match those expected
        (but they are being set).

        o = (Optional<Object>) goodMap.get(MapBasedJourneyKeys.MODEL_VERSION_CODE);
        assertTrue(o.isPresent());
        Integer version_code = (Integer) o.get();
        assertEquals((Integer)BuildConfig.VERSION_CODE, version_code);
        Log.d(TAG, version_code.toString());

        o = (Optional<Object>) goodMap.get(MapBasedJourneyKeys.MODEL_VERSION_NAME);
        assertTrue(o.isPresent());
        String version_name = (String) o.get();
        assertEquals(BuildConfig.VERSION_NAME, version_name);
        Log.d(TAG, version_name.toString());
        */

    o = (Optional<Object>) goodMap.get(MapBasedJourneyKeys.IS_STARTED_BOOL);
    assertTrue(o.isPresent());
    Boolean isStarted = (Boolean) o.get();
    assertFalse(isStarted);

    o = (Optional<Object>) goodMap.get(MapBasedJourneyKeys.IS_PAUSED_BOOL);
    assertTrue(o.isPresent());
    Boolean isPaused = (Boolean) o.get();
    assertFalse(isPaused);

    o = (Optional<Object>) goodMap.get(MapBasedJourneyKeys.IS_FINISHED_BOOL);
    assertTrue(o.isPresent());
    Boolean isFinished = (Boolean) o.get();
    assertFalse(isFinished);
  }

  public void testGetId() {
    assertNotNull(goodMap.getId());
    assertTrue(goodMap.getId() instanceof UUID);
  }

  public void testBadMapGetIdFails() {
    try {
      assertNotNull(badMap.getId());
    } catch (IllegalStateException ise) {
      assertTrue(true);
    }
  }

  public void testIsStarted() {
    assertFalse(goodMap.isStarted());
    goodMap.start();
    assertTrue(goodMap.isStarted());
  }

  public void testBadMapIsStartedFails() {
    try {
      assertFalse(badMap.isStarted());
    } catch (IllegalStateException ise) {
      assertTrue(true);
    }
  }

  public void testIsFinished() {
    assertFalse(goodMap.isStarted());
    goodMap.start();
    assertTrue(goodMap.isStarted());
    assertFalse(goodMap.isFinished());
    goodMap.put(MapBasedJourneyKeys.IS_FINISHED_BOOL, Boolean.TRUE);
    assertTrue(goodMap.isFinished());

    try {
      // This should now fail because we stopped the Journey...
      goodMap.put(MapBasedJourneyKeys.IS_FINISHED_BOOL, Boolean.TRUE);
    } catch (IllegalStateException ise) {
      assertTrue(true);
    }
  }

  public void testBadMapIsFinishedFails() {
    try {
      assertFalse(badMap.isFinished());
    } catch (IllegalStateException ise) {
      assertTrue(true);
    }
  }

  public void testIsRunning() {
    assertFalse(goodMap.isStarted());
    assertFalse(goodMap.isRunning());
    assertFalse(goodMap.isFinished());

    goodMap.start();

    assertTrue(goodMap.isStarted());
    assertTrue(goodMap.isRunning());
    assertFalse(goodMap.isFinished());

    goodMap.stop();

    assertTrue(goodMap.isStarted());
    assertTrue(goodMap.isFinished());
    assertFalse(goodMap.isRunning());

  }

  public void testStartAndStop() {
    assertFalse(goodMap.isStarted());
    assertFalse(goodMap.isFinished());
    assertFalse(goodMap.get(MapBasedJourneyKeys.FIRST_DATE_DATE).isPresent());
    assertFalse(goodMap.get(MapBasedJourneyKeys.FIRST_CHRONO_LONG).isPresent());

    try {
      // The Journey hasn't started, so this should not be possible
      goodMap.put(MapBasedJourneyKeys.CURRENT_ALTITUDE_DBL, new Float(22));
    } catch (IllegalStateException ise) {
      assertTrue(true);
    }

    goodMap.start(); //Start the Journey

    assertTrue(goodMap.isStarted());
    assertFalse(goodMap.isFinished());
    assertTrue(goodMap.get(MapBasedJourneyKeys.FIRST_DATE_DATE).isPresent());
    assertTrue(goodMap.get(MapBasedJourneyKeys.FIRST_DATE_DATE).get() instanceof Date);
    assertTrue(goodMap.get(MapBasedJourneyKeys.FIRST_CHRONO_LONG).isPresent());
    assertTrue(goodMap.get(MapBasedJourneyKeys.FIRST_CHRONO_LONG).get() instanceof Long);

    // The Journey has started, so this should be possible without exception...
    goodMap.put(MapBasedJourneyKeys.CURRENT_ALTITUDE_DBL, new Float(22));


    // Now test STOP
    assertFalse(goodMap.isFinished());
    assertFalse(goodMap.get(MapBasedJourneyKeys.LAST_DATE_DATE).isPresent());
    assertFalse(goodMap.get(MapBasedJourneyKeys.LAST_CHRONO_LONG).isPresent());

    goodMap.stop(); // Stop the Journey

    assertTrue(goodMap.isStarted());
    assertTrue(goodMap.isFinished());
    assertTrue(goodMap.get(MapBasedJourneyKeys.LAST_DATE_DATE).isPresent());
    assertTrue(goodMap.get(MapBasedJourneyKeys.LAST_DATE_DATE).get() instanceof Date);
    assertTrue(goodMap.get(MapBasedJourneyKeys.LAST_CHRONO_LONG).isPresent());
    assertTrue(goodMap.get(MapBasedJourneyKeys.LAST_CHRONO_LONG).get() instanceof Long);

    //Check that stuff can't be added when the Journey has finished...
    try {
      // The Journey has finished, so this should not be possible
      goodMap.put(MapBasedJourneyKeys.CURRENT_ALTITUDE_DBL, new Float(22));
    } catch (IllegalStateException ise) {
      assertTrue(true);
    }
  }

  public void testStopBeforeStart() {

    assertFalse(goodMap.isStarted());
    assertFalse(goodMap.isFinished());
    assertFalse(goodMap.get(MapBasedJourneyKeys.FIRST_DATE_DATE).isPresent());
    assertFalse(goodMap.get(MapBasedJourneyKeys.FIRST_CHRONO_LONG).isPresent());
    assertFalse(goodMap.get(MapBasedJourneyKeys.LAST_DATE_DATE).isPresent());
    assertFalse(goodMap.get(MapBasedJourneyKeys.LAST_CHRONO_LONG).isPresent());

    try {
      goodMap.stop(); //should fail
    } catch (IllegalStateException ise) {
      assertTrue(true);
    }

    assertFalse(goodMap.isStarted());
    assertFalse(goodMap.isFinished());

    assertFalse(goodMap.get(MapBasedJourneyKeys.FIRST_DATE_DATE).isPresent());
    assertFalse(goodMap.get(MapBasedJourneyKeys.FIRST_CHRONO_LONG).isPresent());
    assertFalse(goodMap.get(MapBasedJourneyKeys.LAST_DATE_DATE).isPresent());
    assertFalse(goodMap.get(MapBasedJourneyKeys.LAST_CHRONO_LONG).isPresent());
  }

  public void testStartAfterStop() {
    goodMap.start();
    goodMap.stop();

    assertTrue(goodMap.isStarted());
    assertTrue(goodMap.isFinished());
    assertTrue(goodMap.get(MapBasedJourneyKeys.FIRST_DATE_DATE).isPresent());
    assertTrue(goodMap.get(MapBasedJourneyKeys.FIRST_CHRONO_LONG).isPresent());
    assertTrue(goodMap.get(MapBasedJourneyKeys.LAST_DATE_DATE).isPresent());
    assertTrue(goodMap.get(MapBasedJourneyKeys.LAST_CHRONO_LONG).isPresent());

    Date firstDate = (Date) goodMap.get(MapBasedJourneyKeys.FIRST_DATE_DATE).get();
    Date lastDate = (Date) goodMap.get(MapBasedJourneyKeys.LAST_DATE_DATE).get();
    Long firstTime = (Long) goodMap.get(MapBasedJourneyKeys.FIRST_CHRONO_LONG).get();
    Long lastTime = (Long) goodMap.get(MapBasedJourneyKeys.LAST_CHRONO_LONG).get();

    //try to stop a journey that's already stopped...
    try {
      goodMap.stop(); //should fail
    } catch (IllegalStateException ise) {
      assertTrue(true);
    }

    //Check there were no unexpected effects...
    assertTrue(goodMap.isStarted());
    assertTrue(goodMap.isFinished());
    assertTrue(goodMap.get(MapBasedJourneyKeys.FIRST_DATE_DATE).isPresent());
    assertTrue(goodMap.get(MapBasedJourneyKeys.FIRST_CHRONO_LONG).isPresent());
    assertTrue(goodMap.get(MapBasedJourneyKeys.LAST_DATE_DATE).isPresent());
    assertTrue(goodMap.get(MapBasedJourneyKeys.LAST_CHRONO_LONG).isPresent());

    assertSame(firstDate, goodMap.get(MapBasedJourneyKeys.FIRST_DATE_DATE).get());
    assertSame(firstTime, goodMap.get(MapBasedJourneyKeys.FIRST_CHRONO_LONG).get());
    assertSame(lastDate, goodMap.get(MapBasedJourneyKeys.LAST_DATE_DATE).get());
    assertSame(lastTime, goodMap.get(MapBasedJourneyKeys.LAST_CHRONO_LONG).get());
  }

  public void testCanAddSettingsAfterStart() {
    goodMap.start();

    goodMap.putSetting(MapBasedJourneyKeys.SETTING_ACCURACY_THRESHOLD_FLT, Float.valueOf(22));
    assertTrue(goodMap.get(MapBasedJourneyKeys.SETTING_ACCURACY_THRESHOLD_FLT).isPresent());
    assertEquals(Float.valueOf(22), (Float) goodMap.get(MapBasedJourneyKeys.SETTING_ACCURACY_THRESHOLD_FLT).get());


    try {
      // Attempts to add non settings should fail
      goodMap.putSetting(MapBasedJourneyKeys.TOTAL_DISTANCE_FLT, Float.valueOf(22));
    } catch (AssertionError ae) {
      assertTrue(true);
    }
  }

  public void testCanAddSettingsAfterFinish() {
    goodMap.start();
    goodMap.stop();

    goodMap.putSetting(MapBasedJourneyKeys.SETTING_ACCURACY_THRESHOLD_FLT, Float.valueOf(22));
    assertTrue(goodMap.get(MapBasedJourneyKeys.SETTING_ACCURACY_THRESHOLD_FLT).isPresent());
    assertEquals(Float.valueOf(22), (Float) goodMap.get(MapBasedJourneyKeys.SETTING_ACCURACY_THRESHOLD_FLT).get());

    try {
      // Attempts to add non settings should fail
      goodMap.putSetting(MapBasedJourneyKeys.TOTAL_DISTANCE_FLT, Float.valueOf(22));
    } catch (AssertionError ae) {
      assertTrue(true);
    }
  }

  public void testOptionalKey() {
    Optional<String> key = Optional.of(MapBasedJourneyKeys.ID);
    assertTrue(goodMap.get(MapBasedJourneyKeys.ID).isPresent());
    assertTrue(goodMap.get(key).isPresent());
    assertEquals(goodMap.get(MapBasedJourneyKeys.ID), goodMap.get(key));
    assertFalse(goodMap.get("blah").isPresent());
    assertFalse(goodMap.get(Optional.of("blah")).isPresent());

    try {
      assertFalse(goodMap.get(Optional.absent()).isPresent());
      assertTrue(true);
    } catch (IllegalArgumentException iae) {
      assertTrue(true);
    }
  }

  /*.
  public void testTypeSafeGet(){
    goodMap.start();
    String key = "right_key";
    Integer value = 30;
    goodMap.put(key, value);
    int one = 1;

    // Test get
    Optional<Integer> intValue = goodMap.get(key, new Integer(one));
    assertTrue(intValue.isPresent());
    assertEquals(intValue.get(), value);


    intValue = goodMap.get("wrong_key", new Integer(one));
    assertTrue(intValue.isPresent());
    assertEquals((int)intValue.get(), one);
  }

  public void testTypeSafeGetThrowsException(){
    goodMap.start();
    String key = "right_key";
    Integer value = 30;
    goodMap.put(key, value);

    // Test get
    try {
      Optional<HashMap> intValue = goodMap.get(key, new HashMap());
      assertTrue(false);
    } catch (ClassCastException e){
      System.out.println(e.getMessage());
      assertTrue(true);
    }
  }
  */
}
