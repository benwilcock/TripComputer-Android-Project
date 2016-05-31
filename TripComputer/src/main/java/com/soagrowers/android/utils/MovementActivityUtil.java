package com.soagrowers.android.utils;

import com.google.android.gms.location.DetectedActivity;
import com.soagrowers.android.tripcomputer.data.Constants;


/**
 * Created by Ben on 30/05/2014.
 */
public class MovementActivityUtil {

  public static enum Triggers{
    START_TRIGGER,
    STOP_TRIGGER,
    NO_TRIGGER
  }

    /**
     * Determine if an activity means that the user is moving.
     *
     * @return true if the user seems to be moving, otherwise false
     */

    public static final boolean isMovementDetected(final DetectedActivity activity) {

        boolean movementDetected = false;

        switch (activity.getType()) {
            // These types mean that the user is probably not moving
            case DetectedActivity.STILL:
            case DetectedActivity.TILTING:
            case DetectedActivity.UNKNOWN:
                movementDetected = false;
                break;
            default:
                movementDetected = true;
                break;
        }

        return movementDetected;
    }


    public static final boolean isConfidenceLow(final DetectedActivity activity) {
        if (activity.getConfidence() < Constants.ACTIVITY_START_CONFIDENCE_THRESHOLD
                ||
                activity.getConfidence() < Constants.ACTIVITY_STOP_CONFIDENCE_THRESHOLD) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Map detected activity types to strings
     *
     * @param activityType The detected activity type
     * @return A user-readable name for the type
     */

    public static final String getUserActivityAsString(final int activityType) {
        // No logging because method's used early in boot...

        switch (activityType) {
            case DetectedActivity.IN_VEHICLE:
                return StringUtils.IN_VEHICLE;
            case DetectedActivity.ON_BICYCLE:
                return StringUtils.ON_BICYCLE;
            case DetectedActivity.RUNNING:
                return StringUtils.RUNNING;
            case DetectedActivity.ON_FOOT:
                return StringUtils.ON_FOOT;
            case DetectedActivity.STILL:
                return StringUtils.STILL;
            case DetectedActivity.UNKNOWN:
                return StringUtils.UNKNOWN;
            case DetectedActivity.TILTING:
                return StringUtils.TILTING;
        }
        return StringUtils.UNKNOWN;
    }

  public static final Triggers isEventTrigger(DetectedActivity activity){

    switch (activity.getType()) {
      // These Activities are START triggers when confidence is HIGH
      case DetectedActivity.IN_VEHICLE:
      case DetectedActivity.ON_BICYCLE:
      case DetectedActivity.RUNNING:
        if(activity.getConfidence() >= Constants.ACTIVITY_START_CONFIDENCE_THRESHOLD){
          return Triggers.START_TRIGGER;
        } else {
          return Triggers.NO_TRIGGER;
        }

      // These activities are STOP triggers when confidence is HIGH
      case DetectedActivity.STILL:
        if(activity.getConfidence() >= Constants.ACTIVITY_STOP_CONFIDENCE_THRESHOLD){
          return Triggers.STOP_TRIGGER;
        } else {
          return Triggers.NO_TRIGGER;
        }

      // These activities are neither START or STOP triggers
      case DetectedActivity.ON_FOOT:
      case DetectedActivity.WALKING:
      case DetectedActivity.UNKNOWN:
      case DetectedActivity.TILTING:
        return Triggers.NO_TRIGGER;
    }

    return Triggers.NO_TRIGGER;
  }
}
