package com.soagrowers.android.tripcomputer.events;

import android.location.Location;

import com.soagrowers.android.MockitoCompatibleInstrumentationTestCase;
import com.soagrowers.android.tripcomputer.data.Constants;

import static android.test.MoreAsserts.assertAssignableFrom;
import static android.test.MoreAsserts.assertNotEqual;
import static org.mockito.Mockito.mock;

/**
 * Created by Ben on 17/09/2014.
 */
public class TestLocationUpdatedEvent extends MockitoCompatibleInstrumentationTestCase {

    LocationUpdatedEvent event;
    Location location;
    String provider = "TEST_PROVIDER";

    @Override
    public void setUp() throws Exception {
        super.setUp();

        location = mock(Location.class);
        event = new LocationUpdatedEvent(location);
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
        assertSame(location, event.getLocation());
        assertEquals(location, event.getLocation());
    }
}
