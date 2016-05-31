package com.soagrowers.android.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.soagrowers.android.tripcomputer.BuildConfig;
import com.soagrowers.android.tripcomputer.Keys;
import com.soagrowers.android.tripcomputer.R;
import com.soagrowers.android.tripcomputer.data.Constants;
import com.soagrowers.android.tripcomputer.data.Journey;


/**
 * Created by Ben on 28/06/13.
 */
public class AndroidUtils {

    //the instance variable for this singleton.
    private static final String TAG = AndroidUtils.class.getSimpleName();
    private static AndroidUtils ourInstance = null;
    private static Context mContext = null;
    private static Activity mActivity = null;
    private static Log mLog = null;
    private boolean isPlayServicesAvailable = false;
    private String mVersionName = "?";
    private int mVersionCode = -1;

    /**
     * The PRIVATE constructor of this Singleton.
     *
     * @param theContext
     */

    private AndroidUtils(Context theContext) {

        if (null == theContext) {
            String message = "AndroidUtils can't initialise properly - Null Context";
            RuntimeException e = new InstantiationException(message);
            android.util.Log.e(TAG, message, e);
        }

        mLog = Log.getInstance(mContext);

        this.mContext = theContext;
        if (theContext instanceof Activity) {
            this.mActivity = (Activity) theContext;
        }
    }

    /**
     * Get's the Context the instance was constructed with
     *
     * @return
     */

    public Context getContext() {

        if (null != this.mContext) {
            //return the context
            return mContext;
        } else {
            InstantiationException e = new InstantiationException("AndroidUtils has not been initialised properly.");
            throw e;
        }
    }

    /**
     * Only use this method in situations where you don't need access to UI
     * widgets (such as when using the resource classes to get Strings etc.).
     * DON'T use it to get access to View like components as the behaviour will
     * be unpredictable.
     */

    public static AndroidUtils getInstance() {
        if (null != ourInstance) {
            return ourInstance;
        } else {
            InstantiationException e = new InstantiationException("AndroidUtils has not been initialised properly.");
            throw e;
        }
    }


    /**
     * Used when theme switching...
     *
     * @param activity
     */

    public static void restartActivity(Activity activity) {
        Assert.notNull(activity);
        activity.finish();
        activity.startActivity(new Intent(activity, activity.getClass()));
    }

    /**
     * Helper method to make Toasting the user easy.
     * Used for sending SHORT Toast's to the user
     *
     * @param resourceId
     */

    public void doShortToast(int resourceId) {
        doToast(resourceId, Toast.LENGTH_SHORT);
    }


    public void doToast(int resourceId, int length) {

        //perform a quick toast
        Toast toast = Toast.makeText(mContext, resourceId, length);
        toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.show();
    }

    /**
     * Helper method to make Toasting the user easy.
     * Used for sending LONG Toast's to the user
     *
     * @param resourceId
     */

    public void doLongToast(int resourceId) {
        doToast(resourceId, Toast.LENGTH_LONG);
    }

    /**
     * @param text
     */

