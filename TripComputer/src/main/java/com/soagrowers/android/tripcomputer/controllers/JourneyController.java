package com.soagrowers.android.tripcomputer.controllers;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.SystemClock;
import android.preference.PreferenceManager;

import com.soagrowers.android.tripcomputer.Keys;
import com.soagrowers.android.tripcomputer.R;
import com.soagrowers.android.tripcomputer.asynctasks.ResolveLocalityAsyncTask;
import com.soagrowers.android.tripcomputer.controllers.strategies.CalculateAltitudeStrategy;
import com.soagrowers.android.tripcomputer.controllers.strategies.CalculateHeadingStrategy;
import com.soagrowers.android.tripcomputer.controllers.strategies.CalculateLatAndLongStrategy;
import com.soagrowers.android.tripcomputer.controllers.strategies.CalculateTimeAndDistanceStrategy;
import com.soagrowers.android.tripcomputer.controllers.strategies.JourneyUpdateHandlerStrategy;
import com.soagrowers.android.tripcomputer.controllers.strategies.SetPlaceNamesStrategy;
import com.soagrowers.android.tripcomputer.data.Constants;
import com.soagrowers.android.tripcomputer.data.Journey;
import com.soagrowers.android.tripcomputer.data.JourneyDecorator;
import com.soagrowers.android.tripcomputer.events.ActivityUpdateEvent;
import com.soagrowers.android.tripcomputer.events.JourneyAutoStartEvent;
import com.soagrowers.android.tripcomputer.events.JourneyAutoStopEvent;
import com.soagrowers.android.tripcomputer.events.JourneyNotSavedEvent;
import com.soagrowers.android.tripcomputer.events.JourneySavedEvent;
import com.soagrowers.android.tripcomputer.events.JourneyStartButtonEvent;
import com.soagrowers.android.tripcomputer.events.JourneyStartedEvent;
import com.soagrowers.android.tripcomputer.events.JourneyStopButtonEvent;
import com.soagrowers.android.tripcomputer.events.JourneyStoppedEvent;
import com.soagrowers.android.tripcomputer.events.JourneyUpdatedEvent;
import com.soagrowers.android.tripcomputer.events.LocalityUpdatedEvent;
import com.soagrowers.android.tripcomputer.events.LocationUpdatedEvent;
import com.soagrowers.android.utils.AndroidUtils;
import com.soagrowers.android.utils.Assert;
import com.soagrowers.android.utils.EventManager;
import com.soagrowers.android.utils.JourneyWriter;
import com.soagrowers.android.utils.Log;
import com.soagrowers.android.utils.StringUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * Created by Ben on 30/08/13.
 */
