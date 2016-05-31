package com.soagrowers.android.tripcomputer.converters;

import android.content.Context;

import com.google.common.base.Optional;
import com.soagrowers.android.tripcomputer.data.MapBasedJourney;

/**
 * Created by Ben on 02/10/2014.
 */
public class BasicToStringStrategy extends AbstractToStringStrategy<MapBasedJourney>{

  public BasicToStringStrategy(Context mContext, String key) {
    super(mContext);
    setKey(key);
  }

  @Override
  public Optional<String> toString(MapBasedJourney j) {

    setJourney(j);

    Optional value = getJourney().get(getKey().get());

    if(value.isPresent()){
      return Optional.of(value.get().toString());
    }
    else
    {
      return Optional.absent();
    }
  }
}
