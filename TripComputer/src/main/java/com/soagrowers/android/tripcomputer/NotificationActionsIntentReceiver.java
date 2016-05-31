package com.soagrowers.android.tripcomputer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.common.base.Optional;
import com.soagrowers.android.tripcomputer.data.Constants;
import com.soagrowers.android.tripcomputer.events.JourneyStopButtonEvent;
import com.soagrowers.android.tripcomputer.notifications.actions.NotificationStopAction;
import com.soagrowers.android.tripcomputer.notifications.actions.NotificationToggleAutoAction;
import com.soagrowers.android.tripcomputer.notifications.actions.NotificationToggleBatterySaverAction;
import com.soagrowers.android.tripcomputer.notifications.actions.WearableStopAction;
import com.soagrowers.android.tripcomputer.notifications.actions.WearableToggleAutoAction;
import com.soagrowers.android.tripcomputer.notifications.actions.WearableToggleBatterySaverAction;
import com.soagrowers.android.utils.AndroidUtils;
import com.soagrowers.android.utils.Assert;
import com.soagrowers.android.utils.EventManager;

import de.greenrobot.event.EventBus;


/**
 * Created by Ben on 11/06/2014.
 */
public class NotificationActionsIntentReceiver extends BroadcastReceiver {

  private static final String TAG = NotificationActionsIntentReceiver.class.getSimpleName();

  private Optional<Context> mContext = Optional.absent();
  private Optional<EventBus> mEventBus = Optional.absent();
  private Optional<AndroidUtils> mAndroidUtils = Optional.absent();
  private Optional<SharedPreferences> mSharedPreferences = Optional.absent();
  private Optional<Tracker> mTracker = Optional.absent();

  protected void setContext(Context context){
    this.mContext = Optional.of(context);
  }

  private Context getContext(){
    if(!this.mContext.isPresent()){
      throw new IllegalStateException("The context has not been set!");
    }

    return mContext.get();
  }

  @Override
  public void onReceive(Context context, Intent intent) {

    Log.d(TAG, "onReceive has fired!");
    Assert.notNull(context);
    Assert.notNull(intent);
    Assert.notNull(intent.getAction());

    setContext(context);

    Optional<String> intentAction = Optional.absent();
    if(null != intent && null != intent.getAction()){
      intentAction = Optional.of(intent.getAction());
    }

    if (intentAction.isPresent()) {

      String action = intentAction.get();

      if (action.equals(NotificationStopAction.INTENT_ACTION)) {
        Log.d(TAG, "Received a NOTIFICATION_STOP_ACTION intent.");
        getEventBus().post(new JourneyStopButtonEvent());
        track(Constants.NOTIFICATION_CATEGORY, Constants.STOP_ACTION);
        return;
      }

      if (action.equals(WearableStopAction.INTENT_ACTION)) {
        Log.d(TAG, "Received a WEAR NOTIFICATION_STOP_ACTION intent.");
        getEventBus().post(new JourneyStopButtonEvent());
        track(Constants.WEAR_CATEGORY, Constants.STOP_ACTION);
        return;
      }

      if (action.equals(NotificationToggleAutoAction.INTENT_ACTION)) {
        Log.d(TAG, "Received a NOTIFICATION_TOGGLE_AUTO_ACTION intent.");
        toggleAuto();
        track(Constants.NOTIFICATION_CATEGORY, Constants.TOGGLE_AUTO_ACTION);
        return;
      }

      if (action.equals(WearableToggleAutoAction.INTENT_ACTION)) {
        Log.d(TAG, "Received a WEAR_NOTIFICATION_TOGGLE_AUTO_ACTION intent.");
        toggleAuto();
        track(Constants.WEAR_CATEGORY, Constants.TOGGLE_AUTO_ACTION);
        return;
      }

      if (action.equals(NotificationToggleBatterySaverAction.INTENT_ACTION)) {
        Log.d(TAG, "Received a NOTIFICATION_TOGGLE_BATTERY_SAVER_ACTION intent.");
        toggleSaver();
        track(Constants.NOTIFICATION_CATEGORY, Constants.TOGGLE_SAVER_ACTION);
        return;
      }

      if (action.equals(WearableToggleBatterySaverAction.INTENT_ACTION)) {
        Log.d(TAG, "Received a WEAR_NOTIFICATION_TOGGLE_BATTERY_SAVER_ACTION intent.");
        toggleSaver();
        track(Constants.WEAR_CATEGORY, Constants.TOGGLE_SAVER_ACTION);
        return;
      }
    }
  }

  private EventBus getEventBus() {
    if (!this.mEventBus.isPresent()) {
      setEventBus(EventManager.getInstance());
    }

    return mEventBus.get();
  }

  protected void setEventBus(EventBus bus) {
    this.mEventBus = Optional.of(bus);
  }

  private SharedPreferences getSharedPreferences() {
    if (!this.mSharedPreferences.isPresent()) {
      mSharedPreferences = Optional.of(PreferenceManager.getDefaultSharedPreferences(mContext.get()));
    }

    return mSharedPreferences.get();
  }

  protected void setSharedPreferences(SharedPreferences prefs) {
    this.mSharedPreferences = Optional.of(prefs);
  }

  private AndroidUtils getAndroidUtils() {
    if (!this.mAndroidUtils.isPresent()) {
      mAndroidUtils = Optional.of(AndroidUtils.getInstance(getContext()));
    }

    return mAndroidUtils.get();
  }

  protected void setTracker(Tracker t){
    this.mTracker = Optional.of(t);
  }

  private Tracker getTracker(){
    if(!mTracker.isPresent()){
      Tracker t = TripComputerApplication.getDefaultTracker(getContext());
      setTracker(t);
    }

    return mTracker.get();
  }

  protected void setAndroidUtils(AndroidUtils utils) {
    this.mAndroidUtils = Optional.of(utils);
  }

  private void toggleSaver() {

    AndroidUtils utils = getAndroidUtils();
    SharedPreferences prefs = getSharedPreferences();
    SharedPreferences.Editor editor = prefs.edit();

    if (utils.isBatterySaverOn()) {
      editor.putBoolean(Keys.LOW_POWER_MODE_KEY, false);
      editor.apply();
    } else {
      editor.putBoolean(Keys.LOW_POWER_MODE_KEY, true);
      editor.apply();
    }
  }

  private void toggleAuto() {

    AndroidUtils utils = getAndroidUtils();
    SharedPreferences prefs = getSharedPreferences();

    if (utils.isAutoStartStopEnabled()) {
      SharedPreferences.Editor editor = prefs.edit();
      editor.putBoolean(Keys.AUTO_STARTSTOP_KEY, false);
      editor.apply();
    } else {
      SharedPreferences.Editor editor = prefs.edit();
      editor.putBoolean(Keys.AUTO_STARTSTOP_KEY, true);
      editor.apply();
    }
  }

  private void track(String category, String action){
    // Build and send the Analytics Event.
    getTracker().send(new HitBuilders.EventBuilder()
      .setCategory(category)
      .setAction(action.toString())
      .build());
  }

  @Override
  protected void finalize() throws Throwable {
    if(mEventBus.isPresent()){
      if(mEventBus.get().isRegistered(this)){
        mEventBus.get().unregister(this);
      }
    }

    super.finalize();
  }
}
