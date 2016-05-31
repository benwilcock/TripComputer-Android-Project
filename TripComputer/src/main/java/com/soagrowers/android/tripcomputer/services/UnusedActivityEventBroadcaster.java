package com.soagrowers.android.tripcomputer.services;

import android.content.Context;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;
import com.soagrowers.android.tripcomputer.data.Constants;
import com.soagrowers.android.tripcomputer.events.ActivityUpdateEvent;
import com.soagrowers.android.tripcomputer.events.JourneyAutoStartEvent;
import com.soagrowers.android.tripcomputer.events.JourneyAutoStopEvent;
import com.soagrowers.android.tripcomputer.events.JourneyStatusEvent;
import com.soagrowers.android.utils.AndroidUtils;
import com.soagrowers.android.utils.Assert;
import com.soagrowers.android.utils.EventManager;
import com.soagrowers.android.utils.Log;
import com.soagrowers.android.utils.MovementActivityUtil;


/**
 * This class handles the Activity broadcasting and auto start and stop
 * responsibilities. Can be run as a separate thread, allowing the UI thread
 * to continue unhindered.
 *
 * @see java.lang.Runnable
 */
public class UnusedActivityEventBroadcaster implements Runnable {

    private static final String TAG = UnusedActivityEventBroadcaster.class.getSimpleName();
    private static UnusedActivityEventBroadcaster instance;

    private static Context context;
    private static Log log;
    private static int previousActivityType = DetectedActivity.UNKNOWN;
    private static DetectedActivity detectedActivity;
    private static int consecutiveActivityCount = Constants.ZERO_INT;
    private static boolean isTheDeviceMoving;
    private static boolean isTheActivityNew;
    private static boolean isJourneyUnderway = false;


    /**
     * Construct the thread.
     * Needs to be a singleton because START and STOP events require a history of Activity
     * updates for decision making, therefore long lasting members are necessary.
     */

    private UnusedActivityEventBroadcaster() {
        // Register to receive Events, including most recent (sticky)
        EventManager.getInstance().registerSticky(this);
    }

    /**
     * get an instance, setting the Context.
     *
     * @param theContext
     * @return
     */

    public static UnusedActivityEventBroadcaster getInstance(Context theContext) {
        Assert.notNull(theContext);
        if (null == instance) {
            instance = new UnusedActivityEventBroadcaster();
        }

        context = theContext;
        log = Log.getInstance(theContext);
        return instance;
    }

    /**
     * Use this method to set the users DetectedActivity.
     *
     * @param theActivity
     */

