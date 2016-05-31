package com.soagrowers.android.tripcomputer.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Build;
import android.preference.PreferenceManager;

import com.soagrowers.android.tripcomputer.Keys;
import com.soagrowers.android.tripcomputer.R;
import com.soagrowers.android.utils.Assert;

import java.util.Map;

/**
 * Created by Ben on 15/08/2014.
 */
public class MapBasedJourneyBuilder {

    private static Context c;

    public static MapBasedJourney build(Context context) {

        Assert.notNull(context);
        c = context;

        MapBasedJourney j = new MapBasedJourney();
        j.start();
        setAccuracyThresholdPreference(j);
        setDistanceUnitsPreference(j);
        setDistanceUnitChargePreference(j);

        return j;
    }

    public static MapBasedJourney buildFromMap(Map delegate, Context context) {

        Assert.notNull(context);
        c = context;

        MapBasedJourney j = new MapBasedJourney(delegate);
        setAccuracyThresholdPreference(j);
        setDistanceUnitsPreference(j);
        setDistanceUnitChargePreference(j);

        return j;
    }

    private static MapBasedJourney setAccuracyThresholdPreference(MapBasedJourney j) {

        String defaultValue = getString(R.string.pref_AccuracyThresholdTypeDefaultValue);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(c);
        String accThreshString = sharedPref.getString(Keys.ACCURACY_THRESHOLD_KEY, defaultValue);

        try {
            j.putSetting(MapBasedJourneyKeys.SETTING_ACCURACY_THRESHOLD_FLT, Float.valueOf(accThreshString));
        } catch (NumberFormatException e) {
            j.putSetting(MapBasedJourneyKeys.SETTING_ACCURACY_THRESHOLD_FLT, Float.valueOf(defaultValue));
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(Keys.ACCURACY_THRESHOLD_KEY, defaultValue);
            applyOrCommitPreferenceChanges(editor);
        }

        return j;
    }

    private static MapBasedJourney setDistanceUnitChargePreference(MapBasedJourney j) {
        String defaultValue = getString(R.string.charge_per_unit);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(c);
        String distanceCharge = sharedPref.getString(Keys.DISTANCE_UNIT_CHARGE_KEY, defaultValue);

        try {
            j.putSetting(MapBasedJourneyKeys.SETTING_CHARGE_VALUE_FLT, Float.valueOf(distanceCharge));
        } catch (NumberFormatException e) {
            j.putSetting(MapBasedJourneyKeys.SETTING_CHARGE_VALUE_FLT, Float.valueOf(defaultValue));
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(Keys.DISTANCE_UNIT_CHARGE_KEY, defaultValue);
            applyOrCommitPreferenceChanges(editor);
        }

        return j;
    }

    private static MapBasedJourney setDistanceUnitsPreference(MapBasedJourney j) {
        String[] defaultValue = getStringArray(R.array.pref_DistanceUnitTypeValues);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(c);
        String distanceUnitsString = sharedPref.getString(Keys.DISTANCE_UNITS_KEY, defaultValue[0]);

        try {
            j.putSetting(MapBasedJourneyKeys.SETTING_DISTANCE_UNITS_INT, Integer.valueOf(distanceUnitsString));
        } catch (NumberFormatException nfe) {
            j.putSetting(MapBasedJourneyKeys.SETTING_DISTANCE_UNITS_INT, Integer.valueOf(defaultValue[0]));
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(Keys.DISTANCE_UNITS_KEY, defaultValue[0]);
            applyOrCommitPreferenceChanges(editor);
        }

        return j;
    }

    private static String getString(int resourceId) {
        return c.getString(resourceId);
    }

    public static String[] getStringArray(int resourceId) {
        Resources res = c.getResources();
        return res.getStringArray(resourceId);
    }

    private static void applyOrCommitPreferenceChanges(SharedPreferences.Editor editor) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.FROYO) {
            editor.apply();
        } else {
            editor.commit();
        }
    }
}
