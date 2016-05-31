package com.soagrowers.android.tripcomputer.notifications.actions;

import android.content.Context;

import com.soagrowers.android.tripcomputer.R;
import com.soagrowers.android.tripcomputer.data.Constants;

/**
 * Created by Ben on 12/09/2014.
 */
public class WearableToggleAutoAction extends AbstractNotificationAction {

  public static final int INTENT_ICON = R.drawable.ic_action_settings;
  public static final int INTENT_TITLE = R.string.label_notification_button_toggle_auto;
  public static final String INTENT_ACTION = Constants.WEAR_NOTIFICATION_TOGGLE_AUTO_ACTION;

  public WearableToggleAutoAction() {
    super(INTENT_ICON, INTENT_TITLE, INTENT_ACTION);
  }
}
