package com.soagrowers.android.tripcomputer.converters;

import android.content.Context;

import com.google.common.base.Optional;
import com.soagrowers.android.tripcomputer.R;
import com.soagrowers.android.tripcomputer.data.Constants;
import com.soagrowers.android.tripcomputer.data.MapBasedJourney;
import com.soagrowers.android.utils.ConversionUtils;

import java.text.DecimalFormat;

import static com.soagrowers.android.tripcomputer.data.MapBasedJourneyKeys.SETTING_CHARGE_VALUE_FLT;
import static com.soagrowers.android.tripcomputer.data.MapBasedJourneyKeys.TOTAL_DISTANCE_FLT;

/**
 * Created by Ben on 08/09/2014.
 */
public class CostToStringStrategy extends AbstractToStringStrategy<MapBasedJourney> {

  public CostToStringStrategy(Context context) {
    super(context);
    setFormat(getContext().getString(R.string.format_charge_value));
  }

  @Override
  public Optional<String> toString(MapBasedJourney journey) {

    setJourney(journey);

    Optional<String> o = Optional.absent();

    if (getJourney().get(TOTAL_DISTANCE_FLT).isPresent()
      && getJourney().get(SETTING_CHARGE_VALUE_FLT).isPresent()) {

      //fast fail if we haven't moved
      Float meters = (Float) getJourney().get(TOTAL_DISTANCE_FLT).get();
      Float charge = (Float) getJourney().get(SETTING_CHARGE_VALUE_FLT).get();

      //If either the charge or the distance are 0, return nothing...
      if (charge == Constants.ZERO_FLOAT || meters == Constants.ZERO_FLOAT) {
        return o; // Prevent multiply by Zero.
      }

      //set up the distance param
      int distance;

      switch (getDistanceUnitsSetting()) {
        case Constants.MILES:
          distance = Double.valueOf(Math.floor(ConversionUtils.convertMetersToMiles(meters))).intValue();
          break;

        case Constants.KILOMETERS:
          distance = Double.valueOf(Math.floor(ConversionUtils.convertMetersToKilometers(meters))).intValue();
          break;

        default:
          distance = Double.valueOf(meters).intValue();
          break;
      }

      //Now calculate the cost...
      Float cost = distance * charge;

      //Append the value to the Cost string in the right format...
      StringBuilder sb = new StringBuilder();
      sb.append(getContext().getString(R.string.currency));
      DecimalFormat costFormat = new DecimalFormat(getFormat().get());
      sb.append(costFormat.format(cost));
      o = Optional.of(sb.toString());
    }

    return o;
  }
}
