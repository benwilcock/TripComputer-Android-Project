package com.soagrowers.android.tripcomputer.converters;

import android.content.Context;

import com.google.common.base.Optional;
import com.soagrowers.android.tripcomputer.R;
import com.soagrowers.android.tripcomputer.data.Constants;
import com.soagrowers.android.tripcomputer.data.MapBasedJourney;
import com.soagrowers.android.utils.ConversionUtils;
import com.soagrowers.android.utils.StringUtils;

import java.text.DecimalFormat;

import static com.soagrowers.android.tripcomputer.data.Constants.*;
import static com.soagrowers.android.tripcomputer.data.MapBasedJourneyKeys.SETTING_DISTANCE_UNITS_INT;
import static com.soagrowers.android.tripcomputer.data.MapBasedJourneyKeys.TOTAL_DISTANCE_FLT;
import static com.soagrowers.android.tripcomputer.data.MapBasedJourneyKeys.TOTAL_TIME_LONG;

/**
 * Created by Ben on 01/10/2014.
 */
public class AverageSpeedToStringStrategy extends AbstractToStringStrategy<MapBasedJourney>{

  public AverageSpeedToStringStrategy(Context context) {
    super(context);
  }

  @Override
  public Optional<String> toString(MapBasedJourney j) {

    setJourney(j);
    Optional<String> o = Optional.absent();

    if (j.isPresent(TOTAL_DISTANCE_FLT)
      && j.isPresent(SETTING_DISTANCE_UNITS_INT)
      && j.isPresent(TOTAL_TIME_LONG)
      ) {

      //Get the elapsed Time, Distance & DecimalFormat

      Long elapsed = (Long) getJourney().get(TOTAL_TIME_LONG).get();
      Float distance = (Float) getJourney().get(TOTAL_DISTANCE_FLT).get();
      Long seconds = elapsed / MILLISECONDS_PER_SECOND;

      // Fast fail if we haven't been moving...
      if (distance <= ZERO_FLOAT || seconds <= ZERO_LONG) {
        return o;
      }

      Float averageSpeedInMetersSecond = Float.valueOf(distance / seconds);
      DecimalFormat speedFormat = new DecimalFormat(getContext().getString(R.string.format_average_speed));
      StringBuilder sb = new StringBuilder();

      //Switch String based on MILES, KILOMETERS or METERS

        switch (getDistanceUnitsSetting()) {
          case MILES:
            float mph = ConversionUtils.convertMsecToMph(averageSpeedInMetersSecond);
            sb.append(speedFormat.format(mph));
            sb.append(StringUtils.SPACE);
            sb.append(getContext().getString(R.string.txt_mph));
            break;

          case KILOMETERS:
            float kph = ConversionUtils.convertMsecToKph(averageSpeedInMetersSecond);
            sb.append(speedFormat.format(kph));
            sb.append(StringUtils.SPACE);
            sb.append(getContext().getString(R.string.txt_kph));
            break;

          default:
            sb.append(averageSpeedInMetersSecond);
            sb.append(StringUtils.SPACE);
            sb.append(getContext().getString(R.string.txt_mSec));
            break;
        }

        o = Optional.of(sb.toString());
      }

    return o;
  }
}
