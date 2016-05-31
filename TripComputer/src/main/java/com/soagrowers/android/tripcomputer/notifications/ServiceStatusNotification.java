package com.soagrowers.android.tripcomputer.notifications;

import android.app.Notification;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

import com.google.common.base.Optional;
import com.soagrowers.android.tripcomputer.Keys;
import com.soagrowers.android.tripcomputer.R;
import com.soagrowers.android.tripcomputer.TripComputer;
import com.soagrowers.android.tripcomputer.data.Constants;
import com.soagrowers.android.tripcomputer.data.ImmutableJourney;
import com.soagrowers.android.tripcomputer.data.JourneyDecorator;
import com.soagrowers.android.tripcomputer.events.JourneyStartedEvent;
import com.soagrowers.android.tripcomputer.events.JourneyStoppedEvent;
import com.soagrowers.android.tripcomputer.events.JourneyUpdatedEvent;
import com.soagrowers.android.tripcomputer.notifications.actions.NotificationToggleAutoAction;
import com.soagrowers.android.utils.AndroidUtils;
import com.soagrowers.android.utils.EventManager;
import com.soagrowers.android.utils.StringUtils;

import java.util.Date;

/**
 * Created by Ben on 08/09/2014.
 */
public class ServiceStatusNotification extends AbstractNotification
  implements SharedPreferences.OnSharedPreferenceChangeListener {

  private final Optional<Context> mContext;
  private final AndroidUtils h;
  private final ServiceStatusNotification sharedPreferenceChangeListener;
  private boolean isRunning;

  public ServiceStatusNotification(Context context) {
    super(context,
      Constants.NOTIFICATION_ID_STATUS,
      R.drawable.ic_stat_logo,
      R.string.app_name,
      TripComputer.class);

    // Store the context
    this.mContext = Optional.of(context);
    this.h = AndroidUtils.getInstance(mContext.get());
    this.isRunning = false;

    // Set some notification details
    this.setPriority(NotificationCompat.PRIORITY_LOW);
    this.setTime(new Date().getTime());
    this.setLargeIcon(R.drawable.large_notification_icon);
    this.setAutoCancel(false);
    this.setIconColor(TC_NOTIFICATION_COLOUR);
    this.addAction(new NotificationToggleAutoAction().getAction(mContext.get()));
    this.setAutoRecordStatus();
    this.setAppStatus();

    // Register for App Events
    EventManager.getInstance().register(this);

    //Setup a shared preferences change listener
    //*** MUST be a class member Variable to prevent garbage collection ***
    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext.get());
    this.sharedPreferenceChangeListener = this;
    prefs.registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
  }

  public Notification getJourneyServiceNotification() {
    return this.getNotification();
  }

  private void setAutoRecordStatus() {
    if (h.isAutoStartStopEnabled()) {
      this.setLine1(mContext.get().getString(R.string.notif_txt_auto_is_on_long));
    } else {
      this.setLine1(mContext.get().getString(R.string.notif_txt_auto_is_off_long));
    }
  }

  private void setAppStatus() {
    if (this.isRunning) {
      this.setLine2(mContext.get().getString(R.string.notif_txt_started));
    } else {
      this.setLine2(mContext.get().getString(R.string.notif_txt_idle));
    }
  }

  public void onEvent(JourneyStartedEvent event) {
    this.isRunning = true;
    this.setAutoRecordStatus();
    this.setAppStatus();
    this.buildAndSendNotification();
  }

  public void onEvent(JourneyUpdatedEvent event) {

    this.setAutoRecordStatus();
    ImmutableJourney journey = event.getImmutableJourney();

    if (journey.isRunning()) {
      // Set the Distance sub-message
      StringBuilder message = new StringBuilder();
      message.append(this.mContext.get().getString(R.string.notif_txt_covered));
      message.append(StringUtils.SPACE);
      JourneyDecorator d = new JourneyDecorator(journey, mContext.get());
      message.append(d.getTotalDistanceAsString());
      this.setLine2(message.toString());

      //Set the time
      this.setTime(journey.getStartedDate().getTime());

    } else {
      this.setAppStatus();
    }

    // Send the notification
    this.buildAndSendNotification();
  }

  public void onEvent(JourneyStoppedEvent event) {
    this.isRunning = false;
    this.setAutoRecordStatus();
    this.setAppStatus();
    this.buildAndSendNotification();
  }


  @Override
  public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

    if (key.equals(Keys.AUTO_STARTSTOP_KEY)) {
      this.setTime(new Date().getTime());
      this.setAutoRecordStatus();
      this.buildAndSendNotification();
    }
  }
}
