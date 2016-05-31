package com.soagrowers.android.tripcomputer;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;

import com.soagrowers.android.tripcomputer.data.ImmutableJourney;
import com.soagrowers.android.tripcomputer.data.JourneyDecorator;
import com.soagrowers.android.tripcomputer.events.JourneyStartedEvent;
import com.soagrowers.android.tripcomputer.events.JourneyStoppedEvent;
import com.soagrowers.android.tripcomputer.events.JourneyUpdatedEvent;
import com.soagrowers.android.utils.AndroidUtils;
import com.soagrowers.android.utils.EventManager;
import com.soagrowers.android.utils.Log;


/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 */
public class JourneyInformationFragment extends Fragment {

  //Used for Logging
  private static final String TAG = JourneyInformationFragment.class.getSimpleName();
  private Log log;


  public JourneyInformationFragment() {
    // Required empty public constructor
  }


  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    log = Log.getInstance(getActivity());
  }

  /**
   * Called when the Fragment is no longer resumed.  This is generally
   * tied to {@link android.app.Activity#onPause() Activity.onPause} of the containing
   * Activity's lifecycle.
   */

  @Override
  public void onPause() {
    super.onPause();
    // Unregister from events
    EventManager.getInstance().unregister(this);
  }

  /**
   * Called when the fragment is visible to the user and actively running.
   * This is generally
   * tied to {@link android.app.Activity#onResume() Activity.onResume} of the containing
   * Activity's lifecycle.
   */

  @Override
  public void onResume() {
    super.onResume();
    // Register for events
    EventManager.getInstance().registerSticky(this);
  }


  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    //inflate the layout for this Fragment...
    View journeyInformation = inflater.inflate(R.layout.fragment_journey_information, container, false);
    return journeyInformation;
  }

  /**
   * Used to refresh the screen fragment with all the latest Journey
   * Information such as elapsed time and distance etc.
   *
   * @param event
   */

  public void onEventMainThread(JourneyStartedEvent event) {
    handleEvent(event.getImmutableJourney());
    return;
  }

  public void onEventMainThread(JourneyUpdatedEvent event) {
    handleEvent(event.getImmutableJourney());
    return;
  }

  public void onEventMainThread(JourneyStoppedEvent event) {
    handleEvent(event.getImmutableJourney());
    return;
  }


  protected void handleEvent(ImmutableJourney journey) {

    /* REALLY IMPORTANT - always refresh the instance before doing UI stuff. */
    AndroidUtils h = AndroidUtils.getInstance(getActivity());
    JourneyDecorator d = new JourneyDecorator(journey, getActivity());
    /* REALLY IMPORTANT - always refresh the instance before doing UI stuff. */

    if (d.isRunning() || d.isFinished()) {
      //Find the Stopwatch View
      Chronometer chronometer = (Chronometer) h.findViewById(R.id.chronometer);
      d.configureChronometer(chronometer);

      // It's still going...
      if (d.isRunning()) {
        chronometer.start();
      }

      // It's stopped.
      if (d.isFinished()) {
        chronometer.stop();
      }
    }

    // ORGANISED as per 'fragment_journey_information'...
    h.setTextViewText(R.id.startedTime, d.getStartTimeAsString());
    h.setTextViewText(R.id.endedTime, d.getStopTimeAsString());

    h.setTextViewText(R.id.distance, d.getTotalDistanceAsString());
    h.setTextViewText(R.id.charge, d.getCurrentCostAsString());

    h.setTextViewText(R.id.averageSpeed, d.getAverageSpeedAsString());

    h.setTextViewText(R.id.started_in, d.getFirstPlace());
    h.setTextViewText(R.id.currently_in, d.getCurrentPlace());
    h.setTextViewText(R.id.ended_in, d.getLastPlace());


    //Revealed if ADVANCED CARDS is set...
    if (h.isAdvancedCardsOn()) {
      h.setTextViewText(R.id.currentHeading, d.getHeadingAsString());
      h.setTextViewText(R.id.currentLatitude, d.getCurrentLatitudeAsString());
      h.setTextViewText(R.id.currentLongitude, d.getCurrentLongitudeAsString());
      h.setTextViewText(R.id.accuracy, d.getLocationAccuracyAsString());
      h.setTextViewText(R.id.loc_qty, d.getLocationQuantityAsString());
      h.setTextViewText(R.id.currentAltitude, d.getCurrentAltitudeAsString());
      h.setTextViewText(R.id.activity, d.getCurrentActivityTypeAsString());
    }

    if (h.isDeveloperMode()) {

    }

    //customise the screen based on Status
    this.showAndHide(journey, h);
  }

  /**
   * Customise the screen based on Status.
   * All layouts are 'GONE' by default as per @style/CardItem's visibility setting.
   *
   * @param ij
   * @param h
   */

  private void showAndHide(ImmutableJourney ij, AndroidUtils h) {

    if (ij.getChronometerBase() != Long.MIN_VALUE) {
      h.revealLayout(R.id.layout_duration);
    }

    //The rest need to be set ONLY if they contain data
    //This list is organised in line with fragment_journey_information.xml...
    h.revealLayoutIfContent(R.id.layout_startedTime, R.id.startedTime);
    h.revealLayoutIfContent(R.id.layout_endedTime, R.id.endedTime);

    h.revealLayoutIfContent(R.id.layout_distance, R.id.distance);
    h.revealLayoutIfContent(R.id.layout_charge, R.id.charge);

    h.revealLayoutIfContent(R.id.layout_average_speed, R.id.averageSpeed);


    h.revealLayoutIfContent(R.id.layout_started_in, R.id.started_in);
    h.revealLayoutIfContent(R.id.layout_currently_in, R.id.currently_in);
    h.revealLayoutIfContent(R.id.layout_ended_in, R.id.ended_in);

    //Revealed ONLY if ADVANCED CARDS is set...
    if (h.isAdvancedCardsOn()) {
      h.revealLayoutIfContent(R.id.layout_heading, R.id.currentHeading);
      h.revealLayoutIfContent(R.id.layout_lattitude, R.id.currentLatitude);
      h.revealLayoutIfContent(R.id.layout_longitude, R.id.currentLongitude);
      h.revealLayoutIfContent(R.id.layout_accuracy, R.id.accuracy);
      h.revealLayoutIfContent(R.id.layout_location_quantity, R.id.loc_qty);
      h.revealLayoutIfContent(R.id.layout_altitude, R.id.currentAltitude);
      h.revealLayoutIfContent(R.id.layout_activity, R.id.activity);
    }

    if (h.isDeveloperMode()) {

    }
    return;
  }

  /**
   * Inner class to handle updating the UI.
   * Can be forced to run on the correct thread.


   class UpdateOnUIThread implements Runnable {

   private JourneyUpdateEvent event;

   public UpdateOnUIThread(JourneyUpdateEvent theEvent) {
   event = theEvent;
   }


   @Override public void run() {
   //setMainText the Fragment...
   JourneyInformationFragment.this.handleEvent(event);
   }
   }
   */
}
