package com.soagrowers.android.tripcomputer.converters;

import android.content.Context;
import android.text.format.DateFormat;

import com.google.common.base.Optional;
import com.soagrowers.android.tripcomputer.data.MapBasedJourney;

import java.util.Date;

/**
 * Created by Ben on 08/09/2014.
 */
public class DateToStringStrategy extends AbstractToStringStrategy<MapBasedJourney> {

  public DateToStringStrategy(Context context, String key, int format) {
    super(context);
    setKey(key);
    setFormat(getContext().getString(format));
  }

  @Override
  public Optional<String> toString(MapBasedJourney journey) {

    setJourney(journey);

    Optional<String> value = Optional.absent();
    Optional<Date> date = getJourney().get(getKey().get());
    if (date.isPresent()) {
      Optional<String> format = getFormat();
      if (format.isPresent()) {
        value = Optional.of(DateFormat.format(format.get(), date.get()).toString());
      } else {
        value = Optional.of(DateFormat.getDateFormat(getContext()).format(date.get()));
      }
    }

    return value;
  }
}
