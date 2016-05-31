package com.soagrowers.android.tripcomputer.events;

import android.test.AndroidTestCase;

import com.google.android.gms.location.DetectedActivity;

import static android.test.MoreAsserts.assertAssignableFrom;

/**
 * Created by Ben on 08/09/2014.
 */
public class TestJourneyAutoStartEvent extends AndroidTestCase {

    JourneyAutoStartEvent event;
    DetectedActivity activity;
    final int type = DetectedActivity.IN_VEHICLE;
    final int confidence = 100;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        activity = new DetectedActivity(type, confidence);
        event = new JourneyAutoStartEvent(activity);
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }

    public void testEvent() throws Exception {
        assertAssignableFrom(AbstractEvent.class, event);
        assertEquals(event.getPhysicalActivity().getType(), type);
        assertEquals(event.getPhysicalActivity().getConfidence(), confidence);

        activity = new DetectedActivity(DetectedActivity.STILL, 99);
        event = new JourneyAutoStartEvent(activity);
        assertEquals(event.getPhysicalActivity().getType(), DetectedActivity.STILL);
        assertEquals(event.getPhysicalActivity().getConfidence(), 99);
    }
}
