package com.soagrowers.android.tripcomputer.notifications.actions;

import android.content.Context;

import com.soagrowers.android.tripcomputer.R;
import com.soagrowers.android.tripcomputer.data.Constants;

/**
 * Created by Ben on 12/09/2014.
 */
public class WearableToggleBatterySaverAction extends AbstractNotificationAction {

  public static final int INTENT_ICON = R.drawable.ic_stat_battery;
  public static final int INTENT_TITLE =  R.string.label_notification_button_toggle_saver;
  public static final String INTENT_ACTION = Constants.WEAR_NOTIFICATION_TOGGLE_BATTERY_SAVER_ACTION;

  public WearableToggleBatterySaverAction() {
    super(INTENT_ICON, INTENT_TITLE, INTENT_ACTION);
  }
}
