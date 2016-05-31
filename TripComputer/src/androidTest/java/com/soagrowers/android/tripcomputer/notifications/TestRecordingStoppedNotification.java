package com.soagrowers.android.tripcomputer.notifications;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.test.InstrumentationTestCase;
import android.view.Gravity;

import com.soagrowers.android.MockitoCompatibleInstrumentationTestCase;
import com.soagrowers.android.tripcomputer.R;
import com.soagrowers.android.tripcomputer.data.Constants;
import com.soagrowers.android.tripcomputer.data.Journey;
import com.soagrowers.android.tripcomputer.events.JourneyNotSavedEvent;
import com.soagrowers.android.tripcomputer.events.JourneySavedEvent;
import com.soagrowers.android.tripcomputer.events.JourneyStartedEvent;

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
import static org.mockito.Mockito.when;

/**
 * Created by Ben on 22/09/2014.
 */
public class TestRecordingStoppedNotification extends MockitoCompatibleInstrumentationTestCase {

  // Real objects
  Context mContext;
  RecordingStoppedNotification notificationUnderTest;

  // Mocks
  @Mock
  NotificationManagerCompat mockNotificationManager;
  @Mock
  NotificationCompat.Builder mockNotificationBuilder;
  @Mock
  Notification mockNotification;
  @Mock
  JourneyNotSavedEvent mockNotSavedEvent;
  @Mock
  JourneySavedEvent mockSavedEvent;

  // Notification Data
  private CharSequence title;
  private int smallIconId;
  private CharSequence ticker;
  private Bitmap wearBackground;
  private Bitmap largeIcon;
  private String notSavedText;
  private String youCovered;
  private Journey journey;
  private String youAveraged;
  private int color;


  @Override
  public void setUp() throws Exception {
    super.setUp();
    mContext = getInstrumentation().getTargetContext();

    MockitoAnnotations.initMocks(this);

    notificationUnderTest = new RecordingStoppedNotification(mContext);
    notificationUnderTest.setNotificationBuilder(mockNotificationBuilder);
    notificationUnderTest.setNotificationManager(mockNotificationManager);

    title = mContext.getText(R.string.notif_title_recording_stopped);
    smallIconId = R.drawable.ic_stat_stop;
    ticker = mContext.getText(R.string.notif_txt_stopped);
    notSavedText = mContext.getString(R.string.notif_txt_discarded);
    youCovered = mContext.getString(R.string.notif_txt_covered);
    youAveraged = this.mContext.getString(R.string.notif_txt_you_averaged);
    wearBackground = notificationUnderTest.getScrollableWearBackground(this.mContext, R.drawable.wear_background);
    largeIcon = notificationUnderTest.getLargeNotificationIcon(this.mContext, R.drawable.large_notification_icon);
    color = mContext.getResources().getColor(R.color.TripComputerStopButton);

    journey = new Journey();
    journey.start();
    journey.setDistanceUnits(Constants.KILOMETERS);
    journey.setTotalDistance(10000.0f);
    journey.setTotalTime(Constants.MINUTES_PER_HOUR * Constants.SECONDS_PER_MINUTE * Constants.MILLISECONDS_PER_SECOND);
    journey.stop();

    // Program the mock Builder to return a mock Notification
    when(mockNotificationBuilder.build()).thenReturn(mockNotification);

    //Program the Mock events to return the journey
    when(mockNotSavedEvent.getImmutableJourney()).thenReturn(journey);
    when(mockSavedEvent.getImmutableJourney()).thenReturn(journey);
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
    verify(mockNotificationManager).notify(eq(Constants.NOTIFICATION_ID_RECORDING_STOPPED), eq(mockNotification));
  }

  public void testJourneySavedEventSendsTheNotification() {

    // Fire the Event
    notificationUnderTest.onEvent(mockSavedEvent);
    executeTheCommonAssertions();

    // Check things aren't being set accidentally
    verify(mockNotificationBuilder).setContentText(contains(youCovered));
    verify(mockNotificationBuilder).setSubText(contains(youAveraged));
    verify(mockNotificationBuilder).setTicker(contains((String) ticker));

    // Check the notification manager is called
    verify(mockNotificationManager).notify(eq(Constants.NOTIFICATION_ID_RECORDING_STOPPED), eq(mockNotification));
  }

  public void testJourneyNotSavedEventSendsTheNotification() {

    // Fire the Event
    notificationUnderTest.onEvent(mockNotSavedEvent);

    executeTheCommonAssertions();

    // Check everything is being set as expected
    verify(mockNotificationBuilder).setContentText(contains(youCovered));
    verify(mockNotificationBuilder).setSubText(contains(notSavedText));
    verify(mockNotificationBuilder).setTicker(contains((String) ticker));

    // Check the notification manager is called
    verify(mockNotificationManager).notify(eq(Constants.NOTIFICATION_ID_RECORDING_STOPPED), eq(mockNotification));
  }

  public void testJourneyStartedEventCancelsTheNotification() {

    JourneyStartedEvent startedEvent = mock(JourneyStartedEvent.class);
    notificationUnderTest.onEvent(startedEvent);

    // Check the notification manager is called
    verify(mockNotificationManager).cancel(eq(Constants.NOTIFICATION_ID_RECORDING_STOPPED));

  }

  private void executeTheCommonAssertions() {

    // Check there is a Pending Intent
    assertNotNull(notificationUnderTest.getPendingIntent());

    // Check the Wearable extender is configured as expected
    assertEquals(notificationUnderTest.getWearableExtender().getContentIconGravity(), Gravity.END);

    // Check the Bitmaps are those expected
    //assertNotNull(notificationUnderTest.getLargeIcon());
    assertNotNull(notificationUnderTest.getWearableExtender().getBackground());

    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
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

    //Check the Wear Extender is being added
    verify(mockNotificationBuilder, times(1)).extend(isA(NotificationCompat.WearableExtender.class));
  }
}
