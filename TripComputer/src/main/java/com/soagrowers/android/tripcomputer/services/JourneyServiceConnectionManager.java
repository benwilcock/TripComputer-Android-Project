package com.soagrowers.android.tripcomputer.services;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.soagrowers.android.tripcomputer.BackgroundServiceClient;
import com.soagrowers.android.tripcomputer.events.JourneyStatusEvent;
import com.soagrowers.android.utils.AndroidUtils;
import com.soagrowers.android.utils.Assert;
import com.soagrowers.android.utils.Enums;
import com.soagrowers.android.utils.EventManager;
import com.soagrowers.android.utils.Log;
import com.soagrowers.android.utils.StringUtils;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Created by Ben on 03/09/13.
 */
public final class JourneyServiceConnectionManager implements ServiceConnection {

    private static final String TAG = JourneyServiceConnectionManager.class.getSimpleName();
    private static JourneyServiceConnectionManager instance;
    private static Log log = null;
    private static JourneyService service;
    private static Map<Integer, BackgroundServiceClient> whoIsBound;
    private static boolean isRunning = false;


    /**
     * Private Singleton instance constructor.
     */
    private JourneyServiceConnectionManager() {
        whoIsBound = new ConcurrentHashMap<Integer, BackgroundServiceClient>(10);
        EventManager.getInstance().register(this);
    }

    /**
     * STEP 1.
     * Used by clients to get a manager for their service connection to the JourneyService.
     * If no instance exists it creates one. If there is an instance already it returns it.
     *
     * @param theContext
     * @return
     */

    public static JourneyServiceConnectionManager getInstance(Context theContext) {
        Assert.notNull(theContext);
        log = Log.getInstance(theContext);
        if (null == instance) {
            instance = new JourneyServiceConnectionManager();
        }

        Assert.notNull(instance);
        return instance;
    }


    /**
     * Used by clients to ascertain if the service is available before they call it.
     * Return true if there is a service object, returns false if the service object is null.
     *
     * @return
     */

    public boolean serviceIsAvailable() {
        boolean serviceAvailable = false;

        if (null != service) {
            serviceAvailable = true;
        }

        return serviceAvailable;
    }

    /**
     * STEP 2.
     * Used by clients to request that the service be started on their behalf.
     * Services that are started before being bound are kept alive
     * until specifically stopped. Idempotent, so always called even if the
     * service has already been started. Service won't stop until 'stopService'
     * is called (even if all clients are unbound). Calls 'onStartCommand' on
     * the JourneyService.
     *
     * @param theContext The Activity that will be used as the Intent reference
     *                   and the activity who's 'startService' method will be used.
     */

    public void startService(Context theContext) {

        if (!serviceIsAvailable()) {
            log.i(TAG, "Activity " + theContext.hashCode() + " is STARTING the JourneyService...");
            //Call is idempotent, and ensures onStartCommand is called on the service.
            Intent journeyServiceIntent = new Intent(theContext, JourneyService.class);
            theContext.startService(journeyServiceIntent);
            log.d(TAG, "STARTED the JourneyService");
        }

        logServiceStatus();
    }

    /**
     * STEP 3.
     * Clients use this method to ask for a binding to be established to the
     * JourneyService component on their behalf. Creates an Intent (using the
     * given activity), uses this class as the ServiceConnection which is
     * to receive the callbacks, and binds to the service in BIND_AUTO_CREATE mode.
     * Calls 'bindService' on the given Activity ONLY when this activity is not already
     * listed as a 'bound' activity. Remebers all Activities that receive a binding.
     * <p/>
     * OS must treat all bindings as seperate, that's why we need to remember who has one
     * so that we don't create multiple bindings for each Activity instance (of which there can
     * be many, each in different states)!
     * <p/>
     * You should usually NOT bind and unbind during your activity's onResume() and onPause(),
     * because these callbacks occur at every lifecycle transition and you should keep the
     * processing that occurs at these transitions to a minimum.
     *
     * @param theContext The Activity that will be used as the Intent reference
     *                   and the activity who's 'bindService' method will be used.
     */

