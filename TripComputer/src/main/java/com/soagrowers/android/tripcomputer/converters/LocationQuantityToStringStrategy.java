package com.soagrowers.android.tripcomputer.converters;

import android.content.Context;

import com.google.common.base.Optional;
import com.soagrowers.android.tripcomputer.R;
import com.soagrowers.android.tripcomputer.data.MapBasedJourney;
import com.soagrowers.android.utils.StringUtils;

import java.text.DecimalFormat;

import static com.soagrowers.android.tripcomputer.data.MapBasedJourneyKeys.TOTAL_LOCATIONS_INT;
import static com.soagrowers.android.tripcomputer.data.MapBasedJourneyKeys.TOTAL_LOCATIONS_USED_INT;

/**
 * Created by Ben on 03/10/2014.
 */
public class LocationQuantityToStringStrategy extends AbstractToStringStrategy<MapBasedJourney> {

  public LocationQuantityToStringStrategy(Context mContext) {
    super(mContext);
  }

  @Override
  public Optional<String> toString(MapBasedJourney journey) {

    setJourney(journey);

    StringBuilder sb = new StringBuilder();
    DecimalFormat qtyFormat = new DecimalFormat(getContext().getString(R.string.format_location_qty));

    if (getJourney().isPresent(TOTAL_LOCATIONS_INT)) {
      Integer total_locations = (Integer) getJourney().get(TOTAL_LOCATIONS_INT).get();
      sb.append(qtyFormat.format(total_locations));
    } else {
      return Optional.absent();
    }

    if (getJourney().isPresent(TOTAL_LOCATIONS_USED_INT)) {
      Integer total_locations_used = (Integer) getJourney().get(TOTAL_LOCATIONS_USED_INT).get();
      sb.append(StringUtils.SPACE);
      sb.append(StringUtils.bracket(qtyFormat.format(total_locations_used)));
    }

    return Optional.of(sb.toString());
  }
}
