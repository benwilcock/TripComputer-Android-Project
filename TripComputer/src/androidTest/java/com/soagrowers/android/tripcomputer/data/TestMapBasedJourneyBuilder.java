package com.soagrowers.android.tripcomputer.data;

import android.test.AndroidTestCase;

import java.util.Map;
import java.util.UUID;

/**
 * Created by Ben on 15/08/2014.
 */
public class TestMapBasedJourneyBuilder extends AndroidTestCase {

    MapBasedJourney j;

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        j = MapBasedJourneyBuilder.build(mContext);
    }

    public void testMapBasedJourneyBuild() {

        assertTrue(j.get(MapBasedJourneyKeys.SETTING_ACCURACY_THRESHOLD_FLT).isPresent());
        assertTrue(j.get(MapBasedJourneyKeys.SETTING_DISTANCE_UNITS_INT).isPresent());
        assertTrue(j.get(MapBasedJourneyKeys.SETTING_CHARGE_VALUE_FLT).isPresent());
        assertTrue(j.get(MapBasedJourneyKeys.ID).isPresent());
        assertTrue(j.get(MapBasedJourneyKeys.IS_STARTED_BOOL).isPresent());
        assertTrue(j.get(MapBasedJourneyKeys.IS_PAUSED_BOOL).isPresent());
        assertTrue(j.get(MapBasedJourneyKeys.IS_FINISHED_BOOL).isPresent());

        assertTrue(j.get(MapBasedJourneyKeys.SETTING_ACCURACY_THRESHOLD_FLT).get() instanceof Float);
        assertTrue(j.get(MapBasedJourneyKeys.SETTING_DISTANCE_UNITS_INT).get() instanceof Integer);
        assertTrue(j.get(MapBasedJourneyKeys.SETTING_CHARGE_VALUE_FLT).get() instanceof Float);
        assertTrue(j.get(MapBasedJourneyKeys.ID).get() instanceof UUID);
        assertTrue(j.get(MapBasedJourneyKeys.IS_STARTED_BOOL).get() instanceof Boolean);
        assertTrue(j.get(MapBasedJourneyKeys.IS_PAUSED_BOOL).get() instanceof Boolean);
        assertTrue(j.get(MapBasedJourneyKeys.IS_FINISHED_BOOL).get() instanceof Boolean);

    }

    public void testMapBasedJourneyBuildFromMap() {

        Map m = j.delegate();
        UUID orig_id = (UUID) j.get(MapBasedJourneyKeys.ID).get();

        //Set the journey as started and finished...
        m.put(MapBasedJourneyKeys.IS_STARTED_BOOL, Boolean.TRUE);
        m.put(MapBasedJourneyKeys.IS_FINISHED_BOOL, Boolean.TRUE);

        //Add a key directly to test with...
        String key = "testKey";
        String value = "testValue";
        m.put(key, value);

        MapBasedJourney k = MapBasedJourneyBuilder.buildFromMap(m, mContext);

        // Check the status is as expected
        assertTrue(k.isStarted());
        assertTrue(k.isFinished());
        assertFalse(k.isRunning());

        // Check the key we added manually to the Map is there...
        assertTrue(k.get(key).isPresent());
        assertEquals(value, (String) k.get(key).get());

        //Check keys the builder will have added are there
        assertTrue(j.get(MapBasedJourneyKeys.SETTING_ACCURACY_THRESHOLD_FLT).isPresent());
        assertTrue(j.get(MapBasedJourneyKeys.SETTING_DISTANCE_UNITS_INT).isPresent());
        assertTrue(j.get(MapBasedJourneyKeys.SETTING_CHARGE_VALUE_FLT).isPresent());
        assertTrue(j.get(MapBasedJourneyKeys.ID).isPresent());

        assertTrue(j.get(MapBasedJourneyKeys.SETTING_ACCURACY_THRESHOLD_FLT).get() instanceof Float);
        assertTrue(j.get(MapBasedJourneyKeys.SETTING_DISTANCE_UNITS_INT).get() instanceof Integer);
        assertTrue(j.get(MapBasedJourneyKeys.SETTING_CHARGE_VALUE_FLT).get() instanceof Float);
        assertTrue(j.get(MapBasedJourneyKeys.ID).get() instanceof UUID);

        // Check the UUID's match
        assertEquals(orig_id.toString(), j.get(MapBasedJourneyKeys.ID).get().toString());

        // Check that other keys can't be added
        try {
            k.put(MapBasedJourneyKeys.CURRENT_ALTITUDE_DBL, Float.valueOf(22));
        } catch (IllegalStateException ise) {
            assertTrue(true);
        }

        // Check that settings can still be added
        k.putSetting(MapBasedJourneyKeys.SETTING_ACCURACY_THRESHOLD_FLT, Float.valueOf(22));
        assertTrue(j.get(MapBasedJourneyKeys.SETTING_ACCURACY_THRESHOLD_FLT).isPresent());
        assertTrue(j.get(MapBasedJourneyKeys.SETTING_ACCURACY_THRESHOLD_FLT).get() instanceof Float);
        assertEquals(Float.valueOf(22), (Float) j.get(MapBasedJourneyKeys.SETTING_ACCURACY_THRESHOLD_FLT).get());
    }
}
