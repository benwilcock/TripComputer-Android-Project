package com.soagrowers.android.tripcomputer.data;

import android.content.Context;
import android.os.SystemClock;
import android.text.format.DateFormat;
import android.widget.Chronometer;

import com.google.android.gms.location.DetectedActivity;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.soagrowers.android.tripcomputer.R;
import com.soagrowers.android.utils.AndroidUtils;
import com.soagrowers.android.utils.Assert;
import com.soagrowers.android.utils.ConversionUtils;
import com.soagrowers.android.utils.Log;
import com.soagrowers.android.utils.StringUtils;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.UUID;


/**
 * Created by Ben on 12/12/13.
 */

public final class JourneyDecorator implements ImmutableJourney {

    public static final float MIN_MOVEMENT = 1.0f;
    private static final String TAG = JourneyDecorator.class.getSimpleName();
    private final ImmutableJourney j;
    private final AndroidUtils h;
    private final Log log;


    /**
     * Constructor for setting helpers etc...
     *
     * @param newJourney
     * @param context
     */

    public JourneyDecorator(ImmutableJourney newJourney, Context context) {
        Assert.notNull(newJourney);
        Assert.notNull(context);
        this.j = newJourney;
        this.log = Log.getInstance(context);
        this.h = AndroidUtils.getInstance(context);
    }

    /**
     * Used to get a GSon String that represents a Journey
     *
     * @param journey
     * @return
     */

    public static String getGsonStringForJourney(Journey journey, double version) {
        Assert.notNull(journey);

        //Get a Gson class
        Gson gson = JourneyDecorator.getGson(version);

        //Render the Journey to a JSON string...
        String journeyString = gson.toJson(journey);
        return journeyString;
    }

    /**
     * Used to instantiate the Gson Marshaller class.
     *
     * @return
     */

    private static Gson getGson(double version) {
        //Get a GSON Builder...
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .serializeNulls()
                .setVersion(version)
                .create();
        return gson;
    }

    /**
     * Used to get s Journey from a Gson String.
     *
     * @param gsonJourneyString
     * @return
     */
    //  SLOW!!
    public static Journey getJourneyFromGsonString(String gsonJourneyString, double version) {
        //Get a GSON class
        Assert.notNull(gsonJourneyString);
        Assert.isTrue(!gsonJourneyString.equals(StringUtils.EMPTY_STRING));
        Gson gson = JourneyDecorator.getGson(version);

        //Attempt to deserialise the Journey (if this goes wrong, RuntimeExceptions are thrown)...
        Journey j = gson.fromJson(gsonJourneyString, Journey.class);
        return j;
    }

    /**
     * Returns a String representation of the Longitude (usually a double)
     *
     * @return
     */

    public String getCurrentLongitudeAsString() {

        String lat = StringUtils.EMPTY_STRING;
        if (getCurrentLongitude() < Constants.ZERO_DOUBLE || getCurrentLongitude() > Constants.ZERO_DOUBLE) {
            //the location has been set, so display it.
            lat = String.valueOf(getCurrentLongitude());
        }

        return lat;
    }

    /**
     * Returns a String representation of the Latitude (usually a double)
     *
     * @return
     */

    public String getCurrentLatitudeAsString() {

        String lat = StringUtils.EMPTY_STRING;
        if (getCurrentLatitude() < Constants.ZERO_DOUBLE || getCurrentLatitude() > Constants.ZERO_DOUBLE) {
            //the location has been set, so display it.
            lat = String.valueOf(getCurrentLatitude());
        }

        return lat;
    }

    /**
     * Returns the time and date the Journey started as a String in the specified format.
     * Returns TXT_PRESS_START if the Journey hasn't started.
     *
     * @return
     */

