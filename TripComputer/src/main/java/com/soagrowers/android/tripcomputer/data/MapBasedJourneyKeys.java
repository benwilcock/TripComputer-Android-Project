package com.soagrowers.android.tripcomputer.data;

import com.google.common.collect.ImmutableList;

/**
 * Created by Ben on 13/08/2014.
 */
public class MapBasedJourneyKeys {

    public static final String ID = "_id";
    public static final String VERSION = "_version";
    public static final String MODEL_TYPE = "model_type";
    public static final String MODEL_VERSION_NAME = "model_version_name"; //String
    public static final String MODEL_VERSION_CODE = "model_version_code"; //String
    public static final String IS_STARTED_BOOL = "is_started"; //Boolean
    public static final String IS_PAUSED_BOOL = "is_paused"; //Boolean
    public static final String IS_FINISHED_BOOL = "is_finished"; //Boolean

    public static final String FIRST_DATE_DATE = "first_date"; //Date
    public static final String FIRST_CHRONO_LONG = "first_chrono"; //Long
    public static final String FIRST_LATITUDE_DBL = "first_lat"; //Double
    public static final String FIRST_LONGITUDE_DBL = "first_long"; //Double
    public static final String FIRST_PLACE_TXT = "first_place"; //String
    public static final String FIRST_ACTIVITY_TYPE_INT = "first_activity_type"; //Integer

    public static final String CURRENT_LATITUDE_DBL = "current_lat"; //Double
    public static final String CURRENT_LONGITUDE_DBL = "current_long"; //Double
    public static final String CURRENT_PLACE_TXT = "current_place"; //String
    public static final String CURRENT_ALTITUDE_DBL = "current_altitude"; //Double
    public static final String CURRENT_HEADING_FLT = "current_heading"; //Float
    public static final String CURRENT_SPEED_FLT = "current_speed"; //Float
    public static final String CURRENT_LOCATION_ACCURACY_FLT = "current_location_accuracy"; //Float
    public static final String CURRENT_ACTIVITY_TYPE_INT = "current_activity_type"; //Integer
    public static final String CURRENT_ACTIVITY_CONFIDENCE_INT = "current_activity_confidence"; //Integer

    public static final String LAST_DATE_DATE = "last_date"; //Date
    public static final String LAST_CHRONO_LONG = "last_chrono"; //Long
    public static final String LAST_LATITUDE_DBL = "last_lat"; //Double
    public static final String LAST_LONGITUDE_DBL = "last_long"; //Double
    public static final String LAST_PLACE_TXT = "last_place"; //String
    public static final String LAST_ACTIVITY_TYPE_INT = "last_activity_type"; //Integer

    public static final String TOTAL_TIME_LONG = "total_time"; //Long
    public static final String TOTAL_DISTANCE_FLT = "total_distance"; //Float
    public static final String TOTAL_COST_FLT = "total_cost"; //Float
    public static final String TOTAL_LOCATION_ACCURACY_FLT = "total_location_accuracy"; //Float
    public static final String TOTAL_LOCATIONS_INT = "total_locations"; //Integer
    public static final String TOTAL_LOCATIONS_USED_INT = "total_used_locations"; //Integer
    public static final String TOTAL_LOCATIONS_IGNORED_INT = "total_ignored_locations"; //Integer

    public static final String AVERAGE_LOCATION_ACCURACY_FLT = "average_location_accuracy"; //Float
    public static final String AVERAGE_SPEED_FLT = "average_speed"; //Float

    public static final String SETTING_ACCURACY_THRESHOLD_FLT = "setting_accuracy_threshold"; //Float
    public static final String SETTING_DISTANCE_UNITS_INT = "setting_distance_units"; //Integer
    public static final String SETTING_CHARGE_VALUE_FLT = "setting_charge_value"; //Float

    private static final ImmutableList<String> journeyView;

    static {
        journeyView = ImmutableList.of(
                FIRST_DATE_DATE,
                FIRST_LATITUDE_DBL,
                FIRST_LONGITUDE_DBL,
                FIRST_PLACE_TXT,
                FIRST_CHRONO_LONG,
                //FIRST_ACTIVITY_TYPE_INT,

                LAST_DATE_DATE,
                LAST_LATITUDE_DBL,
                LAST_LONGITUDE_DBL,
                LAST_PLACE_TXT,
                LAST_CHRONO_LONG,
                //LAST_ACTIVITY_TYPE_INT,

                TOTAL_TIME_LONG,
                TOTAL_DISTANCE_FLT,
                TOTAL_LOCATION_ACCURACY_FLT,
                AVERAGE_LOCATION_ACCURACY_FLT,
                TOTAL_LOCATIONS_INT,
                TOTAL_LOCATIONS_USED_INT,
                TOTAL_LOCATIONS_IGNORED_INT

                // AVERAGE_SPEED_FLT
        );
    }

    public static ImmutableList<String> getJourneyView() {
        return journeyView;
    }

    private static final ImmutableList<String> currentReadingsView;

    static {
        currentReadingsView = ImmutableList.of(

                CURRENT_LATITUDE_DBL,
                CURRENT_LONGITUDE_DBL,
                CURRENT_PLACE_TXT,
                CURRENT_ALTITUDE_DBL,
                CURRENT_HEADING_FLT,
                CURRENT_SPEED_FLT,
                CURRENT_LOCATION_ACCURACY_FLT,
                CURRENT_ACTIVITY_TYPE_INT,
                CURRENT_ACTIVITY_CONFIDENCE_INT
        );
    }

    public static ImmutableList<String> getCurrentReadingsView() {
        return currentReadingsView;
    }
}
