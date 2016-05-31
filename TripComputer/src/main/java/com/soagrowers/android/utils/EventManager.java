package com.soagrowers.android.utils;

import de.greenrobot.event.EventBus;


/**
 * Created by Ben on 20/05/2014.
 */
public final class EventManager {

    private static final EventBus BUS = EventBus.getDefault();


    public static EventBus getInstance() {
        return BUS;
    }

    private EventManager() {
        // No instances.
    }
}
