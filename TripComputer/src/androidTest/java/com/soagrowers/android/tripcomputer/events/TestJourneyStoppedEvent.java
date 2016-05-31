package com.soagrowers.android.tripcomputer.events;

import com.soagrowers.android.tripcomputer.data.Constants;
import com.soagrowers.android.tripcomputer.data.ImmutableJourney;
import com.soagrowers.android.tripcomputer.data.Journey;

import junit.framework.TestCase;

import static android.test.MoreAsserts.assertAssignableFrom;
import static android.test.MoreAsserts.assertNotEqual;

/**
 * Created by Ben on 17/09/2014.
 */
public class TestJourneyStoppedEvent extends TestCase {

    JourneyStoppedEvent event;
    ImmutableJourney journey;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        journey = new Journey();
        event = new JourneyStoppedEvent(journey);
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }

    public void testEvent() throws Exception {
        assertAssignableFrom(AbstractEvent.class, event);
        assertAssignableFrom(JourneyStatusEvent.class, event);

        assertTrue(event.getEventId() > Constants.ZERO_LONG);
        assertTrue(event.getEventTime() > Constants.ZERO_LONG);
        assertNotEqual(event.getEventId(), Constants.ZERO_LONG);
        assertNotEqual(event.getEventTime(), Constants.ZERO_LONG);
        assertEquals(event.getEventSource(), Constants.NOT_SET);
        assertSame(journey, event.getImmutableJourney());

        assertSame(JourneyStatusEvent.Type.STOP_EVENT, event.getType());
    }
}
