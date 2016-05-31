package com.soagrowers.android.tripcomputer.services;

import android.content.Context;

import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.location.DetectedActivity;
import com.soagrowers.android.MockitoCompatibleInstrumentationTestCase;
import com.soagrowers.android.tripcomputer.data.Constants;
import com.soagrowers.android.tripcomputer.events.ActivityUpdateEvent;
import com.soagrowers.android.tripcomputer.events.AutoStartTrigger;
import com.soagrowers.android.tripcomputer.events.AutoStopTrigger;
import com.soagrowers.android.tripcomputer.events.JourneyAutoStartEvent;
import com.soagrowers.android.tripcomputer.events.JourneyAutoStopEvent;
import com.soagrowers.android.tripcomputer.events.JourneyStartedEvent;
import com.soagrowers.android.tripcomputer.events.JourneyStoppedEvent;
import com.soagrowers.android.utils.AndroidUtils;

import org.mockito.MockitoAnnotations;
import org.mockito.MockitoAnnotations.Mock;

import java.util.Map;

import de.greenrobot.event.EventBus;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Ben on 29/09/2014.
 */
public class TestActivityEventProducer extends MockitoCompatibleInstrumentationTestCase {

  ActivityEventProducer testProducer;
  DetectedActivity activity;
  int HIGH_START_CONFIDENCE;
  int LOW_START_CONFIDENCE;
  int HIGH_STOP_CONFIDENCE;
  int LOW_STOP_CONFIDENCE;

  @Mock
  EventBus mockEventBus;

  @Mock
  AndroidUtils mockAndroidUtils;

  @Mock
  JourneyStartedEvent mockJourneyStartedEvent;

  @Mock
  JourneyStoppedEvent mockJourneyStoppedEvent;

  @Mock
  Tracker mockTracker;

  public TestActivityEventProducer() {
    HIGH_START_CONFIDENCE = Constants.ACTIVITY_START_CONFIDENCE_THRESHOLD + 3;
    LOW_START_CONFIDENCE = Constants.ACTIVITY_START_CONFIDENCE_THRESHOLD - 3;
    HIGH_STOP_CONFIDENCE = Constants.ACTIVITY_STOP_CONFIDENCE_THRESHOLD + 3;
    LOW_STOP_CONFIDENCE = Constants.ACTIVITY_STOP_CONFIDENCE_THRESHOLD - 3;
  }

  @Override
  public void setUp() throws Exception {
    super.setUp();
    MockitoAnnotations.initMocks(this);

    when(mockAndroidUtils.isAutoStartStopEnabled()).thenReturn(true);

    testProducer = new ActivityEventProducer(getInstrumentation().getTargetContext());
    testProducer.setAndroidUtils(mockAndroidUtils);
    testProducer.setEventBus(mockEventBus);
    testProducer.setTracker(mockTracker);
    testProducer.setIsRunning(false);
    verify(mockEventBus, times(1)).registerSticky(testProducer);
  }

  @Override
  public void tearDown() throws Exception {
    testProducer = null;
    super.tearDown();
  }

  public void testBodgedSetupFails() throws Exception {

    // In this case, we have no setDetectedActivity() call, so this should fail
    try {
      testProducer.run();
      assertTrue(false);
    } catch (IllegalStateException e) {
      assertTrue(true);
    }

    verify(mockEventBus, never()).post(anyObject());
  }

  public void testSuccessfulSetup() {
    activity = new DetectedActivity(DetectedActivity.IN_VEHICLE, 0);
    testProducer.setDetectedActivity(activity);
    testProducer.run();
    verify(mockEventBus, never()).post(anyObject());
  }

  public void testRunWithHighConfidenceInVehicleWhenNotRunning() {

    activity = new DetectedActivity(DetectedActivity.IN_VEHICLE, HIGH_START_CONFIDENCE);
    testProducer.setDetectedActivity(activity);
    testProducer.setIsRunning(false);

    // Perform test
    testProducer.run();

    // Verify state
    verify(mockEventBus, never()).post(isA(ActivityUpdateEvent.class));
    verify(mockEventBus, times(1)).post(isA(AutoStartTrigger.class));
    verify(mockEventBus, never()).post(isA(JourneyAutoStartEvent.class));
    verify(mockTracker, never()).send(isA(Map.class));
    verify(mockEventBus, never()).post(isA(AutoStopTrigger.class));
  }

  public void testRunWithHighConfidenceInVehicleWhenRunning() {

    activity = new DetectedActivity(DetectedActivity.IN_VEHICLE, HIGH_START_CONFIDENCE);
    testProducer.setDetectedActivity(activity);
    testProducer.setIsRunning(true);

    // Perform test
    testProducer.run();

    // Verify state
    verify(mockEventBus, times(1)).post(isA(ActivityUpdateEvent.class));
    verify(mockEventBus, never()).post(isA(AutoStartTrigger.class));
    verify(mockEventBus, never()).post(isA(AutoStopTrigger.class));
    verify(mockTracker, never()).send(isA(Map.class));
  }

