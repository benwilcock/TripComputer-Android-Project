package com.soagrowers.android.tripcomputer.converters;

import android.content.Context;

import com.google.common.base.Optional;
import com.soagrowers.android.tripcomputer.R;
import com.soagrowers.android.tripcomputer.data.MapBasedJourney;
import com.soagrowers.android.utils.StringUtils;

import java.text.DecimalFormat;

import static com.soagrowers.android.tripcomputer.data.MapBasedJourneyKeys.CURRENT_HEADING_FLT;

/**
 * Created by Ben on 03/10/2014.
 */
public class HeadingToStringStrategy  extends AbstractToStringStrategy<MapBasedJourney>{
  public HeadingToStringStrategy(Context mContext) {
    super(mContext);
    setKey(CURRENT_HEADING_FLT);
    setFormat(getContext().getString(R.string.format_degrees));
  }

  @Override
  public Optional<String> toString(MapBasedJourney mapBasedJourney) {
    setJourney(mapBasedJourney);
    Optional<String> o = Optional.absent();

    if (getJourney().isPresent(getKey().get())) {

      Float heading = (Float) getJourney().get(getKey().get()).get();
      DecimalFormat degreesFormat = new DecimalFormat(getFormat().get());

      StringBuilder sb = new StringBuilder();
      sb.append(StringUtils.convertToCompassString(heading));
      sb.append(StringUtils.SPACE);
      sb.append(StringUtils.OPEN_BRACKET);
      sb.append(degreesFormat.format(heading));
      sb.append(StringUtils.DEGREES);
      sb.append(StringUtils.CLOSE_BRACKET);
      o = Optional.of(sb.toString());
    }

    return o;
  }
}