    public boolean bindService(Context theContext, BackgroundServiceClient theClient) {
        log.d(TAG, "BINDING to the JourneyService...");

        boolean serviceWasBound = false;

        if (!whoIsBound.containsKey(theClient.hashCode())) {

            //This Activity is not currently bound, so create an new Intent
            Intent journeyServiceIntent = new Intent(theContext, JourneyService.class);

            //request a new binding for the Activity
            serviceWasBound = theContext.bindService(journeyServiceIntent, instance, Context.BIND_AUTO_CREATE);

            if (serviceWasBound) {
                // Add the Client to the list of bound clients
                whoIsBound.put(theClient.hashCode(), theClient);
                log.d(TAG, "New Service binding requested. Waiting for callback (to onServiceConnected())");
            } else {
                // Log & throw an error (could be a leaked connection!)
                IllegalStateException ise = new IllegalStateException("BINDING FAILED. JourneyService will be unavailable.");
                log.e(TAG, ise.getMessage(), ise);
                throw ise;
            }

        } else {
            // This client already has a binding, so we'll return it to prevent a leak...
            log.d(TAG, theContext.hashCode() + " is already BOUND to the JourneyService");
            serviceWasBound = true;
            BackgroundServiceClient client = whoIsBound.get(theClient.hashCode());
            client.onServiceConnected(Enums.SERVICE_TYPE.JOURNEY_SERVICE);
        }

        return serviceWasBound;
    }

    /**
     * Called when a connection to the Service has been established, with
     * the {@link android.os.IBinder} of the communication channel to the
     * Service.
     *
     * @param name          The concrete component name of the service that has
     *                      been connected.
     * @param serviceBinder The IBinder of the Service's communication channel,
     */
    @Override

    public void onServiceConnected(ComponentName name, IBinder serviceBinder) {
        log.d(TAG, "Connected to Service component: " + name.flattenToShortString());

        // We've bound to LocalService, cast the IBinder and get LocalService instance
        JourneyService.JourneyServiceBinder binderServiceReference = (JourneyService.JourneyServiceBinder) serviceBinder;
        service = binderServiceReference.getService();
        Assert.notNull(service);

        log.d(TAG, "BOUND to the JourneyService");
        logServiceStatus();

        Collection<BackgroundServiceClient> clients = whoIsBound.values();
        for (BackgroundServiceClient client : clients) {
            client.onServiceConnected(Enums.SERVICE_TYPE.JOURNEY_SERVICE);
        }
    }

    /**
     * Used by clients to get a reference to the service.
     * Asserts that the service is not null (in Build.DEBUG mode)
     *
     * @return JourneyServiceContract
     * @throws com.soagrowers.android.tripcomputer.services.ServiceConnectionException when the manager has not been properly initialised.
     */

    public JourneyService getService() {

        if (serviceIsAvailable()) {
            return service;
        } else {
            ServiceConnectionException sce = new ServiceConnectionException("The service hasn't been bound yet. Has everything been properly initialised?");
            log.e(TAG, sce.getMessage(), sce);
            throw sce;
        }
    }

    /**
     * STEP 4.
     * Called when a client Activity wants to unbind from the service because it is STOPPING.
     * If the client Activity is in the list of those with a binding, the Activity's
     * 'unbindService' will be called using a reference to this
     * JourneyServiceConnectionManager class as it's parameter. The client Activity is then
     * removed from the list of those who have a binding, so that multiple 'unbind' requests
     * do not happen for the same client.
     * <p/>
     * You should usually NOT bind and unbind during your activity's onResume() and onPause(),
     * because these callbacks occur at every lifecycle transition and you should keep the
     * processing that occurs at these transitions to a minimum.
     *
     * @param theContext The Activity who's 'unbindService' method will be used.
     */

