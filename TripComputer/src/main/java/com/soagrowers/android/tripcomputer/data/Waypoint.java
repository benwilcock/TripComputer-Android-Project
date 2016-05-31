package com.soagrowers.android.tripcomputer.data;

/**
 * Created by Ben on 13/02/14.
 */
public final class Waypoint {

    private long id;
    private long journeyId;
    private final double latitude;
    private final double longitude;
    private final float accuracy;
    private final String place;
    private final String datetime;


    public Waypoint(double latitude, double longitude, float accuracy, String place, String datetime) {
        this.id = Constants.ZERO_LONG;
        this.journeyId = Constants.ZERO_LONG;
        this.latitude = latitude;
        this.longitude = longitude;
        this.accuracy = accuracy;
        this.place = place;
        this.datetime = datetime;
    }

    public long getId() {
        return id;
    }

    public long getJourneyId() {
        return journeyId;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public float getAccuracy() {
        return accuracy;
    }

    public String getPlace() {
        return place;
    }

    public String getDatetime() {
        return datetime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Waypoint waypoint = (Waypoint) o;

        if (Float.compare(waypoint.accuracy, accuracy) != 0) return false;
        if (id != waypoint.id) return false;
        if (journeyId != waypoint.journeyId) return false;
        if (Double.compare(waypoint.latitude, latitude) != 0) return false;
        if (Double.compare(waypoint.longitude, longitude) != 0) return false;
        if (!datetime.equals(waypoint.datetime)) return false;
        if (!place.equals(waypoint.place)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = (int) (id ^ (id >>> 32));
        result = 31 * result + (int) (journeyId ^ (journeyId >>> 32));
        temp = Double.doubleToLongBits(latitude);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(longitude);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (accuracy != +0.0f ? Float.floatToIntBits(accuracy) : 0);
        result = 31 * result + place.hashCode();
        result = 31 * result + datetime.hashCode();
        return result;
    }
}
