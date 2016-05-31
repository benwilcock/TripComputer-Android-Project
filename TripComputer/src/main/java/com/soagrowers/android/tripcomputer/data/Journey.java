package com.soagrowers.android.tripcomputer.data;

import android.location.Location;
import android.os.SystemClock;
import android.text.format.DateFormat;

import com.google.gson.annotations.SerializedName;
import com.google.gson.annotations.Since;
import com.soagrowers.android.utils.Log;
import com.soagrowers.android.utils.StringUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public final class Journey implements ImmutableJourney {

    private static final String TAG = Journey.class.getCanonicalName();
    private static final Log log = Log.getInstance();

    //Transient items are NOT serialised...
    private transient String currentPlace = StringUtils.EMPTY_STRING;

    //Identifiers
    @SerializedName("_id")
    @Since(Constants.VERSION_ONE_DOT_ZERO)
    private final UUID id = UUID.randomUUID();
    @SerializedName("_version")
    @Since(Constants.VERSION_ONE_DOT_ZERO)
    private final String version = id.toString().concat(StringUtils.DASH + String.valueOf(1));

    //Started/First stuff...
    @SerializedName("startedDate")
    @Since(Constants.VERSION_ONE_DOT_ZERO)
    private Date startedDate = Constants.START_OF_TIME;
    @SerializedName("firstPlace")
    @Since(Constants.VERSION_ONE_DOT_ZERO)
    private String firstPlace = StringUtils.EMPTY_STRING;

    //Current stuff...
    @SerializedName("currentAltitude")
    @Since(Constants.VERSION_ONE_DOT_ONE)
    private double currentAltitude = Constants.ZERO_DOUBLE;
    @SerializedName("currentLatitude")
    @Since(Constants.VERSION_ONE_DOT_ZERO)
    private double currentLatitude = Constants.ZERO_DOUBLE;
    @SerializedName("currentLongitude")
    @Since(Constants.VERSION_ONE_DOT_ZERO)
    private double currentLongitude = Constants.ZERO_DOUBLE;
    @SerializedName("currentHeading")
    @Since(Constants.VERSION_ONE_DOT_ZERO)
    private float currentHeading = Constants.NEG_FLOAT;
    @SerializedName("currentAccuracy")
    @Since(Constants.VERSION_ONE_DOT_ZERO)
    private float currentAccuracy = Constants.ZERO_FLOAT;
    @SerializedName("usedLocationCount")
    @Since(Constants.VERSION_ONE_DOT_ZERO)
    private int usedLocationCount = Constants.ZERO_INT;
    @SerializedName("unusedLocationCount")
    @Since(Constants.VERSION_ONE_DOT_ZERO)
    private int unusedLocationCount = Constants.ZERO_INT;
    @SerializedName("accuracyThreshold")
    @Since(Constants.VERSION_ONE_DOT_ZERO)
    private float accuracyThreshold = Constants.ZERO_FLOAT;
    @SerializedName("distanceUnits")
    @Since(Constants.VERSION_ONE_DOT_ZERO)
    private int distanceUnits = Constants.ZERO_INT;
    @SerializedName("chargeValue")
    @Since(Constants.VERSION_ONE_DOT_ZERO)
    private float chargeValue = Constants.ZERO_FLOAT;
    @SerializedName("activityType")
    @Since(Constants.VERSION_ONE_DOT_ONE)
    private int activityType = Constants.NEG_INT;
    @SerializedName("activityConfidence")
    @Since(Constants.VERSION_ONE_DOT_ONE)
    private int activityConfidence = Constants.NEG_INT;

    //Stopped/Last stuff...
    @SerializedName("stoppedDate")
    @Since(Constants.VERSION_ONE_DOT_ZERO)
    private Date stoppedDate = Constants.START_OF_TIME;
    @SerializedName("lastPlace")
    @Since(Constants.VERSION_ONE_DOT_ZERO)
    private String lastPlace = StringUtils.EMPTY_STRING;

    //Totals...
    @SerializedName("totalTime")
    @Since(Constants.VERSION_ONE_DOT_ZERO)
    private long totalTime = Constants.ZERO_LONG;
    @SerializedName("totalDistance")
    @Since(Constants.VERSION_ONE_DOT_ZERO)
    private float totalDistance = Constants.ZERO_FLOAT;
    @SerializedName("totalAccuracy")
    @Since(Constants.VERSION_ONE_DOT_ZERO)
    private float totalAccuracy = Constants.ZERO_FLOAT;

    //Timers...
    @SerializedName("chronoBase")
    @Since(Constants.VERSION_ONE_DOT_ZERO)
    private long chronoBase = Long.MIN_VALUE;
    @SerializedName("chronoStop")
    @Since(Constants.VERSION_ONE_DOT_ZERO)
    private long chronoStop = Long.MIN_VALUE;

    //Waypoints
    @SerializedName("waypoints")
    @Since(Constants.VERSION_ONE_DOT_ZERO)
    private List<Waypoint> waypoints;

    //Status and State...
    @SerializedName("isStarted")
    @Since(Constants.VERSION_ONE_DOT_ZERO)
    private boolean isStarted = false;
    @SerializedName("isFinished")
    @Since(Constants.VERSION_ONE_DOT_ZERO)
    private boolean isFinished = false;


    /**
     * Constructor. Needs Accuracy Threshold.
     */
    public Journey() {
    }

    /**
     * Used to START a Journey.
     * Called by the JourneyController class.
     */
    public void start() {
        if (isFinished) {
            throw new RuntimeException("An attempt was made to start a finished Journey");
        }

        //set the statuses
        this.isStarted = true;
        this.isFinished = false;

        //set the start time...
        this.startedDate = new Date();
        this.chronoBase = SystemClock.elapsedRealtime();
    }


    /**
     * Used to STOP a Journey.
     * Called by the JourneyController class.
     */
    public void stop() {

        //setting the statuses
        this.isStarted = true;
        this.isFinished = true;

        //set the stopped time...
        this.stoppedDate = new Date();
        this.chronoStop = SystemClock.elapsedRealtime();
    }

  /*
  ***********************************************************
  * Begin SETTERS and GETTERS
  ***********************************************************
  */

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public boolean isStarted() {
        return isStarted;
    }

    @Override
    public boolean isFinished() {
        if (isStarted == true && isFinished == true) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isRunning() {
        if (isStarted == true && isFinished == false) {
            return true;
        }
        return false;
    }

    @Override
    public String getCurrentPlace() {
        if (null == currentPlace) {
            return StringUtils.EMPTY_STRING;
        }
        return currentPlace;
    }

    public void setCurrentPlace(String currentPlace) {
        this.currentPlace = currentPlace;
    }

    @Override
    public String getFirstPlace() {
        return firstPlace;
    }

    public void setFirstPlace(String firstPlace) {
        //can only be set once after construction
        if (this.firstPlace.equals(StringUtils.EMPTY_STRING)) {
            this.firstPlace = firstPlace;
        }

        return;
    }

    @Override
    public String getLastPlace() {
        return lastPlace;
    }


    public void setLastPlace(String lastPlace) {
        this.lastPlace = lastPlace;
    }

    /**
     * Get the total distance for the Journey (always in Meters)
     *
     * @return
     */
    @Override
    public float getTotalDistance() {
        return totalDistance;
    }

    /**
     * Set the total distance for the Journey (always in Meters)
     *
     * @return
     */
    public void setTotalDistance(float totalDistance) {
        this.totalDistance = totalDistance;
    }

    @Override
    public Date getStartedDate() {

        Date copyOfStartTime = null;
        if (null != startedDate) {
            copyOfStartTime = new Date();
            copyOfStartTime.setTime(startedDate.getTime());
        }

        return copyOfStartTime;
    }

    @Override
    public Date getStoppedDate() {

        Date copyOfStopTime = null;
        if (null != stoppedDate) {
            copyOfStopTime = new Date();
            copyOfStopTime.setTime(stoppedDate.getTime());
        }

        return copyOfStopTime;
    }

    @Override
    public long getChronometerBase() {
        return this.chronoBase;
    }

    @Override
    public long getChronometerStop() {
        return chronoStop;
    }

    /**
     * Total time for the Journey (in Millis)
     *
     * @return
     */
    @Override
    public long getTotalTime() {
        return totalTime;
    }

    /**
     * Total time for the Journey (in Millis)
     *
     * @param totalTime
     */
    public void setTotalTime(long totalTime) {
        this.totalTime = totalTime;
    }

    @Override
    public double getCurrentLatitude() {
        return currentLatitude;
    }


    public void setCurrentLatitude(double currentLatitude) {
        this.currentLatitude = currentLatitude;
    }

    @Override
    public double getCurrentLongitude() {
        return currentLongitude;
    }

    public void setCurrentLongitude(double currentLongitude) {
        this.currentLongitude = currentLongitude;
    }

    @Override
    public int getUnusedLocationCount() {
        return unusedLocationCount;
    }

    @Override
    public int getUsedLocationCount() {
        return usedLocationCount;
    }

    @Override
    public int getTotalLocationCount() {
        return usedLocationCount + unusedLocationCount;
    }

    @Override
    public float getAccuracyThreshold() {
        return accuracyThreshold;
    }

    public Journey setAccuracyThreshold(float accuracyThreshold) {
        this.accuracyThreshold = accuracyThreshold;
        return this;
    }

    @Override
    public float getCurrentAccuracy() {
        return currentAccuracy;
    }

    @Override
    public int getCurrentActivityType() {
        return activityType;
    }

    public void setCurrentActivityType(int activityType) {
        this.activityType = activityType;
    }

    @Override
    public int getCurrentActivityConfidence() {
        return activityConfidence;
    }

    public void setCurrentActivityConfidence(int activityConfidence) {
        this.activityConfidence = activityConfidence;
    }

    @Override
    public float getTotalAccuracy() {
        return totalAccuracy;
    }

    @Override
    public int getDistanceUnits() {
        return distanceUnits;
    }

    public Journey setDistanceUnits(int distanceUnits) {
        this.distanceUnits = distanceUnits;
        return this;
    }

    @Override
    public float getChargeValue() {
        return chargeValue;
    }

    public Journey setChargeValue(float chargeValue) {
        this.chargeValue = chargeValue;
        return this;
    }

    @Override
    public float getCurrentHeading() {
        return currentHeading;
    }

    @Override
    public double getCurrentAltitude() {
        return currentAltitude;
    }

    public void setCurrentAltitude(double currentAltitude) {
        this.currentAltitude = currentAltitude;
    }

    public void setCurrentHeading(float currentHeading) {
        this.currentHeading = currentHeading;
    }

    /**
     * Add a Location to the count of those that were NOT used in our Journey calculations.
     *
     * @param newLocation
     */
    public void addUnusedLocation(Location newLocation) {
        this.unusedLocationCount++;
        this.currentAccuracy = newLocation.getAccuracy();
        this.totalAccuracy = this.totalAccuracy + this.currentAccuracy;
    }

    /**
     * Add a Location to the count of those that were used in our Journey Calculations.
     * If the storeWaypoint parameter is set to TRUE, the location is used as a WayPoint
     * and added to the List of waypoints.
     *
     * @param newLocation
     * @param storeWaypoint true if the location should be used as a waypoint
     */
    public void addUsedLocation(Location newLocation, boolean storeWaypoint) {
        this.usedLocationCount++;
        this.currentAccuracy = newLocation.getAccuracy();
        this.totalAccuracy = this.totalAccuracy + this.currentAccuracy;


        if (storeWaypoint) {
            //Waypoints SHOULD be stored...

            if (null == this.waypoints) {
                //The Waypoint List is NULL null, Initialise it
                //Should give better performance when NOT storing waypoints...
                this.waypoints = new ArrayList<Waypoint>(1000);
            }

            //Create a Waypoint for this Location...
            Waypoint waypoint = new Waypoint(
                    newLocation.getLatitude(),
                    newLocation.getLongitude(),
                    newLocation.getAccuracy(),
                    this.getCurrentPlace(),
                    DateFormat.format(StringUtils.UTC_DATE_FORMAT, Calendar.getInstance().getTime()).toString()
            );

            //Store this Waypoint to our list of Waypoints...
            this.waypoints.add(waypoint);
        }
    }

    @Override
    public String toString() {
        return "Journey{" +
                "currentPlace='" + currentPlace + '\'' +
                ", id=" + id +
                ", version='" + version + '\'' +
                ", startedDate=" + startedDate +
                ", firstPlace='" + firstPlace + '\'' +
                ", currentLatitude=" + currentLatitude +
                ", currentLongitude=" + currentLongitude +
                ", currentHeading=" + currentHeading +
                ", currentAccuracy=" + currentAccuracy +
                ", usedLocationCount=" + usedLocationCount +
                ", unusedLocationCount=" + unusedLocationCount +
                ", accuracyThreshold=" + accuracyThreshold +
                ", distanceUnits=" + distanceUnits +
                ", chargeValue=" + chargeValue +
                ", stoppedDate=" + stoppedDate +
                ", lastPlace='" + lastPlace + '\'' +
                ", totalTime=" + totalTime +
                ", totalDistance=" + totalDistance +
                ", totalAccuracy=" + totalAccuracy +
                ", chronoBase=" + chronoBase +
                ", chronoStop=" + chronoStop +
                ", waypoints=" + waypoints +
                ", isStarted=" + isStarted +
                ", isFinished=" + isFinished +
                '}';
    }

    /**
     * Compares this instance with the specified object and indicates if they
     * are equal. In order to be equal, {@code o} must represent the same object
     * as this instance using a class-specific comparison. The general contract
     * is that this comparison should be reflexive, symmetric, and transitive.
     * Also, no object reference other than null is equal to null.
     * <p/>
     * <p>The default implementation returns {@code true} only if {@code this ==
     * o}. See <a href="{@docRoot}reference/java/lang/Object.html#writing_equals">Writing a correct
     * {@code equals} method</a>
     * if you intend implementing your own {@code equals} method.
     * <p/>
     * <p>The general contract for the {@code equals} and {@link
     * #hashCode()} methods is that if {@code equals} returns {@code true} for
     * any two objects, then {@code hashCode()} must return the same value for
     * these objects. This means that subclasses of {@code Object} usually
     * override either both methods or neither of them.
     *
     * @param o the object to compare this instance with.
     * @return {@code true} if the specified object is equal to this {@code
     * Object}; {@code false} otherwise.
     * @see #hashCode
     */
    @Override
    public boolean equals(Object o) {

        if (!(o instanceof Journey)) {
            // It's not a Journey
            return false;
        }

        if (super.equals(o)) {
            // It is the same instance
            return true;
        }

        if (this.chronoBase == ((Journey) o).getChronometerBase()) {
            // It has exactly the same start time
            return true;
        }

        // It's got a different start time
        return false;
    }

    /**
     * Returns an integer hash code for this object. By contract, any two
     * objects for which {@link #equals} returns {@code true} must return
     * the same hash code value. This means that subclasses of {@code Object}
     * usually override both methods or neither method.
     * <p/>
     * <p>Note that hash values must not change over time unless information used in equals
     * comparisons also changes.
     * <p/>
     * <p>See <a href="{@docRoot}reference/java/lang/Object.html#writing_hashCode">Writing a correct
     * {@code hashCode} method</a>
     * if you intend implementing your own {@code hashCode} method.
     *
     * @return this object's hash code.
     * @see #equals
     */
    @Override
    public int hashCode() {
        return super.hashCode();
    }
}