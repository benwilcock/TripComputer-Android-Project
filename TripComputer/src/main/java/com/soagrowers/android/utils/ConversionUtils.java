package com.soagrowers.android.utils;

/**
 * Created by Ben on 20/11/13.
 */
public final class ConversionUtils {

    //default value for some returns
    private static int ZERO = 0;

    //Give us a way to get MPH
    private static final float MPH_CONVERSION_FACTOR = 0.447037222f;
    private static final float KPH_CONVERSION_FACTOR = 0.2777777777777778f;

    //give us a way to convert meters to miles...
    private static final float MILES_CONVERSION_FACTOR = 0.000621371192f;

    //give us a way to convert meters to kilometers...
    private static final float KILOMETERS_CONVERSION_FACTOR = 0.001f;

    private ConversionUtils() {
        //private constrictor to prevent construction
        //static methods only
    }

    /**
     * Converts meters to miles.
     *
     * @param meters
     * @return
     */
    public synchronized static final float convertMetersToMiles(float meters) {
        return meters * MILES_CONVERSION_FACTOR;
    }

    public synchronized static final float convertMetersToKilometers(float meters) {
        return meters * KILOMETERS_CONVERSION_FACTOR;
    }

    public synchronized static final float convertMsecToKph(float msec) {

        //guard against division by 0...
        if (msec != 0.0f) {
            //return Math.round(msec / KPH_CONVERSION_FACTOR);
            return (msec / KPH_CONVERSION_FACTOR);
        } else {
            return ZERO;
        }
    }

    /**
     * Converts the meters per second metric used in location to Miles Per Hour.
     *
     * @param msec
     * @return
     */
    public synchronized static final float convertMsecToMph(float msec) {

        //guard against division by 0...
        if (msec != 0.0f) {
            //return Math.round(msec / MPH_CONVERSION_FACTOR);
            return (msec / MPH_CONVERSION_FACTOR);
        } else {
            return ZERO;
        }
    }
}
