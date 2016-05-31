package com.soagrowers.android.tripcomputer.notifications;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.test.InstrumentationTestCase;
import android.view.Gravity;

import com.soagrowers.android.MockitoCompatibleInstrumentationTestCase;
import com.soagrowers.android.tripcomputer.Keys;
import com.soagrowers.android.tripcomputer.R;
import com.soagrowers.android.tripcomputer.data.Constants;
import com.soagrowers.android.tripcomputer.events.JourneyAutoStartEvent;
import com.soagrowers.android.tripcomputer.events.JourneyStartButtonEvent;
import com.soagrowers.android.tripcomputer.events.JourneyStartedEvent;
import com.soagrowers.android.tripcomputer.events.JourneyStoppedEvent;
import com.soagrowers.android.tripcomputer.notifications.actions.NotificationStopAction;
import com.soagrowers.android.tripcomputer.notifications.actions.NotificationToggleAutoAction;
import com.soagrowers.android.tripcomputer.notifications.actions.NotificationToggleBatterySaverAction;
import com.soagrowers.android.tripcomputer.notifications.actions.WearableStopAction;
import com.soagrowers.android.tripcomputer.notifications.actions.WearableToggleAutoAction;
import com.soagrowers.android.tripcomputer.notifications.actions.WearableToggleBatterySaverAction;
import com.soagrowers.android.utils.AndroidUtils;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.contains;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

/**
 * Created by Ben on 22/09/2014.
 */
public class TestRecordingStartedNotification extends MockitoCompatibleInstrumentationTestCase {

  // Real objects
  Context mContext;
  RecordingStartedNotification notificationUnderTest;

  // Mocks
  @Mock NotificationManagerCompat mockNotificationManager;
  @Mock NotificationCompat.Builder mockNotificationBuilder;
  @Mock Notification mockNotification;
  @Mock JourneyAutoStartEvent mockAutoStart;
  @Mock JourneyStartButtonEvent mockManualStart;

  // Notification Data
  private CharSequence title;
  private int smallIconId;
  private CharSequence ticker;
  private boolean isAutoStartStopOn;
  private boolean isBatterySaverOn;
  private String autoOn;
  private String autoOff;
  private String saverOn;
  private String saverOff;
  private Bitmap wearBackground;
  private Bitmap largeIcon;
  private int color;


  @Override
  public void setUp() throws Exception {
    super.setUp();
    mContext = getInstrumentation().getTargetContext();

    MockitoAnnotations.initMocks(this);

    notificationUnderTest = new RecordingStartedNotification(mContext);
    notificationUnderTest.setNotificationBuilder(mockNotificationBuilder);
    notificationUnderTest.setNotificationManager(mockNotificationManager);

    title = mContext.getText(R.string.notif_title_recording_started);
    smallIconId = R.drawable.ic_stat_start;
    ticker = mContext.getText(R.string.notif_txt_started);
    isAutoStartStopOn = AndroidUtils.getInstance(mContext).isAutoStartStopEnabled();
    isBatterySaverOn = AndroidUtils.getInstance(mContext).isBatterySaverOn();
    autoOn = mContext.getString(R.string.notif_txt_auto_is_on);
    autoOff = mContext.getString(R.string.notif_txt_auto_is_off);
    saverOn = mContext.getString(R.string.notif_txt_saver_is_on);
    saverOff = mContext.getString(R.string.notif_txt_saver_is_off);
    wearBackground = notificationUnderTest.getScrollableWearBackground(this.mContext, R.drawable.wear_background);
    largeIcon = notificationUnderTest.getLargeNotificationIcon(this.mContext, R.drawable.large_notification_icon);

    // Program the mock Builder to return a mock Notification
    when(mockNotificationBuilder.build()).thenReturn(mockNotification);
    color = this.mContext.getResources().getColor(R.color.TripComputerStartButton);
  }

  @Override
  public void tearDown() throws Exception {
    super.tearDown();
  }

  public void testInitialConstructionOfTheNotification() throws Exception {

    // Test the Notification is generated and sent
    notificationUnderTest.buildAndSendNotification();

    executeTheCommonAssertions();

    // Check things aren't being set accidentally
    verify(mockNotificationBuilder, never()).setContentText(anyString());
    verify(mockNotificationBuilder, never()).setSubText(anyString());
    verify(mockNotificationBuilder, never()).setTicker(anyString());

    // Check the notification manager is called
    verify(mockNotificationManager).notify(eq(Constants.NOTIFICATION_ID_RECORDING_STARTED), eq(mockNotification));
  }

