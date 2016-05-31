package com.soagrowers.android.tripcomputer.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.location.DetectedActivity;
import com.google.common.base.Optional;
import com.soagrowers.android.tripcomputer.Keys;
import com.soagrowers.android.tripcomputer.TripComputerApplication;
import com.soagrowers.android.tripcomputer.data.Constants;
import com.soagrowers.android.tripcomputer.events.ActivityUpdateEvent;
import com.soagrowers.android.tripcomputer.events.AutoStartTrigger;
import com.soagrowers.android.tripcomputer.events.AutoStopTrigger;
import com.soagrowers.android.tripcomputer.events.JourneyAutoStartEvent;
import com.soagrowers.android.tripcomputer.events.JourneyAutoStopEvent;
import com.soagrowers.android.tripcomputer.events.JourneyStartedEvent;
import com.soagrowers.android.tripcomputer.events.JourneyStoppedEvent;
import com.soagrowers.android.utils.AndroidUtils;
import com.soagrowers.android.utils.EventManager;
import com.soagrowers.android.utils.MovementActivityUtil;

import java.util.Map;

import de.greenrobot.event.EventBus;

/**
 * Created by Ben on 29/09/2014.
 */
public class ActivityEventProducer implements Runnable, SharedPreferences.OnSharedPreferenceChangeListener {

  public static final String ACTIVITY_EVENT_PRODUCER = "ACTIVITY_EVENT_PRODUCER";
  private static Optional<ActivityEventProducer> mInstance = Optional.absent();
  private final ActivityEventProducer sharedPreferenceChangeListener;
  private Optional<Context> mContext = Optional.absent();
  private Optional<AndroidUtils> mAndroidUtils = Optional.absent();
  private boolean autoStartStop = false;
  private boolean isRunning = false;
  private long firstStopEventTime = Constants.ZERO_LONG;
  private Optional<EventBus> mEventBus = Optional.absent();
  private Optional<Tracker> mTracker = Optional.absent();
  private Optional<DetectedActivity> mDetectedActivity = Optional.absent();

