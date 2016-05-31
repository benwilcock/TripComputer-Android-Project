package com.soagrowers.android.tripcomputer;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.soagrowers.android.utils.AndroidUtils;
import com.soagrowers.android.utils.Assert;
import com.soagrowers.android.utils.Log;
import com.soagrowers.android.utils.StringUtils;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import hugo.weaving.DebugLog;


/**
 * Created by Ben on 16/12/13.
 */
public class SettingsFragment extends PreferenceFragment
  implements SharedPreferences.OnSharedPreferenceChangeListener,
  Preference.OnPreferenceChangeListener {

  private static final String TAG = SettingsFragment.class.getCanonicalName();
  private static String[] distanceUnitNames;
  private static String[] distanceUnitValues;
  private static String[] accuracyNames;
  private static String[] accuracyValues;
  private Log log = null;
  private AndroidUtils h = null;
  private Map<String, String> DISTANCE_UNITS_MAP = new HashMap<String, String>(3);
  private Map<String, String> ACCURACY_THRESHOLDS_MAP = new HashMap<String, String>(3);

  private DecimalFormat rateFormatter;
  //private String mSelectedValue;


  @DebugLog
  @Override
  public void onCreate(Bundle savedInstanceState) {

    this.log = Log.getInstance(getActivity().getApplicationContext());
    this.h = AndroidUtils.getInstance(getActivity().getApplicationContext());

    super.onCreate(savedInstanceState);
    addPreferencesFromResource(R.xml.preferences);

    rateFormatter = new DecimalFormat(h.getString(R.string.fixed_rate_format));
    rateFormatter.applyPattern(h.getString(R.string.fixed_rate_format));

    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
      EditTextPreference unitCharge = (EditTextPreference) getPreferenceScreen().findPreference(Keys.DISTANCE_UNIT_CHARGE_KEY);
      unitCharge.setOnPreferenceChangeListener(this);
      log.v(TAG, "Registered SettingsActivity as a listener to EditTextPreference " + Keys.DISTANCE_UNIT_CHARGE_KEY);
    }

    populateDistanceUnitNamesMap();
    populateAccuracyThresholdNamesMap();
  }

  /**
   * Called after {@link #onCreate} &mdash; or after {onRestart} when
   * the activity had been stopped, but is now again being displayed to the
   * user.  It will be followed by {@link #onResume}.
   * <p/>
   * <p><em>Derived classes must call through to the super class's
   * implementation of this method.  If they do not, an exception will be
   * thrown.</em></p>
   *
   * @see #onCreate
   * @see #onStop
   * @see #onResume
   */

  @Override
  public void onStart() {
    super.onStart();
    GoogleAnalytics.getInstance(getActivity())
      .reportActivityStart(getActivity());
  }


  @Override
  public void onResume() {
    super.onResume();
    populateDistanceUnitNamesMap();

    //register as a listener...
    SharedPreferences sharedPref = getPreferenceScreen().getSharedPreferences();
    sharedPref.registerOnSharedPreferenceChangeListener(this);
    log.d(TAG, "Registered as an OnSharedPreferenceChangeListener.");

    //Update the summary with user input data
    this.updateDistanceUnitsPrefSummary(sharedPref);
    this.updateUnitChargePrefSummary(sharedPref);
    this.updateAccuracyThresholdPrefSummary(sharedPref);
  }


  @Override
  public void onPause() {
    super.onPause();
    //register as a listener...
    SharedPreferences sharedPref = getPreferenceScreen().getSharedPreferences();
    sharedPref.unregisterOnSharedPreferenceChangeListener(this);
    log.d(TAG, "Unegistered SettingsActivity as an OnSharedPreferenceChangeListener.");
  }

  @Override
  public void onStop() {
    GoogleAnalytics.getInstance(getActivity()).reportActivityStop(getActivity());
    super.onStop();
  }

  /**
   * Callback method listening out for changes to the Preferences.
   *
   * @param sharedPreferences
   * @param key
   */

  public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

    if (key.equals(Keys.DISTANCE_UNITS_KEY)) {
      //Get the current selection
      String value = sharedPreferences.getString(key, "");
      log.i(TAG, "Distance units has been changed to " + this.DISTANCE_UNITS_MAP.get(value));
      this.updateDistanceUnitsPrefSummary(sharedPreferences);
    }

    if (key.equals(Keys.DISTANCE_UNIT_CHARGE_KEY)) {
      //Get the current selection
      String defaultValue = AndroidUtils.getInstance().getString(R.string.charge_per_unit);
      String value = sharedPreferences.getString(key, defaultValue);
      log.i(TAG, "Charge has been changed to: " + value);
      this.updateUnitChargePrefSummary(sharedPreferences);
    }

    if (key.equals(Keys.ACCURACY_THRESHOLD_KEY)) {
      //Get the current selection
      String value = sharedPreferences.getString(key, "");
      log.i(TAG, "Accuracy Threshold has been changed to: " + this.ACCURACY_THRESHOLDS_MAP.get(value));
      this.updateAccuracyThresholdPrefSummary(sharedPreferences);
    }

    if (key.equals(Keys.NIGHT_MODE_KEY)) {
      log.i(TAG, "Screen Theme setting has changed (requires a UI restart)...");
      SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
      SharedPreferences.Editor editor = pref.edit().putBoolean(Keys.REBOOT_ACTIVITY_KEY, true);
      AndroidUtils.getInstance().applyPreferenceChanges(editor);
    }
  }


  /**
   * Updates the display to add a summary item containing the name of the currently
   * selected Distance Unit (e.g. Kilometers).
   */

  private void updateDistanceUnitsPrefSummary(SharedPreferences sharedPreferences) {

    //Get the Distance Unit preference object
    Preference distanceUnitsPref = findPreference(Keys.DISTANCE_UNITS_KEY);

    //Get the current selection
    String selected = sharedPreferences.getString(Keys.DISTANCE_UNITS_KEY, distanceUnitValues[0]);
    StringBuilder sb = new StringBuilder();
    sb.append(getResources().getString(R.string.pref_DistanceUnitsSummaryPrefix));
    sb.append(StringUtils.SPACE);
    sb.append(this.DISTANCE_UNITS_MAP.get(selected));
    //Update the summary
    distanceUnitsPref.setSummary(sb.toString());

    //Update the unit charge summary also...
    updateUnitChargePrefSummary(sharedPreferences);
  }

  /**
   * Updates the display to add a summary item containing the name of the currently
   * selected Accuracy Threshold (e.g. LOW).
   */

  private void updateAccuracyThresholdPrefSummary(SharedPreferences sharedPreferences) {

    //Get the Accuracy Threshold preference object
    Preference accuracyThresholdPref = findPreference(Keys.ACCURACY_THRESHOLD_KEY);

    //Get the current selection
    String selected = sharedPreferences.getString(Keys.ACCURACY_THRESHOLD_KEY, accuracyValues[0]);

    //Build the pref summary
    StringBuilder sb = new StringBuilder();
    sb.append(getResources().getString(R.string.pref_AccuracyThresholdSummaryPrefix));
    sb.append(StringUtils.SPACE);
    sb.append(selected);
    sb.append(getResources().getString(R.string.txt_m));

    //Update the summary with user input data
    accuracyThresholdPref.setSummary(sb.toString());
  }

  /**
   * Updates the display to add a summary item containing the name of the currently
   * selected Distance Unit (e.g. Kilometers).
   */

  private void updateUnitChargePrefSummary(SharedPreferences sharedPreferences) {

    //Get the Distance Unit preference object
    Preference distanceChargePref = findPreference(Keys.DISTANCE_UNIT_CHARGE_KEY);

    //Get the current selection
    String defaultValue = AndroidUtils.getInstance(getActivity()).getString(R.string.charge_per_unit);
    String chargeValue = sharedPreferences.getString(Keys.DISTANCE_UNIT_CHARGE_KEY, defaultValue);

    //Update the summary with user input data
    StringBuilder sb = new StringBuilder();
    sb.append(getResources().getString(R.string.currency));
    sb.append(chargeValue);
    sb.append(getResources().getString(R.string.pence));
    sb.append(StringUtils.SPACE);
    sb.append(getResources().getString(R.string.per));
    sb.append(StringUtils.SPACE);

    //Add the Distance Units (Miles etc)
    String units = sharedPreferences.getString(Keys.DISTANCE_UNITS_KEY, distanceUnitValues[0]);
    String unitName = this.DISTANCE_UNITS_MAP.get(units);
    sb.append(unitName.substring(0, unitName.length() - 1));

    distanceChargePref.setSummary(sb.toString());
  }

  /**
   * Set up the MAP of Distance Unit Names (e.g. '0 = Miles')
   */

  private void populateDistanceUnitNamesMap() {

    //if the Maps are null, populate them
    if (DISTANCE_UNITS_MAP.size() < 1) {
      log.v(TAG, "Initialising the Distance Unit Names MAP.");
      Resources res = getResources();
      distanceUnitValues = res.getStringArray(R.array.pref_DistanceUnitTypeValues);
      distanceUnitNames = res.getStringArray(R.array.pref_dialogChoicesUnitTypes);
      for (int i = 0; i < distanceUnitValues.length; i++) {
        this.DISTANCE_UNITS_MAP.put(distanceUnitValues[i], distanceUnitNames[i]);
        log.v(TAG, "Adding - Value: " + distanceUnitValues[i] + " Name: " + distanceUnitNames[i]);
      }
      log.v(TAG, "Distance Unit Names MAP has been initialised.");
    }
  }

  /**
   * Set up the MAP of Distance Unit Names (e.g. '0 = Miles')
   */

  private void populateAccuracyThresholdNamesMap() {

    //if the Maps are null, populate them
    if (ACCURACY_THRESHOLDS_MAP.size() < 1) {
      log.v(TAG, "Initialising the Accuracy Threshold Names MAP.");
      Resources res = getResources();
      accuracyValues = res.getStringArray(R.array.pref_AccuracyThresholdTypeValues);
      accuracyNames = res.getStringArray(R.array.pref_dialogChoicesAccuracyThresholdTypes);
      for (int i = 0; i < accuracyValues.length; i++) {
        this.ACCURACY_THRESHOLDS_MAP.put(accuracyValues[i], accuracyNames[i]);
        log.v(TAG, "Adding - Value: " + accuracyValues[i] + " Name: " + accuracyNames[i]);
      }
      log.v(TAG, "Accuracy Threshold Names MAP has been initialised.");
    }
  }

  /**
   * Called when a Preference has been changed by the user. This is
   * called before the state of the Preference is about to be updated and
   * before the state is persisted.
   *
   * @param preference The changed Preference.
   * @param newValue   The new value of the Preference.
   * @return True to setMainText the state of the Preference with the new value.
   */

  @Override
  public boolean onPreferenceChange(Preference preference, Object newValue) {
    Assert.notNull(newValue);
    boolean result = false;

    if (preference instanceof EditTextPreference && preference.getKey().equals(Keys.DISTANCE_UNIT_CHARGE_KEY)) {
      if (newValue instanceof String) {
        String text = (String) newValue;
        if (!text.trim().equals(StringUtils.EMPTY_STRING)) {
          text = text.trim();
          try {
            float amount = Float.parseFloat(text);
            text = rateFormatter.format(amount);
            log.d(TAG, "EditText Distance Charge Value parsed to a float as: " + text);

            //setting the value here because there is no other way to send the formatted value back...
            newValue = text;

            result = true;
          } catch (Throwable e) {
            log.e(TAG, "Unable to parse the users text entry to a float.", e);
            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(R.string.pref_InputFailureTitle);
            builder.setMessage(R.string.pref_InputFailureMessage);
            builder.setPositiveButton(android.R.string.ok, null);
            builder.show();
            result = false;
          }
        } else {
          log.d(TAG, "EditText value was empty");
        }
      }
    }
    return result;
  }
}
