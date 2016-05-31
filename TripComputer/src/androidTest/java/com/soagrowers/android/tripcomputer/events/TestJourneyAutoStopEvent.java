package com.soagrowers.android.tripcomputer.events;

import com.google.android.gms.location.DetectedActivity;

import junit.framework.TestCase;

import static android.test.MoreAsserts.assertAssignableFrom;

/**
 * Created by Ben on 08/09/2014.
 */
public class TestJourneyAutoStopEvent extends TestCase {

    JourneyAutoStopEvent event;
    DetectedActivity activity;
    final int type = DetectedActivity.STILL;
    final int confidence = 100;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        activity = new DetectedActivity(type, confidence);
        event = new JourneyAutoStopEvent(activity);
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }

    public void testEvent() throws Exception {
        assertAssignableFrom(AbstractEvent.class, event);
        assertEquals(event.getPhysicalActivity().getType(), type);
        assertEquals(event.getPhysicalActivity().getConfidence(), confidence);

        activity = new DetectedActivity(DetectedActivity.IN_VEHICLE, 99);
        event = new JourneyAutoStopEvent(activity);
        assertEquals(event.getPhysicalActivity().getType(), DetectedActivity.IN_VEHICLE);
        assertEquals(event.getPhysicalActivity().getConfidence(), 99);
    }
}
