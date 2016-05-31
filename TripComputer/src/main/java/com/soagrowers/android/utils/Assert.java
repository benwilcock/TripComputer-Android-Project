package com.soagrowers.android.utils;

import com.soagrowers.android.tripcomputer.BuildConfig;

/**
 * Created by Ben on 23/02/14.
 */
public final class Assert {


    public static void isTrue(boolean truism) {
        if (BuildConfig.DEBUG && truism == false) {
            throw new AssertionError("Assertion Failed Error.");
        }
    }

    public static void isFalse(boolean falsehood) {
        if (BuildConfig.DEBUG && falsehood == true) {
            throw new AssertionError("Assertion Failed Error.");
        }
    }

    /**
     * Check if an object variable is Null or not.
     * Only works if BuildConfig.DEBUG == true
     *
     * @param object
     * @throws AssertionError when the Object is null.
     */
    public static void notNull(Object object) {
        if (BuildConfig.DEBUG && null == object) {
            throw new AssertionError("Unexpected Null Parameter Error");
        }
    }
}