  public void testAutoStartEventSendsTheNotification() {

    // Fire the Event
    notificationUnderTest.onEvent(mockAutoStart);
    executeTheCommonAssertions();

    verify(mockNotificationBuilder, times(1)).setTicker(contains((String) ticker));
    verify(mockNotificationBuilder, times(1)).setSound(isA(Uri.class));

    if (isAutoStartStopOn) {
      verify(mockNotificationBuilder, times(1)).setContentText(autoOn);
    } else {
      verify(mockNotificationBuilder, times(1)).setContentText(autoOff);
    }
    if (isBatterySaverOn) {
      verify(mockNotificationBuilder, times(1)).setSubText(saverOn);
    } else {
      verify(mockNotificationBuilder, times(1)).setSubText(saverOff);
    }

    // Check the notification manager is called
    verify(mockNotificationManager).notify(eq(Constants.NOTIFICATION_ID_RECORDING_STARTED), eq(mockNotification));
  }

  public void testManualStartEventSendsTheNotification() {

    // Fire the Event
    notificationUnderTest.onEvent(mockManualStart);
    executeTheCommonAssertions();

    verify(mockNotificationBuilder, times(1)).setTicker(contains((String) ticker));
    verify(mockNotificationBuilder, never()).setSound(isA(Uri.class));

    if (isAutoStartStopOn) {
      verify(mockNotificationBuilder, times(1)).setContentText(autoOn);
    } else {
      verify(mockNotificationBuilder, times(1)).setContentText(autoOff);
    }
    if (isBatterySaverOn) {
      verify(mockNotificationBuilder, times(1)).setSubText(saverOn);
    } else {
      verify(mockNotificationBuilder, times(1)).setSubText(saverOff);
    }

    // Check the notification manager is called
    verify(mockNotificationManager).notify(eq(Constants.NOTIFICATION_ID_RECORDING_STARTED), eq(mockNotification));
  }

  public void testSharedPrefStartStopChangeUpdatesTheNotification() {

    // Fire the Event
    SharedPreferences mockPrefs = mock(SharedPreferences.class);
    notificationUnderTest.setIsRunning(true);
    notificationUnderTest.onSharedPreferenceChanged(mockPrefs, Keys.AUTO_STARTSTOP_KEY);

    executeTheCommonAssertions();

    if (isAutoStartStopOn) {
      verify(mockNotificationBuilder, times(1)).setContentText(autoOn);
    } else {
      verify(mockNotificationBuilder, times(1)).setContentText(autoOff);
    }

    // Check the notification manager is called
    verify(mockNotificationManager).notify(eq(Constants.NOTIFICATION_ID_RECORDING_STARTED), eq(mockNotification));

    // Stuff that should not happen
    verify(mockNotificationBuilder, never()).setSubText(anyString());
    verify(mockNotificationBuilder, never()).setTicker(anyString());
  }

  public void testSharedPrefBatterySaverChangeUpdatesTheNofification() {

    // Fire the Event
    SharedPreferences mockPrefs = mock(SharedPreferences.class);
    notificationUnderTest.setIsRunning(true);
    notificationUnderTest.onSharedPreferenceChanged(mockPrefs, Keys.LOW_POWER_MODE_KEY);

    executeTheCommonAssertions();

    if (isBatterySaverOn) {
      verify(mockNotificationBuilder, times(1)).setSubText(saverOn);
    } else {
      verify(mockNotificationBuilder, times(1)).setSubText(saverOff);
    }

    // Check the notification manager is called
    verify(mockNotificationManager).notify(eq(Constants.NOTIFICATION_ID_RECORDING_STARTED), eq(mockNotification));

    // Stuff that should not happen
    verify(mockNotificationBuilder, never()).setContentText(anyString());
    verify(mockNotificationBuilder, never()).setTicker(anyString());
  }


  public void testSharedPrefChangeIgnoredWhenNotRunning() {
    // Fire the Event
    SharedPreferences mockPrefs = mock(SharedPreferences.class);
    notificationUnderTest.setIsRunning(false);
    notificationUnderTest.onSharedPreferenceChanged(mockPrefs, Keys.AUTO_STARTSTOP_KEY);
    notificationUnderTest.onSharedPreferenceChanged(mockPrefs, Keys.LOW_POWER_MODE_KEY);
    verifyZeroInteractions(mockNotificationBuilder);
  }

  public void testJourneyStoppedEventCancelsTheNotification() {

    JourneyStoppedEvent stoppedEvent = mock(JourneyStoppedEvent.class);
    notificationUnderTest.onEvent(stoppedEvent);

    // Check the notification manager is called
    verify(mockNotificationManager).cancel(eq(Constants.NOTIFICATION_ID_RECORDING_STARTED));

  }

