package com.soagrowers.android.tripcomputer.notifications;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.view.Gravity;

import com.google.common.base.Optional;
import com.soagrowers.android.tripcomputer.Keys;
import com.soagrowers.android.tripcomputer.R;
import com.soagrowers.android.tripcomputer.TripComputer;
import com.soagrowers.android.tripcomputer.data.Constants;
import com.soagrowers.android.tripcomputer.events.JourneyAutoStartEvent;
import com.soagrowers.android.tripcomputer.events.JourneyStartButtonEvent;
import com.soagrowers.android.tripcomputer.events.JourneyStoppedEvent;
import com.soagrowers.android.tripcomputer.notifications.actions.NotificationStopAction;
import com.soagrowers.android.tripcomputer.notifications.actions.NotificationToggleBatterySaverAction;
import com.soagrowers.android.tripcomputer.notifications.actions.WearableStopAction;
import com.soagrowers.android.tripcomputer.notifications.actions.WearableToggleAutoAction;
import com.soagrowers.android.tripcomputer.notifications.actions.WearableToggleBatterySaverAction;
import com.soagrowers.android.utils.AndroidUtils;
import com.soagrowers.android.utils.EventManager;
import com.soagrowers.android.utils.Log;

import java.util.Date;

/**
 * Created by Ben on 08/09/2014.
 */
public class RecordingStartedNotification extends AbstractNotification implements
  SharedPreferences.OnSharedPreferenceChangeListener {

  private static final String TAG = RecordingStartedNotification.class.getSimpleName();
  private static int STARTED_NOTIFICATION_ICON_COLOUR;
  private static Log log;
  private final SharedPreferences.OnSharedPreferenceChangeListener sharedPreferenceChangeListener;
  private final AndroidUtils h;
  private Optional<Context> mContext = Optional.absent();
  private boolean isRunning = false;

  protected RecordingStartedNotification(Context context, NotificationCompat.WearableExtender extender) {
    this(context);
    this.setWearableExtender(extender);
  }

  public RecordingStartedNotification(Context context) {
    super(context,
      Constants.NOTIFICATION_ID_RECORDING_STARTED,
      R.drawable.ic_stat_start,
      R.string.notif_title_recording_started,
      TripComputer.class);

    this.mContext = Optional.of(context);
    this.log = Log.getInstance(this.mContext.get());
    this.h = AndroidUtils.getInstance(mContext.get());
    this.STARTED_NOTIFICATION_ICON_COLOUR = this.mContext.get().getResources().getColor(R.color.TripComputerStartButton);

    //Setup a shared preferences change listener
    //*** MUST be a class member Variable to prevent garbage collection ***
    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext.get());
    this.sharedPreferenceChangeListener = this;
    prefs.registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);

    // Register for App Events
    EventManager.getInstance().register(this);

    // Set up a few basics for the Notification
    this.setPriority(NotificationCompat.PRIORITY_HIGH);
    this.setAutoCancel(true);
    this.setLargeIcon(R.drawable.large_notification_icon);
    this.setLocalOnly(false);
    this.setIconColor(STARTED_NOTIFICATION_ICON_COLOUR);
    this.addAction(new NotificationStopAction().getAction(this.mContext.get()));
    this.addAction(new NotificationToggleBatterySaverAction().getAction(this.mContext.get()));

    // Create new WearableExtender and add actions
    this.getWearableExtender().addAction(new WearableStopAction().getAction(this.mContext.get()));
    this.getWearableExtender().addAction(new WearableToggleAutoAction().getAction(this.mContext.get()));
    this.getWearableExtender().addAction(new WearableToggleBatterySaverAction().getAction(this.mContext.get()));
    this.getWearableExtender().setBackground(getScrollableWearBackground(this.mContext.get(), R.drawable.wear_background));
    this.getWearableExtender().setContentIconGravity(Gravity.END);
  }

  private void setAutoRecordStatus() {
    if (h.isAutoStartStopEnabled()) {
      this.setLine1(mContext.get().getString(R.string.notif_txt_auto_is_on));
    } else {
      this.setLine1(mContext.get().getString(R.string.notif_txt_auto_is_off));
    }
  }

  private void setBatterySaverStatus() {
    if (h.isBatterySaverOn()) {
      this.setLine2(mContext.get().getString(R.string.notif_txt_saver_is_on));
    } else {
      this.setLine2(mContext.get().getString(R.string.notif_txt_saver_is_off));
    }
  }

  private void setStartNotificationDefaults() {
    this.setIsRunning(true);
    this.setTicker(R.string.notif_txt_started);
    this.setTime(new Date().getTime());
    this.setAutoRecordStatus();
    this.setBatterySaverStatus();
  }

  public void onEvent(JourneyStartButtonEvent event) {
    //Does not need a sound
    setStartNotificationDefaults();
    this.doDing(false);
    this.buildAndSendNotification();
  }

  public void onEvent(JourneyAutoStartEvent event) {
    //This should have a sound
    this.setStartNotificationDefaults();
    this.doDing(true);
    this.buildAndSendNotification();
  }

  protected void setIsRunning(boolean running) {
    this.isRunning = running;
  }

  public void onEvent(JourneyStoppedEvent event) {
    this.setIsRunning(false);
    this.cancel();
  }

  @Override
  public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
    if (this.isRunning && key.equals(Keys.AUTO_STARTSTOP_KEY)) {
      this.setAutoRecordStatus();
      this.buildAndSendNotification();
    }

    if (this.isRunning && key.equals(Keys.LOW_POWER_MODE_KEY)) {
      this.setBatterySaverStatus();
      this.buildAndSendNotification();
    }
  }
}
