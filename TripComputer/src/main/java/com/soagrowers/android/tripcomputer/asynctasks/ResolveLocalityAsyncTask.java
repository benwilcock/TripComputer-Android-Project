package com.soagrowers.android.tripcomputer.asynctasks;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;

import com.soagrowers.android.tripcomputer.R;
import com.soagrowers.android.tripcomputer.events.LocalityUpdatedEvent;
import com.soagrowers.android.utils.EventManager;
import com.soagrowers.android.utils.Log;
import com.soagrowers.android.utils.StringUtils;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by Ben on 01/08/13.
 */
public class ResolveLocalityAsyncTask extends AsyncTask<Object, Void, String> {

    private static final String TAG = ResolveLocalityAsyncTask.class.getCanonicalName();
    private static final Log log = Log.getInstance();


    public ResolveLocalityAsyncTask() {
    }


    @Override
    protected String doInBackground(Object... params) {

        //unpack the params
        Location location = (Location) params[0];
        Context context = (Context) params[1];

        //calculate the current location name (this can take a while)...
        String place = this.calculateTheCurrentPlaceName(location, context);

        //finally return the findings...
        return place;
    }

    /**
     * This is where we set the new values in the UI
     *
     * @param place
     */
    @Override
    protected void onPostExecute(String place) {
        //call the superclass first
        super.onPostExecute(place);
        log.d(TAG, "Setting the current Place to: " + place);
        LocalityUpdatedEvent event = new LocalityUpdatedEvent(place);
        EventManager.getInstance().post(event);
        //this.mController.updateCurrentPlace(place);
    }

    /**
     * Uses a Geocoder to reverse lookup a place name from a given Location.
     * Requires a Network (mobile or WIFI).
     * Takes a while.
     */
    private String calculateTheCurrentPlaceName(Location loc, Context context) {

        //If no place name could be legitimately determined (i.e. not an IO error, we're off reservation)
        //we'll return the "undetermined" place instead.
        StringBuilder place = new StringBuilder();

        try {
            //get a geocoder to resolve the place name...
            log.v(TAG, "Getting a GEO-Coder");
            Geocoder myLocation = new Geocoder(context, Locale.getDefault());

            //Get the address list for this location from the Geocoder
            List<Address> myList = myLocation.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);

            //if the address list has some entries, get the first address in the list
            if (null != myList && myList.size() > 0) {

                //Getting the first address in the AddressList
                Address address = myList.get(0);
                log.v(TAG, "The current Address is: " + address.toString());

                //if the place (locality) has a value, get it
                if (null == address.getLocality()) {
                    place.append(context.getString(R.string.error_nolocality));
                    // There is no locality, but the address lines may reveal more...
//          if(null != address.getAddressLine(1)) {
//            place = address.getAddressLine(1);
//          } else if(null != address.getAddressLine(2)) {
//            place = address.getAddressLine(2);
//          } else {}
                } else {
                    //get the locality as the place name
                    place.append(address.getLocality());
                }

                if (null != address.getCountryCode()) {
                    place.append(StringUtils.SPACE);
                    place.append(StringUtils.OPEN_BRACKET);
                    place.append(address.getCountryCode());
                    place.append(StringUtils.CLOSE_BRACKET);
                }
            } else {
                log.d(TAG, "The Location has no nearby addresses.");
                place.delete(0, place.length());
                place.append(context.getString(R.string.error_noAddresses));
            }
        } catch (IOException e) {
            //The network timed out or was unavailable
            log.d(TAG, "There was an IOException (Network Error) getting the Locality.", e);
            log.i(TAG, "Location name could not be resolved: " + e.getMessage());
            place.delete(0, place.length());
            place.append(context.getString(R.string.error_ioexception));
        } catch (NullPointerException npe) {
            //no Location fix has been found yet.
            log.e(TAG, "There was an NullPointerException getting the place.", npe);
            log.v(TAG, "NullPointerException message: " + npe.getMessage());
            place.delete(0, place.length());
            place.append(context.getString(R.string.error_nolocality));
        }

        log.d(TAG, "Current Location: " + place);
        return place.toString();
    }
}
