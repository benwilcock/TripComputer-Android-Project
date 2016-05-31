package com.soagrowers.android.tripcomputer.services;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.ActivityRecognitionClient;
import com.soagrowers.android.tripcomputer.R;
import com.soagrowers.android.tripcomputer.data.Constants;
import com.soagrowers.android.utils.AndroidUtils;
import com.soagrowers.android.utils.Log;


/**
 * Created by Ben on 10/04/2014.
 */
public class ActivityUpdateManager implements
        ServiceManager,
        GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener {

    private static final String TAG = ActivityUpdateManager.class.getSimpleName();
    private static final long UPDATE_INTERVAL = Constants.MILLISECONDS_PER_SECOND * Constants.ACTIVITY_UPDATE_INTERVAL_IN_SECONDS;

    private static Log log;
    private static Context mContext;
    private static ActivityUpdateManager instance;

    private ActivityRecognitionClient mActivityRecognitionClient = null;
    private boolean isRunning = false;

    private ActivityUpdateManager(Context context) {
        mContext = context;
        log = Log.getInstance(mContext);
        log.i(TAG, "ActivityUpdateManager STARTED");
    }

    public static ActivityUpdateManager getInstance(Context theContext) {
        if (null == instance) {
            instance = new ActivityUpdateManager(theContext);
        } else {
            mContext = theContext;
        }

        return instance;
    }

    /**
     * STEP 1. in the process of Registering for Location events
     * Begins the process of registering this class for Location updates.
     * Creates a LocationClient and asks it to 'connect' to the Location services.
     * Requires GooglePlayServices to be available.
     *
     * @throws JourneyStartException when GooglePlayServices are not available
     */

    @Override
    public void connect() throws ServiceConnectionException {

        if (this.isRunning() == false) {
            if (AndroidUtils.getInstance(mContext).areGooglePlayServicesConnected()) {

                //Create a new ActivityRecognitionClient....
                log.d(TAG, "Creating a new ActivityRecognitionClient");
                mActivityRecognitionClient = this.getActivityRecognitionClient();

                if (!mActivityRecognitionClient.isConnecting() && !mActivityRecognitionClient.isConnected()) {
                    // Request a connection
                    log.i(TAG, "Requesting a connection to GooglePlayServices' activity-recognition-service");
                    this.mActivityRecognitionClient.connect();
                } else {
                    log.w(TAG, "Connection already in progress. Ignoring connection request.");
                }
            } else {
                //GooglePlayServices are missing, don't try to get an Activity client, it will fail...
                log.v(TAG, "Google Play Services are NOT connected.");
                AndroidUtils.getInstance(mContext).doLongToast(R.string.error_play_missing);
                ServiceConnectionException e = new ServiceConnectionException(GooglePlayServicesUtil.getErrorString(GooglePlayServicesUtil.isGooglePlayServicesAvailable(mContext)));
                log.e(TAG, "Activity Recognition can't start without Google Play Services. Please fix this issue first.", e);
                throw e;
            }

            // The OS will now callback to onConnected()
            // this tells us we are OK to use the service...
        }
    }


    /**
     * STEP 2.
     * In the process of Registering for Activity events.
     * Requests updates on the user's activity based on the setMainText interval and
     * the PendingIntent given will be used to receive callbacks.
     *
     * @param bundle
     */

    @Override
    public void onConnected(Bundle bundle) {

        if (mActivityRecognitionClient.isConnected()) {

      /*
      * Request activity recognition updates using the preset
      * detection interval and PendingIntent. This call is
      * synchronous.
      */

            //log.d(TAG, "Requesting Activity updates.");
            mActivityRecognitionClient.requestActivityUpdates(UPDATE_INTERVAL, this.getRequestPendingIntent());

            //Set a timer to check for updates being received...
            //super.sendEmptyMessageDelayed(mUpdateCount, CHECK_DELAY_DURATION);

            //remember what just happened
            this.isRunning = true;
        }

        log.i(TAG, "User Activity Monitoring has Started.");
    }

    /**
     * Step 4.
     * Begin the process of disconnecting from Activity updates.
     * Asks our Client to remove us from Activity Updates, cancel the PendingIntent and then
     * disconnect the client.
     * The OS will call onDisconnected when this has happened.
     */

    @Override
    public void disconnect() {

        if (this.isRunning()) {
            log.i(TAG, "Stopping Activity Recognition.");

            // Cancel the PendingIntent.
            // This stops Intents from arriving at the IntentService, even if the
            // request to disconnect fails.
            getRequestPendingIntent().cancel();

            // Attempt to unregister from updates and disconnect
            if (mActivityRecognitionClient.isConnected()) {
      /*
      * Request an end to activity updates, un-registering as a receiver
      * and cancelling the pending intent.
      */
                log.i(TAG, "Still Connected. Un-registering from Activity Updates.");

                // Remove ourselves from future Activity updates
                mActivityRecognitionClient.removeActivityUpdates(getRequestPendingIntent());

                // Remember what just happened
                this.isRunning = false;

                // And also disconnect the client
                mActivityRecognitionClient.disconnect();
                mActivityRecognitionClient = null;
            } else {
                log.w(TAG, "No Connection available. Ignoring disconnect request.");
            }
        }
    }


    /**
     * Step 5.
     * Complete the process of disconnecting from Activity updates.
     * The ActivityRecognitionClient will now have closed its connection and no more
     * Activity updates will be received.
     */

    @Override
    public void onDisconnected() {

        isRunning = false;

        // Delete the client
        mActivityRecognitionClient = null;

        // In debug mode, log the disconnection
        log.d(TAG, "Disconnected. The ActivityRecognitionClient has been disconnected");
    }


    /**
     * Exceptional Step.
     * Called by the OS if the connection to Google PLay Services Location Manager was not successful.
     *
     * @param connectionResult
     */

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        log.i(TAG, "Connection failed.");

    /*
    * Google Play services can resolve some errors it detects.
    * If the error has a resolution, try sending an Intent to
    * a Google Play services activity that can resolve
    * error.
    */
        if (connectionResult.hasResolution()) {
            log.e(TAG, "A resolvable failure is affecting Google Play Location services!");
        } else {
            log.e(TAG, "GooglePLayServices ActivityRecognitionClient Connection Failed. Error code: " + connectionResult.getErrorCode());
        }

        isRunning = false;
    }


    private boolean isRunning() {
        return isRunning;
    }

    /**
     * Called by Step 1.
     * Get a PendingIntent to send with the request to get activity recognition updates. Location
     * Services issues the Intent inside this PendingIntent whenever a activity recognition setMainText
     * occurs.
     *
     * @return A PendingIntent for the IntentService that handles activity recognition updates.
     */

    private PendingIntent getRequestPendingIntent() {

        // Create an Intent pointing to the IntentService
        Intent intent = new Intent(mContext, ActivityEventService.class);

    /*
     * Return a PendingIntent to start the IntentService.
     * Always create a PendingIntent sent to Location Services
     * with FLAG_UPDATE_CURRENT, so that sending the PendingIntent
     * again updates the original. Otherwise, Location Services
     * can't match the PendingIntent to requests made with it.
     */
        PendingIntent mActivityRecognitionPendingIntent = PendingIntent.getService(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        log.d(TAG, "PendingIntent for Activity updates: " + mActivityRecognitionClient.toString());
        return mActivityRecognitionPendingIntent;
    }

    /**
     * Called by STEP 1.
     *
     * @return
     */

    private ActivityRecognitionClient getActivityRecognitionClient() {

        if (null == this.mActivityRecognitionClient) {
            mActivityRecognitionClient = new ActivityRecognitionClient(mContext, this, this);
        }

        return mActivityRecognitionClient;
    }
}
