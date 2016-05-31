package com.soagrowers.android.tripcomputer;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.LocationServices;


public abstract class AbstractGooglePlayServicesActivity extends ActionBarActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = AbstractGooglePlayServicesActivity.class.getSimpleName();

    private static final String KEY_IN_RESOLUTION = "is_in_resolution";

    /**
     * Request code for auto Google Play Services error resolution.
     */
    protected static final int REQUEST_CODE_RESOLUTION = 1;

    /**
     * Google API client.
     */
    private GoogleApiClient mGoogleApiClient;

    /**
     * Determines if the client is in a resolution state, and
     * waiting for resolution intent to return.
     */
    private boolean mIsInResolution = false;

    /**
     * Set when play services is available
     */
    private boolean isPlayServicesAvailable = false;

    /**
     * Set when it's necessary to show a dialogue to the user.
     */
    private static boolean mCanShowPlayDialog = true;


    private int errorCode;

    /**
     * Called when the activity is starting. Restores the activity state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            mIsInResolution = savedInstanceState.getBoolean(KEY_IN_RESOLUTION, false);
            mCanShowPlayDialog = savedInstanceState.getBoolean(Keys.SHOW_PLAY_DIALOG_KEY);
        }

        isPlayServicesAvailable = isGooglePlayServicesAvailable();

        if (!isPlayServicesAvailable && !mIsInResolution) {
            Log.i(TAG, "Google Play Services are UNAVAILABLE.");

            if (!attemptPlayServicesRecovery(errorCode)) {
                Log.e(TAG, "The issue with Google Play Services is NOT RECOVERABLE!");
                this.doToast(R.string.error_play_disconnected);
            }
        }

        return;
    }

    /**
     * Called when the Activity is made visible.
     * A connection to Play Services need to be initiated as
     * soon as the activity is visible. Registers {@code ConnectionCallbacks}
     * and {@code OnConnectionFailedListener} on the
     * activities itself.
     */
    @Override
    protected void onStart() {
        super.onStart();

        if (isGooglePlayServicesAvailable()) {
            if (mGoogleApiClient == null) {
                mGoogleApiClient = new GoogleApiClient.Builder(this)
                        // Optionally, add additional APIs and scopes if required.
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this)
                        .addApi(LocationServices.API)
                        .addApi(ActivityRecognition.API)
                        .build();
            }
            mGoogleApiClient.connect();
        }
    }

    /**
     * Called when activity gets invisible. Connection to Play Services needs to
     * be disconnected as soon as an activity is invisible.
     */
    @Override
    protected void onStop() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * Saves the resolution state.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_IN_RESOLUTION, mIsInResolution);
        outState.putBoolean(Keys.SHOW_PLAY_DIALOG_KEY, this.mCanShowPlayDialog);
    }

    /**
     * Handles Google Play Services resolution callbacks.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_RESOLUTION:
                retryConnecting();
                break;
        }
    }

    private void retryConnecting() {
        mIsInResolution = false;
        if (!mGoogleApiClient.isConnecting()) {
            mGoogleApiClient.connect();
        }
    }

    /**
     * Called when {@code mGoogleApiClient} is connected.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        Log.i(TAG, "GoogleApiClient connected");
        this.isPlayServicesAvailable = true;
        // TODO: Start making API requests.
    }

    /**
     * Called when {@code mGoogleApiClient} connection is suspended.
     */
    @Override
    public void onConnectionSuspended(int cause) {
        Log.i(TAG, "GoogleApiClient connection suspended");
        this.isPlayServicesAvailable = false;
        retryConnecting();
    }

    /**
     * Called when {@code mGoogleApiClient} is trying to connect but failed.
     * Handle {@code result.getResolution()} if there is a resolution
     * available.
     */
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(TAG, "GoogleApiClient connection failed: " + result.toString());

        if (!result.hasResolution()) {
            // Show a localized error dialog.
            GooglePlayServicesUtil.getErrorDialog(
                    result.getErrorCode(), this, 0, new OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            retryConnecting();
                        }
                    }
            ).show();
            return;
        }
        // If there is an existing resolution error being displayed or a resolution
        // activity has started before, do nothing and wait for resolution
        // progress to be completed.
        if (mIsInResolution) {
            return;
        }
        mIsInResolution = true;
        try {
            result.startResolutionForResult(this, REQUEST_CODE_RESOLUTION);
        } catch (SendIntentException e) {
            Log.e(TAG, "Exception while starting resolution activity", e);
            retryConnecting();
        }
    }


    /**
     * Called by onCreate()
     * Check for GooglePlayServices...
     */

    private boolean attemptPlayServicesRecovery(int errorCode) {

        if (GooglePlayServicesUtil.isUserRecoverableError(errorCode) && mCanShowPlayDialog) {

            //get a dialogue from GooglePlayServicesUtil to handle the error
            Log.i(TAG, "Attempting to fix the Google Play Services issue...");
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(errorCode, this, 0);

            //if the Dialog has been created, show the dialogue
            if (dialog != null) {
                dialog.show();
                this.mCanShowPlayDialog = false;
            }

            return true;
        } else {
            // Services not connected.
            return false;
        }
    }

    private boolean isGooglePlayServicesAvailable() {

        // Check that Google Play services is available
        errorCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        switch (errorCode) {
            case ConnectionResult.SUCCESS:
                isPlayServicesAvailable = true;
                Log.d(TAG, "Play services ARE connected");
                break;
            case ConnectionResult.SERVICE_MISSING:
                Log.w(TAG, "Google Play Services are MISSING.");
                isPlayServicesAvailable = false;
                break;
            case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED:
                Log.w(TAG, "Google Play Services requires and UPDATE.");
                isPlayServicesAvailable = false;
                break;
            case ConnectionResult.SERVICE_DISABLED:
                Log.w(TAG, "Google Play Services are DISABLED.");
                isPlayServicesAvailable = false;
                break;
            default:
                Log.e(TAG, "Google Play Services result UNEXPECTED!");
                isPlayServicesAvailable = false;
        }

        return isPlayServicesAvailable;
    }

    private void doToast(int resourceId) {

        //perform a quick toast
        Toast toast = Toast.makeText(this, resourceId, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.show();
    }

    protected boolean isPlayServicesAvailable() {
        return isPlayServicesAvailable && !mIsInResolution;
    }
}
