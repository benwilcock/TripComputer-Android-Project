package com.soagrowers.android.tripcomputer.events;

import com.soagrowers.android.tripcomputer.data.ImmutableJourney;
import com.soagrowers.android.utils.Assert;

/**
 * Created by Ben on 26/06/2014.
 */
public class JourneyNotSavedEvent extends AbstractEvent {

    private ImmutableJourney journey;

    public JourneyNotSavedEvent(ImmutableJourney journey) {
        super();
        Assert.notNull(journey);
        this.journey = journey;
    }

    public ImmutableJourney getImmutableJourney() {
        return journey;
    }
}
