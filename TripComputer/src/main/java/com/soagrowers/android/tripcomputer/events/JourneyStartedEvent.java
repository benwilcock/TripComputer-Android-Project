package com.soagrowers.android.tripcomputer.events;

import com.soagrowers.android.tripcomputer.data.ImmutableJourney;

/**
 * Created by Ben on 16/09/2014.
 */
public class JourneyStartedEvent extends JourneyStatusEvent {

    public JourneyStartedEvent(ImmutableJourney journey) {
        super(Type.START_EVENT, journey);
    }
}
