package com.soagrowers.android.tripcomputer.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;
import com.soagrowers.android.utils.Log;


public class ActivityEventService extends Service {

  private static final String TAG = ActivityEventService.class.getSimpleName();
  private static Log log;

  public ActivityEventService() {
  }


  @Override
  public IBinder onBind(Intent intent) {
    throw new UnsupportedOperationException("Not implemented");
  }

  /**
   * Called by the system when the service is first created.  Do not call this method directly.
   */

  @Override
  public void onCreate() {
    super.onCreate();
    log = Log.getInstance(getApplicationContext());
  }

  /**
   * Called by the system every time a client explicitly starts the service by calling
   * {@link android.content.Context#startService}, providing the arguments it supplied and a
   * unique integer token representing the start request.  Do not call this method directly.
   * <p/>
   *
   * @param intent  The Intent supplied to {@link android.content.Context#startService},
   *                as given.  This may be null if the service is being restarted after
   *                its process has gone away, and it had previously returned anything
   *                except {@link #START_STICKY_COMPATIBILITY}.
   * @param flags   Additional data about this start request.  Currently either
   *                0, {@link #START_FLAG_REDELIVERY}, or {@link #START_FLAG_RETRY}.
   * @param startId A unique integer representing this specific request to
   *                start.  Use with {@link #stopSelfResult(int)}.
   * @return The return value indicates what semantics the system should
   * use for the service's current started state.  It may be one of the
   * constants associated with the {@link #START_CONTINUATION_MASK} bits.
   * @see #stopSelfResult(int)
   */

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {

    super.onStartCommand(intent, flags, startId);

    // If the Intent contains an ActivityRecognitionResult...
    if (ActivityRecognitionResult.hasResult(intent)) {
      log.v(TAG, "Processing a new Activity Recognition Alert...");

      // Get the most probable activity
      ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
      DetectedActivity mostProbableActivity = result.getMostProbableActivity();

      // Setup the Event producer
      ActivityEventProducer producer = ActivityEventProducer.getInstance(getApplicationContext());
      producer.setDetectedActivity(mostProbableActivity);

      //Do the work on another thread...
      new Thread(producer).start(); // OS calls run()...
    }

    return START_NOT_STICKY;
  }

  /**
   * Called by the system to notify a Service that it is no longer used and is being removed.  The
   * service should clean up any resources it holds (threads, registered
   * receivers, etc) at this point.  Upon return, there will be no more calls
   * in to this Service object and it is effectively dead.  Do not call this method directly.
   */

  @Override
  public void onDestroy() {
    super.onDestroy();
  }
}