  public void testRunWithHighConfidenceStillWhenNotRunning() {

    activity = new DetectedActivity(DetectedActivity.STILL, HIGH_STOP_CONFIDENCE);
    testProducer.setDetectedActivity(activity);
    testProducer.setIsRunning(false);

    // Perform test
    testProducer.run();

    // Verify state
    verify(mockEventBus, never()).post(isA(ActivityUpdateEvent.class));
    verify(mockEventBus, never()).post(isA(AutoStartTrigger.class));
    verify(mockEventBus, never()).post(isA(AutoStopTrigger.class));
    verify(mockEventBus, never()).post(isA(JourneyAutoStartEvent.class));
    verify(mockEventBus, never()).post(isA(JourneyAutoStopEvent.class));
    verify(mockTracker, never()).send(isA(Map.class));
  }

  public void testRunWithLowConfidenceInVehicleWhenNotRunning() {

    activity = new DetectedActivity(DetectedActivity.IN_VEHICLE, LOW_START_CONFIDENCE);
    testProducer.setDetectedActivity(activity);
    testProducer.setIsRunning(false);

    // Perform test
    testProducer.run();

    // Verify state
    verify(mockEventBus, never()).post(isA(ActivityUpdateEvent.class));
    verify(mockEventBus, never()).post(AutoStartTrigger.class);
    verify(mockEventBus, never()).post(AutoStopTrigger.class);
    verify(mockEventBus, never()).post(isA(JourneyAutoStartEvent.class));
    verify(mockEventBus, never()).post(isA(JourneyAutoStopEvent.class));
    verify(mockTracker, never()).send(isA(Map.class));
  }

  public void testRunWithHighConfidenceStillWhenRunning() {
    activity = new DetectedActivity(DetectedActivity.STILL, HIGH_STOP_CONFIDENCE);
    testProducer.setDetectedActivity(activity);
    testProducer.setIsRunning(true);

    // Perform test
    testProducer.run();

    // Verify state
    verify(mockEventBus, times(1)).post(isA(ActivityUpdateEvent.class));
    verify(mockEventBus, never()).post(isA(AutoStartTrigger.class));
    verify(mockEventBus, times(1)).post(isA(AutoStopTrigger.class));
    verify(mockEventBus, never()).post(isA(JourneyAutoStartEvent.class));
    verify(mockEventBus, never()).post(isA(JourneyAutoStopEvent.class));
    verify(mockTracker, never()).send(isA(Map.class));
  }

  public void testRunWithAutoStartStopOffWhenNotRunning() {
    // Make sure AutoStartStop is OFF
    when(mockAndroidUtils.isAutoStartStopEnabled()).thenReturn(false);
    testProducer.setAndroidUtils(mockAndroidUtils);

    // Create a START activity of HIGH CONFIDENCE
    activity = new DetectedActivity(DetectedActivity.IN_VEHICLE, HIGH_START_CONFIDENCE);
    testProducer.setDetectedActivity(activity);
    testProducer.setIsRunning(false);

    // Perform the test
    testProducer.run();

    // Verify events are not produced
    verify(mockEventBus, never()).post(isA(ActivityUpdateEvent.class));
    verify(mockEventBus, never()).post(isA(AutoStartTrigger.class));
    verify(mockEventBus, never()).post(isA(AutoStopTrigger.class));
    verify(mockEventBus, never()).post(isA(JourneyAutoStartEvent.class));
    verify(mockEventBus, never()).post(isA(JourneyAutoStopEvent.class));
    verify(mockTracker, never()).send(isA(Map.class));
  }

  public void testRunWithAutoStartStopOffWhenRunning() {
    // Make sure AutoStartStop is OFF
    when(mockAndroidUtils.isAutoStartStopEnabled()).thenReturn(false);
    testProducer.setAndroidUtils(mockAndroidUtils);

    // Create a START activity of HIGH CONFIDENCE
    activity = new DetectedActivity(DetectedActivity.IN_VEHICLE, HIGH_START_CONFIDENCE);
    testProducer.setDetectedActivity(activity);
    testProducer.setIsRunning(true);

    // Perform the test
    testProducer.run();

    // Verify events are not produced
    verify(mockEventBus, times(1)).post(isA(ActivityUpdateEvent.class));
    verify(mockEventBus, never()).post(isA(AutoStartTrigger.class));
    verify(mockEventBus, never()).post(isA(AutoStopTrigger.class));
    verify(mockEventBus, never()).post(isA(JourneyAutoStartEvent.class));
    verify(mockEventBus, never()).post(isA(JourneyAutoStopEvent.class));
    verify(mockTracker, never()).send(isA(Map.class));
  }


  public void testJourneyStartedEvent() {
    assertFalse(testProducer.isRunning());
    assertEquals(Constants.ZERO_LONG, testProducer.getFirstStopEventTime());
    testProducer.onEvent(mockJourneyStartedEvent);
    assertTrue(testProducer.isRunning());
    assertEquals(Constants.ZERO_LONG, testProducer.getFirstStopEventTime());
    verify(mockEventBus, never()).post(anyObject());
    verify(mockTracker, never()).send(isA(Map.class));
  }