    public String getStartTimeAsString() {
        Assert.notNull(getStartedDate());

        StringBuilder sb = new StringBuilder();
        sb.append(StringUtils.EMPTY_STRING);

        if (isRunning() || isFinished()) {
            //The Journey isJourneyRunning or isFinished.
            String format = h.getString(R.string.format_dateandtime);
            sb.append(DateFormat.format(format, getStartedDate()).toString());
        }

        return sb.toString();
    }

    /**
     * Returns the Stopped Date and Time in the specified format. Returns TXT_PRESS_STOP if
     * the Stop Date has not been set but the Journey isJourneyRunning or EMPTY_STRING if
     * The Journey has never been started.
     *
     * @return
     */

    public String getStopTimeAsString() {
        Assert.notNull(getStoppedDate());

        StringBuilder sb = new StringBuilder();
        sb.append(StringUtils.EMPTY_STRING);

        //Journey is no longer running and the stop date has been set...
        if (isFinished()) {
            String format = h.getString(R.string.format_dateandtime);
            sb.append(DateFormat.format(format, j.getStoppedDate()).toString());
        }

        return sb.toString();
    }

    /**
     * Returns the total distance travelled as a String (in Miles, Kilometers, Meters)
     * Returns EMPTY_STRING if the Journey hasn't started.
     * Returns EMPTY_STRING if we haven't moved yet.
     *
     * @return
     */

    public String getTotalDistanceAsString() {
        StringBuilder sb = new StringBuilder();
        sb.append(StringUtils.EMPTY_STRING);

        //Fast Fail if the Journey hasn't started yet.
        if (!isRunning() && !isFinished()) {
            return sb.toString();
        }

        //fast fail if we haven't moved
        float meters = j.getTotalDistance();
//        if (meters == Constants.ZERO_FLOAT) {
//            return StringUtils.EMPTY_STRING;
//        }

        DecimalFormat df = new DecimalFormat(this.h.getString(R.string.format_distance_value));
        int distance;

        switch (j.getDistanceUnits()) {
            case Constants.MILES:
                distance = Double.valueOf(
                        Math.floor(ConversionUtils.convertMetersToMiles(
                                meters))
                ).intValue();
                sb.append(df.format(distance));
                sb.append(StringUtils.SPACE);
                sb.append(this.h.getString(R.string.txt_miles));
                break;

            case Constants.KILOMETERS:
                distance = Double.valueOf(
                        Math.floor(ConversionUtils.convertMetersToKilometers(
                                meters))
                ).intValue();
                sb.append(df.format(distance));
                sb.append(StringUtils.SPACE);
                sb.append(this.h.getString(R.string.txt_kilometers));
                break;

            default:
                distance = Double.valueOf(
                        Math.floor(meters)).intValue();
                sb.append(df.format(distance));
                sb.append(StringUtils.SPACE);
                sb.append(this.h.getString(R.string.txt_meters));
                break;
        }

        return sb.toString();
    }

    /**
     * Returns the average speed as a String in the right format (MPH, KPH, mSEC).
     * Returns EMPTY_STRING if the Journey hasn't started.
     * Returns EMPTY_STRING if the TotalDistance or TotalTime is too small.
     * se the distance and the time elapsed in millis to calculate average Speed.
     */

