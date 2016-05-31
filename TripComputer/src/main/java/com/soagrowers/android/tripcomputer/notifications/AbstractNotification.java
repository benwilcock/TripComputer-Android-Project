package com.soagrowers.android.tripcomputer.notifications;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.format.DateFormat;

import com.google.common.base.Optional;
import com.soagrowers.android.tripcomputer.R;
import com.soagrowers.android.utils.Assert;
import com.soagrowers.android.utils.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Created by Ben on 28/01/14.
 */
public abstract class AbstractNotification {

  private static String TAG = AbstractNotification.class.getCanonicalName();
  protected static int TC_NOTIFICATION_COLOUR;
  private Optional<NotificationCompat.Builder> mNotificationBuilder = Optional.absent();
  private Optional<NotificationManagerCompat> mNotificationManager = Optional.absent();

  // Notification data.
  private Optional<Context> mContext = Optional.absent();
  private Optional<Integer> mNotificationId = Optional.absent();
  private Optional<Integer> mNotificationSmallIconResId = Optional.absent();
  private Optional<Integer> mNotificationTitleTextResId = Optional.absent();
  private Optional<Class> mNotificationIntentTargetClass = Optional.absent();
  private Optional<PendingIntent> mNotificationPendingIntent = Optional.absent();
  private Optional<Long> mNotificationWhen = Optional.absent();
  private Optional<String> mNotificationContentText = Optional.absent();
  private Optional<String> mNotificationSubText = Optional.absent();
  private Optional<Integer> mNotificationPriority = Optional.absent();
  private Optional<String> mNotificationTickerText = Optional.absent();
  private Optional<Integer> mNotificationLargeIconResId = Optional.absent();
  private Optional<Bitmap> mNotificationLargeIcon = Optional.absent();
  private Optional<Boolean> mNotificationAutoCancel = Optional.absent();
  private Optional<Uri> mNotificationSoundUri = Optional.absent();
  private Optional<Integer> mNotificationIconColor = Optional.of(TC_NOTIFICATION_COLOUR);
  private Optional<NotificationCompat.WearableExtender>
    mNotificationWearableExtender = Optional.absent();
  private List<NotificationCompat.Action> mActions = new ArrayList<NotificationCompat.Action>();
  private boolean isSoundRequired = false;
  private boolean isLocalOnly = true;


  /**
   * Used to privately construct the helper using the setting required for the App's unique
   * notifications.
   */