    public void doLongToast(StringBuilder text) {
        Toast toast = Toast.makeText(mContext, text, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.show();
    }


    public void doLongToast(String text) {
        Toast toast = Toast.makeText(mContext, text, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.show();
    }


    public TextView setTextViewText(int resourceId, String value) {
        Assert.isTrue(resourceId > -1);
        Assert.notNull(value);

        //get the TxtView
        TextView txtView = (TextView) this.getActivity().findViewById(resourceId);
        if (null != txtView) {
            txtView.setText(value);
        }
        return txtView;
    }

    /**
     * Returns the parent mActivity
     *
     * @return
     */
    public Activity getActivity() {
        return mActivity;
    }


    public TextView setTextViewVisibility(int resourceId, int visibility) {
        Assert.isTrue(resourceId > -1);
        Assert.isTrue(visibility > -1);

        TextView txtView = (TextView) this.getActivity().findViewById(resourceId);
        if (txtView != null) {
            txtView.setVisibility(visibility);
        }
        return txtView;
    }


    public View findViewById(int resourceId) {
        Assert.isTrue(resourceId > -1);
        View view = this.getActivity().findViewById(resourceId);
        return view;
    }

    /**
     * Removes a button from View
     *
     * @param resourceId
     */

    public void hideButton(int resourceId) {
        //remove button from view
        View button = this.getActivity().findViewById(resourceId);
        button.setEnabled(false);
        button.setVisibility(View.GONE);
        return;
    }

    /**
     * Restore a button in the view
     *
     * @param resourceId
     */

    public void revealButton(int resourceId) {
        //restore the button from view
        View button = this.getActivity().findViewById(resourceId);
        button.setEnabled(true);
        button.setVisibility(View.VISIBLE);
        return;
    }

    /**
     * Removes a button from View
     *
     * @param resourceId
     */
    public void hideLayout(int resourceId) {
        mLog.v(TAG, "hideLayout(resourceId)");
        //remove button from view
        View view = this.getActivity().findViewById(resourceId);
        view.setVisibility(View.GONE);
        return;
    }


    public void revealLayout(int resourceId) {
        //restore the button from view
        View view = this.getActivity().findViewById(resourceId);
        view.setVisibility(View.VISIBLE);
        return;
    }


    public void revealLayoutIfContent(int layoutId, int textViewId) {
        //restore the button from view
        View layout = this.getActivity().findViewById(layoutId);
        TextView txtView = (TextView) this.getActivity().findViewById(textViewId);
        if (null == txtView.getText() || txtView.getText().equals(StringUtils.EMPTY_STRING)) {
            //There is nothing to show...
            layout.setVisibility(View.GONE);
        } else {
            //There is something to show...
            layout.setVisibility(View.VISIBLE);
        }
        return;
    }


    public boolean getBoolean(int resourceId) {
        Resources res = this.getContext().getResources();
        return res.getBoolean(resourceId);
    }


    public float getFraction(int resourceId) {
        Resources res = this.getContext().getResources();
        return res.getFraction(resourceId, 1, 1);
    }

    /**
     * Get the distance units from the shared preferences (where they're held
     * as a String, and return them as an int. In the case where the preference has
     * never been set, it will return a default of the first in the associated array.
     *
     * @return The distance Unit setting
     */

    public int getDistanceUnitsPreference() {
        String[] defaultValue = AndroidUtils.getInstance(getContext()).getStringArray(R.array.pref_DistanceUnitTypeValues);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        String distanceUnitsString = sharedPref.getString(Keys.DISTANCE_UNITS_KEY, defaultValue[0]);
        mLog.v(TAG, "Distance Unit Preference is currently set to: " + distanceUnitsString);
        return Integer.valueOf(distanceUnitsString);
    }

    /**
     * Get an instance of the TripComputer Helper class.
     * REALLY IMPORTANT - Uoi must generally use getInstance(Context)
     * otherwise calls to get UI widgets between different instances
     * of the same mActivity can fail!
     *
     * @return
     */

    public static AndroidUtils getInstance(Context context) {
        Assert.notNull(context);
        ourInstance = new AndroidUtils(context);
        return ourInstance;
    }

    public Journey buildNewJourney() {
        Journey j = new Journey();
        j.setAccuracyThreshold(this.getAccuracyThresholdPreference())
                .setDistanceUnits(this.getDistanceUnitsPreference())
                .setChargeValue(this.getDistanceUnitChargePreference());
        return j;
    }


    public String[] getStringArray(int resourceId) {
        Resources res = this.getContext().getResources();
        return res.getStringArray(resourceId);
    }


    public float getDistanceUnitChargePreference() {
        float chargeValue = Constants.ZERO_FLOAT;
        String defaultValue = this.getString(R.string.charge_per_unit);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        String distanceCharge = sharedPref.getString(Keys.DISTANCE_UNIT_CHARGE_KEY, defaultValue);
        mLog.v(TAG, "Unit Charge Preference is currently set to: " + distanceCharge);

        try {
            chargeValue = Float.valueOf(distanceCharge).floatValue();
        } catch (NumberFormatException e) {
            mLog.e(TAG, "The DistanceCharge in the SharedPreferences could not be parsed to a Float.", e);
            mLog.i(TAG, "Setting corrupt DistanceCharge in the SharedPreferences to default: " + defaultValue);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(Keys.DISTANCE_UNIT_CHARGE_KEY, defaultValue);
            applyPreferenceChanges(editor);
        }

        return chargeValue;
    }


    public String getString(int resourceId) {
        return this.getContext().getString(resourceId);
    }


    public void applyPreferenceChanges(SharedPreferences.Editor editor) {
        editor.apply();
    }

    /**
     * @return The Accuracy Threshold Preference value
     */

    public float getAccuracyThresholdPreference() {
        float accuracyThreshold = Constants.ZERO_FLOAT;
        String defaultValue = this.getString(R.string.pref_AccuracyThresholdTypeDefaultValue);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        String accThreshString = sharedPref.getString(Keys.ACCURACY_THRESHOLD_KEY, defaultValue);
        mLog.v(TAG, "ACCURACY THRESHOLD Preference is currently set to: " + accThreshString);

        try {
            accuracyThreshold = Float.valueOf(accThreshString).floatValue();
        } catch (NumberFormatException e) {
            mLog.e(TAG, "The ACCURACY THRESHOLD in the SharedPreferences could not be parsed to a Float.", e);
            mLog.i(TAG, "Resetting corrupt ACCURACY THRESHOLD in the SharedPreferences to default: " + defaultValue);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(Keys.ACCURACY_THRESHOLD_KEY, defaultValue);
            applyPreferenceChanges(editor);
        }

        return accuracyThreshold;
    }

    /**
     * @return
     */

    public boolean areGooglePlayServicesConnected() {

        // Check that Google Play services is available
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this.getContext());

        switch (resultCode) {
            case ConnectionResult.SUCCESS:
                isPlayServicesAvailable = true;
                mLog.d(TAG, "Play services ARE connected");
                break;
            case ConnectionResult.SERVICE_MISSING:
                mLog.w(TAG, "Google Play Services are MISSING.");
                isPlayServicesAvailable = false;
                break;
            case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED:
                mLog.w(TAG, "Google Play Services requires and UPDATE.");
                isPlayServicesAvailable = false;
                break;
            case ConnectionResult.SERVICE_DISABLED:
                mLog.w(TAG, "Google Play Services are DISABLED.");
                isPlayServicesAvailable = false;
                break;
            default:
                mLog.e(TAG, "Google Play Services result UNEXPECTED!");
                isPlayServicesAvailable = false;
        }

        return isPlayServicesAvailable;
    }


    public boolean getFeature(int featureId) {
        Resources res = this.getContext().getResources();
        return res.getBoolean(featureId);
    }


    public final String getAppVersion() {
        StringBuilder buffer = new StringBuilder();
        buffer.append("App Version: ");
        buffer.append(getVersionName());
        buffer.append(StringUtils.NEW_LINE);
        buffer.append("Build No: ");
        buffer.append(getVersionCode());
        buffer.append(StringUtils.NEW_LINE);
        buffer.append(StringUtils.COPYRIGHT);
        buffer.append("2014 SOA Growers Ltd.");
        buffer.append(StringUtils.NEW_LINE);
        buffer.append("All rights reserved.");
        return buffer.toString();
    }


    public final int getVersionCode() {
        try {
            String pkg = this.getContext().getPackageName();
            PackageInfo info = this.getContext().getPackageManager().getPackageInfo(pkg, 0);
            mVersionCode = info.versionCode;
        } catch (Exception e) {
            mLog.e(TAG, "Unable to get the VersionCode attribute from the system.", e);
        }

        return mVersionCode;
    }

    public final int getShowcaseShotNumber() {
        String versionName = getVersionName();
        String version = versionName.substring(0, versionName.lastIndexOf("."));
        version = version.replace(".", "");
        Integer number = Integer.valueOf(version);
        mLog.v(TAG, "Decided Showcase shot number should be: " + number);
        return number;
    }


    public final String getVersionName() {
        try {
            String pkg = this.getContext().getPackageName();
            PackageInfo info = this.getContext().getPackageManager().getPackageInfo(pkg, 0);
            mVersionName = info.versionName;
        } catch (Exception e) {
            mLog.e(TAG, "Unable to get the VersionName attribute from the system.", e);
        }

        return mVersionName;
    }


    public boolean isPlaceValid(String thePlace) {

        boolean result = false;
        if (thePlace.equals(this.getString(R.string.error_ioexception))
                || thePlace.equals(this.getString(R.string.error_noAddresses))
                || thePlace.equals(this.getString(R.string.error_nolocality))
                || thePlace.equals(this.getString(R.string.error_undetermined))
                || thePlace.equals(this.getString(R.string.error_unknown))) {

            mLog.v(TAG, "Place: '" + thePlace + "' is INVALID");
            result = false;
        } else {
            result = true;
        }

        return result;
    }

    /**
     * @return
     * @deprecated
     */

    public boolean isDeveloperMode() {
        return BuildConfig.DEBUG;
    }


    public boolean isNightMode() {
        return PreferenceManager.getDefaultSharedPreferences(this.getContext()).getBoolean(Keys.NIGHT_MODE_KEY, false);
    }


    public boolean isStayAwakeMode() {
        return PreferenceManager.getDefaultSharedPreferences(this.getContext()).getBoolean(Keys.STAY_AWAKE_KEY, false);
    }


    public boolean isAdvancedCardsOn() {
        return PreferenceManager.getDefaultSharedPreferences(this.getContext()).getBoolean(Keys.ADVANCED_CARDS_KEY, false);
    }


    public boolean isRebootRequired() {
        return PreferenceManager.getDefaultSharedPreferences(this.getContext()).getBoolean(Keys.REBOOT_ACTIVITY_KEY, false);
    }

    /**
     * Get the LowPowerMode setting from the shared preferences.
     *
     * @return
     */

    public boolean isBatterySaverOn() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        boolean lowPowerMode = sharedPref.getBoolean(Keys.LOW_POWER_MODE_KEY, true);
        mLog.v(TAG, "BatterySaver: " + lowPowerMode);
        return lowPowerMode;
    }


    public boolean isAutoStartStopEnabled() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        boolean startStopSetting = sharedPref.getBoolean(Keys.AUTO_STARTSTOP_KEY, false);
        return startStopSetting;
    }


    public boolean isIgnoreShortTripsEnabled() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        boolean startStopSetting = sharedPref.getBoolean(Keys.IGNORE_SHORT_TRIPS_KEY, false);
        return startStopSetting;
    }
}