    public String getAverageSpeedAsString() {
        StringBuilder sb = new StringBuilder();
        sb.append(StringUtils.EMPTY_STRING);

        if (!isRunning() && !isFinished()) {
            //Fast Fail if the Journey hasn't started yet.
            return sb.toString();
        }

        if (j.getTotalDistance() < MIN_MOVEMENT || j.getTotalTime() < Constants.MILLISECONDS_PER_SECOND) {
            //Fast fail if we haven't moved enough
            //Fast fail if not enough time has elapsed
            return sb.toString();
        }

        //Get the elapsed Time, Distance & DecimalFormat
        long secs = j.getTotalTime() / Constants.MILLISECONDS_PER_SECOND;
        long distance = Float.valueOf(j.getTotalDistance()).longValue();
        DecimalFormat speedFormat = new DecimalFormat(this.h.getString(R.string.format_average_speed));

        //Calculate the average speed...
        long averageSpeedInMetersSecond = distance / secs;

        //Switch String based on MILES, KILOMETERS or METERS
        switch (j.getDistanceUnits()) {
            case Constants.MILES:
                log.v(TAG, "Average Speed will be in MILES per hour");
                float mph = ConversionUtils.convertMsecToMph(averageSpeedInMetersSecond);
                sb.append(speedFormat.format(mph));
                sb.append(StringUtils.SPACE);
                sb.append(this.h.getString(R.string.txt_mph));
                break;

            case Constants.KILOMETERS:
                log.v(TAG, "Average Speed will be in KILOMETERS per hour");
                float kph = ConversionUtils.convertMsecToKph(averageSpeedInMetersSecond);
                sb.append(speedFormat.format(kph));
                sb.append(StringUtils.SPACE);
                sb.append(this.h.getString(R.string.txt_kph));
                break;

            default:
                log.v(TAG, "Average Speed will be in METERS per second");
                sb.append(averageSpeedInMetersSecond);
                sb.append(StringUtils.SPACE);
                sb.append(this.h.getString(R.string.txt_mSec));
                break;
        }

        return sb.toString();
    }


    /**
     * Returns the Cost of the Journey by multiplying the distance by the charge value.
     * Returns EMPTY_STRING if the Journey hasn't started.
     * Returns EMPTY_STRING if either the Distance or the Charge Value are set to ZERO_FLOAT.
     * Returns the charge Value
     *
     * @return
     */

    public String getCurrentCostAsString() {

        //set up the result
        StringBuilder sb = new StringBuilder();
        sb.append(StringUtils.EMPTY_STRING);

        //Fast Fail if the Journey hasn't started yet.
        if (!isRunning() && !isFinished()) {
            return sb.toString();
        }

        //fast fail if we haven't moved
        float meters = j.getTotalDistance();
        float charge = j.getChargeValue();

        //If either the charge or the distance are 0, return nothing...
        if (charge == Constants.ZERO_FLOAT || meters == Constants.ZERO_FLOAT) {
            return sb.toString();
        }

        //set up the distance param
        int distance;

        //Switch the distance travelled based on MILES, KILOMETERS or METERS
        switch (j.getDistanceUnits()) {
            case Constants.MILES:
                distance = Double.valueOf(Math.floor(ConversionUtils.convertMetersToMiles(meters))).intValue();
                break;

            case Constants.KILOMETERS:
                distance = Double.valueOf(Math.floor(ConversionUtils.convertMetersToKilometers(meters))).intValue();
                break;

            default:
                distance = Double.valueOf(meters).intValue();
                break;
        }

        //Now calculate the cost...
        float cost = distance * charge;

        //Append the value to the Cost string in the right format...
        sb.append(this.h.getString(R.string.currency));
        DecimalFormat costFormat = new DecimalFormat(this.h.getString(R.string.format_charge_value));
        sb.append(costFormat.format(cost));

        log.d(TAG, "Cost calculated as: " + sb.toString());
        return sb.toString();
    }


    public Chronometer configureChronometer(Chronometer chronometer) {
        Assert.notNull(chronometer);

        if (j.isRunning()) {
            //use the present chronoBase value
            chronometer.setBase(j.getChronometerBase());
        }

        if (j.isFinished()) {
            //trip has stopped
            //How long was the trip?
            long tripLength = j.getChronometerStop() - j.getChronometerBase();
            //take the Trip length from the current time and use that
            long currentChronoBase = SystemClock.elapsedRealtime() - tripLength;
            //set the chronometer's base.
            chronometer.setBase(currentChronoBase);
        }
        return chronometer;
    }

    /**
     * Returns the Location accuracy in the format
     * "0.0m (0.0m)". Returns EMPTY_STRING when the Journey hasn't started or
     * if the Average can't be calculated.
     *
     * @return
     */

