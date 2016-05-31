package com.soagrowers.android;

import android.test.InstrumentationTestCase;

/**
 * Created by Ben on 17/09/2014.
 */
public abstract class MockitoCompatibleInstrumentationTestCase extends InstrumentationTestCase{

    @Override
    public void setUp() throws Exception {
        super.setUp();

        //int a cache (used by dexmaker for Mockito)
        System.setProperty( "dexmaker.dexcache", getInstrumentation().getTargetContext().getCacheDir().getPath() );
    }
}
