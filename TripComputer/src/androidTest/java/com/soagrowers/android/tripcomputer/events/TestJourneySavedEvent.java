package com.soagrowers.android.tripcomputer.events;

import com.soagrowers.android.tripcomputer.data.Constants;
import com.soagrowers.android.tripcomputer.data.Journey;

import junit.framework.TestCase;

import static android.test.MoreAsserts.assertAssignableFrom;
import static android.test.MoreAsserts.assertNotEqual;

/**
 * Created by Ben on 17/09/2014.
 */
public class TestJourneySavedEvent extends TestCase {

    JourneySavedEvent event;
    Journey journey;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        journey = new Journey();
        event = new JourneySavedEvent(journey);
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
        assertSame(journey, event.getImmutableJourney());
    }
}
