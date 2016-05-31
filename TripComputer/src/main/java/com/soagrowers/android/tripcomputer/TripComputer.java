package com.soagrowers.android.tripcomputer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.android.vending.billing.IInAppBillingService;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.common.base.Optional;
import com.soagrowers.android.tripcomputer.data.Skus;
import com.soagrowers.android.tripcomputer.services.JourneyServiceConnectionManager;
import com.soagrowers.android.utils.AndroidUtils;
import com.soagrowers.android.utils.Enums;
import com.soagrowers.android.utils.Log;
import com.soagrowers.android.utils.StringUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * This Activity is designed to be very dumb. Control of the Journey is delegated
 * to the JourneyService. This Activity will bind to the Service at startup and
 * then register itself for updates once the service feels that something has changed.
 * <p/>
 * If the activity stops and is destroyed and another starts (such as when using the
 * Notification Intent), the activity just binds to the service to get the details
 * of the current Journey before displaying the details and drawing it's buttons etc.
 * <p/>
 * In the Manifest, the activity mode is SingleTop. This is to satisfy the needs of
 * The Options menu activities which can get confused if the standard more is used
 * when multiple Activities have been created and destroyed.
 * <p/>
 * When the service feels that something has changed, it calls the onJourneyUpdate()
 * method to prompt the activity to setMainText the screen. Care should be taken in this
 * method when accessing UI features such as Views and Chronographs as using the wrong
 * client reference results in strange things happenning to the screen (blanks mostly).
 */