  protected AbstractNotification(Context context, int notificationId,
                                 int notificationSmallIconResId, int notificationTitleTextResId,
                                 Class notificationIntentTargetClass) {

    this.mContext = Optional.of(context);
    this.mNotificationId = Optional.of(notificationId);
    this.mNotificationSmallIconResId = Optional.of(notificationSmallIconResId);
    this.mNotificationTitleTextResId = Optional.of(notificationTitleTextResId);
    this.mNotificationIntentTargetClass = Optional.of(notificationIntentTargetClass);
    this.mNotificationPendingIntent = Optional.of(getPendingIntentForApplication(this.mContext.get(), this.mNotificationIntentTargetClass.get()));
    this.mNotificationWhen = Optional.of(new Date().getTime());
    this.mNotificationSoundUri = Optional.of(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
    this.TC_NOTIFICATION_COLOUR = this.mContext.get().getResources().getColor(R.color.TripComputerDayColorPrimary);
  }

  protected static Bitmap getLargeNotificationIcon(Context context, int id) {
    int mLargeIconWidth, mLargeIconHeight;
    Drawable mLargeIcon = context.getResources().getDrawable(id);
    mLargeIconHeight =
      (int) context.getResources().getDimension(android.R.dimen.notification_large_icon_height);
    mLargeIconWidth =
      (int) context.getResources().getDimension(android.R.dimen.notification_large_icon_width);
    Bitmap b = Bitmap.createBitmap(mLargeIconWidth, mLargeIconHeight, Bitmap.Config.ARGB_8888);
    Canvas c = new Canvas(b);
    mLargeIcon.setBounds(0, 0, mLargeIconWidth, mLargeIconHeight);
    mLargeIcon.draw(c);
    return b;
  }

  protected static Bitmap getScrollableWearBackground(Context context, int id) {
    int mLargeIconWidth, mLargeIconHeight;
    Drawable mLargeIcon = context.getResources().getDrawable(id);
    mLargeIconHeight = 400;
    mLargeIconWidth = 640;
    Bitmap b = Bitmap.createBitmap(mLargeIconWidth, mLargeIconHeight, Bitmap.Config.ARGB_8888);
    Canvas c = new Canvas(b);
    mLargeIcon.setBounds(0, 0, mLargeIconWidth, mLargeIconHeight);
    mLargeIcon.draw(c);
    return b;
  }

  /**
   * Creates a Pending intent that returns us to the App
   */

  private static PendingIntent getPendingIntentForApplication(Context context, Class target) {
    //where to go from the Notification...
    Intent notificationIntent = new Intent(context, target);
    notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

    // The stack builder object will contain an artificial back stack for the
    // started Activity. This ensures that navigating backward from the Activity leads out of
    // your application to the Home screen.
    TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
    // Adds the back stack for the Intent (but not the Intent itself)
    stackBuilder.addParentStack(target);
    // Adds the Intent that starts the Activity to the top of the stack
    stackBuilder.addNextIntent(notificationIntent);

    return stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
  }

  protected NotificationManagerCompat getNotificationManager() {

    if (!this.mNotificationManager.isPresent()) {
      // If we haven't injected one, build it
      setNotificationManager(NotificationManagerCompat.from(mContext.get()));
    }

    return mNotificationManager.get();
  }

  protected void setNotificationManager(NotificationManagerCompat mgr) {
    this.mNotificationManager = Optional.of(mgr);
  }

  protected NotificationCompat.Builder getNotificationBuilder() {
    if (!this.mNotificationBuilder.isPresent()) {
      // If we haven't injected one, build it.
      setNotificationBuilder(new NotificationCompat.Builder(mContext.get()));
    }

    return mNotificationBuilder.get();
  }

  protected void setNotificationBuilder(NotificationCompat.Builder builder) {
    this.mNotificationBuilder = Optional.of(builder);
  }

  /**
   * Used every time a Notification is required to be built.
   */
  protected Notification getNotification() {

    //Build (but don't send the notification

    NotificationCompat.Builder builder = getNotificationBuilder();

    if (mNotificationTitleTextResId.isPresent()) {
      builder.setContentTitle(mContext.get().getText(mNotificationTitleTextResId.get()));
    }

    if (mNotificationContentText.isPresent()) {
      builder.setContentText(this.mNotificationContentText.get());
    }

    if (mNotificationSubText.isPresent()) {
      builder.setSubText(this.mNotificationSubText.get());
    }

    if (mNotificationSmallIconResId.isPresent()) {
      builder.setSmallIcon(mNotificationSmallIconResId.get());
    }
    if (mNotificationPendingIntent.isPresent()) {
      builder.setContentIntent(this.mNotificationPendingIntent.get());
    }
    if (mNotificationWhen.isPresent()) {
      builder.setWhen(this.mNotificationWhen.get());
    }

    if (mNotificationPriority.isPresent()) {
      builder.setPriority(this.mNotificationPriority.get());
    }

    if (mNotificationTickerText.isPresent()) {
      builder.setTicker(this.mNotificationTickerText.get());
    }

    if (this.mNotificationLargeIcon.isPresent()) {
      builder.setLargeIcon(this.mNotificationLargeIcon.get());
    }

    if (this.mNotificationAutoCancel.isPresent()) {
      builder.setAutoCancel(this.mNotificationAutoCancel.get());
    }

    if (mNotificationWearableExtender.isPresent()) {
      builder.extend(this.mNotificationWearableExtender.get());
    }

    if (isSoundRequired) {
      builder.setSound(mNotificationSoundUri.get());
    }

    if(mNotificationIconColor.isPresent()){
      builder.setColor(mNotificationIconColor.get());
    }

    // Define whether notifications should be bridged to Wear...
    builder.setLocalOnly(isLocalOnly);

    // Make all our notifications private
    builder.setVisibility(Notification.VISIBILITY_PRIVATE);

    for (NotificationCompat.Action action : mActions) {
      builder.addAction(action);
    }

    // Now build the notification...
    Notification notification = builder.build();

    // Destroy the builder (or duplicate Actions get re-added)
    this.mNotificationBuilder = Optional.absent();

    // Return the notification
    return notification;
  }

  protected void buildAndSendNotification() {
    Notification notification = getNotification();
    getNotificationManager().notify(mNotificationId.get(), notification);
  }

  protected void setSmallIcon(int smallIconResId) {
    this.mNotificationSmallIconResId = Optional.of(smallIconResId);
  }

  protected void setLine1(String message) {
    this.mNotificationContentText = Optional.of(message);
  }

  protected void setLine2(String message) {
    this.mNotificationSubText = Optional.of(message);
  }

  protected void setTime(long time) {
    this.mNotificationWhen = Optional.of(time);
  }

  protected void setPriority(int priority) {
    this.mNotificationPriority = Optional.of(priority);
  }

  protected void setIconColor(int color) {
    this.mNotificationIconColor = Optional.of(color);
  }

  protected void setAutoCancel(boolean shouldCancel) {
    this.mNotificationAutoCancel = Optional.of(shouldCancel);
  }

  protected void setTicker(int tickerTextResId) {
    StringBuilder text = new StringBuilder(mContext.get().getText(tickerTextResId));
    text.append(StringUtils.COLON);
    text.append(StringUtils.SPACE);
    text.append(DateFormat.getTimeFormat(mContext.get()).format(new Date()));
    this.mNotificationTickerText = Optional.of(text.toString());
  }

  protected Bitmap getLargeIcon() {
    return this.mNotificationLargeIcon.get();
  }

  protected void setLargeIcon(int drawableResId) {
    this.mNotificationLargeIconResId = Optional.of(drawableResId);
    //this.mNotificationLargeIcon = Optional.of(getLargeNotificationIcon(mContext.get(), mNotificationLargeIconResId.get()));
  }

  protected void addAction(NotificationCompat.Action action) {
    Assert.notNull(action);
    mActions.add(action);
  }

  protected List<NotificationCompat.Action> getActions() {
    return mActions;
  }

  protected NotificationCompat.WearableExtender getWearableExtender() {
    if (!this.mNotificationWearableExtender.isPresent()) {
      this.mNotificationWearableExtender = Optional.of(new NotificationCompat.WearableExtender());
    }

    return mNotificationWearableExtender.get();
  }

  protected void setWearableExtender(NotificationCompat.WearableExtender extender) {
    this.mNotificationWearableExtender = Optional.of(extender);
  }

  protected void doDing(boolean ding) {
    this.isSoundRequired = ding;
  }

  protected void setLocalOnly(boolean local) {
    this.isLocalOnly = local;
  }

  public void cancel() {
    getNotificationManager().cancel(mNotificationId.get());
  }

  public void cancelAll() {
    getNotificationManager().cancelAll();
  }

  protected PendingIntent getPendingIntent() {
    return mNotificationPendingIntent.get();
  }

      /*

    Proved unreliable when reusing NotificationId's (required with Notifications that are
    associated with Foreground Services like the Journey Service.

    Proved unreliable on Wear (which counts down not up).



    protected void setNotificationUsesChronometer(boolean trigger) {
        if (mBuilder.isPresent()) {
            mBuilder.get().setUsesChronometer(trigger);
        } else {
            throw new IllegalStateException("Notification Builder is missing");
        }
    }*/
}
