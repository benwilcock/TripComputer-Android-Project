package com.soagrowers.android.tripcomputer.utils;

import com.google.android.gms.location.DetectedActivity;
import com.soagrowers.android.tripcomputer.data.Constants;
import com.soagrowers.android.utils.MovementActivityUtil;
import static com.soagrowers.android.utils.MovementActivityUtil.Triggers;

import junit.framework.TestCase;

/**
 * Created by Ben on 29/09/2014.
 */
public class TestMovementActivityUtil extends TestCase{

  DetectedActivity activity;
  public static int HIGH_START_CONFIDENCE;
  public static int LOW_START_CONFIDENCE;
  public static int HIGH_STOP_CONFIDENCE;
  public static int LOW_STOP_CONFIDENCE;

  public TestMovementActivityUtil() {
    HIGH_START_CONFIDENCE = Constants.ACTIVITY_START_CONFIDENCE_THRESHOLD + 3;
    LOW_START_CONFIDENCE = Constants.ACTIVITY_START_CONFIDENCE_THRESHOLD - 3;
    HIGH_STOP_CONFIDENCE = Constants.ACTIVITY_STOP_CONFIDENCE_THRESHOLD + 3;
    LOW_STOP_CONFIDENCE = Constants.ACTIVITY_STOP_CONFIDENCE_THRESHOLD - 3;
  }

  @Override
  public void setUp() throws Exception {
    super.setUp();
  }

  @Override
  public void tearDown() throws Exception {
    super.tearDown();
  }

  public void testInVehicle() throws Exception {

    activity = new DetectedActivity(DetectedActivity.IN_VEHICLE, HIGH_START_CONFIDENCE);
    assertEquals(Triggers.START_TRIGGER, MovementActivityUtil.isEventTrigger(activity));

    activity = new DetectedActivity(DetectedActivity.IN_VEHICLE, LOW_START_CONFIDENCE);
    assertEquals(Triggers.NO_TRIGGER, MovementActivityUtil.isEventTrigger(activity));
  }

  public void testOnBike() throws Exception {

    activity = new DetectedActivity(DetectedActivity.ON_BICYCLE, HIGH_START_CONFIDENCE);
    assertEquals(Triggers.START_TRIGGER, MovementActivityUtil.isEventTrigger(activity));

    activity = new DetectedActivity(DetectedActivity.ON_BICYCLE, LOW_START_CONFIDENCE);
    assertEquals(Triggers.NO_TRIGGER, MovementActivityUtil.isEventTrigger(activity));
  }

  public void testRunning() throws Exception {

    activity = new DetectedActivity(DetectedActivity.RUNNING, HIGH_START_CONFIDENCE);
    assertEquals(Triggers.START_TRIGGER, MovementActivityUtil.isEventTrigger(activity));

    activity = new DetectedActivity(DetectedActivity.RUNNING, LOW_START_CONFIDENCE);
    assertEquals(Triggers.NO_TRIGGER, MovementActivityUtil.isEventTrigger(activity));
  }

  public void testStill() throws Exception {

    activity = new DetectedActivity(DetectedActivity.STILL, HIGH_STOP_CONFIDENCE);
    assertEquals(Triggers.STOP_TRIGGER, MovementActivityUtil.isEventTrigger(activity));

    activity = new DetectedActivity(DetectedActivity.STILL, LOW_STOP_CONFIDENCE);
    assertEquals(Triggers.NO_TRIGGER, MovementActivityUtil.isEventTrigger(activity));
  }

  public void testOnFoot() throws Exception {

    activity = new DetectedActivity(DetectedActivity.ON_FOOT, HIGH_START_CONFIDENCE);
    assertEquals(Triggers.NO_TRIGGER, MovementActivityUtil.isEventTrigger(activity));

    activity = new DetectedActivity(DetectedActivity.ON_FOOT, LOW_START_CONFIDENCE);
    assertEquals(Triggers.NO_TRIGGER, MovementActivityUtil.isEventTrigger(activity));
  }

  public void testWalking() throws Exception {

    activity = new DetectedActivity(DetectedActivity.WALKING, HIGH_START_CONFIDENCE);
    assertEquals(Triggers.NO_TRIGGER, MovementActivityUtil.isEventTrigger(activity));

    activity = new DetectedActivity(DetectedActivity.WALKING, LOW_START_CONFIDENCE);
    assertEquals(Triggers.NO_TRIGGER, MovementActivityUtil.isEventTrigger(activity));
  }

  public void testTilting() throws Exception {

    activity = new DetectedActivity(DetectedActivity.TILTING, HIGH_START_CONFIDENCE);
    assertEquals(Triggers.NO_TRIGGER, MovementActivityUtil.isEventTrigger(activity));

    activity = new DetectedActivity(DetectedActivity.TILTING, LOW_START_CONFIDENCE);
    assertEquals(Triggers.NO_TRIGGER, MovementActivityUtil.isEventTrigger(activity));
  }

  public void testUnknown() throws Exception {

    activity = new DetectedActivity(DetectedActivity.UNKNOWN, HIGH_START_CONFIDENCE);
    assertEquals(Triggers.NO_TRIGGER, MovementActivityUtil.isEventTrigger(activity));

    activity = new DetectedActivity(DetectedActivity.UNKNOWN, LOW_START_CONFIDENCE);
    assertEquals(Triggers.NO_TRIGGER, MovementActivityUtil.isEventTrigger(activity));
  }

}
