package com.soagrowers.android.tripcomputer.notifications;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.test.InstrumentationTestCase;
import android.view.Gravity;

import com.soagrowers.android.MockitoCompatibleInstrumentationTestCase;
import com.soagrowers.android.tripcomputer.Keys;
import com.soagrowers.android.tripcomputer.R;
import com.soagrowers.android.tripcomputer.data.Constants;
import com.soagrowers.android.tripcomputer.data.Journey;
import com.soagrowers.android.tripcomputer.events.JourneyStartedEvent;
import com.soagrowers.android.tripcomputer.events.JourneyStoppedEvent;
import com.soagrowers.android.tripcomputer.events.JourneyUpdatedEvent;
import com.soagrowers.android.tripcomputer.notifications.actions.NotificationStopAction;
import com.soagrowers.android.tripcomputer.notifications.actions.NotificationToggleAutoAction;
import com.soagrowers.android.tripcomputer.notifications.actions.NotificationToggleBatterySaverAction;
import com.soagrowers.android.tripcomputer.notifications.actions.WearableStopAction;
import com.soagrowers.android.tripcomputer.notifications.actions.WearableToggleAutoAction;
import com.soagrowers.android.tripcomputer.notifications.actions.WearableToggleBatterySaverAction;
import com.soagrowers.android.utils.AndroidUtils;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.anyBoolean;
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
public class TestServiceStatusNotification extends MockitoCompatibleInstrumentationTestCase {

  // Real objects
  Context mContext;
  ServiceStatusNotification notificationUnderTest;

  // Mocks
  @Mock NotificationManagerCompat mockNotificationManager;
  @Mock NotificationCompat.Builder mockNotificationBuilder;
  @Mock Notification mockNotification;
  @Mock JourneyStoppedEvent mockStoppedEvent;
  @Mock JourneyStartedEvent mockStartedEvent;
  @Mock JourneyUpdatedEvent mockUpdatedEvent;

  // Notification Data
  private CharSequence title;
  private int smallIconId;
  private boolean isAutoStartStopOn;
  private boolean isBatterySaverOn;
  private String autoOn;
  private String autoOff;
  private String saverOn;
  private String saverOff;
  private Bitmap wearBackground;
  private Bitmap largeIcon;
  private Journey journey;
  private String standby;
  private String started;
  private String youCovered;
  private int color;


  @Override
  public void setUp() throws Exception {
    super.setUp();
    mContext = getInstrumentation().getTargetContext();

    MockitoAnnotations.initMocks(this);

    notificationUnderTest = new ServiceStatusNotification(mContext);
    notificationUnderTest.setNotificationBuilder(mockNotificationBuilder);
    notificationUnderTest.setNotificationManager(mockNotificationManager);

    title = mContext.getText(R.string.app_name);
    smallIconId = R.drawable.ic_stat_logo;
    isAutoStartStopOn = AndroidUtils.getInstance(mContext).isAutoStartStopEnabled();
    isBatterySaverOn = AndroidUtils.getInstance(mContext).isBatterySaverOn();
    autoOn = mContext.getString(R.string.notif_txt_auto_is_on_long);
    autoOff = mContext.getString(R.string.notif_txt_auto_is_off_long);
    saverOn = mContext.getString(R.string.notif_txt_saver_is_on);
    saverOff = mContext.getString(R.string.notif_txt_saver_is_off);
    standby = mContext.getString(R.string.notif_txt_idle);
    started = mContext.getString(R.string.notif_txt_started);
    youCovered = mContext.getString(R.string.notif_txt_covered);
    wearBackground = notificationUnderTest.getScrollableWearBackground(this.mContext, R.drawable.wear_background);
    largeIcon = notificationUnderTest.getLargeNotificationIcon(this.mContext, R.drawable.large_notification_icon);
    color = mContext.getResources().getColor(R.color.TripComputerDayColorPrimary);

    journey = new Journey();
    journey.start();
    journey.setDistanceUnits(Constants.KILOMETERS);
    journey.setTotalDistance(10000.0f);
    journey.setTotalTime(Constants.MINUTES_PER_HOUR * Constants.SECONDS_PER_MINUTE * Constants.MILLISECONDS_PER_SECOND);

    // Program the mock Builder to return a mock Notification
    when(mockNotificationBuilder.build()).thenReturn(mockNotification);

    //Program the mock Update event to return the journey
    when(mockUpdatedEvent.getImmutableJourney()).thenReturn(journey);
  }

  @Override
  public void tearDown() throws Exception {
    super.tearDown();
  }

  public void testInitialConstructionOfTheNotification() throws Exception {

    // Test the Notification is generated and sent
    notificationUnderTest.getJourneyServiceNotification();

    executeTheCommonAssertions();

    // Check things aren't being set accidentally
    verify(mockNotificationBuilder, times(1)).setSubText(standby);


  }

