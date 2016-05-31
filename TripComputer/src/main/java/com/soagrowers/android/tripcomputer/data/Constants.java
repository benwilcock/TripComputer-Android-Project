package com.soagrowers.android.tripcomputer.data;

import java.util.Date;

/**
 * Created by Ben on 15/11/13.
 */
public final class Constants {

  // Milliseconds per second
  public static final int MILLISECONDS_PER_SECOND = 1000;
  public static final int SECONDS_PER_MINUTE = 60;
  public static final int ONE_MINUTE = MILLISECONDS_PER_SECOND * SECONDS_PER_MINUTE;
  public static final long PLAY_SERVICE_CHECK_DELAY = ONE_MINUTE;
  public static final int ACTIVITY_STOP_TIME_DELAY = 5 * ONE_MINUTE;
  public static final int MINUTES_PER_HOUR = 60;
  public static final double ZERO_DOUBLE = 0.0d;
  public static final float ZERO_FLOAT = 0.0f;
  public static final int ZERO_INT = 0;
  public static final long ZERO_LONG = 0l;
  public static final Date START_OF_TIME = new Date(ZERO_LONG);
  public static final float NEG_FLOAT = -1.0f;
  public static final int NEG_INT = -1;
  public static final int MILES = 0;
  public static final int KILOMETERS = 1;
  public static final int METERS = 2;
  // Notifications
  public static final int NOTIFICATION_ID_STATUS = 31122013;
  public static final int NOTIFICATION_ID_RECORDING_STARTED = 30092014;
  public static final int NOTIFICATION_ID_RECORDING_STOPPED = 31092014;
  // Rules
  public static final float MIN_TRIP_DISTANCE_IN_METERS = 800.00f;
  // Activity Recognition
  public static final int ACTIVITY_UPDATE_INTERVAL_IN_SECONDS = 90;
  public static final int ACTIVITY_START_TRIGGER_THRESHOLD = 0;
  public static final int ACTIVITY_STOP_TRIGGER_THRESHOLD = 3;
  public static final int ACTIVITY_START_CONFIDENCE_THRESHOLD = 85;
  public static final int ACTIVITY_STOP_CONFIDENCE_THRESHOLD = 85;
  // Version Numbers
  public static final double VERSION_ONE_DOT_ZERO = 1.0;
  public static final double VERSION_ONE_DOT_ONE = 1.1;
  public static final double CURRENT_VERSION = VERSION_ONE_DOT_ONE;
  public static final double VERSION_ONE_DOT_TWO = 1.2;
  public static final double VERSION_TWO_DOT_ZERO = 2.0;
  // Showcase View Triggers
  public static final int SHOWCASE_VIEW_SHOT_SUFFIX_ONE = 1; // Showcase
  public static final int SHOWCASE_VIEW_SHOT_SUFFIX_TWO = 2; // Showcase
  public static final int SHOWCASE_VIEW_SHOT_SUFFIX_THREE = 3; // Showcase


  // Notification Intent Actions (Used in Manifest!)
  public static final String NOTIFICATION_STOP_ACTION = "com.soagrowers.android.tripcomputer.NOTIFICATION_STOP_ACTION";
  public static final String NOTIFICATION_TOGGLE_AUTO_ACTION = "com.soagrowers.android.tripcomputer.NOTIFICATION_TOGGLE_AUTO_ACTION";
  public static final String NOTIFICATION_TOGGLE_BATTERY_SAVER_ACTION = "com.soagrowers.android.tripcomputer.NOTIFICATION_TOGGLE_BATTERY_SAVER_ACTION";
  public static final String WEAR_NOTIFICATION_TOGGLE_BATTERY_SAVER_ACTION = "com.soagrowers.android.tripcomputer.WEAR_NOTIFICATION_TOGGLE_BATTERY_SAVER_ACTION";
  public static final String WEAR_NOTIFICATION_STOP_ACTION = "com.soagrowers.android.tripcomputer.WEAR_NOTIFICATION_STOP_ACTION";
  public static final String WEAR_NOTIFICATION_TOGGLE_AUTO_ACTION = "com.soagrowers.android.tripcomputer.WEAR_NOTIFICATION_TOGGLE_AUTO_ACTION";

  // Reused internal strings
  public static final String NOT_SET = "NOT_SET";

  // Used in Analytics events
  public static final String NOTIFICATION_CATEGORY = "NotificationButtons";
  public static final String WEAR_CATEGORY = "WearButtons";
  public static final String UI_CATEGORY = "UiButtons";
  public static final String AUTOMATION_CATEGORY = "AutomationEvents";
  public static final String JOURNEY_CATEGORY = "JourneyEvents";
  public static final String SETTINGS_CATEGORY = "SettingsEvents";

  public static final String STOP_ACTION = "Stop";
  public static final String START_ACTION = "Start";
  public static final String TOGGLE_AUTO_ACTION = "ToggleAuto";
  public static final String AUTO_ON_ACTION = "AutoOn";
  public static final String AUTO_OFF_ACTION = "AutoOff";
  public static final String TOGGLE_SAVER_ACTION = "ToggleSaver";
  public static final String SAVER_ON_ACTION = "SaverOn";
  public static final String SAVER_OFF_ACTION = "SaverOn";
  public static final String SAVED_ACTION = "Saved";
  public static final String DISCARDED_ACTION = "Discarded";

  public static final String DISTANCE_MTRS_LABEL = "DistanceMtrs";

  // Used with File Output...
  public static final String LOG_FILE_FOLDER_NAME = "Logs";
  public static final String LOG_FILE_NAME_PREFIX = "TripComputerLog";
  public static final String JOURNEY_FILE_FOLDER_NAME = "Journeys";
  public static final String JOURNEY_FILE_NAME_PREFIX = "TripComputerJourney";
}