  public void testJourneyStoppedEvent() {
    testProducer.setIsRunning(true);
    assertTrue(testProducer.isRunning());
    assertEquals(Constants.ZERO_LONG, testProducer.getFirstStopEventTime());
    testProducer.onEvent(mockJourneyStoppedEvent);
    assertFalse(testProducer.isRunning());
    assertEquals(Constants.ZERO_LONG, testProducer.getFirstStopEventTime());
    verify(mockEventBus, never()).post(anyObject());
    verify(mockTracker, never()).send(isA(Map.class));
  }


  public void testAutoStartTriggerEventWhenNotRunning() {
    activity = new DetectedActivity(DetectedActivity.IN_VEHICLE, HIGH_START_CONFIDENCE);
    testProducer.setDetectedActivity(activity);
    testProducer.setIsRunning(false);

    AutoStartTrigger trigger = new AutoStartTrigger(ActivityEventProducer.ACTIVITY_EVENT_PRODUCER, activity);
    testProducer.onEvent(trigger);

    verify(mockEventBus, times(1)).post(isA(JourneyAutoStartEvent.class));
    verify(mockTracker, times(1)).send(isA(Map.class));
    verify(mockEventBus, never()).post(isA(ActivityUpdateEvent.class));
    verify(mockEventBus, never()).post(isA(JourneyAutoStopEvent.class));
    assertEquals(Constants.ZERO_LONG, testProducer.getFirstStopEventTime());
  }

  public void testAutoStartTriggerEventWhenRunning() {
    activity = new DetectedActivity(DetectedActivity.IN_VEHICLE, HIGH_START_CONFIDENCE);
    testProducer.setDetectedActivity(activity);

    // Because the Journey is running - no Trigger event should be produced.
    testProducer.setIsRunning(true);

    AutoStartTrigger trigger = new AutoStartTrigger(ActivityEventProducer.ACTIVITY_EVENT_PRODUCER, activity);
    testProducer.onEvent(trigger);

    verify(mockEventBus, never()).post(isA(ActivityUpdateEvent.class));
    verify(mockEventBus, never()).post(isA(JourneyAutoStartEvent.class));
    verify(mockEventBus, never()).post(isA(JourneyAutoStopEvent.class));
    verify(mockTracker, never()).send(isA(Map.class));
  }

  public void testAutoStopTriggerEventWhenRunningWithInsignificantDelay() {
    activity = new DetectedActivity(DetectedActivity.STILL, HIGH_STOP_CONFIDENCE);
    testProducer.setDetectedActivity(activity);
    testProducer.setIsRunning(true);
    assertEquals(Constants.ZERO_LONG, testProducer.getFirstStopEventTime());

    AutoStopTrigger trigger = new AutoStopTrigger(ActivityEventProducer.ACTIVITY_EVENT_PRODUCER, activity);
    long eventTime = trigger.getEventTime();
    testProducer.onEvent(trigger);

    //check the first event time is being recorded...
    assertEquals(eventTime, testProducer.getFirstStopEventTime());

    // verify no events are getting raised yet...
    verify(mockEventBus, never()).post(isA(ActivityUpdateEvent.class));
    verify(mockEventBus, never()).post(isA(JourneyAutoStartEvent.class));
    verify(mockEventBus, never()).post(isA(JourneyAutoStopEvent.class));
    verify(mockTracker, never()).send(isA(Map.class));
  }

  public void testAutoStopTriggerEventWhenRunningWithSignificantDelay() {
    activity = new DetectedActivity(DetectedActivity.STILL, HIGH_STOP_CONFIDENCE);
    testProducer.setDetectedActivity(activity);
    testProducer.setIsRunning(true);
    assertEquals(Constants.ZERO_LONG, testProducer.getFirstStopEventTime());

    AutoStopTrigger trigger = new AutoStopTrigger(ActivityEventProducer.ACTIVITY_EVENT_PRODUCER, activity);

    // Pretend a previous stop trigger was received...
    testProducer.setFirstStopEventTime(trigger.getEventTime());

    // Set the trigger event time to far enough in the future to cause STOP event to fire...
    long eventTime = trigger.getEventTime() + Constants.ACTIVITY_STOP_TIME_DELAY + 3;
    trigger.setEventTime(eventTime);

    // Test it
    testProducer.onEvent(trigger);

    // verify events is getting raised...
    verify(mockEventBus, never()).post(isA(ActivityUpdateEvent.class));
    verify(mockEventBus, never()).post(isA(JourneyAutoStartEvent.class));
    verify(mockEventBus, times(1)).post(isA(JourneyAutoStopEvent.class));
    verify(mockTracker, times(1)).send(isA(Map.class));
    assertEquals(Constants.ZERO_LONG, testProducer.getFirstStopEventTime());
  }
}
