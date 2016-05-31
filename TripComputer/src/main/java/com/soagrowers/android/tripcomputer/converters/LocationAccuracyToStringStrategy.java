package com.soagrowers.android.tripcomputer.converters;

import android.content.Context;

import com.google.common.base.Optional;
import com.soagrowers.android.tripcomputer.R;
import com.soagrowers.android.tripcomputer.data.MapBasedJourney;
import com.soagrowers.android.utils.StringUtils;

import java.text.DecimalFormat;

import static com.soagrowers.android.tripcomputer.data.MapBasedJourneyKeys.CURRENT_LOCATION_ACCURACY_FLT;
import static com.soagrowers.android.tripcomputer.data.MapBasedJourneyKeys.TOTAL_LOCATIONS_INT;
import static com.soagrowers.android.tripcomputer.data.MapBasedJourneyKeys.TOTAL_LOCATION_ACCURACY_FLT;

/**
 * Created by Ben on 03/10/2014.
 */
public class LocationAccuracyToStringStrategy extends AbstractToStringStrategy<MapBasedJourney> {

  public LocationAccuracyToStringStrategy(Context mContext) {
    super(mContext);
    setFormat(getContext().getString(R.string.format_location_accuracy));
  }

  @Override
  public Optional<String> toString(MapBasedJourney journey) {

    setJourney(journey);

    Optional<String> o = Optional.absent();

    if (getJourney().isPresent(TOTAL_LOCATION_ACCURACY_FLT)
      && getJourney().isPresent(TOTAL_LOCATIONS_INT)
      && getJourney().isPresent(CURRENT_LOCATION_ACCURACY_FLT)) {
      //Calculate the average accuracy number
      Float tot_loc_accuracy = (Float) getJourney().get(TOTAL_LOCATION_ACCURACY_FLT).get();
      Integer tot_loc_count = (Integer) getJourney().get(TOTAL_LOCATIONS_INT).get();
      Float curr_accuracy = (Float) getJourney().get(CURRENT_LOCATION_ACCURACY_FLT).get();

      if (tot_loc_accuracy > 0 && tot_loc_count > 0) {

        // report the Average accuracy
        StringBuilder sb = new StringBuilder();
        DecimalFormat accuracyFormat = new DecimalFormat(getFormat().get());

        Float avg_accuracy = tot_loc_accuracy / tot_loc_count;
        sb.append(accuracyFormat.format(curr_accuracy));
        sb.append(getContext().getString(R.string.txt_m));

        // append the current accuracy...
        sb.append(StringUtils.SPACE);
        sb.append(StringUtils.OPEN_BRACKET);
        sb.append(accuracyFormat.format(avg_accuracy));
        sb.append(getContext().getString(R.string.txt_m));
        sb.append(StringUtils.CLOSE_BRACKET);

        o = Optional.of(sb.toString());
      }
    }

    return o;
  }
}
