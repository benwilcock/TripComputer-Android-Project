package com.soagrowers.android.tripcomputer.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.os.Message;
import android.preference.PreferenceManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.soagrowers.android.tripcomputer.Keys;
import com.soagrowers.android.tripcomputer.R;
import com.soagrowers.android.tripcomputer.data.Constants;
import com.soagrowers.android.tripcomputer.events.LocationUpdatedEvent;
import com.soagrowers.android.utils.AndroidUtils;
import com.soagrowers.android.utils.Assert;
import com.soagrowers.android.utils.EventManager;
import com.soagrowers.android.utils.Log;


/**
 * Extends Handler so that it can periodically check if Location Updates have
 * FROZEN. Queue's the check by calling sendEmptyMessageDelayed(). Handles the check
 * (triggered by the OS) in handleMessage();
 * <p/>
 * Implements sharedPreferenceChangeListener so that it can respond to changes in the
 * LOW_POWER_MODE preference in onSharedPreferenceChanged().
 * <p/>
 * Implements LocationListener so that it can take care of Location changes
 * in onLocationChanged()
 * <p/>
 * Created by Ben on 07/02/14.
 */
public class LocationUpdateManager
        //extends
        //Handler
        implements
        LocationListener,
        GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener,
        SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = LocationUpdateManager.class.getSimpleName();

    private static final int UPDATE_INTERVAL_IN_SECONDS = 5;
    private static final long UPDATE_INTERVAL = Constants.MILLISECONDS_PER_SECOND * UPDATE_INTERVAL_IN_SECONDS;
    private static final int FASTEST_INTERVAL_IN_SECONDS = 1;
    private static final long FASTEST_INTERVAL = Constants.MILLISECONDS_PER_SECOND * FASTEST_INTERVAL_IN_SECONDS;
    //private static final long CHECK_DELAY_DURATION = Constants.MILLISECONDS_PER_SECOND * 300;

    private static LocationClient mLocationClient;
    private static SharedPreferences.OnSharedPreferenceChangeListener sharedPreferenceChangeListener;
    private static LocationListener locationChangeListener;

    private final Log log;
    private final Context context;
    private int updateCount = Constants.ZERO_INT;

    /**
     * Used to construct a LocationUpdateManager
     *
     * @param context
     */

    public LocationUpdateManager(Context context) {
        Assert.notNull(context);

        this.context = context;
        this.log = Log.getInstance(context);

        //Setup a shared preferences change listener
        //*** MUST be a class member Variable to prevent garbage collection ***
        sharedPreferenceChangeListener = this;
        locationChangeListener = this;

        //connect to shared prefs...
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
    }

    /**
     * STEP 2. in the process of Registering for Location events
     * Called automatically by the OS/GPS once the class has been added as a Location client.
     * Creates a LocationRequest and configures it according to the preferences LOW_POWER_MODE setting
     * and sets the ideal desired interval between Location updates. It then uses the LocationRequest
     * to request location updates from the LocationClient. Finally sets a reminder to the Handler
     * to check back to see if LocationUpdates started as expected.
     *
     * @param bundle
     */

    @Override
    public void onConnected(Bundle bundle) {

        // Create the LocationRequest...
        LocationRequest mLocationRequest = LocationRequest.create();

        boolean lowPowerMode = AndroidUtils.getInstance(context).isBatterySaverOn();
        // Set the power accuracy level according to the users setting.
        if (lowPowerMode) {
            //This is the LOW Power Mode setting...
            mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        } else {
            //This is the HIGH Power Mode setting...
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        }

        // Set the setMainText interval
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        // Set the fastest setMainText interval
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

        if (this.mLocationClient.isConnected()) {
            log.i(TAG, "LocationClient is connected");
            //register for location updates
            log.d(TAG, "Registering a Request for Location Updates");
            this.mLocationClient.requestLocationUpdates(mLocationRequest, this); //a callback to onLocationChanged() will tell us when our position changes.
            log.i(TAG, "Registered a Request for Location updates (LowPowerMode = " + lowPowerMode + ")");
            log.v(TAG, "Lookout for callbacks to onLocationChanged()");

            // Send the last known good location right now for speed...
            this.onLocationChanged(this.mLocationClient.getLastLocation());

            //Set a reminder to check for updates being received...
            //super.sendEmptyMessageDelayed(this.updateCount, this.CHECK_DELAY_DURATION);
        }
    }

    /**
     * Step 5. Complete the process of disconnecting from Location updates.
     * The LocationClient will now have closed it's Location Updates connection down and no more
     * Location updates will be received.
     */

    @Override
    public void onDisconnected() {
        // Display the connection status
        log.i(TAG, "Location Client has been disconnected from the Location service");
        log.d(TAG, "LocationClient.isConnected() is now returning: " + mLocationClient.isConnected());
    }

    /**
     * STEP 3. in the process of Registering for Location events
     * The OS/GPS will call this public method every time it feels the location has
     * changed. We then tell each registered consumer about the location setMainText.
     *
     * @param newLocation
     */


    @Override
    public void onLocationChanged(Location newLocation) {
        // Ignore nulls - which can happen rarely according to
        // https://developer.android.com/reference/com/google/android/gms/location/LocationClient.html#getLastLocation()
        if (null != newLocation) {
            //advance the update count number...
            this.updateCount++;

            // Publish the LocationChangedEvent
            EventManager.getInstance().post(new LocationUpdatedEvent(newLocation));
        }
    }

    /**
     * Exceptional Step.
     * Called by the OS if the connection to Google PLay Services Location Manager was not successful.
     *
     * @param connectionResult
     */

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
  /*
   * Google Play services can resolve some errors it detects.
   * If the error has a resolution, try sending an Intent to
   * a Google Play services activity that can resolve
   * error.
   */
        if (connectionResult.hasResolution()) {
            log.e(TAG, "A resolvable failure is affecting Google Play Location services!");
        } else {
            log.e(TAG, "GooglePLayServices Connection Failed. Error code: " + connectionResult.getErrorCode());
        }
    }

    /**
     * Listen for changes to the LOW_POWER_MODE Preference.
     * This preference is used to controls the technique used to monitor our geographic Location.
     *
     * @param sharedPreferences
     * @param key
     */

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        //Handle Location Tracking Power Mode changes
        if (key.equals(Keys.LOW_POWER_MODE_KEY)) {
            try {
                //change the power mode (will pick up the setting)
                //if we've connected to Location services...
                if (null != this.mLocationClient && this.mLocationClient.isConnected()) {

                    //unregister from location updates
                    this.disconnectLocationManager();

                    //now reconnect
                    this.startLocationMonitoring();
                }
            } catch (JourneyStartException jse) {
                //If Journey recording can't start...
                log.e(TAG, "PowerMode setting can't be changed (No LocationClient. GooglePlayServices?): " + jse.getMessage(), jse);

            }
        }
        return;
    }

    /**
     * Step 4. Begin the process of disconnecting from Location updates.
     * Asks our LocationClient to remove us from Location Updates and then
     * disconnects the client. The OS will call onDisconnected when this happens.
     */

    public void disconnectLocationManager() {

        //if we're already connected or in the process of connecting
        try {
            if (this.mLocationClient.isConnected() || this.mLocationClient.isConnecting()) {
                //unregister from location updates
                log.d(TAG, "Unregistering from Location Updates");
                mLocationClient.removeLocationUpdates(this);
                mLocationClient.disconnect();
                log.i(TAG, "Unregistered from Location Updates");
                //the OS will now callback to onDisconnected()
            }
        } catch (Exception e) {
            log.e(TAG, "disconnectLocationManager() FAILED: " + e.getMessage(), e);
        }
        return;
    }

    /**
     * STEP 1. in the process of Registering for Location events
     * Begins the process of registering this class for Location updates.
     * Creates a LocationClient and asks it to 'connect' to the Location services.
     * Requires GooglePlayServices to be available.
     *
     * @throws JourneyStartException when GooglePlayServices are not available
     */

    public void startLocationMonitoring() throws JourneyStartException {

        if (AndroidUtils.getInstance(context).areGooglePlayServicesConnected()) {

            //Create a new LocationClient....
            log.d(TAG, "Creating a new Location client");
            this.mLocationClient = new LocationClient(context, this, this);

            //connect the Location Client to Location Services...
            if (!mLocationClient.isConnected() && !mLocationClient.isConnecting()) {

                //Connect to Location Services...
                log.i(TAG, "Asking the LocationClient to Connect...");
                mLocationClient.connect();
                log.v(TAG, "Lookout for OS callback to onConnected([Bundle])");
                // The OS will now callback to onConnected()
                // this tells us we are OK to use the service...
            }
        } else {
            //GooglePlayServices are missing, don't try to get a Location client, it will fail...
            log.v(TAG, "Google Play Services are NOT connected.");
            AndroidUtils.getInstance(context).doLongToast(R.string.error_play_missing);
            JourneyStartException e = new JourneyStartException(GooglePlayServicesUtil.getErrorString(GooglePlayServicesUtil.isGooglePlayServicesAvailable(context)));
            log.e(TAG, "Can't track Position. Google Play Services is missing.", e);
            throw e;
        }
    }

    /**
     * Subclasses of Handler implement this method in order to receive Timer messages.
     * Used to check if Location Updates are FROZEN. If they are frozen, it recycles the
     * LocationClient connection to reconnect to Google Play Services LocationManager.
     *
     * @param msg
     */
    //@Override
    public void handleMessage(Message msg) {
        //super.handleMessage(msg);

        //Check if updates have Frozen
        if (msg.what == this.updateCount) {
            //Looks like they've frozen, but we could have Stopped the Journey...
            try {
                //if we're still connected to Location services...
                if (null != this.mLocationClient && this.mLocationClient.isConnected()) {

                    log.v(TAG, "Location Manager is still connected.");
                    log.w(TAG, "Location updates have FROZEN on updateText: " + msg.what);
                    log.d(TAG, "Recycling our LocationClient connection.");

                    //unregister from location updates
                    this.disconnectLocationManager();

                    //now reconnect (adds another timer on onConnected())
                    this.startLocationMonitoring();
                }
            } catch (Exception e) {
                //If Journey recording can't restart...
                log.e(TAG, "LocationManager's connection can't be recycled", e);
            }
        }
        return;
    }
}
