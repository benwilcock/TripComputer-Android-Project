package com.soagrowers.android.tripcomputer.converters;

import android.content.Context;
import android.content.res.Resources;
import android.test.InstrumentationTestCase;

import com.google.android.gms.location.DetectedActivity;

/**
 * Created by Ben on 08/09/2014.
 */
public class TestDetectedActivityConfidenceToPresentableText extends InstrumentationTestCase {

    private Context mContext;
    private Resources mRes;
    private AbstractToStringStrategy converter;
    private DetectedActivity activity;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        mContext = getInstrumentation().getTargetContext();
        mRes = getInstrumentation().getTargetContext().getResources();
        converter = new DetectedActivityConfidenceToStringStrategy(mContext);
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }

    public void testNinetyNinePercentConfidence() throws Exception {
        activity = new DetectedActivity(DetectedActivity.IN_VEHICLE, 99);
        assertEquals("(99%)", converter.toString(activity).get());
    }
}
