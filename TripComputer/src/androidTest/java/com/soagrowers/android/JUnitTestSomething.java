package com.soagrowers.android;

import android.util.Log;

import com.soagrowers.android.tripcomputer.data.Constants;

import junit.framework.TestCase;

/**
 * Created by Ben on 01/08/2014.
 */
public class JUnitTestSomething extends TestCase {

    private static final String TAG = JUnitTestSomething.class.getSimpleName();

    public void testSomethingDumb() {
        assertEquals("These match!", Constants.ZERO_LONG, Constants.ZERO_LONG);

        try {
            assertEquals(Constants.ZERO_INT, Constants.ACTIVITY_START_CONFIDENCE_THRESHOLD);
            assertTrue(false);
            Log.e(TAG, "Activity confidence was unexpectedly ZERO.");
        } catch (AssertionError e) {
            assertTrue(true);
            Log.v(TAG, "Activity confidence was unexpectedly OK.");
        }
    }
}