    public void setDetectedActivity(ActivityRecognitionResult theActivity) {
        Assert.notNull(theActivity);
        detectedActivity = theActivity.getMostProbableActivity();

        // Pre-process the Activity setMainText.
        isTheDeviceMoving = MovementActivityUtil.isMovementDetected(detectedActivity);
        isTheActivityNew = isActivityChanged();

        if (MovementActivityUtil.isConfidenceLow(detectedActivity)) {
            consecutiveActivityCount = Constants.ZERO_INT;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Activity: " + MovementActivityUtil.getUserActivityAsString(detectedActivity.getType()));
        sb.append(". Confidence: " + detectedActivity.getConfidence());
        sb.append("%. Moving: " + isTheDeviceMoving);
        sb.append(". Changed: " + isTheActivityNew);
        sb.append(". Consecutive: " + consecutiveActivityCount);
        sb.append(". Underway: " + isJourneyUnderway);
        log.i(TAG, sb.toString());
    }


    /**
     * Used by the EventBus to tell us about Journey Events
     *
     * @param event
     */

    public void onEvent(JourneyStatusEvent event) {

        switch (event.getType()) {

            case START_EVENT:
                isJourneyUnderway = true;
                consecutiveActivityCount = Constants.ZERO_INT;
                break;

            case STOP_EVENT:
                isJourneyUnderway = false;
                consecutiveActivityCount = Constants.ZERO_INT;
                break;

            default:
                break;
        }
    }

    /**
     * Starts executing the active part of the class' code. This method is
     * called when a thread is started that has been created with a class which
     * implements {@code Runnable}.
     */

    @Override
    public void run() {
        Assert.notNull(detectedActivity);
        AndroidUtils h = AndroidUtils.getInstance(context);

        // Always broadcast the Activity setMainText
        this.publishActivityUpdateEvent();

        // Is the Auto START & STOP preference set?
        if (h.isAutoStartStopEnabled() == true) {
            // Is our activity stable?
            if (isTheActivityNew == false) {
                // Are we moving or stationary?
                if (isTheDeviceMoving) {
                    // Are we recording?
                    if (!isJourneyUnderway) {
                        // Auto start Journey Recording!;
                        this.autoStart();
                    }
                } else {
                    if (isJourneyUnderway) {
                        // Auto Stop Journey Recording!
                        this.autoStop();
                    }
                }
            }
        }
    }

    /**
     * Tests to see if the activity has changed.
     *
     * @return true if the user's current activity is different from the previous most probable
     * activity; otherwise, false.
     */

    private boolean isActivityChanged() {
        int currentActivityType = detectedActivity.getType();

        boolean activityStatus = false;

        // If the previous type isn't the same as the current type, the activity has changed
        if (previousActivityType != currentActivityType) {

            StringBuilder sb = new StringBuilder();
            sb.append("The users physical activity has changed from ");
            sb.append(MovementActivityUtil.getUserActivityAsString(previousActivityType));
            sb.append(" to ");
            sb.append(MovementActivityUtil.getUserActivityAsString(currentActivityType));
            log.d(TAG, sb.toString());

            // Overwrite the previous activity.
            previousActivityType = currentActivityType;

            // Reset the counter
            consecutiveActivityCount = Constants.ZERO_INT;
            activityStatus = true;

        } else {
            // Activity hasn't changed
            // Just increment the counter
            consecutiveActivityCount++;
            activityStatus = false;
        }

        return activityStatus;
    }


    /**
     * Handles the requirement to auto start the Journey recording facility
     */

    private void autoStart() {
        Assert.notNull(detectedActivity);

        if (consecutiveActivityCount >= Constants.ACTIVITY_START_TRIGGER_THRESHOLD
                &&
                detectedActivity.getConfidence() >= Constants.ACTIVITY_START_CONFIDENCE_THRESHOLD
                &&
                isJourneyUnderway == false) {

            // Broadcast an Auto Start Event....
            EventManager.getInstance().post(new JourneyAutoStartEvent(detectedActivity));
            log.i(TAG, "AUTO START EVENT triggered by the users activity.");

            // Now reset the activity count.
            consecutiveActivityCount = Constants.ZERO_INT;
        }
    }

    /**
     * Handles the requirement to auto start the Journey recording facility
     */

    private void autoStop() {
        Assert.notNull(detectedActivity);

        if (consecutiveActivityCount >= Constants.ACTIVITY_STOP_TRIGGER_THRESHOLD
                &&
                detectedActivity.getConfidence() >= Constants.ACTIVITY_STOP_CONFIDENCE_THRESHOLD
                &&
                isJourneyUnderway == true) {


            // Broadcast an Auto Stop Event....
            EventManager.getInstance().post(new JourneyAutoStopEvent(detectedActivity));
            log.i(TAG, "AUTO STOP EVENT triggered by the users activity.");

            // Now reset the activity count.
            consecutiveActivityCount = Constants.ZERO_INT;
        }
    }


    /**
     * Use a LocalBroadcastManager to send out an setMainText about the Activity Recognition result.
     */

    private void publishActivityUpdateEvent() {

        // Broadcast the Activity...
        if (isJourneyUnderway == true) {
            // Broadcast an Activity Update
            EventManager.getInstance().post(new ActivityUpdateEvent(detectedActivity));
            log.v(TAG, "Published an ActivityUpdateEvent: " + MovementActivityUtil.getUserActivityAsString(detectedActivity.getType()));
        }
    }


    @Override
    protected void finalize() throws Throwable {
        EventManager.getInstance().unregister(this);
        super.finalize();
    }
}
