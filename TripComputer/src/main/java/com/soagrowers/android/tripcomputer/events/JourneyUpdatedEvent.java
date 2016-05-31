package com.soagrowers.android.tripcomputer.events;

import com.soagrowers.android.tripcomputer.data.Journey;

/**
 * Created by Ben on 22/05/2014.
 */
public class JourneyUpdatedEvent extends JourneyStatusEvent {

    public JourneyUpdatedEvent(Journey theJourney) {
        super(Type.UPDATE_EVENT, theJourney);
    }
}
