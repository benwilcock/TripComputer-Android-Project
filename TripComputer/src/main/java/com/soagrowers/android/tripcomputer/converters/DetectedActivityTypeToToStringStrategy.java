package com.soagrowers.android.tripcomputer.converters;

import android.content.Context;

import com.google.android.gms.location.DetectedActivity;
import com.google.common.base.Optional;
import com.soagrowers.android.tripcomputer.R;
import com.soagrowers.android.tripcomputer.data.Constants;

/**
 * Created by Ben on 08/09/2014.
 */
public class DetectedActivityTypeToToStringStrategy extends AbstractToStringStrategy<DetectedActivity> {


    private static String IN_VEHICLE = Constants.NOT_SET;
    private static String ON_BICYCLE = Constants.NOT_SET;
    private static String ON_FOOT = Constants.NOT_SET;
    private static String RUNNING = Constants.NOT_SET;
    private static String STILL = Constants.NOT_SET;
    private static String UNKNOWN = Constants.NOT_SET;
    private static String TILTING = Constants.NOT_SET;

    public DetectedActivityTypeToToStringStrategy(Context context) {
        super(context);
        IN_VEHICLE = context.getString(R.string.txt_driving);
        ON_BICYCLE = context.getString(R.string.txt_cycling);
        ON_FOOT = context.getString(R.string.txt_walking);
        RUNNING = context.getString(R.string.txt_running);
        STILL = context.getString(R.string.txt_still);
        UNKNOWN = context.getString(R.string.txt_unknown);
        TILTING = context.getString(R.string.txt_tilting);
    }



    @Override
    public Optional<String> toString(DetectedActivity detectedActivity) {

        //add the activity type
        switch (detectedActivity.getType()) {
            case DetectedActivity.IN_VEHICLE:
                return Optional.of(IN_VEHICLE);
            case DetectedActivity.ON_BICYCLE:
                return Optional.of(ON_BICYCLE);
            case DetectedActivity.ON_FOOT:
                return Optional.of(ON_FOOT);
            case DetectedActivity.RUNNING:
                return Optional.of(RUNNING);
            case DetectedActivity.STILL:
                return Optional.of(STILL);
            case DetectedActivity.UNKNOWN:
                return Optional.of(UNKNOWN);
            case DetectedActivity.TILTING:
                return Optional.of(TILTING);
            default:
                return Optional.absent();
        }
    }
}
