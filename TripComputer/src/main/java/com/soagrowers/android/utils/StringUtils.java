package com.soagrowers.android.utils;


/**
 * Created by Ben on 20/11/13.
 */
public final class StringUtils {

    //Various useful strings
    public static final String NEW_LINE = System.getProperty("line.separator");
    public static final String EMPTY_STRING = "";
    public static final String SPACE = " ";
    public static final String COLON = ":";
    public static final String OPEN_BRACKET = "(";
    public static final String CLOSE_BRACKET = ")";
    public static final String PERIOD = ".";
    public static final String PERCENT = "%";
    public static final String COMMA = ",";
    public static final String QUOTE = "\"";
    public static final String AT = "@";
    public static final Object DEGREES = "°";
    public static final String SLASH = "/";
    public static final String DASH = "-";
    public static final String COPYRIGHT = "©";

    // Activity detection types...
    public static final String IN_VEHICLE = "Driving";
    public static final String ON_BICYCLE = "Cycling";
    public static final String ON_FOOT = "Walking";
    public static final String RUNNING = "Running";
    public static final String STILL = "Still";
    public static final String UNKNOWN = "Unknown";
    public static final String TILTING = "Tilting";

    //Date formats...
    public static final String UTC_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";

    private StringUtils() {
        //private constructor to prevent access
        //static methods only
    }


    public static synchronized String quote(String quotation) {
        StringBuilder builder = new StringBuilder(QUOTE);
        builder.append(quotation);
        builder.append(QUOTE);
        return builder.toString();
    }


    public static synchronized String bracket(String text) {
        StringBuilder builder = new StringBuilder(OPEN_BRACKET);
        builder.append(text);
        builder.append(CLOSE_BRACKET);
        return builder.toString();
    }


    public static String convertToCompassString(float heading) {

        String bearingText = "N";
        if ((360 >= heading && heading >= 337.5) || (0 <= heading && heading <= 22.5))
            bearingText = "N";
        else if (heading > 22.5 && heading < 67.5) bearingText = "NE";//NE
        else if (heading >= 67.5 && heading <= 112.5) bearingText = "E";//E
        else if (heading > 112.5 && heading < 157.5) bearingText = "SE";//SE
        else if (heading >= 157.5 && heading <= 202.5) bearingText = "S";//S
        else if (heading > 202.5 && heading < 247.5) bearingText = "SW";//SW
        else if (heading >= 247.5 && heading <= 292.5) bearingText = "W";//W
        else if (heading > 292.5 && heading < 337.5) bearingText = "NW";//NW
        else bearingText = "?";//?

        return bearingText;
    }
}
