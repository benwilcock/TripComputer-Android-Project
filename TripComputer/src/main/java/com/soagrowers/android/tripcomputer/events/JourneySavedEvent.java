package com.soagrowers.android.tripcomputer.events;

import com.soagrowers.android.tripcomputer.data.ImmutableJourney;
import com.soagrowers.android.tripcomputer.data.Journey;
import com.soagrowers.android.utils.Assert;

/**
 * Created by Ben on 26/06/2014.
 */
public class JourneySavedEvent extends AbstractEvent {

    private final Journey journey;

    public JourneySavedEvent(Journey journey) {
        super();
        Assert.notNull(journey);
        this.journey = journey;
    }

    public ImmutableJourney getImmutableJourney() {
        return (ImmutableJourney) journey;
    }
}
