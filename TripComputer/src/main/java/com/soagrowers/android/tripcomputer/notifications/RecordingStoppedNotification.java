package com.soagrowers.android.tripcomputer.notifications;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.view.Gravity;
import android.widget.Chronometer;

import com.google.common.base.Optional;
import com.soagrowers.android.tripcomputer.R;
import com.soagrowers.android.tripcomputer.TripComputer;
import com.soagrowers.android.tripcomputer.data.Constants;
import com.soagrowers.android.tripcomputer.data.ImmutableJourney;
import com.soagrowers.android.tripcomputer.data.JourneyDecorator;
import com.soagrowers.android.tripcomputer.events.JourneyNotSavedEvent;
import com.soagrowers.android.tripcomputer.events.JourneySavedEvent;
import com.soagrowers.android.tripcomputer.events.JourneyStartedEvent;
import com.soagrowers.android.utils.EventManager;
import com.soagrowers.android.utils.StringUtils;

import java.util.Date;

/**
 * Created by Ben on 08/09/2014.
 */
public class RecordingStoppedNotification extends AbstractNotification {

  private Optional<Context> mContext = Optional.absent();
  private static int STOPPED_NOTIFICATION_ICON_COLOUR;

  public RecordingStoppedNotification(Context context) {
    super(context,
      Constants.NOTIFICATION_ID_RECORDING_STOPPED,
      R.drawable.ic_stat_stop,
      R.string.notif_title_recording_stopped,
      TripComputer.class);

    this.mContext = Optional.of(context);

    // Set up a few basics for the Notification
    this.setPriority(NotificationCompat.PRIORITY_HIGH);
    this.setLargeIcon(R.drawable.large_notification_icon);
    this.setAutoCancel(true);
    this.setLocalOnly(false);
    this.STOPPED_NOTIFICATION_ICON_COLOUR = this.mContext.get().getResources().getColor(R.color.TripComputerStopButton);
    this.setIconColor(STOPPED_NOTIFICATION_ICON_COLOUR);

    // Create new WearableExtender and add actions
    this.getWearableExtender().setBackground(
      getScrollableWearBackground(this.mContext.get(), R.drawable.wear_background));
    this.getWearableExtender().setContentIconGravity(Gravity.END);

    // Register for App Events
    EventManager.getInstance().register(this);
  }

  private void setJourneyDetails(ImmutableJourney journey) {
    JourneyDecorator d = new JourneyDecorator(journey, mContext.get());
    StringBuilder sb = new StringBuilder();
    sb.append(mContext.get().getString(R.string.notif_txt_covered));
    sb.append(StringUtils.SPACE);
    sb.append(d.getTotalDistanceAsString());
    sb.append(StringUtils.SPACE);
    sb.append(mContext.get().getString(R.string.notif_txt_in));
    sb.append(StringUtils.SPACE);
    Chronometer c = new Chronometer(this.mContext.get());
    c.setFormat(this.mContext.get().getString(R.string.format_chronometer_initial));
    d.configureChronometer(c);
    sb.append(c.getText());
    this.setLine1(sb.toString());
  }

  private void setAverageSpeed(ImmutableJourney journey) {
    JourneyDecorator d = new JourneyDecorator(journey, mContext.get());
    StringBuilder sb = new StringBuilder();

    if (!d.getAverageSpeedAsString().equals(StringUtils.EMPTY_STRING)) {
      sb.append(this.mContext.get().getString(R.string.notif_txt_you_averaged));
      sb.append(StringUtils.SPACE);
      sb.append(d.getAverageSpeedAsString());
    }

    this.setLine2(sb.toString());
  }

  public void onEvent(JourneySavedEvent event) {
    this.setTicker(R.string.notif_txt_stopped);
    this.setTime(new Date().getTime());
    this.setJourneyDetails(event.getImmutableJourney());
    this.setAverageSpeed(event.getImmutableJourney());
    this.buildAndSendNotification();
  }

  public void onEvent(JourneyNotSavedEvent event) {
    this.setTicker(R.string.notif_txt_stopped);
    this.setTime(new Date().getTime());
    this.setJourneyDetails(event.getImmutableJourney());
    this.setLine2(mContext.get().getString(R.string.notif_txt_discarded));
    this.buildAndSendNotification();
  }

  public void onEvent(JourneyStartedEvent event) {
    this.cancel();
  }
}
