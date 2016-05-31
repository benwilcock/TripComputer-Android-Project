package com.soagrowers.android.tripcomputer.events;

import com.soagrowers.android.tripcomputer.data.ImmutableJourney;
import com.soagrowers.android.utils.Assert;

/**
 * Created by Ben on 23/05/2014.
 */
public abstract class JourneyStatusEvent extends AbstractEvent {

    public enum Type {
        START_EVENT,
        UPDATE_EVENT,
        STOP_EVENT
    }

    private Type type;
    private ImmutableJourney journey;

    protected JourneyStatusEvent(Type type, ImmutableJourney journey) {
        super();
        this.type = type;
        Assert.notNull(journey);
        this.journey = journey;
    }

    public Type getType() {
        return type;
    }

    public ImmutableJourney getImmutableJourney() {
        return journey;
    }
}