  protected ActivityEventProducer(Context context) {
    this.mContext = Optional.of(context);
    this.mAndroidUtils = Optional.of(AndroidUtils.getInstance(mContext.get()));
    this.autoStartStop = this.mAndroidUtils.get().isAutoStartStopEnabled();
    this.mEventBus = Optional.of(EventManager.getInstance());
    this.mEventBus.get().registerSticky(this);

    //Setup a shared preferences change listener
    //*** MUST be a class member Variable to prevent garbage collection ***
    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext.get());
    this.sharedPreferenceChangeListener = this;
    prefs.registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
  }

  /**
   * Required in order to get a Producer instance.
   * @param context
   * @return
   */
  public static ActivityEventProducer getInstance(Context context) {

    if (!mInstance.isPresent()) {
      mInstance = Optional.of(new ActivityEventProducer(context));
    }

    return mInstance.get();
  }

  /**
   * Required in order to setup the Producer correctly.
   * @param currentActivity
   */
  public void setDetectedActivity(DetectedActivity currentActivity) {
    this.mDetectedActivity = Optional.of(currentActivity);
  }

  /**
   * Called by the OS in order to RUN the Producer code offline.
   */
  @Override
  public void run() {

    // Make sure setup has been done correctly
    this.checkSetup();

    // Publish an activity update for other components to use (e.g. UI)...
    this.publishActivityUpdateEvent();

    // Return quick if AUTO is OFF
    if(autoStartStop == false){
      // Auto is OFF
      return;
    }

    // Check for an event Trigger
    switch (MovementActivityUtil.isEventTrigger(getDetectedActivity())) {

      case NO_TRIGGER:
        // Break the STOP cycle - as events are not predictably static
        resetLastStopEventTime();
        break;

      case START_TRIGGER:
        if (isRunning == false) { // Publish StartTrigger event.
          mEventBus.get().post(new AutoStartTrigger(ACTIVITY_EVENT_PRODUCER, getDetectedActivity()));
        }
        break;

      case STOP_TRIGGER:
        if (isRunning == true) { // Publish a StopTrigger
          mEventBus.get().post(new AutoStopTrigger(ACTIVITY_EVENT_PRODUCER, getDetectedActivity()));
        }
        break;
    }

    return;
  }

  /**
   * Used to publish an ActivityUpdateEvent (when isRunning == true).
   */
  private void publishActivityUpdateEvent() {

    if (isRunning() == true) {
      // If running, publish an event so that the UI can update...
      this.mEventBus.get().post(new ActivityUpdateEvent(getDetectedActivity()));
    }
  }

  /**
   * Called when the App starts Journey Recording
   * @param event
   */
  public void onEvent(JourneyStartedEvent event) {
    this.isRunning = true;
    resetLastStopEventTime();
  }

  /**
   * Called when a valid Auto stop trigger is produced
   * @param autoStartTrigger
   */
  public void onEvent(AutoStartTrigger autoStartTrigger) {
    if (isRunning == false) {
      mEventBus.get().post(new JourneyAutoStartEvent(getDetectedActivity()));
      resetLastStopEventTime();
      track(Constants.AUTOMATION_CATEGORY, Constants.START_ACTION);
    }
  }

  /**
   * Called when a valid Auto stop trigger is produced
   * @param autoStopTrigger
   */
  public void onEvent(AutoStopTrigger autoStopTrigger) {
    if (isRunning == true) {
      // Issue a stop event
      long eventTime = autoStopTrigger.getEventTime();

      if (this.firstStopEventTime == Constants.ZERO_LONG) {
        // This is the first stop event we've received
        this.firstStopEventTime = eventTime;
      } else {
        long timeDifference = eventTime - this.firstStopEventTime;
        if (timeDifference > Constants.ACTIVITY_STOP_TIME_DELAY) {
          // This has been consistent
          mEventBus.get().post(new JourneyAutoStopEvent(getDetectedActivity()));
          track(Constants.AUTOMATION_CATEGORY, Constants.STOP_ACTION);
          resetLastStopEventTime();
        }
      }
    }
  }

  /**
   * Called when the App stops Journey Recording
   * @param event
   */
  public void onEvent(JourneyStoppedEvent event) {
    this.isRunning = false;
    resetLastStopEventTime();
  }


  private void track(String category, String action){
    Tracker t = getTracker();
      t.send(getTrackingEvent(category, action));
  }

  private Map<String, String> getTrackingEvent(String category, String action){
    return new HitBuilders.EventBuilder()
      .setCategory(category)
      .setAction(action)
      .build();
  }

  protected void setTracker(Tracker t){
    this.mTracker = Optional.of(t);
  }

  private Tracker getTracker(){
    if(!this.mTracker.isPresent()){
      Tracker t = TripComputerApplication.getDefaultTracker(getContext());
      setTracker(t);
    }

    return mTracker.get();
  }

  /*

  The following are required for TESTING purposes....

   */

  protected Context getContext(){return mContext.get();}
  protected void setContext(Context context) {
    mContext = Optional.of(context);
  }

  protected void setAndroidUtils(AndroidUtils utils) {
    mAndroidUtils = Optional.of(utils);
    this.autoStartStop = mAndroidUtils.get().isAutoStartStopEnabled();
  }

  protected DetectedActivity getDetectedActivity() {
    this.checkSetup();
    return mDetectedActivity.get();
  }

  private void checkSetup() throws IllegalStateException {
    if (!mDetectedActivity.isPresent()) {
      // Fast fail.
      throw new IllegalStateException("The ActivityEventProducer producer has not been setup correctly. It requires a DetectedActivity to be set via setDetectedActivity() before calling run().");
    }

    if (!mEventBus.isPresent()) {
      throw new IllegalStateException("The ActivityEventProducer cant issue events - No EventBus has been set.");
    }
  }

  protected void setEventBus(EventBus eventBus) {
    mEventBus = Optional.of(eventBus);
    mEventBus.get().registerSticky(this);
  }

  public boolean isRunning() {
    return isRunning;
  }

  protected void setIsRunning(boolean running) {
    isRunning = running;
  }

  public long getFirstStopEventTime() {
    return firstStopEventTime;
  }

  protected void setFirstStopEventTime(long time) {
    this.firstStopEventTime = time;
  }

  private void resetLastStopEventTime() {
    this.setFirstStopEventTime(Constants.ZERO_LONG);
  }

  @Override
  public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
    if (key.equals(Keys.AUTO_STARTSTOP_KEY)) {
      this.autoStartStop = this.mAndroidUtils.get().isAutoStartStopEnabled();
    }
  }

  @Override
  protected void finalize() throws Throwable {

    if (mEventBus.isPresent()) {
      mEventBus.get().unregister(this);
    }

    super.finalize();
  }
}
