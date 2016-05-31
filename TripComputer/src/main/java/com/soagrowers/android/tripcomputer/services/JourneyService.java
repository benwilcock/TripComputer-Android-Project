package com.soagrowers.android.tripcomputer.services;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.preference.PreferenceManager;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.common.base.Optional;
import com.soagrowers.android.tripcomputer.Keys;
import com.soagrowers.android.tripcomputer.TripComputerApplication;
import com.soagrowers.android.tripcomputer.controllers.JourneyController;
import com.soagrowers.android.tripcomputer.converters.AbstractToStringStrategy;
import com.soagrowers.android.tripcomputer.converters.DetectedActivityTypeToToStringStrategy;
import com.soagrowers.android.tripcomputer.data.Constants;
import com.soagrowers.android.tripcomputer.events.JourneyAutoStartEvent;
import com.soagrowers.android.tripcomputer.events.JourneyAutoStopEvent;
import com.soagrowers.android.tripcomputer.events.JourneyNotSavedEvent;
import com.soagrowers.android.tripcomputer.events.JourneySavedEvent;
import com.soagrowers.android.tripcomputer.events.JourneyStartButtonEvent;
import com.soagrowers.android.tripcomputer.events.JourneyStatusEvent;
import com.soagrowers.android.tripcomputer.events.JourneyStopButtonEvent;
import com.soagrowers.android.tripcomputer.notifications.RecordingStartedNotification;
import com.soagrowers.android.tripcomputer.notifications.RecordingStoppedNotification;
import com.soagrowers.android.tripcomputer.notifications.ServiceStatusNotification;
import com.soagrowers.android.utils.AndroidUtils;
import com.soagrowers.android.utils.Assert;
import com.soagrowers.android.utils.EventManager;
import com.soagrowers.android.utils.Log;
import com.soagrowers.android.utils.StringUtils;

import java.util.Map;

import de.greenrobot.event.EventBus;


/**
 * Created by Ben on 03/09/13.
 */
