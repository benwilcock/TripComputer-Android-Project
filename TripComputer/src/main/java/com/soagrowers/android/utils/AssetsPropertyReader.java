package com.soagrowers.android.utils;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by Ben on 12/02/14.
 */

public class AssetsPropertyReader {

    private static final String TAG = AssetsPropertyReader.class.getCanonicalName();
    private Context context;
    private Properties properties;


    public AssetsPropertyReader(Context context) {

        this.context = context;
        this.properties = new Properties();
    }

    public Properties getProperties(String FileName) throws IOException {

        /**
         * getAssets() Return an AssetManager instance for your
         * application's package. AssetManager Provides access to an
         * application's raw asset files;
         */
        AssetManager assetManager = context.getAssets();
        /**
         * Open an asset using ACCESS_STREAMING mode. This
         */
        InputStream inputStream = assetManager.open(FileName);
        /**
         * Loads properties from the specified InputStream,
         */
        properties.load(inputStream);

        //return the file...
        return properties;
    }

}