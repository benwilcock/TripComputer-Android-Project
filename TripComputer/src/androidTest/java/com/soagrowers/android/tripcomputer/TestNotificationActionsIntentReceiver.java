package com.soagrowers.android.tripcomputer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.google.android.gms.analytics.Tracker;
import com.soagrowers.android.MockitoCompatibleInstrumentationTestCase;
import com.soagrowers.android.tripcomputer.events.JourneyStopButtonEvent;
import com.soagrowers.android.tripcomputer.notifications.actions.NotificationStopAction;
import com.soagrowers.android.tripcomputer.notifications.actions.NotificationToggleAutoAction;
import com.soagrowers.android.tripcomputer.notifications.actions.NotificationToggleBatterySaverAction;
import com.soagrowers.android.tripcomputer.notifications.actions.WearableStopAction;
import com.soagrowers.android.tripcomputer.notifications.actions.WearableToggleAutoAction;
import com.soagrowers.android.tripcomputer.notifications.actions.WearableToggleBatterySaverAction;
import com.soagrowers.android.utils.AndroidUtils;

import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.Map;

import de.greenrobot.event.EventBus;

import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.isA;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.Mock;

/**
 * Created by Ben on 15/09/2014.
 */
public class TestNotificationActionsIntentReceiver extends MockitoCompatibleInstrumentationTestCase {

  @Mock
  EventBus mockEventBus;
  @Mock
  Intent mockStopActionIntent;
  @Mock
  Intent mockToggleAutoActionIntent;
  @Mock
  Intent mockWearStopActionIntent;
  @Mock
  Intent mockWearToggleAutoActionIntent;
  @Mock
  Intent mockToggleSaverActionIntent;
  @Mock
  Intent mockWearToggleSaverActionIntent;
  @Mock
  AndroidUtils mockAndroidUtils;
  @Mock
  SharedPreferences mockSharedPreferences;
  @Mock
  SharedPreferences.Editor mockSharedPreferencesEditor;
  @Mock
  Tracker mockTracker;

  NotificationActionsIntentReceiver receiver;
  private Context mContext;


  @Override
  public void setUp() throws Exception {
    super.setUp();
    MockitoAnnotations.initMocks(this);
    mContext = getInstrumentation().getTargetContext();
    receiver = new NotificationActionsIntentReceiver();
    receiver.setEventBus(mockEventBus);
    receiver.setAndroidUtils(mockAndroidUtils);
    receiver.setSharedPreferences(mockSharedPreferences);
    receiver.setTracker(mockTracker);

    when(mockStopActionIntent.getAction()).thenReturn(NotificationStopAction.INTENT_ACTION);
    when(mockWearStopActionIntent.getAction()).thenReturn(WearableStopAction.INTENT_ACTION);

    when(mockToggleAutoActionIntent.getAction()).thenReturn(NotificationToggleAutoAction.INTENT_ACTION);
    when(mockWearToggleAutoActionIntent.getAction()).thenReturn(WearableToggleAutoAction.INTENT_ACTION);

    when(mockToggleSaverActionIntent.getAction()).thenReturn(NotificationToggleBatterySaverAction.INTENT_ACTION);
    when(mockWearToggleSaverActionIntent.getAction()).thenReturn(WearableToggleBatterySaverAction.INTENT_ACTION);

    when(mockSharedPreferences.edit()).thenReturn(mockSharedPreferencesEditor);
    when(mockSharedPreferencesEditor.putBoolean(anyString(), anyBoolean())).thenReturn(mockSharedPreferencesEditor);

    /*
    doAnswer(new Answer<Void>() {
      @Override
      public Void answer(InvocationOnMock invocationOnMock) throws Throwable {
        return null;
      }
    }).when(mockSharedPreferencesEditor).apply();
    doAnswer(new Answer<SharedPreferences.Editor>() {
      @Override
      public SharedPreferences.Editor answer(InvocationOnMock invocationOnMock) throws Throwable {
        return mockSharedPreferencesEditor;
      }
    }).when(mockSharedPreferences).edit();
    when(mockSharedPreferencesEditor.commit()).thenReturn(true);
    */
  }

  @Override
  public void tearDown() throws Exception {
    super.tearDown();
  }

  public void testNotificationStopActionReceived() throws Exception {
    receiver.onReceive(mContext, mockStopActionIntent);
    verify(mockEventBus).post(isA(JourneyStopButtonEvent.class));
    verify(mockTracker).send(isA(Map.class));
  }

  public void testWearNotificationStopActionReceived() throws Exception {
    receiver.onReceive(mContext, mockWearStopActionIntent);
    verify(mockEventBus).post(isA(JourneyStopButtonEvent.class));
    verify(mockTracker).send(isA(Map.class));
  }