public class TripComputer extends AbstractGooglePlayServicesActivity implements
  BackgroundServiceClient {

  //Logging TAG
  private static final String TAG = TripComputer.class.getSimpleName();

  //Dialogue's and Toasts
  private static boolean mShowLoadingJourneyToast = true;
  private static JourneyServiceConnectionManager journeyServiceConnection;
  /**
   * Used for In-App Billing.
   * Info here...
   * http://developer.android.com/google/play/billing/billing_integrate.html#billing-permission
   */

  IInAppBillingService mService;
  ServiceConnection mServiceConn = new ServiceConnection() {
    @Override
    public void onServiceDisconnected(ComponentName name) {
      mService = null;
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
      mService = IInAppBillingService.Stub.asInterface(service);
    }
  };
  //Helper classes for general tasks
  private AndroidUtils h;
  private Log log;
  //Service & Service Connection related properties
  private ShareActionProvider mShareActionProvider;
  private ShowcaseView sv;

  /**
   * Android L (lollipop, API 21) introduced a new problem when trying to invoke implicit intent,
   * "java.lang.IllegalArgumentException: Service Intent must be explicit"
   * <p/>
   * If you are using an implicit intent, and know only 1 target would answer this intent,
   * This method will help you turn the implicit intent into the explicit form.
   * <p/>
   * Inspired from SO answer: http://stackoverflow.com/a/26318757/1446466
   *
   * @param context
   * @param implicitIntent - The original implicit intent
   * @return Explicit Intent created from the implicit original intent
   */
  public Optional<Intent> createExplicitFromImplicitIntent(Context context, Intent implicitIntent) {
    // Retrieve all services that can match the given intent
    PackageManager pm = context.getPackageManager();
    List<ResolveInfo> resolveInfo = pm.queryIntentServices(implicitIntent, 0);

    // Make sure only one match was found
    if (resolveInfo == null || resolveInfo.size() == 0){
      log.e(TAG, "Intent service query could not find a match for " + implicitIntent.getAction());
      return Optional.absent();
    } else if (resolveInfo.size() > 1){
      log.e(TAG, "Intent service query found more than one match for " + implicitIntent.getAction());
      return Optional.absent();
    }

    // Get component info and create ComponentName
    ResolveInfo serviceInfo = resolveInfo.get(0);
    String packageName = serviceInfo.serviceInfo.packageName;
    String className = serviceInfo.serviceInfo.name;
    ComponentName component = new ComponentName(packageName, className);

    // Create a new intent. Use the old one for extras and such reuse
    Intent explicitIntent = new Intent(implicitIntent);

    // Set the component to be explicit
    explicitIntent.setComponent(component);

    return Optional.of(explicitIntent);
  }

  /**
   * Callback that is called by the OS to let us know when a Service binding has
   * occurred between this class and a background Service.
   * <p/>
   * Override from JourneyServiceBinder
   *
   * @see com.soagrowers.android.tripcomputer.services.JourneyServiceConnectionManager
   */

  @Override
  public void onServiceConnected(Enums.SERVICE_TYPE service) {
    this.h = AndroidUtils.getInstance(this);

    switch (service) {

      case JOURNEY_SERVICE:
        log.i(TAG, "CONNECTED to the JourneyService.");
        break;

      default:
        break;
    }
  }

  /**
   * The Journey Service has CRASHED or Stopped UNEXPECTEDLY.
   */

  @Override
  public void onServiceDisconnected(Enums.SERVICE_TYPE serviceName) {
    this.h = AndroidUtils.getInstance(this);

    switch (serviceName) {

      case JOURNEY_SERVICE:
        log.w(TAG, "DISCONNECTED from the JourneyService.");
        break;

      default:
        break;
    }
  }

  /**
   * Initialises the activity.
   * The system will call onRestoreInstanceState() if the app was previously stopped.
   * The system then calls onStart() followed by onResume().
   * Override from Activity
   *
   * @param savedInstanceState
   */

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    //Do the stuff that should happen before calling the superclass
    //Includes initialising the LOG...
    performPreSuperTasks();

    //Now call the superclass
    super.onCreate(savedInstanceState);

    if (super.isPlayServicesAvailable()) {

      // Dump out the state...
      setupTheLogAndLogTheSetup();

      //Initialise the preferences
      //PreferenceManager.getDefaultSharedPreferences(this).edit().clear().commit();
      //PreferenceManager.setDefaultValues(this, R.xml.preferences, true);
      PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

      //Get a Journey Service Manager
      journeyServiceConnection = JourneyServiceConnectionManager.getInstance(this);

      //Attempt to start the service
      journeyServiceConnection.startService(this);

      //Start the In App Billing helper
      Optional<Intent> explicitIntent = createExplicitFromImplicitIntent(this, new Intent("com.android.vending.billing.InAppBillingService.BIND"));
      if(explicitIntent.isPresent()) {
        bindService(explicitIntent.get(),
          mServiceConn,
          Context.BIND_AUTO_CREATE
        );
      } else {
        log.e(TAG, "The Explicit Intent for the InApp Billing Service could not be created.");
      }

      //Now create the VIEW
      setContentView(R.layout.activity_journey_runner);
      //Toolbar toolbar = (Toolbar)findViewById(R.id.trip_computer_toolbar);
      //setSupportActionBar(toolbar);

      // Showcase the new features (if first run)
      showcaseNewFeatures();

      //Log the App Version
      log.i(TAG, "CREATED TripComputer Activity: " + this.hashCode());
    }

    return;
  }

  private void showcaseNewFeatures() {
    // Add a ShowcaseView Layout...
    RelativeLayout.LayoutParams buttonPos = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    buttonPos.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
    buttonPos.addRule(RelativeLayout.CENTER_VERTICAL);
    int small_margin = ((Number) (getResources().getDisplayMetrics().density * 24)).intValue();
    if (small_margin < 1) {
      small_margin = 1;
    }
    buttonPos.setMargins(small_margin, small_margin, small_margin, small_margin);

    // Create ONE SHOT ShowcaseView
    sv = new ShowcaseView.Builder(this, false)
      .setTarget(new ViewTarget(R.id.button_start, this))
      .setContentTitle(R.string.showcase_getting_started_txt)
      .setContentText(R.string.showcase_start_instruction_txt)
      .singleShot(h.getShowcaseShotNumber())
      .setStyle(R.style.TripComputerShowcaseTheme)
      .build(); // Displays the view

    // Set some final prefs...
    sv.setButtonText(getString(R.string.showcase_gotit_button_txt));
    sv.setHideOnTouchOutside(false);
    sv.setButtonPosition(buttonPos);
  }

  @Override
  public int hashCode() {
    return super.hashCode();
  }

  /**
   * The user is Stopping the Activity, such as when they go to the RecentApps screen.
   * The system will then call onRestart() if the activity is re-activated.
   * <p/>
   * Override from Activity
   */

  @Override
  protected void onStop() {

    log.i(TAG, "STOPPING TripComputer Activity: " + this.hashCode());

    if (super.isPlayServicesAvailable()) {

      //Stop the analytics tracking
      GoogleAnalytics.getInstance(this).reportActivityStop(this);

      // Unbind from the service (it will keep running in the background)
      journeyServiceConnection.unBindService(this, this);
    }

    //Log our status...
    log.i(TAG, "STOPPED TripComputer Activity: " + this.hashCode());

    //empty the Log to the LogFile
    log.flushLog();

    //now chain to the superclass
    super.onStop();
    //onDestroy() is logically next, but onRestart() is possible.
  }

  private void setupTheLogAndLogTheSetup() {

    log.i(TAG, "TripComputer VERSION: " + h.getAppVersion().replace(StringUtils.NEW_LINE, StringUtils.SPACE));

    //Check for developer mode behaviours
    if (BuildConfig.DEBUG) {
      this.log.i(TAG, "Debug: ON");

      //Turn on Exceptions for thread performance warnings
      if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {

        StrictMode.enableDefaults();
        this.log.i(TAG, "Strict Mode: ON");
      }
    }

    // Get the status of analytics tracking...
    if (GoogleAnalytics.getInstance(this).isDryRunEnabled()) {
      this.log.i(TAG, "Analytics Tracking Dry Run: ON");
    } else {
      this.log.i(TAG, "Analytics Tracking Dry Run: OFF");
    }

    // Pre-configure Google Analytics Tracking...
    TripComputerApplication app = (TripComputerApplication) this.getApplication();
    app.getTracker(TripComputerApplication.TrackerName.APP_TRACKER);

    //Check if Auto Start/Stop is on...
    if (h.isAutoStartStopEnabled()) {
      this.log.i(TAG, "Auto Start: ON");
    } else {
      this.log.i(TAG, "Auto Start: OFF");
    }

    //Check if Advanced Cards are on...
    if (h.isAdvancedCardsOn()) {
      this.log.i(TAG, "Advanced Cards: ON");
    } else {
      this.log.i(TAG, "Advanced Cards: OFF");
    }
  }

  /**
   * Called by onCreate()
   * Stuff that should happen before call to super()...
   */

  private void performPreSuperTasks() {

    try {
      //Try to get a customised Log instance first (used everywhere)...
      log = Log.getInstance(this);

      //send our first LOG entry...
      log.i(TAG, "CREATING TripComputer Activity: " + this.hashCode());

      // Trying to get an instance of AndroidUtils
      // (second because AndroidUtils depends on Log)
      this.h = AndroidUtils.getInstance(this);

    } catch (com.soagrowers.android.utils.InstantiationException e) {
      //Can't do much without it so stop now...
      android.util.Log.e(TAG, "TripComputer's pre-launch tasks have FAILED", e);
      throw e;
    }

    //check if the Theme needs changing...
    //this.nightMode = this.h.isNightMode();
    if (this.h.isNightMode()) {
      this.setTheme(R.style.AppBaseThemeNight); //MUST be before super.onCreate()!
      log.i(TAG, "Night Theme: ON");
    } else {
      this.setTheme(R.style.AppBaseThemeDay); //MUST be before super.onCreate()!
      log.i(TAG, "Night Theme: OFF");
    }

    //Check if the screen should stay on while running...
    //this.stayAwake = h.isStayAwakeMode();
    if (h.isStayAwakeMode()) {
      //ask for the screen to stay on
      this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
      this.log.i(TAG, "Stay Awake: ON");
    } else {
      this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
      this.log.i(TAG, "Stay Awake: OFF");
    }
  }

  /**
   * Perform any final cleanup before an activity is destroyed.  This can
   * happen either because the activity is finishing (someone called
   * {@link #finish} on it, or because the system is temporarily destroying
   * this instance of the activity to save space.  You can distinguish
   * between these two scenarios with the {@link #isFinishing} method.
   * <p/>
   * <p><em>Note: do not count on this method being called as a place for
   * saving data! For example, if an activity is editing data in a content
   * provider, those edits should be committed in either {@link #onPause} or
   * {@link #onSaveInstanceState}, not here.</em> This method is usually implemented to
   * free resources like threads that are associated with an activity, so
   * that a destroyed activity does not leave such things around while the
   * rest of its application is still running.  There are situations where
   * the system will simply kill the activity's hosting process without
   * calling this method (or any others) in it, so it should not be used to
   * do things that are intended to remain around after the process goes
   * away.
   * <p/>
   * <p><em>Derived classes must call through to the super class's
   * implementation of this method.  If they do not, an exception will be
   * thrown.</em></p>
   *
   * @see #onPause
   * @see #onStop
   * @see #finish
   * @see #isFinishing
   */

  @Override
  protected void onDestroy() {

    log.i(TAG, "DESTROYING TripComputer Activity: " + this.hashCode());

    if (super.isPlayServicesAvailable()) {

      //Stop the service (this will only work if the journey has finished)
      journeyServiceConnection.stopService(this);

      // Unbind from InAppBilling
      if (mService != null) {
        unbindService(mServiceConn);
      }
    }

    //empty the Log to the LogFile
    log.i(TAG, "DESTROYED TripComputer Activity: " + this.hashCode());
    log.flushLog();
    log = null;
    h = null;

    //call the parent to do it's final tasks...
    super.onDestroy();
  }

  /**
   * Still visible, but paused because something else came to the front.
   * onPause is followed by onResume() (if the activity resumes after the pause) or onStop()
   * if the activity is stopping completely.
   */

  @Override
  protected void onPause() {
    log.i(TAG, "PAUSING TripComputer Activity: " + this.hashCode());
    log.i(TAG, "PAUSED TripComputer Activity: " + this.hashCode());
    super.onPause();
    //onResume() or onStop(), depending on the system.
  }

  protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
    log.i(TAG, "New intent received for TripComputer " + this.hashCode() + " with flags " + intent.getFlags());
  }

  /**
   * This is called when the app returns to a running state,
   * or achieves a running state for the first time.
   * <p/>
   * Called by the system after onStart().
   * <p/>
   * If the user stops the app the next call will be to onPause().
   */

  @Override
  protected void onResume() {
    super.onResume();
    this.h = AndroidUtils.getInstance(this);

    if (super.isPlayServicesAvailable()) {

      log.i(TAG, "RESUMING TripComputer Activity: " + this.hashCode());

      //If we're in NIGHT_MODE, Dim the system buttons...
      View me = this.findViewById(R.id.activity_journey_runner);
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
        if (h.isNightMode()) {
          me.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
        } else {
          me.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        }
      }

      log.i(TAG, "RESUMED TripComputer Activity: " + this.hashCode());

      //If we switched Theme's, a reboot is required so onCreate is called...
      if (h.isRebootRequired()) {

        //First set the REBOOT preference back so we don't loop...
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit().putBoolean(Keys.REBOOT_ACTIVITY_KEY, false);
        h.applyPreferenceChanges(editor);

        //Now Restart the activity so the Theme will be changed...
        log.i(TAG, "FINISHING of the TripComputer Activity: " + this.hashCode());
        AndroidUtils.restartActivity(this);
      }
    }
  }

  /**
   * Save the Activities basic state attribs because the App is shutting down
   *
   * @param outState
   */

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    outState.putBoolean(Keys.SHOW_LOADING_JOURNEY_TOAST_KEY, this.mShowLoadingJourneyToast);
    super.onSaveInstanceState(outState);
  }

  /**
   * Called when the activity is first starting (after onCreate()) and when the activity is
   * restarting (after onRestart()). The system then calls onResume().
   */

  @Override
  protected void onStart() {

    super.onStart();

    if (super.isPlayServicesAvailable()) {

      log.i(TAG, "STARTING TripComputer Activity: " + this.hashCode());

      //Get an Analytics tracker to report uncaught exceptions etc.
      GoogleAnalytics.getInstance(this).reportActivityStart(this);

      boolean bound = journeyServiceConnection.bindService(this, this);

      if (bound == false) {
        log.e(TAG, "BINDING FAILURE. TripComputer cannot start without the JourneyService. QUITTING.");
        h.doLongToast(R.string.error_service_startup);
        this.finish();
      }

      //Log that we've started
      log.i(TAG, "STARTED TripComputer Activity: " + this.hashCode());
      //the next call is to onResume()
    }
  }

  /**
   * Restore the Activities basic state attribs because the App is starting up
   * Override from Activity
   *
   * @param state
   */

  @Override
  protected void onRestoreInstanceState(Bundle state) {
    //call the parent first
    super.onRestoreInstanceState(state);
    if (state != null) {
      this.mShowLoadingJourneyToast = state.getBoolean(Keys.SHOW_LOADING_JOURNEY_TOAST_KEY);
    }
  }

  /**
   * The user is Restarting the activity, for example when returning from the Recent Apps screen
   * or from the SettingsActivity. The system then calls onStart() also.
   * Override from Activity
   */

  @Override
  protected void onRestart() {
    super.onRestart();
    log.i(TAG, "RESTARTING TripComputer Activity: " + this.hashCode());
    log.i(TAG, "RESTARTED TripComputer Activity: " + this.hashCode());
  }

  /**
   * Inflates the options menu in the ActionBar
   * Override from Activity
   *
   * @param menu
   * @return
   */

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {

    if (super.isPlayServicesAvailable()) {


      //Inflate the menu
      getMenuInflater().inflate(R.menu.tripcomputer_actionbar, menu);

      try {
        // Set up ShareActionProvider's share Intent
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.action_share_subject_line));
        intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.url_app));

        //Add the ShareActionProvider to the menu item 'Share App'
        MenuItem shareItem = menu.findItem(R.id.action_share);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);
        mShareActionProvider.setShareIntent(intent);
      } catch (Exception e) {
        log.e(TAG, "There was a problem setting up the share action. Removing it from the menu", e);
        menu.removeItem(R.id.action_share);
      }

      // Remove the show products feature.
      this.h = AndroidUtils.getInstance(this);
      if (!h.getBoolean(R.bool.feature_showProducts)) {
        menu.removeItem(R.id.action_get_products);
      }

      return true;
    } else {
      return false;
    }
  }

  /**
   * Handles the ActionBar Menu Items being selected.
   * Override from Activity
   *
   * @param item
   * @return
   */

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {

    //Create an Intent local variable to handle the forward actions.
    Intent intent;

    // Handle item selection
    switch (item.getItemId()) {

      case R.id.action_settings:
        intent = new Intent(this, SettingsActivity.class);
        this.startActivity(intent);
        return true;

      case R.id.action_version:
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.dialog_title_version);
        builder.setMessage(h.getAppVersion());
        builder.setIcon(R.drawable.ic_launcher);
        builder.setPositiveButton(android.R.string.ok, null);
        builder.show();
        return true;

      case R.id.action_get_products:
        showProducts();
        return true;

      case R.id.action_help:
        intent = new Intent(Intent.ACTION_VIEW, Uri.parse(h.getString(R.string.url_help)));
        startActivity(intent);
        return true;

      case R.id.action_rate:
        intent = new Intent(Intent.ACTION_VIEW, Uri.parse(h.getString(R.string.url_rate)));
        startActivity(intent);
        return true;

      default:
        return super.onOptionsItemSelected(item);
    }
  }

  /**
   * In-App Billing Implementation....
   */

  private void showProducts() {

    log.i(TAG, "IAB: Checking for billable products.");
    String packName = getPackageName(); //"com.soagrowers.android.tripcomputer"; //getPackageName();

    StringBuilder results = new StringBuilder(packName);
    results.append(StringUtils.NEW_LINE);

    try {
      // todo: DON'T DO THIS ON THE UI THREAD!!!!!!!!
      Bundle skuDetails = mService.getSkuDetails(3, packName, Skus.SKU_TYPE_INAPP, Skus.getSkuListAsBundle());
      int response = skuDetails.getInt("RESPONSE_CODE");
      results.append("ResponseCode: " + response);
      results.append(StringUtils.NEW_LINE);

      if (response == 0) {
        ArrayList<String> responseList
          = skuDetails.getStringArrayList("DETAILS_LIST");

        if (responseList.size() < 1) {
          log.w(TAG, "IAB: No Products found!");
        }

        for (String thisResponse : responseList) {
          JSONObject object = new JSONObject(thisResponse);
//          results.append("ProductId: ");
//          results.append(object.getString(Skus.SKU_DETAILS_KEY_ID));
          results.append(" Title: ");
          results.append(object.getString(Skus.SKU_DETAILS_KEY_TITLE));
          results.append(" Desc: ");
          results.append(object.getString(Skus.SKU_DETAILS_KEY_DESC));
          results.append(" Price: ");
          results.append(object.getString(Skus.SKU_DETAILS_KEY_PRICE));
          results.append(StringUtils.NEW_LINE);
        }

        log.i(TAG, "IAB Result: " + results.toString());

      } else {
        // Codes: http://developer.android.com/google/play/billing/billing_reference.html
        log.w(TAG, "IAB Failed: ResponseCode - " + response);
      }
    } catch (RemoteException e) {
      e.printStackTrace();
      log.e(TAG, "IAB Failure", e);
    } catch (JSONException e) {
      e.printStackTrace();
      log.e(TAG, "IAB JSON Failure", e);
    }

    Dialog dialog = new AlertDialog.Builder(this)
      .setTitle("IAB Qry Results")
      .setMessage(results.toString())
      .setPositiveButton("OK", null)
      .create();
    dialog.show();
  }
}