package com.soagrowers.android.tripcomputer;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.github.amlcurran.showcaseview.OnShowcaseEventListener;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.soagrowers.android.tripcomputer.data.Constants;
import com.soagrowers.android.tripcomputer.data.ImmutableJourney;
import com.soagrowers.android.tripcomputer.events.JourneyStartButtonEvent;
import com.soagrowers.android.tripcomputer.events.JourneyStartedEvent;
import com.soagrowers.android.tripcomputer.events.JourneyStopButtonEvent;
import com.soagrowers.android.tripcomputer.events.JourneyStoppedEvent;
import com.soagrowers.android.tripcomputer.events.JourneyUpdatedEvent;
import com.soagrowers.android.utils.AndroidUtils;
import com.soagrowers.android.utils.EventManager;
import com.soagrowers.android.utils.Log;


/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link com.soagrowers.android.tripcomputer.ButtonBarFragment.TripComputerActions} interface
 * to handle interaction events.
 */
public class ButtonBarFragment extends Fragment
  implements View.OnClickListener, OnShowcaseEventListener {

  //Used in LOGS
  private static final String TAG = ButtonBarFragment.class.getSimpleName();
  private Log log = Log.getInstance();

  public ButtonBarFragment() {
    // Required empty public constructor
  }


  @Override
  public void onCreate(Bundle savedInstanceState) {
    //call the super class first
    super.onCreate(savedInstanceState);
    log = Log.getInstance(getActivity());
    return;
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
    //View buttonBar = inflater.inflate(R.layout.fragment_button_bar, container, false);
    View buttonBar = inflater.inflate(R.layout.fragment_floating_button_bar, container, false);

    //Attach this Fragment to the buttons
    //LinearLayout b = (LinearLayout) buttonBar.findViewById(R.id.button_start);
    FloatingActionButton b = (FloatingActionButton) buttonBar.findViewById(R.id.button_start);
    b.setOnClickListener(this);
    //b = (LinearLayout) buttonBar.findViewById(R.id.button_stop);
    b = (FloatingActionButton) buttonBar.findViewById(R.id.button_stop);
    b.setOnClickListener(this);
    return buttonBar;
  }

  /**
   * Called when a view has been clicked.
   *
   * @param v The view that was clicked.
   */

  @Override
  public void onClick(View v) {

    switch (v.getId()) {

      case R.id.button_start:
        log.v(TAG, "User clicked START button");
        this.handleStart(v);
        break;

      case R.id.button_stop:
        log.v(TAG, "User clicked STOP button");
        this.handleStop(v);
        break;

      default:
        log.e(TAG, "Unknown button clicked!");
        return;
    }

    return;
  }

  /**
   * Deals with the START button being pressed.
   * Manages the button state and visibility.
   * Delegates the startJourney activity to the parent activity.
   *
   * @param view
   */

  public void handleStart(View view) {

    EventManager.getInstance().post(new JourneyStartButtonEvent());
    Tracker t = ((TripComputerApplication) this.getActivity().getApplication()).getTracker(TripComputerApplication.TrackerName.APP_TRACKER);
    t.send(new HitBuilders.EventBuilder()
      .setCategory(Constants.UI_CATEGORY)
      .setAction(Constants.START_ACTION)
      .build());
    return;
  }


  public void handleStop(View view) {

    EventManager.getInstance().post(new JourneyStopButtonEvent());
    Tracker t = ((TripComputerApplication) this.getActivity().getApplication()).getTracker(TripComputerApplication.TrackerName.APP_TRACKER);
    t.send(new HitBuilders.EventBuilder()
      .setCategory(Constants.UI_CATEGORY)
      .setAction(Constants.STOP_ACTION)
      .build());
    return;
  }

  /**
   * Make sure the screen buttons reflect the status of the Journey.
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
    AndroidUtils h = AndroidUtils.getInstance(getActivity());

    //Journey has NOT started yet.
    if (!journey.isStarted()) {
      log.v(TAG, "The Journey is yet to begin.");

      //hide the Stop button
      h.hideButton(R.id.button_stop);

      //reveal the Start button
      h.revealButton(R.id.button_start);
    }

    //Journey HAS started.
    if (journey.isRunning()) {
      log.v(TAG, "The Journey is already underway.");

      //hide the startJourney button
      h.hideButton(R.id.button_start);

      //reveal the stopJourney button & current location
      h.revealButton(R.id.button_stop);
    }

    //Journey has FINISHED.
    if (journey.isFinished()) {
      log.v(TAG, "There was a Journey but it's finished.");

      //hide the Stop button
      h.hideButton(R.id.button_stop);

      //reveal the Start button
      h.revealButton(R.id.button_start);
    }
  }

  @Override
  public void onShowcaseViewHide(ShowcaseView showcaseView) {
    View startButton = getActivity().findViewById(R.id.button_start);
    startButton.setEnabled(true);
    startButton.setClickable(true);
  }

  @Override
  public void onShowcaseViewDidHide(ShowcaseView showcaseView) {
    View startButton = getActivity().findViewById(R.id.button_start);
    startButton.setEnabled(true);
    startButton.setClickable(true);
  }

  @Override
  public void onShowcaseViewShow(ShowcaseView showcaseView) {
    View startButton = getActivity().findViewById(R.id.button_start);
    startButton.setEnabled(false);
    startButton.setClickable(false);
  }


  /**
   * This interface must be implemented by activities that contain this
   * fragment to allow an interaction in this fragment to be communicated
   * to the activity and potentially other fragments contained in that
   * activity.
   * <p/>
   * See the Android Training lesson <a href=
   * "http://developer.android.com/training/basics/fragments/communicating.html"
   * >Communicating with Other Fragments</a> for more information.
   */
  public interface TripComputerActions {
    boolean startJourney();

    void stopJourney();
  }
}