  public void testNotificationToggleAutoActionReceivedWhenAutoIsOn() throws Exception {
    when(mockAndroidUtils.isAutoStartStopEnabled()).thenReturn(true);
    receiver.onReceive(mContext, mockToggleAutoActionIntent);
    verify(mockSharedPreferences, times(1)).edit();
    verify(mockSharedPreferencesEditor, times(1)).putBoolean(Keys.AUTO_STARTSTOP_KEY, false);
    verify(mockSharedPreferencesEditor, times(1)).apply();
    verify(mockTracker).send(isA(Map.class));
  }

  public void testNotificationToggleAutoActionReceivedWhenAutoIsOff() throws Exception {
    when(mockAndroidUtils.isAutoStartStopEnabled()).thenReturn(false);
    receiver.onReceive(mContext, mockToggleAutoActionIntent);
    verify(mockSharedPreferences, times(1)).edit();
    verify(mockSharedPreferencesEditor, times(1)).putBoolean(Keys.AUTO_STARTSTOP_KEY, true);
    verify(mockSharedPreferencesEditor, times(1)).apply();
    verify(mockTracker).send(isA(Map.class));
  }

  public void testWearNotificationToggleAutoActionReceivedWhenAutoIsOn() throws Exception {
    when(mockAndroidUtils.isAutoStartStopEnabled()).thenReturn(true);
    receiver.onReceive(mContext, mockWearToggleAutoActionIntent);
    verify(mockSharedPreferences, times(1)).edit();
    verify(mockSharedPreferencesEditor, times(1)).putBoolean(Keys.AUTO_STARTSTOP_KEY, false);
    verify(mockSharedPreferencesEditor, times(1)).apply();
    verify(mockTracker).send(isA(Map.class));
  }

  public void testWearNotificationToggleAutoActionReceivedWhenAutoIsOff() throws Exception {
    when(mockAndroidUtils.isAutoStartStopEnabled()).thenReturn(false);
    receiver.onReceive(mContext, mockWearToggleAutoActionIntent);
    verify(mockSharedPreferences, times(1)).edit();
    verify(mockSharedPreferencesEditor, times(1)).putBoolean(Keys.AUTO_STARTSTOP_KEY, true);
    verify(mockSharedPreferencesEditor, times(1)).apply();
    verify(mockTracker).send(isA(Map.class));
  }

  public void testNotificationToggleBatterySaverActionReceivedWhenSaverIsOn() throws Exception {
    when(mockAndroidUtils.isBatterySaverOn()).thenReturn(true);
    receiver.onReceive(mContext, mockToggleSaverActionIntent);
    verify(mockSharedPreferences, times(1)).edit();
    verify(mockSharedPreferencesEditor, times(1)).putBoolean(Keys.LOW_POWER_MODE_KEY, false);
    verify(mockSharedPreferencesEditor, times(1)).apply();
    verify(mockTracker).send(isA(Map.class));
  }

  public void testNotificationToggleBatterySaverActionReceivedWhenSaverIsOff() throws Exception {
    when(mockAndroidUtils.isBatterySaverOn()).thenReturn(false);
    receiver.onReceive(mContext, mockToggleSaverActionIntent);
    verify(mockSharedPreferences, times(1)).edit();
    verify(mockSharedPreferencesEditor, times(1)).putBoolean(Keys.LOW_POWER_MODE_KEY, true);
    verify(mockSharedPreferencesEditor, times(1)).apply();
    verify(mockTracker).send(isA(Map.class));
  }

  public void testWearNotificationToggleBatterySaverActionReceivedWhenSaverIsOn() throws Exception {
    when(mockAndroidUtils.isBatterySaverOn()).thenReturn(true);
    receiver.onReceive(mContext, mockWearToggleSaverActionIntent);
    verify(mockSharedPreferences, times(1)).edit();
    verify(mockSharedPreferencesEditor, times(1)).putBoolean(Keys.LOW_POWER_MODE_KEY, false);
    verify(mockSharedPreferencesEditor, times(1)).apply();
    verify(mockTracker).send(isA(Map.class));
  }

  public void testWearNotificationToggleBatterySaverActionReceivedWhenSaverIsOff() throws Exception {
    when(mockAndroidUtils.isBatterySaverOn()).thenReturn(false);
    receiver.onReceive(mContext, mockWearToggleSaverActionIntent);
    verify(mockSharedPreferences, times(1)).edit();
    verify(mockSharedPreferencesEditor, times(1)).putBoolean(Keys.LOW_POWER_MODE_KEY, true);
    verify(mockSharedPreferencesEditor, times(1)).apply();
    verify(mockTracker).send(isA(Map.class));
  }
}
