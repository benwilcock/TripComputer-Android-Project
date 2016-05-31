package com.soagrowers.android.tripcomputer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.soagrowers.android.tripcomputer.services.JourneyService;


/**
 * Created by Ben on 11/06/2014.
 */
public class BootCompletedIntentReceiver extends BroadcastReceiver {

    private static final String TAG = BootCompletedIntentReceiver.class.getSimpleName();


    @Override
    public void onReceive(Context context, Intent intent) {

        Log.i(TAG, "onReceive has fired!");
        //Check for the BOOT_COMPLETED action
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            Log.i(TAG, "Received a BOOT_COMPLETED intent.");
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
            boolean startStopSetting = sharedPref.getBoolean(Keys.AUTO_STARTSTOP_KEY, false);
            if (startStopSetting) {
                Log.i(TAG, "Start Stop is ON");
                Intent pushIntent = new Intent(context, JourneyService.class);
                context.startService(pushIntent);
            }
        }
    }
}