    public String getLocationAccuracyAsString() {

        //set up the helper classes
        StringBuilder sb = new StringBuilder();
        sb.append(StringUtils.EMPTY_STRING);

        if (!isRunning() && !isFinished()) {
            //Journey hasn't started.
            return sb.toString();
        }

        //Calculate the average accuracy number
        float average = Constants.ZERO_FLOAT;
        if (this.getTotalAccuracy() > 0 && this.getTotalLocationCount() > 0) {
            //The accuracy and the Location Count have been set

            //Calculate the average
            average = this.getTotalAccuracy() / this.getTotalLocationCount();

            //Report the current accuracy
            DecimalFormat accuracyFormat = new DecimalFormat(this.h.getString(R.string.format_location_accuracy));
            sb.append(accuracyFormat.format(this.getCurrentAccuracy()));
            sb.append(this.h.getString(R.string.txt_m));
            sb.append(StringUtils.SPACE);

            //report the average accuracy...
            sb.append(StringUtils.bracket(accuracyFormat.format(average) + this.h.getString(R.string.txt_m)));
        }

        return sb.toString();
    }

    /**
     * Returns the Location quantity in the format
     * " 0 (0) ". Returns an EMPTY_STRING when the Journey is yet to begin or has no Locations.
     *
     * @return
     */

    public String getLocationQuantityAsString() {
        StringBuilder sb = new StringBuilder();
        sb.append(StringUtils.EMPTY_STRING);

        if (!isRunning() && !isFinished()) {
            //Journey hasn't started.
            return sb.toString();
        }

        if (this.getTotalLocationCount() > Constants.ZERO_INT) {
            //Location count is one or more...
            DecimalFormat qtyFormat = new DecimalFormat(this.h.getString(R.string.format_location_qty));
            sb.append(qtyFormat.format(this.getUsedLocationCount()));
            sb.append(StringUtils.SPACE);
            sb.append(StringUtils.bracket(qtyFormat.format(this.getTotalLocationCount())));
        }

        return sb.toString();
    }

    /**
     * Returns the Heading as a string in the format
     * "N (0°)". returns EMPTY_STRING when the Journey hasn't started or has a negative Heading value
     *
     * @return
     */

    public String getHeadingAsString() {
        StringBuilder sb = new StringBuilder();
        sb.append(StringUtils.EMPTY_STRING);

        if (!isRunning() && !isFinished()) {
            //Journey hasn't started.
            return sb.toString();
        }

        if (this.getCurrentHeading() != Constants.NEG_FLOAT) {
            //At least one heading has been set...
            DecimalFormat degreesFormat = new DecimalFormat(this.h.getString(R.string.format_degrees));
            sb.append(StringUtils.convertToCompassString(j.getCurrentHeading()));
            sb.append(StringUtils.SPACE);
            sb.append(StringUtils.OPEN_BRACKET);
            sb.append(degreesFormat.format(j.getCurrentHeading()));
            sb.append(StringUtils.DEGREES);
            sb.append(StringUtils.CLOSE_BRACKET);
        }

        return sb.toString();
    }


    public String getCurrentAltitudeAsString() {

        StringBuilder sb = new StringBuilder();

        if (getCurrentAltitude() == Constants.ZERO_DOUBLE) {
            sb.append(StringUtils.EMPTY_STRING);
        } else {

            String format = this.h.getString(R.string.format_altitude);
            DecimalFormat df = new DecimalFormat(format);
            sb.append(df.format(getCurrentAltitude()));
            sb.append(StringUtils.SPACE);
            sb.append(this.h.getString(R.string.txt_meters));
        }

        return sb.toString();
    }

    /**
     * Map detected activity types to strings
     *
     * @return A user-readable name for the type
     */

