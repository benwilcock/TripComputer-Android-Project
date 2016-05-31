package com.soagrowers.android.tripcomputer.notifications.actions;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.google.common.base.Optional;

/**
 * Created by Ben on 12/09/2014.
 */
public abstract class AbstractNotificationAction {

  private Optional<Integer> mActionIconResId = Optional.absent();
  private Optional<Integer> mActionTitleResId = Optional.absent();
  private Optional<String> mActionTitleText = Optional.absent();
  private Optional<PendingIntent> mIntent = Optional.absent();
  private Optional<String> mIntentActionString = Optional.absent();
  private Optional<NotificationCompat.Action> mNotificationAction = Optional.absent();

  public AbstractNotificationAction(int actionIconResId, int titleResId, String intentActionKey) {
    mActionIconResId = Optional.of(actionIconResId);
    mActionTitleResId = Optional.of(titleResId);
    mIntentActionString = Optional.of(intentActionKey);
  }

  private static final PendingIntent getPendingIntentForAction(Context context, String intentAction) {
    Intent intent = new Intent();
    intent.setAction(intentAction);
    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    return pendingIntent;
  }

  public NotificationCompat.Action getAction(Context context) {

    if(!mNotificationAction.isPresent()){

      if(!mIntent.isPresent()) {
        mIntent = Optional.of(getPendingIntentForAction(context, mIntentActionString.get()));
      }

      mActionTitleText = Optional.of(context.getString(mActionTitleResId.get()));
      NotificationCompat.Action action = new NotificationCompat.Action.Builder(mActionIconResId.get(), mActionTitleText.get(), mIntent.get()).build();
      mNotificationAction = Optional.of(action);
    }

    return mNotificationAction.get();
  }
}
