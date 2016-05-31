package com.soagrowers.android.tripcomputer.converters;

import android.content.Context;

import com.google.common.base.Optional;
import com.soagrowers.android.tripcomputer.R;
import com.soagrowers.android.tripcomputer.data.Constants;
import com.soagrowers.android.tripcomputer.data.MapBasedJourney;
import com.soagrowers.android.utils.ConversionUtils;
import com.soagrowers.android.utils.StringUtils;

import java.text.DecimalFormat;

import static com.soagrowers.android.tripcomputer.data.MapBasedJourneyKeys.SETTING_DISTANCE_UNITS_INT;

/**
 * Created by Ben on 08/09/2014.
 */
public class DistanceToStringStrategy extends AbstractToStringStrategy<MapBasedJourney> {

  public DistanceToStringStrategy(Context context, String key) {
    super(context);
    setKey(key);
    setFormat(getContext().getString(R.string.format_distance_value));
  }

  @Override
  public Optional<String> toString(MapBasedJourney journey) {

    setJourney(journey);

    if (!getKey().isPresent()) {
      throw new IllegalStateException("Incorrectly configured. Set a MAP 'key'");
    }

    Optional<String> o = Optional.absent();

    if (journey.get(getKey().get()).isPresent() && getJourney().get(SETTING_DISTANCE_UNITS_INT).isPresent()) {

      Float meters = (Float) getJourney().get(getKey().get()).get();
      StringBuilder sb = new StringBuilder();

      // Fast fail on ZERO meters
      if (meters <= Constants.ZERO_FLOAT) {
        return o;
      }

      DecimalFormat df = new DecimalFormat(getFormat().get());
      Integer distance = Constants.ZERO_INT;

      if (journey.get(SETTING_DISTANCE_UNITS_INT).isPresent()) {
        Integer measurements = (Integer) journey.get(SETTING_DISTANCE_UNITS_INT).get();
        switch (measurements) {
          case Constants.MILES:
            distance = Double.valueOf(Math.floor(ConversionUtils.convertMetersToMiles(meters))).intValue();
            sb.append(df.format(distance));
            sb.append(StringUtils.SPACE);
            sb.append(getContext().getString(R.string.txt_miles));
            break;
          case Constants.KILOMETERS:
            distance = Double.valueOf(Math.floor(ConversionUtils.convertMetersToKilometers(meters))).intValue();
            sb.append(df.format(distance));
            sb.append(StringUtils.SPACE);
            sb.append(getContext().getString(R.string.txt_kilometers));
            break;
          default:
            distance = Double.valueOf(Math.floor(meters)).intValue();
            sb.append(df.format(distance));
            sb.append(StringUtils.SPACE);
            sb.append(getContext().getString(R.string.txt_meters));
            break;
        }

        o = Optional.of(sb.toString());
      }
    }

    return o;
  }
}
