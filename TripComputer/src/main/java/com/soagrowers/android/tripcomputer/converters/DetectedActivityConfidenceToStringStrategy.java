package com.soagrowers.android.tripcomputer.converters;

import android.content.Context;

import com.google.android.gms.location.DetectedActivity;
import com.google.common.base.Optional;
import com.soagrowers.android.tripcomputer.data.Constants;
import com.soagrowers.android.utils.Assert;
import com.soagrowers.android.utils.StringUtils;

/**
 * Created by Ben on 08/09/2014.
 */
public class DetectedActivityConfidenceToStringStrategy extends AbstractToStringStrategy<DetectedActivity> {


    public DetectedActivityConfidenceToStringStrategy(Context context) {
        super(context);
    }

    @Override
    public Optional<String> toString(DetectedActivity detectedActivity) {

        Assert.notNull(detectedActivity);
        StringBuilder sb = new StringBuilder();
        int activityConfidence = detectedActivity.getConfidence();

        if (activityConfidence > Constants.ZERO_INT) {
            sb.append(StringUtils.OPEN_BRACKET);
            sb.append(String.valueOf(activityConfidence));
            sb.append(StringUtils.PERCENT);
            sb.append(StringUtils.CLOSE_BRACKET);
        }

        return Optional.of(sb.toString());
    }
}