    public void unBindService(Context theContext, BackgroundServiceClient theClient) {

        if (whoIsBound.containsKey(theClient.hashCode())) {
            try {
                log.d(TAG, "UNBINDING from the JourneyService");
                theContext.unbindService(this);
                whoIsBound.remove(theClient.hashCode());
                logServiceStatus();
                log.d(TAG, "UNBOUND from the JourneyService");
            } catch (Exception e) {
                log.e(TAG, "Failed to UNBIND from the JourneyService: " + e.getMessage(), e);
            }
        }
    }

    /**
     * STEP 5.
     * Called when the client would like to stop the service because it is being destroyed.
     * If there is still a journey running, the service must continue to run in the background
     * and the request to 'stopService' is ignored. If the list of bound clients is not
     * empty, an attempt to stop whithout an unbind is being made.
     *
     * @param theContext The Activity that will be used as the Intent reference
     *                   and the activity who's 'stopService' method will be used.
     * @return
     */

    public boolean stopService(Context theContext) {
        Assert.notNull(theContext);
        AndroidUtils h = AndroidUtils.getInstance(theContext);

        log.i(TAG, "Context " + theContext.hashCode() + " is STOPPING the JourneyService...");
        boolean stopResult = false;

        if (
                (serviceIsAvailable() && isRunning)
                        ||
                        h.isAutoStartStopEnabled()
                ) {
            // Fast fail if there is a Journey running.
            // Fast fail if the AUTO START STOP is ON.
            log.d(TAG, "IGNORING a request to STOP the JourneyService.");
            return stopResult;
        }

        if (!serviceIsAvailable() || whoIsBound.size() == 0) {
            //only stop after the last client has already left or the service was Disconnected...
            Intent journeyServiceIntent = new Intent(theContext, JourneyService.class);
            stopResult = theContext.stopService(journeyServiceIntent);
            service = null;
            log.d(TAG, "STOPPED the JourneyService");
        } else {
            log.d(TAG, "IGNORING request to STOP the JourneyService - Some clients are still bound.");
        }

        logServiceStatus();
        return stopResult;
    }


    /**
     * Called when a connection to the Service has been lost.  This typically
     * happens when the process hosting the service has crashed or been killed.
     * This does <em>not</em> remove the ServiceConnection itself -- this
     * binding to the service will remain active, and you will receive a call
     * to {@link #onServiceConnected} when the Service is next running.
     *
     * @param name The concrete component name of the service whose
     *             connection has been lost.
     */
    @Override

    public void onServiceDisconnected(ComponentName name) {
        log.d(TAG, "Disconnected service component: " + name.flattenToShortString());

        //Throw out the service and the list of who was bound to it
        service = null;
        whoIsBound.clear();

        log.d(TAG, "JourneyServiceConnection has been DISCONNECTED");
        logServiceStatus();

        //Tell the clients we were disconnected...
        Collection<BackgroundServiceClient> clients = whoIsBound.values();
        for (BackgroundServiceClient client : clients) {
            client.onServiceDisconnected(Enums.SERVICE_TYPE.JOURNEY_SERVICE);
        }
    }


    /**
     * Dumps the status of the Service at this time.
     * If there is a service object, it also dumps the running states.
     */

    private void logServiceStatus() {

        StringBuilder sb = new StringBuilder();

        sb.append("Service isAvailable: " + serviceIsAvailable());
        if (serviceIsAvailable()) {
            sb.append(StringUtils.PERIOD);
            sb.append(StringUtils.SPACE);
            sb.append("Journey RUNNING: " + isRunning);
        }

        sb.append(" Bindings:");

        for (Integer i : whoIsBound.keySet()) {
            sb.append(StringUtils.SPACE);
            sb.append(i);
        }

        //Log the Service Status.
        Log.getInstance().d(TAG, sb.toString());
    }

    public void onEvent(JourneyStatusEvent event) {
        switch (event.getType()) {

            case START_EVENT:
                isRunning = true;
                break;

            case STOP_EVENT:
                isRunning = false;
                break;
        }
    }
}