    public String getCurrentActivityTypeAsString() {
        StringBuilder sb = new StringBuilder();

        //add the activity type
        switch (j.getCurrentActivityType()) {
            case DetectedActivity.IN_VEHICLE:
                sb.append(this.h.getString(R.string.txt_driving));
                break;
            case DetectedActivity.ON_BICYCLE:
                sb.append(this.h.getString(R.string.txt_cycling));
                break;
            case DetectedActivity.ON_FOOT:
                sb.append(this.h.getString(R.string.txt_walking));
                break;
            case DetectedActivity.RUNNING:
                sb.append(this.h.getString(R.string.txt_running));
                break;
            case DetectedActivity.STILL:
                sb.append(this.h.getString(R.string.txt_still));
                break;
            case DetectedActivity.UNKNOWN:
                sb.append(this.h.getString(R.string.txt_unknown));
                break;
            case DetectedActivity.TILTING:
                sb.append(this.h.getString(R.string.txt_tilting));
                break;
            default:
                return StringUtils.EMPTY_STRING;
        }

        //Now add the confidence level if known...
        if (sb.length() > 0 && j.getCurrentActivityConfidence() > Constants.ZERO_INT) {
            sb.append(StringUtils.SPACE);
            sb.append(StringUtils.OPEN_BRACKET);
            sb.append(String.valueOf(j.getCurrentActivityConfidence()));
            sb.append(StringUtils.PERCENT);
            sb.append(StringUtils.CLOSE_BRACKET);
        }

        return sb.toString();
    }

  /*

  Below are the ImmutableJourney Overrides....

   */

    @Override
    public UUID getId() {
        return j.getId();
    }

    @Override
    public boolean isStarted() {
        return j.isStarted();
    }

    @Override
    public boolean isFinished() {
        return j.isFinished();
    }

    @Override
    public boolean isRunning() {
        return j.isRunning();
    }

    @Override
    public String getCurrentPlace() {
        return j.getCurrentPlace();
    }

    @Override
    public String getFirstPlace() {
        return j.getFirstPlace();
    }

    @Override
    public String getLastPlace() {
        if (j.isRunning()) {
            return StringUtils.EMPTY_STRING;
        } else {
            return j.getLastPlace();
        }
    }

    @Override
    public float getTotalDistance() {
        return j.getTotalDistance();
    }

    @Override
    public Date getStartedDate() {
        return j.getStartedDate();
    }

    @Override
    public Date getStoppedDate() {
        return j.getStoppedDate();
    }

    @Override
    public long getChronometerBase() {
        return j.getChronometerBase();
    }

    @Override
    public long getChronometerStop() {
        return j.getChronometerStop();
    }

    @Override
    public long getTotalTime() {
        return j.getTotalTime();
    }

    @Override
    public double getCurrentLatitude() {
        return j.getCurrentLatitude();
    }

    @Override
    public double getCurrentLongitude() {
        return j.getCurrentLongitude();
    }

    @Override
    public int getUnusedLocationCount() {
        return j.getUnusedLocationCount();
    }

    @Override
    public int getUsedLocationCount() {
        return j.getUsedLocationCount();
    }

    @Override
    public int getTotalLocationCount() {
        return j.getTotalLocationCount();
    }

    @Override
    public float getAccuracyThreshold() {
        return j.getAccuracyThreshold();
    }

    @Override
    public float getCurrentAccuracy() {
        return j.getCurrentAccuracy();
    }

    @Override
    public float getTotalAccuracy() {
        return j.getTotalAccuracy();
    }

    @Override
    public int getDistanceUnits() {
        return j.getDistanceUnits();
    }

    @Override
    public float getChargeValue() {
        return j.getChargeValue();
    }

    @Override
    public float getCurrentHeading() {
        return j.getCurrentHeading();
    }

    @Override
    public double getCurrentAltitude() {
        return j.getCurrentAltitude();
    }

    @Override
    public int getCurrentActivityType() {
        return j.getCurrentActivityType();
    }

    @Override
    public int getCurrentActivityConfidence() {
        return j.getCurrentActivityConfidence();
    }
}
