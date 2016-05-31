package com.soagrowers.android.tripcomputer.data;

import android.content.Context;
import android.content.res.Resources;
import android.os.SystemClock;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.Chronometer;

import com.soagrowers.android.tripcomputer.R;
import com.soagrowers.android.tripcomputer.TripComputer;

import static com.soagrowers.android.tripcomputer.data.MapBasedJourneyKeys.FIRST_CHRONO_LONG;

/**
 * Created by Ben on 19/08/2014.
 */
public class TestMapBasedJourneyUiWrapperChrono extends ActivityInstrumentationTestCase2<TripComputer> {

    private TripComputer mActivity;
    private Context mContext;
    private Resources mRes;
    private MapBasedJourney testJourney;
    private MapBasedJourneyUiWrapper testWrapper;

    public TestMapBasedJourneyUiWrapperChrono() {
        super("com.soagrowers.android.tripcomputer", TripComputer.class);
    }

    public TestMapBasedJourneyUiWrapperChrono(Class<TripComputer> activityClass) {
        super(activityClass);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mActivity = getActivity();
        mContext = getInstrumentation().getTargetContext();
        mRes = getInstrumentation().getTargetContext().getResources();
        //mActivity = (Activity)getInstrumentation().getTargetContext();
        testJourney = new MapBasedJourney();
        testWrapper = new MapBasedJourneyUiWrapper(testJourney, mContext);
    }

    public void testConfigureChronometer() throws InterruptedException {
        final Chronometer chrono = (Chronometer) mActivity.findViewById(R.id.chronometer);
        assertNotNull(chrono);

        // Before start
        testWrapper.configureChronometer(chrono);
        assertNotNull(chrono.getText());

        // Start the Journey
        testJourney.start();

        // Make the start appear to be 10s ago...
        Long start = (Long) testJourney.get(FIRST_CHRONO_LONG).get();
        start = start - (10 * Constants.MILLISECONDS_PER_SECOND);
        testJourney.put(FIRST_CHRONO_LONG, start);

        // Get the time as it is now...
        Long now = SystemClock.elapsedRealtime();

        // Get the estimated elapsed time in SECS...
        Long time = (now - start) / Constants.MILLISECONDS_PER_SECOND;

        // configure the chronometer view on the UI thread...
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                testWrapper.configureChronometer(chrono);
            }
        });

        // Sync with the thread
        getInstrumentation().waitForIdleSync();

        // Perform the test
        assertNotNull(chrono.getText());
        assertEquals("00:" + time + "s", chrono.getText());
    }
}
