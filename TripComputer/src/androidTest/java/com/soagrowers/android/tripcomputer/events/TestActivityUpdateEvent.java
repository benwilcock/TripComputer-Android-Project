package com.soagrowers.android.tripcomputer.events;

import com.google.android.gms.location.DetectedActivity;
import com.soagrowers.android.tripcomputer.data.Constants;

import junit.framework.TestCase;

import static android.test.MoreAsserts.assertAssignableFrom;
import static android.test.MoreAsserts.assertNotEqual;

/**
 * Created by Ben on 17/09/2014.
 */
public class TestActivityUpdateEvent extends TestCase {

    ActivityUpdateEvent event;
    DetectedActivity activity;
    final int type = DetectedActivity.IN_VEHICLE;
    final int confidence = 100;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        activity = new DetectedActivity(type, confidence);
        event = new ActivityUpdateEvent(activity);
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }

    public void testEvent() throws Exception {
        assertAssignableFrom(AbstractEvent.class, event);
        assertTrue(event.getEventId() > Constants.ZERO_LONG);
        assertTrue(event.getEventTime() > Constants.ZERO_LONG);
        assertNotEqual(event.getEventId(), Constants.ZERO_LONG);
        assertNotEqual(event.getEventTime(), Constants.ZERO_LONG);
        assertEquals(event.getEventSource(), Constants.NOT_SET);
        assertSame(activity, event.getActivity());
    }
}