  private void executeTheCommonAssertions() {

    // Check there is a Pending Intent
    assertNotNull(notificationUnderTest.getPendingIntent());



    // Check the Bitmaps are those expected
    //assertNotNull(notificationUnderTest.getLargeIcon());
    assertNotNull(notificationUnderTest.getWearableExtender().getBackground());

    if(Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
      assertTrue(notificationUnderTest.getWearableExtender().getBackground().sameAs(wearBackground));
      //assertTrue(notificationUnderTest.getLargeIcon().sameAs(largeIcon));
    }

    // Verify the Builder's being configured as expected
    verify(mockNotificationBuilder).setContentTitle(eq(title));
    verify(mockNotificationBuilder).setSmallIcon(eq(smallIconId));
    verify(mockNotificationBuilder).setContentIntent(isA(PendingIntent.class));
    verify(mockNotificationBuilder).setWhen(anyLong());
    verify(mockNotificationBuilder).setPriority(eq(NotificationCompat.PRIORITY_HIGH));
    verify(mockNotificationBuilder).setAutoCancel(true);
    verify(mockNotificationBuilder).setLocalOnly(false);
    verify(mockNotificationBuilder).setColor(color);
    verify(mockNotificationBuilder, never()).setLargeIcon(isA(Bitmap.class));

    //Check the Notification Action buttons are being added
    verify(mockNotificationBuilder, times(2)).addAction(isA(NotificationCompat.Action.class));

    assertTrue(notificationUnderTest.getActions().get(0) instanceof NotificationCompat.Action);
    assertEquals(notificationUnderTest.getActions().get(0).icon, NotificationStopAction.INTENT_ICON);
    assertEquals(notificationUnderTest.getActions().get(0).title, mContext.getString(NotificationStopAction.INTENT_TITLE));


    assertTrue(notificationUnderTest.getActions().get(1) instanceof NotificationCompat.Action);
    assertEquals(notificationUnderTest.getActions().get(1).icon, NotificationToggleBatterySaverAction.INTENT_ICON);
    assertEquals(notificationUnderTest.getActions().get(1).title, mContext.getString(NotificationToggleBatterySaverAction.INTENT_TITLE));

    try {
      assertNull(notificationUnderTest.getActions().get(2));
      assertTrue(false);
    } catch (IndexOutOfBoundsException e){
      assertTrue(true);
    }

    // Check the Wearable extender is configured as expected
    assertEquals(notificationUnderTest.getWearableExtender().getContentIconGravity(), Gravity.END);
    assertTrue(notificationUnderTest.getWearableExtender().getActions().size() > 0);
    assertNotNull(notificationUnderTest.getWearableExtender().getActions().get(0));
    assertNotNull(notificationUnderTest.getWearableExtender().getActions().get(1));
    assertNotNull(notificationUnderTest.getWearableExtender().getActions().get(2));

    try {
      assertNull(notificationUnderTest.getWearableExtender().getActions().get(4));
      assertTrue(false);
    } catch (IndexOutOfBoundsException e){
      assertTrue(true);
    }

    assertTrue(notificationUnderTest.getWearableExtender().getActions().get(0) instanceof NotificationCompat.Action);
    assertEquals(notificationUnderTest.getWearableExtender().getActions().get(0).icon, WearableStopAction.INTENT_ICON);
    assertEquals(notificationUnderTest.getWearableExtender().getActions().get(0).title, mContext.getString(WearableStopAction.INTENT_TITLE));

    assertTrue(notificationUnderTest.getWearableExtender().getActions().get(1) instanceof NotificationCompat.Action);
    assertEquals(notificationUnderTest.getWearableExtender().getActions().get(1).icon, WearableToggleAutoAction.INTENT_ICON);
    assertEquals(notificationUnderTest.getWearableExtender().getActions().get(1).title, mContext.getString(WearableToggleAutoAction.INTENT_TITLE));

    assertTrue(notificationUnderTest.getWearableExtender().getActions().get(2) instanceof NotificationCompat.Action);
    assertEquals(notificationUnderTest.getWearableExtender().getActions().get(2).icon, WearableToggleBatterySaverAction.INTENT_ICON);
    assertEquals(notificationUnderTest.getWearableExtender().getActions().get(2).title, mContext.getString(WearableToggleBatterySaverAction.INTENT_TITLE));

    verify(mockNotificationBuilder, times(1)).extend(isA(NotificationCompat.WearableExtender.class));
  }
}