  public void testSharedPrefStartStopChangeUpdatesTheNofification() {

    // Fire the Event
    SharedPreferences mockPrefs = mock(SharedPreferences.class);
    notificationUnderTest.onSharedPreferenceChanged(mockPrefs, Keys.AUTO_STARTSTOP_KEY);

    executeTheCommonAssertions();

    verifyAutoStopStatus();

    verify(mockNotificationBuilder, times(1)).setSubText(standby);

    // Check the notification manager is called
    verify(mockNotificationManager).notify(eq(Constants.NOTIFICATION_ID_STATUS), eq(mockNotification));
  }

  public void testJourneyStoppedEventUpdatesNotification(){

    notificationUnderTest.onEvent(mockStoppedEvent);
    executeTheCommonAssertions();
    verifyAutoStopStatus();
    verify(mockNotificationBuilder, times(1)).setSubText(standby);

    // Check the notification manager is called
    verify(mockNotificationManager).notify(eq(Constants.NOTIFICATION_ID_STATUS), eq(mockNotification));
  }

  public void testJourneyStartedEventUpdatesNotification(){

    notificationUnderTest.onEvent(mockStartedEvent);
    executeTheCommonAssertions();
    verifyAutoStopStatus();
    verify(mockNotificationBuilder, times(1)).setSubText(started);

    // Check the notification manager is called
    verify(mockNotificationManager).notify(eq(Constants.NOTIFICATION_ID_STATUS), eq(mockNotification));
  }

  public void testRunningJourneyUpdatedEventUpdatesNotification(){

    notificationUnderTest.onEvent(mockUpdatedEvent);
    executeTheCommonAssertions();
    verifyAutoStopStatus();

    verify(mockNotificationBuilder, times(1)).setSubText(contains(youCovered));

    // Check the notification manager is called
    verify(mockNotificationManager).notify(eq(Constants.NOTIFICATION_ID_STATUS), eq(mockNotification));
  }

  public void testStoppedJourneyUpdatedEventUpdatesNotification(){

    //Stop the journey
    journey.stop();
    notificationUnderTest.onEvent(mockUpdatedEvent);
    executeTheCommonAssertions();
    verifyAutoStopStatus();

    // Check the standby message is used
    verify(mockNotificationBuilder, times(1)).setSubText(standby);

    // Check the notification manager is called
    verify(mockNotificationManager).notify(eq(Constants.NOTIFICATION_ID_STATUS), eq(mockNotification));
  }

  private void verifyAutoStopStatus() {
    if (isAutoStartStopOn) {
      verify(mockNotificationBuilder, times(1)).setContentText(autoOn);
    } else {
      verify(mockNotificationBuilder, times(1)).setContentText(autoOff);
    }
  }

  private void executeTheCommonAssertions() {

    // Check there is a Pending Intent
    assertNotNull(notificationUnderTest.getPendingIntent());

    // Check the Bitmaps are those expected
    //assertNotNull(notificationUnderTest.getLargeIcon());

    if(Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
      //assertTrue(notificationUnderTest.getLargeIcon().sameAs(largeIcon));
    }

    // Verify the Builder's being configured as expected
    verify(mockNotificationBuilder).setContentTitle(eq(title));
    verify(mockNotificationBuilder).setSmallIcon(eq(smallIconId));
    verify(mockNotificationBuilder).setContentIntent(isA(PendingIntent.class));
    verify(mockNotificationBuilder).setWhen(anyLong());
    verify(mockNotificationBuilder).setPriority(eq(NotificationCompat.PRIORITY_LOW));
    verify(mockNotificationBuilder).setAutoCancel(false);
    verify(mockNotificationBuilder).setLocalOnly(true);
    verify(mockNotificationBuilder).setColor(color);
    verify(mockNotificationBuilder, never()).setLargeIcon(isA(Bitmap.class));

    verifyAutoStopStatus();

    // Check no Ticker is set
    verify(mockNotificationBuilder, never()).setTicker(anyString());

    // Check the Notification Action buttons are being added
    verify(mockNotificationBuilder, times(1)).addAction(isA(NotificationCompat.Action.class));

    assertTrue(notificationUnderTest.getActions().get(0) instanceof NotificationCompat.Action);
    assertEquals(notificationUnderTest.getActions().get(0).icon, NotificationToggleAutoAction.INTENT_ICON);
    assertEquals(notificationUnderTest.getActions().get(0).title, mContext.getString(NotificationToggleAutoAction.INTENT_TITLE));

    try {
      assertNull(notificationUnderTest.getActions().get(1));
      assertTrue(false);
    } catch (IndexOutOfBoundsException e){
      assertTrue(true);
    }
  }

  /*


    // Check the notification manager is called
    verify(mockNotificationManager).notify(eq(Constants.NOTIFICATION_ID_STATUS), eq(mockNotification));
   */
}