public class JourneyService extends Service implements
  SharedPreferences.OnSharedPreferenceChangeListener {

  private static final String TAG = JourneyService.class.getSimpleName();

  //Static members...
  private static boolean serviceIsRunning = false;

  //Helper classes
  private Log log = null;
  private AndroidUtils h = null;

  private SharedPreferences.OnSharedPreferenceChangeListener sharedPreferenceChangeListener;
  //private NotificationHelper notificationHelper = null;

  private ServiceStatusNotification serviceStatusNotification = null;
  private RecordingStartedNotification recordingStartedNotification = null;
  private RecordingStoppedNotification recordingStoppedNotification = null;
  private LocationUpdateManager locationUpdateManager;
  private IBinder journeyServiceBinder;
  private boolean isRunning = false;
  private AbstractToStringStrategy activityToToStringStrategy;
  private Optional<EventBus> bus = Optional.absent();

  /**
   * Creates the Service.
   */
  public JourneyService() {
    //super();
    this.journeyServiceBinder = new JourneyServiceBinder();
    this.sharedPreferenceChangeListener = this;
  }

  /**
   * The system calls this method when the service is first created, to perform one-time
   * setup procedures (before it calls either onStartCommand() or onBind()). If the service
   * is already running, this method is not called.
   */

  @Override
  public void onCreate() {

    //Call the superclass first
    super.onCreate();

    //Set-up the Helper classes
    log = Log.getInstance(getApplicationContext());
    h = AndroidUtils.getInstance(getApplicationContext());

    activityToToStringStrategy = new DetectedActivityTypeToToStringStrategy(getApplicationContext());

    //Setup a shared preferences change listener
    //*** MUST be a class member Variable to prevent garbage collection ***
    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
    prefs.registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
  }

  /**
   * Used by calls to startService() to startJourney the service in the background.
   * This method then promotes itself to the foreground so that it cannot be killed.
   *
   * @param intent
   * @param flags
   * @param startId
   * @return
   */

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    h = AndroidUtils.getInstance(getApplicationContext());

    //call the super class first...
    super.onStartCommand(intent, flags, startId);

    // Register with the EventBus
    bus = Optional.of(EventManager.getInstance());
    if (bus.isPresent() && !bus.get().isRegistered(this)) {
      bus.get().register(this);
    }

    // Start the JourneyController
    JourneyController.getInstance(getApplicationContext());

    try {
      // If Auto Start and Stop is required, start the activity monitoring NOW.
      if (h.isAutoStartStopEnabled()) {
        startUserActivityMonitoring();
      }
    } catch (ServiceConnectionException sce) {
      //silently don't bother...
      log.e(TAG, "Unable to start the ActivityUpdateManager", sce);
    }

    //If this is the first time we've started (i.e not returning)...
    if (this.isServiceRunning() == false) {

      this.serviceStatusNotification = new ServiceStatusNotification(getApplicationContext());
      this.recordingStartedNotification = new RecordingStartedNotification(getApplicationContext());
      this.recordingStoppedNotification = new RecordingStoppedNotification(getApplicationContext());

      // Start the service as a foreground (persistent) service
      log.d(TAG, "Starting as a Foreground service...");
      super.startForeground(Constants.NOTIFICATION_ID_STATUS,
        serviceStatusNotification.getJourneyServiceNotification());
      log.i(TAG, "JourneyService has STARTED in the FOREGROUND");

      //set the service to 'Running'
      this.serviceIsRunning = true;
    }

    //return the startup result
    return START_NOT_STICKY;
  }


  /**
   * Starts activity recognition.
   */

  private void startUserActivityMonitoring() {
    ActivityUpdateManager.getInstance(getApplicationContext()).connect();
    // Events will now be fired when the activity changes
  }

  /**
   * Stops activity recognition
   */

  private void stopUserActivityMonitoring() {
    ActivityUpdateManager.getInstance(getApplicationContext()).disconnect();
  }


  /**
   * used by the User Interface once bound...
   */

  private boolean isServiceRunning() {
    log.v(TAG, "Is Service Running: " + String.valueOf(this.serviceIsRunning));
    return this.serviceIsRunning;
  }

  /**
   * @return
   */

  private JourneyController getJourneyController() {
    return JourneyController.getInstance(getApplicationContext());
  }


  /**
   * Used by events to set-up Journey recording
   *
   * @throws JourneyStartException
   */

  private boolean startJourney() {

    boolean started = false;

    if (!isRunning) {

      // Start User Activity Monitoring (it may already be started, doesn't matter)...
      this.startUserActivityMonitoring();

      try {
        // Start the Location Monitoring
        this.locationUpdateManager = new LocationUpdateManager(getApplicationContext());
        this.locationUpdateManager.startLocationMonitoring();//can throw Exception for GoogPS missing!
      } catch (JourneyStartException e) {
        if (bus.isPresent()) {
          bus.get().post(e);
        }
      }

      // All done.
      started = true;
      log.i(TAG, "Journey recording has STARTED");

    } else {
      started = false;
      log.d(TAG, "IGNORING request to START a Journey. isJourneyRunning: " + isRunning);
    }
    return started;
  }

  /**
   * Used by events to stop monitoring when trips finish.
   */

  private boolean stopJourney() {

    boolean stopped = false;

    if (isRunning) {
      Assert.notNull(this.locationUpdateManager);

      //Remove ourselves from Location updates and disconnect the LocationUpdateManager
      this.locationUpdateManager.disconnectLocationManager();

      // If Auto START and STOP is OFF,
      // STOP activity monitoring NOW
      if (h.isAutoStartStopEnabled() == false) {
        stopUserActivityMonitoring();
      }

      stopped = true;
      log.i(TAG, "Journey recording has STOPPED");
    } else {
      stopped = false;
      log.d(TAG, "IGNORING request to STOP a Journey. isJourneyRunning: " + isRunning);
    }

    return stopped;
  }


  /**
   * Called by the OS when the service is being stopped.
   * The system calls this method when the service is no longer used and is being destroyed.
   * Your service should implement this to clean up any resources such as threads, registered
   * listeners, receivers, etc.
   * <p/>
   * This is the last call the service receives.
   */

  @Override
  public void onDestroy() {
    log.i(TAG, "Stopping the User Activity Monitoring");
    stopUserActivityMonitoring();

    log.i(TAG, "Unregistering from Preferences updates");
    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
    prefs.unregisterOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);

    log.i(TAG, "Disconnecting the Location Update Manager");
    if (null != this.locationUpdateManager) {
      this.locationUpdateManager.disconnectLocationManager();
      this.locationUpdateManager = null;
    }

    log.i(TAG, "Removing all TripComputer Notifications");
    serviceStatusNotification.cancelAll();
    log.d(TAG, "TripComputer Notifications removed");

    log.i(TAG, "Stopping the foreground JourneyService");
    super.stopForeground(true);

    this.serviceIsRunning = false;
    log.i(TAG, "JourneyService has STOPPED");
    super.onDestroy();
  }

  /**
   * Unless you provide binding for your service, you don't need to implement this
   * method, because the default implementation returns null.
   *
   * @see android.app.Service#onBind
   */

  @Override
  public IBinder onBind(Intent intent) {
    log.d(TAG, "JourneyService Binding requested. Intent: " + intent.toString());
    return journeyServiceBinder;
  }

  /**
   * Called when all journeyUpdateListeners have disconnected from a particular interface
   * published by the service.  The default implementation does nothing and
   * returns false.
   *
   * @param intent The Intent that was used to bind to this service,
   *               as given to {@link android.content.Context#bindService
   *               Context.bindService}.  Note that any extras that were included with
   *               the Intent at that point will <em>not</em> be seen here.
   * @return Return true if you would like to have the service's
   * {@link #onRebind} method later called when new journeyUpdateListeners bind to it.
   */

  @Override
  public boolean onUnbind(Intent intent) {
    log.d(TAG, "Service UnBinding requested. Intent: " + intent.toString());
    return super.onUnbind(intent);
  }

  /**
   * Called when new journeyUpdateListeners have connected to the service, after it had
   * previously been notified that all had disconnected in its
   * {@link #onUnbind}.  This will only be called if the implementation
   * of {@link #onUnbind} was overridden to return true.
   *
   * @param intent The Intent that was used to bind to this service,
   *               as given to {@link android.content.Context#bindService
   *               Context.bindService}.  Note that any extras that were included with
   *               the Intent at that point will <em>not</em> be seen here.
   */

  @Override
  public void onRebind(Intent intent) {
    super.onRebind(intent);
  }


  public void onEvent(JourneyStartButtonEvent event) {
    log.i(TAG, "Journey recording request - START MANUAL");
    if (this.startJourney()) {

      // Get an Analytics Event tracker.
      Tracker t = ((TripComputerApplication) getApplication())
        .getTracker(TripComputerApplication.TrackerName.APP_TRACKER);

      // Build and send the Analytics Event.
      t.send(new HitBuilders.EventBuilder()
        .setCategory(Constants.JOURNEY_CATEGORY)
        .setAction(Constants.START_ACTION)
        .build());
    }
  }


  public void onEvent(JourneyStopButtonEvent event) {
    log.i(TAG, "Journey recording request - STOP MANUAL");
    if (this.stopJourney()) {

      // Get an Analytics Event tracker.
      Tracker t = ((TripComputerApplication) getApplication())
        .getTracker(TripComputerApplication.TrackerName.APP_TRACKER);

      // Build and send the Analytics Event.
      t.send(new HitBuilders.EventBuilder()
        .setCategory(Constants.JOURNEY_CATEGORY)
        .setAction(Constants.STOP_ACTION)
        .build());
    }
  }


  public void onEvent(JourneyAutoStartEvent event) {
    log.i(TAG, "Journey recording request - AUTO START");

    if (this.startJourney()) {

      // Get an Analytics Event tracker.
      Tracker t = ((TripComputerApplication) getApplication())
        .getTracker(TripComputerApplication.TrackerName.APP_TRACKER);

      StringBuilder action = new StringBuilder();
      action.append(Constants.START_ACTION);

      Optional<String> activity = activityToToStringStrategy.toString(event.getPhysicalActivity());
      if (activity.isPresent()) {
        action.append(StringUtils.SPACE);
        action.append(StringUtils.bracket(activity.get()));
      }

      // Build and send the Analytics Event.
      t.send(new HitBuilders.EventBuilder()
        .setCategory(Constants.JOURNEY_CATEGORY)
        .setAction(action.toString())
        .build());
    }
  }

  /**
   * Needs Main Thread for TOAST in Controller.SaveJourney
   *
   * @param event
   */

  public void onEventMainThread(JourneyAutoStopEvent event) {
    log.i(TAG, "Journey recording request - AUTO STOP");

    if (this.stopJourney()) {

      // Get an Analytics Event tracker.
      Tracker t = ((TripComputerApplication) getApplication())
        .getTracker(TripComputerApplication.TrackerName.APP_TRACKER);

      StringBuilder action = new StringBuilder();
      action.append(Constants.STOP_ACTION);

      Optional<String> activity = activityToToStringStrategy.toString(event.getPhysicalActivity());
      if (activity.isPresent()) {
        action.append(StringUtils.SPACE);
        action.append(StringUtils.bracket(activity.get()));
      }

      // Build and send the Analytics Event.
      t.send(new HitBuilders.EventBuilder()
        .setCategory(Constants.JOURNEY_CATEGORY)
        .setAction(action.toString())
        .build());
    }
  }


  public void onEvent(JourneySavedEvent event) {
    // Get an Analytics Event tracker.
    Tracker t = ((TripComputerApplication) getApplication())
      .getTracker(TripComputerApplication.TrackerName.APP_TRACKER);

    // Build and send the Analytics Event.
    t.send(new HitBuilders.EventBuilder()
      .setCategory(Constants.JOURNEY_CATEGORY)
      .setAction(Constants.SAVED_ACTION)
      .setLabel(Constants.DISTANCE_MTRS_LABEL)
      .setValue(Float.valueOf(event.getImmutableJourney().getTotalDistance()).longValue())
      .build());
  }


  public void onEvent(JourneyNotSavedEvent event) {
    // Get an Analytics Event tracker.
    Tracker t = ((TripComputerApplication) getApplication())
      .getTracker(TripComputerApplication.TrackerName.APP_TRACKER);

    // Build and send the Analytics Event.
    t.send(new HitBuilders.EventBuilder()
      .setCategory(Constants.JOURNEY_CATEGORY)
      .setAction(Constants.DISCARDED_ACTION)
      .setLabel(Constants.DISTANCE_MTRS_LABEL)
      .setValue(Float.valueOf(event.getImmutableJourney().getTotalDistance()).longValue())
      .build());
  }

  public void onEvent(JourneyStatusEvent event) {
    switch (event.getType()) {

      case START_EVENT:
        isRunning = true;
        break;

      case STOP_EVENT:
        isRunning = false;
        break;
    }
  }

  /**
   * Called when a shared preference is changed, added, or removed. This
   * may be called even if a preference is set to its existing value.
   * <p/>
   * <p>This callback will be run on your main thread.
   *
   * @param sharedPreferences The {@link android.content.SharedPreferences} that received
   *                          the change.
   * @param key               The key of the preference that was changed, added, or
   */

  @Override
  public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

    if (key.equals(Keys.AUTO_STARTSTOP_KEY)) {
      Tracker t = ((TripComputerApplication) getApplication())
        .getTracker(TripComputerApplication.TrackerName.APP_TRACKER);

      // If Auto Start and Stop is required, start the activity monitoring NOW.
      if (h.isAutoStartStopEnabled()) {
        startUserActivityMonitoring();
      }

      Map<String, String> event;
      if(h.isAutoStartStopEnabled()){
        event = new HitBuilders.EventBuilder()
          .setCategory(Constants.SETTINGS_CATEGORY)
          .setAction(Constants.AUTO_ON_ACTION)
          .build();
      } else {
        event = new HitBuilders.EventBuilder()
          .setCategory(Constants.SETTINGS_CATEGORY)
          .setAction(Constants.AUTO_OFF_ACTION)
          .build();
      }

      // Build and send the Analytics Event.
      t.send(event);
    }

    if (key.equals(Keys.LOW_POWER_MODE_KEY)) {
      // Get an Analytics Event tracker.
      Tracker t = ((TripComputerApplication) getApplication())
        .getTracker(TripComputerApplication.TrackerName.APP_TRACKER);

      Map<String, String> event;
      if (h.isBatterySaverOn()) {
        event = new HitBuilders.EventBuilder()
          .setCategory(Constants.SETTINGS_CATEGORY)
          .setAction(Constants.SAVER_ON_ACTION)
          .build();
      } else {
        event = new HitBuilders.EventBuilder()
          .setCategory(Constants.SETTINGS_CATEGORY)
          .setAction(Constants.SAVER_OFF_ACTION)
          .build();
      }

      // Build and send the Analytics Event.
      t.send(event);
    }
  }

  /**
   * This is the binder class. It's used by onCreate(). Calls to onBind return this class.
   */
  public class JourneyServiceBinder extends Binder {
    //add a getService method to return this service

    /**
     * The getService method returns the class type of the main Service.
     *
     * @return
     */

    JourneyService getService() {
      // Return this instance of LocalService so journeyUpdateListeners can call public methods
      return JourneyService.this;
    }
  }
}
