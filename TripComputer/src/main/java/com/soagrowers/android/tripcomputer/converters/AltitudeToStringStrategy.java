package com.soagrowers.android.tripcomputer.converters;

import android.content.Context;

import com.google.common.base.Optional;
import com.soagrowers.android.tripcomputer.R;
import com.soagrowers.android.tripcomputer.data.MapBasedJourney;
import com.soagrowers.android.utils.StringUtils;

import java.text.DecimalFormat;

import static com.soagrowers.android.tripcomputer.data.MapBasedJourneyKeys.CURRENT_ALTITUDE_DBL;

/**
 * Created by Ben on 03/10/2014.
 */
public class AltitudeToStringStrategy extends AbstractToStringStrategy<MapBasedJourney>{

  private final String METERS;

  public AltitudeToStringStrategy(Context mContext) {
    super(mContext);
    setKey(CURRENT_ALTITUDE_DBL);
    setFormat(mContext.getString(R.string.format_altitude));
    METERS = mContext.getString(R.string.txt_meters);
  }

  @Override
  public Optional<String> toString(MapBasedJourney journey) {

    setJourney(journey);
    Optional<String> o = Optional.absent();

    if (getJourney().isPresent(getKey().get())) {
      Double altitude = (Double) getJourney().get(getKey().get()).get();
      DecimalFormat df = new DecimalFormat(getFormat().get());
      StringBuilder sb = new StringBuilder();
      sb.append(df.format(altitude));
      sb.append(StringUtils.SPACE);
      sb.append(METERS);
      o = Optional.of(sb.toString());
    }

    return o;
  }
}
