package com.soagrowers.android.tripcomputer.events;

import com.soagrowers.android.tripcomputer.data.ImmutableJourney;

/**
 * Created by Ben on 16/09/2014.
 */
public class JourneyStoppedEvent extends JourneyStatusEvent {

    public JourneyStoppedEvent(ImmutableJourney journey) {
        super(Type.STOP_EVENT, journey);
    }
}
