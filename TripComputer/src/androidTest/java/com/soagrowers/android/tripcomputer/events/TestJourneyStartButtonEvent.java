package com.soagrowers.android.tripcomputer.events;

import com.soagrowers.android.tripcomputer.data.Constants;

import junit.framework.TestCase;

import static android.test.MoreAsserts.assertAssignableFrom;
import static android.test.MoreAsserts.assertNotEqual;

/**
 * Created by Ben on 17/09/2014.
 */
public class TestJourneyStartButtonEvent extends TestCase {

    JourneyStartButtonEvent event;
    String source = "TEST";

    @Override
    public void setUp() throws Exception {
        super.setUp();
        event = new JourneyStartButtonEvent();
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
        assertSame(event.getEventSource(), Constants.NOT_SET);
    }
}
