package com.soagrowers.android.tripcomputer;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

import com.soagrowers.android.utils.AndroidUtils;

import hugo.weaving.DebugLog;


/**
 * Created by Ben on 16/12/13.
 */
public class SettingsActivity extends ActionBarActivity {

  private static final String TAG = SettingsActivity.class.getCanonicalName();

  @DebugLog
  @Override
  public void onCreate(Bundle savedInstanceState) {

    //check if the Theme needs changing
    if (AndroidUtils.getInstance(this).isNightMode()) {
      Log.d(TAG, "Starting SettingsActivity using Night theme...");
      this.setTheme(R.style.AppBaseThemeNight);
    } else {
      Log.d(TAG, "Starting SettingsActivity using Day theme...");
      this.setTheme(R.style.AppBaseThemeDay);
    }

    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_settings);
  }
}
