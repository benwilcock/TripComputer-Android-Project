package com.soagrowers.android.tripcomputer.events;

import android.os.SystemClock;

import com.soagrowers.android.tripcomputer.data.Constants;
import com.soagrowers.android.utils.Assert;

import java.util.Random;

/**
 * Created by Ben on 16/09/2014.
 */
public abstract class AbstractEvent {

    private long eventId = Constants.ZERO_LONG;
    private long eventTime = Constants.ZERO_LONG;
    private String eventSource = Constants.NOT_SET;


    protected AbstractEvent() {
        eventId = Math.abs(new Random().nextLong());
        eventTime = SystemClock.elapsedRealtime();
    }

    protected AbstractEvent(String eventSource) {
        this();
        Assert.notNull(eventSource);
        this.eventSource = eventSource;
    }

    public long getEventId() {
        return eventId;
    }

    protected void setEventId(long eventId) {
        this.eventId = eventId;
    }

    public long getEventTime() {
        return eventTime;
    }

    public void setEventTime(long eventTime) {
        this.eventTime = eventTime;
    }

    public String getEventSource() {
        return eventSource;
    }

    public void setEventSource(String eventSource) {
        this.eventSource = eventSource;
    }
}
