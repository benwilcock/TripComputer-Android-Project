package com.soagrowers.android.tripcomputer.converters;

import android.content.Context;

import com.google.common.base.Optional;
import com.soagrowers.android.tripcomputer.data.MapBasedJourney;

import static com.soagrowers.android.tripcomputer.data.Constants.MILES;
import static com.soagrowers.android.tripcomputer.data.MapBasedJourneyKeys.SETTING_DISTANCE_UNITS_INT;

/**
 * Created by Ben on 08/09/2014.
 */
public abstract class AbstractToStringStrategy<T> {

  private Optional<Context> mContext = Optional.absent();
  private Optional<String> key = Optional.absent();
  private Optional<String> format = Optional.absent();
  private Optional<MapBasedJourney> journey = Optional.absent();

  protected AbstractToStringStrategy(){}

  public AbstractToStringStrategy(Context mContext) {
    this.mContext = Optional.of(mContext);
  }

  /*
  public AbstractToStringStrategy(Context mContext, String key) {
    this.mContext = Optional.of(mContext);
    this.key = Optional.of(key);
    this.format = Optional.of(key);
  }

  public AbstractToStringStrategy(Context mContext, String key, int format) {
    this.mContext = Optional.of(mContext);
    this.key = Optional.of(key);
    this.format = Optional.of(this.mContext.get().getString(format));
  }
  */

  protected Integer getDistanceUnitsSetting() {

    Optional<Integer> i = Optional.of(MILES);

    if (getJourney().get(SETTING_DISTANCE_UNITS_INT).isPresent()) {
      i = Optional.of((Integer) getJourney().get(SETTING_DISTANCE_UNITS_INT).get());
    }

    return i.get();
  }

  public Context getContext() {
    return mContext.get();
  }

  public void setContext(Context context) {
    this.mContext = Optional.of(context);
  }

  public Optional<String> getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = Optional.of(key);
  }

  public Optional<String> getFormat() {
    return format;
  }

  public void setFormat(String format) {
    this.format = Optional.of(format);
  }

  public MapBasedJourney getJourney() {
    return journey.get();
  }

  public void setJourney(MapBasedJourney journey) {
    this.journey = Optional.of(journey);
  }

  public abstract Optional<String> toString(T t);
}