public class JourneyController implements
        SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = JourneyController.class.getSimpleName();
    private static final int GOECODER_PAUSE_INTERVAL_SECONDS = 30 * Constants.MILLISECONDS_PER_SECOND;
    private static JourneyController instance;
    private static Context context = null;

    private SharedPreferences.OnSharedPreferenceChangeListener sharedPreferenceChangeListener;
    private AndroidUtils h = null;
    private Log log = null;

    private Journey currentJourney = null;
    private long lastLocalityCheckTime = Constants.ZERO_LONG;
    private List<JourneyUpdateHandlerStrategy> updateHandlers = new ArrayList<JourneyUpdateHandlerStrategy>();
    private SetPlaceNamesStrategy placeNamesStrategy = new SetPlaceNamesStrategy();

    /**
     * Protected constructor - use the getInstance static method
     */

    private JourneyController(Context context) {

        Assert.notNull(context);
        this.log = Log.getInstance(context);
        this.context = context;

        this.h = AndroidUtils.getInstance(this.context);

        this.resetJourneyStrategies();

        //Setup a shared preferences change listener
        //*** MUST be a class member Variable to prevent garbage collection ***
        this.sharedPreferenceChangeListener = this;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.context);
        prefs.registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);

        // Register as an Event receiver
        EventManager.getInstance().register(this);

        // Set the current Journey to be the last known good Journey (includes event).
        this.setCurrentJourney(loadPreviousJourney());
    }


    public static JourneyController getInstance(Context theContext) {

        if (null == instance) {
            instance = new JourneyController(theContext);
        } else {
            context = theContext;
        }

        return instance;
    }


    private void resetJourneyStrategies() {
        if (updateHandlers.size() == 0) {
            // Initialise the strategies - FRESH are REQUIRED!
            updateHandlers.add(new CalculateTimeAndDistanceStrategy());
            updateHandlers.add(new CalculateLatAndLongStrategy());
            updateHandlers.add(new CalculateHeadingStrategy());
            updateHandlers.add(new CalculateAltitudeStrategy());
        } else {
            for (Iterator<JourneyUpdateHandlerStrategy> iterator = updateHandlers.iterator(); iterator.hasNext(); ) {
                JourneyUpdateHandlerStrategy next = iterator.next();
                next.reset();
            }
        }
    }


    private Context getApplicationContext() {
        Assert.notNull(this.context);
        return context;
    }

    private Journey getCurrentJourney() {
        Assert.notNull(this.currentJourney);
        return this.currentJourney;
    }

    private void setCurrentJourney(Journey j) {
        Assert.notNull(j);
        this.currentJourney = j;
    }

    /**
     * Used to begin Journey recording
     */

    private void startJourney() {

        if (!this.getCurrentJourney().isRunning()) {

            // Reset the last locality check time...
            this.lastLocalityCheckTime = Constants.ZERO_LONG;

            // Initialise the Journey we're starting to manage.
            this.setCurrentJourney(h.buildNewJourney());

            // Start the Journey
            this.getCurrentJourney().start();

            //Publish the JourneyStatusEvent & JourneyUpdateEvent...
            EventManager.getInstance().postSticky(new JourneyStartedEvent(this.getCurrentJourney()));
        }
    }


    /**
     * Used to stop Journey recording (on a Running Journey)...
     */

    private void stopJourney() {

        //Journey should still be running
        if (this.getCurrentJourney().isRunning()) {

            //Journey is finished so blank the Current Place...
            this.getCurrentJourney().setCurrentPlace(StringUtils.EMPTY_STRING);

            //Now stop the Journey (stops the timer etc.)
            this.getCurrentJourney().stop();

            //Publish the JourneyStatusEvent & JourneyUpdateEvent...
            EventManager.getInstance().postSticky(new JourneyStoppedEvent(this.getCurrentJourney()));

            // Finally, store and Log the Journey if appropriate. (SLOW)
            this.saveCurrentJourney();
            this.resetJourneyStrategies();
        }
    }


    public void onEvent(LocationUpdatedEvent event) {

        Location newLocation = event.getLocation();

        if (this.getCurrentJourney().isRunning()) {
            log.v(TAG, "New Location's accuracy: " + newLocation.getAccuracy());

            // How accurate is the new Location FIX?
            float accuracyRadius = newLocation.getAccuracy();

            //If the location accuracy is HIGH...
            if (accuracyRadius < this.getCurrentJourney().getAccuracyThreshold()) {

                for (Iterator<JourneyUpdateHandlerStrategy> iterator = updateHandlers.iterator(); iterator.hasNext(); ) {
                    JourneyUpdateHandlerStrategy next = iterator.next();
                    next.execute(this.getCurrentJourney(), newLocation);
                }

            } else {
                //If the location accuracy is LOW...
                this.getCurrentJourney().addUnusedLocation(newLocation);
                log.d(TAG, "IGNORING the Location. [ACCURACY Failed]");
            }

            //Update the current PLACE readout
            //Create an async task to prevent locking the UI thread...
            if ((SystemClock.elapsedRealtime() - this.lastLocalityCheckTime) >= GOECODER_PAUSE_INTERVAL_SECONDS) {
                //Use another thread to do the Address reverse lookup...
                ResolveLocalityAsyncTask locationTask = new ResolveLocalityAsyncTask();
                locationTask.execute(newLocation, getApplicationContext());
                this.lastLocalityCheckTime = SystemClock.elapsedRealtime();
            }
        }

        // Broadcast a Journey Update Event...
        EventManager.getInstance().postSticky(new JourneyUpdatedEvent(getCurrentJourney()));
    }

    public void onEvent(JourneyAutoStartEvent event) {
        startJourney();
    }

    public void onEvent(JourneyStartButtonEvent event) {
        startJourney();
    }

    public void onEventMainThread(JourneyAutoStopEvent event) {
        stopJourney();
    }

    public void onEventMainThread(JourneyStopButtonEvent event) {
        stopJourney();
    }

    public void onEvent(ActivityUpdateEvent event) {
        if (this.getCurrentJourney().isRunning()) {
            getCurrentJourney().setCurrentActivityType(event.getActivity().getType());
            getCurrentJourney().setCurrentActivityConfidence(event.getActivity().getConfidence());
        }
    }

    public void onEvent(LocalityUpdatedEvent event) {
        this.placeNamesStrategy.execute(this.getCurrentJourney(), event.getLocality());
    }

    /**
     * Used on start-up when the service has been bound to get the last Journey from
     * the SharedPref's storage facility.
     *
     * @return
     */

    private Journey loadPreviousJourney() {

        Journey loadedJourney = null;

        //First check for the persist feature...
        if (this.h.getFeature(R.bool.feature_persistLastJourney)) {

            //Get the Last Journey STRING & VERSION from the shared prefs...
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            String gsonJourneyString = prefs.getString(Keys.FROZEN_GSON_JOURNEY_KEY, StringUtils.EMPTY_STRING);
            double version = (double) prefs.getFloat(Keys.FROZEN_GSON_JOURNEY_VERSION_KEY, (float) Constants.VERSION_ONE_DOT_ZERO);

            //If the STRING in prefs is not empty, it could be JSON encoded...
            if (!gsonJourneyString.equals(StringUtils.EMPTY_STRING)) {

                try {
                    //Attempt to un-marshall the Journey from the prefs String...
                    loadedJourney = JourneyDecorator.getJourneyFromGsonString(gsonJourneyString, version);


                    //Make the Journey reflect the current preferences...
                    loadedJourney.setChargeValue(h.getDistanceUnitChargePreference())
                            .setDistanceUnits(h.getDistanceUnitsPreference())
                            .setAccuracyThreshold(h.getAccuracyThresholdPreference());

                    log.i(TAG, "The previous Journey has been loaded from SharedPrefs");

                } catch (Exception e) {
                    //sometimes if the classes are out of alignment and exception is thrown
                    log.e(TAG, "Problem defrosting the previous Journey string.", e);

                    //Overwrite the Journey so it doesn't happen again
                    SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
                    editor.putString(Keys.FROZEN_GSON_JOURNEY_KEY, StringUtils.EMPTY_STRING);
                    editor.putFloat(Keys.FROZEN_GSON_JOURNEY_VERSION_KEY, (float) Constants.VERSION_ONE_DOT_ZERO);
                    h.applyPreferenceChanges(editor);
                    log.d(TAG, "The previous Journey string was corrupted - removed from SharedPrefs");
                }
            }
        }

        if (null == loadedJourney) {
            // No suitable journey could be loaded, so create a blank...
            loadedJourney = h.buildNewJourney();
        }

        Assert.notNull(loadedJourney);

        // Publish an event containing the last known journey.
        EventManager.getInstance().postSticky(new JourneyUpdatedEvent(loadedJourney));
        return loadedJourney;
    }


    private void saveCurrentJourney() {
        Assert.notNull(this.getCurrentJourney());

        // Short trips should be ignored
        if (this.h.isIgnoreShortTripsEnabled()
                && // And the distance covered is too short
                this.getCurrentJourney().getTotalDistance() < Constants.MIN_TRIP_DISTANCE_IN_METERS) {

            // Tell the user the Journey was ignored
            this.h.doLongToast(R.string.toast_ignore_journey);
            log.i(TAG, "Journey was too short and will not be saved. IGNORED SAVE");

            EventManager.getInstance().post(new JourneyNotSavedEvent(this.getCurrentJourney()));

            // Load the previous known good journey.
            this.setCurrentJourney(loadPreviousJourney());


            return;
        }

        // Do this once because it's slow...
        String journeyString = JourneyDecorator.getGsonStringForJourney(this.getCurrentJourney(), Constants.CURRENT_VERSION);

        if (this.h.getFeature(R.bool.feature_persistLastJourney)) {
            // The Save Last Journey feature is ENABLED...
            saveJourneyToPrefs(journeyString);
        }

        if (this.h.getFeature(R.bool.feature_persistAllJourneys)) {
            // The Persist All Journeys feature is ENABLED...
            saveJourneyToFile(journeyString);
        }

        EventManager.getInstance().post(new JourneySavedEvent(this.getCurrentJourney()));
    }


    private void saveJourneyToFile(String jsonJourney) {
        //Write the journey to a file...
        Assert.notNull(jsonJourney);
        JourneyWriter journeyWriter = new JourneyWriter(this.getApplicationContext(), jsonJourney);
        Thread newThread = new Thread(journeyWriter, "journeywriter");
        newThread.start();
    }


    private void saveJourneyToPrefs(String jsonJourney) {
        // Store the Journey in the shared prefs
        Assert.notNull(jsonJourney);
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
        editor.putString(Keys.FROZEN_GSON_JOURNEY_KEY, jsonJourney);
        editor.putFloat(Keys.FROZEN_GSON_JOURNEY_VERSION_KEY, (float) Constants.CURRENT_VERSION);
        h.applyPreferenceChanges(editor);
        log.d(TAG, "The Journey has been Frozen to SharedPrefs");
    }

    /**
     * Called when a shared preference is changed, added, or removed. This
     * may be called even if a preference is set to its existing value.
     * <p/>
     * <p>This callback will be run on your main thread.
     *
     * @param sharedPreferences The {@link android.content.SharedPreferences} that received
     *                          the change.
     * @param key               The key of the preference that was changed, added, or
     */
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        //Handle Accuracy Threshold changes
        if (key.equals(Keys.ACCURACY_THRESHOLD_KEY)) {
            this.getCurrentJourney().setAccuracyThreshold(this.h.getAccuracyThresholdPreference());
        }

        if (key.equals(Keys.DISTANCE_UNITS_KEY)) {
            this.getCurrentJourney().setDistanceUnits(this.h.getDistanceUnitsPreference());
        }

        if (key.equals(Keys.DISTANCE_UNIT_CHARGE_KEY)) {
            this.getCurrentJourney().setChargeValue(this.h.getDistanceUnitChargePreference());
        }

        //Update the UI with any changes
        EventManager.getInstance().postSticky(new JourneyUpdatedEvent(getCurrentJourney()));

    }
}
