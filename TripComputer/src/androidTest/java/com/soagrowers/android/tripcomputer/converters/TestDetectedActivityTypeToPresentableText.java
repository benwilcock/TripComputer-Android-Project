package com.soagrowers.android.tripcomputer.converters;

import android.content.Context;
import android.content.res.Resources;
import android.test.InstrumentationTestCase;

import com.google.android.gms.location.DetectedActivity;
import com.soagrowers.android.tripcomputer.R;

/**
 * Created by Ben on 08/09/2014.
 */
public class TestDetectedActivityTypeToPresentableText extends InstrumentationTestCase {

    private Context mContext;
    private Resources mRes;
    private AbstractToStringStrategy converter;
    private DetectedActivity activity;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        mContext = getInstrumentation().getTargetContext();
        mRes = getInstrumentation().getTargetContext().getResources();
        converter = new DetectedActivityTypeToToStringStrategy(mContext);
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }

    public void testInVehicle() throws Exception {
        activity = new DetectedActivity(DetectedActivity.IN_VEHICLE, 20);
        assertEquals(mRes.getString(R.string.txt_driving), converter.toString(activity).get());
    }

    public void testOnBicycle() throws Exception {
        activity = new DetectedActivity(DetectedActivity.ON_BICYCLE, 20);
        assertEquals(mRes.getString(R.string.txt_cycling), converter.toString(activity).get());
    }

    public void testOnFoot() throws Exception {
        activity = new DetectedActivity(DetectedActivity.ON_FOOT, 20);
        assertEquals(mRes.getString(R.string.txt_walking), converter.toString(activity).get());
    }

    public void testRunning() throws Exception {
        activity = new DetectedActivity(DetectedActivity.RUNNING, 20);
        assertEquals(mRes.getString(R.string.txt_running), converter.toString(activity).get());
    }

    public void testStill() throws Exception {
        activity = new DetectedActivity(DetectedActivity.STILL, 20);
        assertEquals(mRes.getString(R.string.txt_still), converter.toString(activity).get());
    }

    public void testUnknown() throws Exception {
        activity = new DetectedActivity(DetectedActivity.UNKNOWN, 20);
        assertEquals(mRes.getString(R.string.txt_unknown), converter.toString(activity).get());
    }

    public void testTilting() throws Exception {
        activity = new DetectedActivity(DetectedActivity.TILTING, 20);
        assertEquals(mRes.getString(R.string.txt_tilting), converter.toString(activity).get());
    }
}
