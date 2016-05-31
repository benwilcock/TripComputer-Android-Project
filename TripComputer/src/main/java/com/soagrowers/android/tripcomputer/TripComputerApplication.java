package com.soagrowers.android.tripcomputer;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.soagrowers.android.utils.Assert;

import java.util.HashMap;


/**
 * Created by Ben on 02/04/2014.
 */
public class TripComputerApplication extends Application {

  //Logging TAG
  private static final String TAG = TripComputerApplication.class.getSimpleName();
  private static HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();

  public TripComputerApplication() {

    super();
  }

  public static Tracker getDefaultTracker(Context context) {
    Assert.notNull(context);

    Tracker t;

    if (mTrackers.containsKey(TrackerName.APP_TRACKER)) {
      t = mTrackers.get(TrackerName.APP_TRACKER);
    } else {
      GoogleAnalytics analytics = GoogleAnalytics.getInstance(context);
      Log.v(TAG, "Creating the APP TRACKER instance.");
      t = analytics.newTracker(R.xml.app_tracker);
      mTrackers.put(TrackerName.APP_TRACKER, t);
    }

    return t;
  }

  public synchronized Tracker getTracker(TrackerName trackerId) {

    // If we've NOT instantiated it before, instantiate it...
    if (!mTrackers.containsKey(trackerId)) {

      GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
      Tracker t;

      switch (trackerId) {

        case APP_TRACKER:
          Log.v(TAG, "Creating the APP TRACKER instance.");
          t = analytics.newTracker(R.xml.app_tracker);
          mTrackers.put(trackerId, t);
          break;

        default:
          Log.e(TAG, "Unknown Analytics TrackerId: "+trackerId);
          throw new IllegalArgumentException("Unknown Analytics TrackerId: "+trackerId);
      }
    }

    // Now get and return it...
    return mTrackers.get(trackerId);
  }

  public enum TrackerName {
    APP_TRACKER, // Tracker used only in this app.
    //GLOBAL_TRACKER, // Tracker used by all the apps from a company. eg: roll-up tracking.
    //ECOMMERCE_TRACKER, // Tracker used by all ecommerce transactions from a company.
  }
}
