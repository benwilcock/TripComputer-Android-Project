package com.soagrowers.android.tripcomputer.data;


import android.os.SystemClock;

import com.google.common.base.Optional;
import com.google.common.collect.ForwardingMap;
import com.soagrowers.android.tripcomputer.BuildConfig;
import com.soagrowers.android.utils.Assert;
import com.soagrowers.android.utils.StringUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Ben on 11/08/2014.
 */
public class MapBasedJourney extends ForwardingMap<String, Object> {

  final Map<String, Object> delegate;

  public MapBasedJourney() {

    this.delegate = new HashMap<String, Object>();
    this.delegate.put(MapBasedJourneyKeys.ID, UUID.randomUUID());
    this.delegate.put(MapBasedJourneyKeys.MODEL_TYPE, this.getClass().getCanonicalName());
    this.delegate.put(MapBasedJourneyKeys.MODEL_VERSION_NAME, BuildConfig.VERSION_NAME);
    this.delegate.put(MapBasedJourneyKeys.MODEL_VERSION_CODE, BuildConfig.VERSION_CODE);
    this.delegate.put(MapBasedJourneyKeys.IS_STARTED_BOOL, Boolean.FALSE);
    this.delegate.put(MapBasedJourneyKeys.IS_PAUSED_BOOL, Boolean.FALSE);
    this.delegate.put(MapBasedJourneyKeys.IS_FINISHED_BOOL, Boolean.FALSE);

  }

  protected MapBasedJourney(Map<String, Object> map) {
    this.delegate = map;
  }

  @Override
  protected Map<String, Object> delegate() {
    return delegate;
  }

  @Override
  public Optional get(Object key) {
    Assert.notNull(key);
    if (!(key instanceof String)) {
      throw new IllegalArgumentException("Key values should be of type 'String' and not of type " + key.getClass().getSimpleName());
    }
    Assert.isFalse(key.equals(""));
    Assert.isFalse(key.equals("null"));
    return get((String) key);
  }

  public Optional get(Optional<String> key) {
    Assert.notNull(key);
    if (key.isPresent()) {
      if (isPresent(key.get())) {
        return get(key.get());
      }
    }
    return Optional.absent();
  }

  public Optional get(String key) {
    Assert.notNull(key);
    Assert.isFalse(key.equals(""));
    Assert.isFalse(key.equals("null"));

    Optional value;

    if (containsKey(key)) {

      Object obj = this.delegate.get(key);
      Assert.notNull(obj);

      if (null == obj) {
        value = Optional.absent();
      } else {
        value = Optional.of(obj);
      }

    } else {
      value = Optional.absent();
    }

    Assert.notNull(value);
    return value;
  }

  public boolean isPresent(String key) {
    return this.get(key).isPresent();
  }

  @Override
  public Optional put(String key, Object value) {
    Assert.notNull(key);
    Assert.isFalse(key.equals(""));
    Assert.isFalse(key.equals("null"));
    Assert.notNull(value);

    if (isRunning()) {
      Object obj = super.put(key, value);

      if (null == obj) {
        return Optional.of(value);
      } else {
        return Optional.of(obj);
      }
    } else {
      // The Journey is NOT running at the moment.
      throw new IllegalStateException("Journey needs to be running for values to be PUT!");
    }
  }

  public <T> Optional<T> put(Optional<String> key, Optional<T> value) {
    if (key.isPresent() == false
      || key.get().equals(StringUtils.EMPTY_STRING)) {
      throw new IllegalArgumentException("Key values should be of type 'Optional<String>' and contain a valid value");
    }

    if (value.isPresent() == false) {
      throw new IllegalArgumentException("Values should be of type 'Optional<?>' and should not be Absent");
    }


    this.put(key.get(), value.get());
    return value;
  }


  protected Optional putSetting(String key, Object value) {
    Assert.notNull(key);
    Assert.isFalse(key.equals(""));
    Assert.isFalse(key.equals("null"));
    Assert.isTrue(key.contains("setting"));
    Assert.notNull(value);

    Object obj = super.put(key, value);
    if (null == obj) {
      return Optional.of(value);
    } else {
      return Optional.of(obj);
    }
  }

  public UUID getId() {
    Optional id = get(MapBasedJourneyKeys.ID);
    if (id.isPresent()) {
      return (UUID) id.get();
    } else {
      UUID uid = UUID.randomUUID();
      put(MapBasedJourneyKeys.ID, uid);
      return uid;
    }
  }

  public boolean isStarted() {
    Optional isStarted = get(MapBasedJourneyKeys.IS_STARTED_BOOL);
    if (isStarted.isPresent()) {
      return (Boolean) isStarted.get();
    } else {
      throw new IllegalStateException("Key: " + MapBasedJourneyKeys.IS_STARTED_BOOL + " is missing from the JourneyMap.");
    }
  }

  public boolean isFinished() {
    Optional isFinished = get(MapBasedJourneyKeys.IS_FINISHED_BOOL);
    if (isFinished.isPresent()) {
      return (Boolean) isFinished.get();
    } else {
      throw new IllegalStateException("Key: " + MapBasedJourneyKeys.IS_FINISHED_BOOL + " is missing from the JourneyMap.");
    }
  }

  public boolean isRunning() {
    Boolean isStarted = isStarted();
    Boolean isFinished = isFinished();

    if (isStarted == true && isFinished == false) {
      return true;
    } else {
      return false;
    }
  }

  /**
   * Used to START a Journey.
   *
   * @see com.soagrowers.android.tripcomputer.data.MapBasedJourneyBuilder
   */
  public void start() {
    if (isFinished()) {
      throw new IllegalStateException("An attempt was made to START a FINISHED Journey");
    }

    //set the statuses & start time...
    this.delegate.put(MapBasedJourneyKeys.IS_STARTED_BOOL, Boolean.TRUE);
    this.delegate.put(MapBasedJourneyKeys.FIRST_DATE_DATE, new Date());
    this.delegate.put(MapBasedJourneyKeys.FIRST_CHRONO_LONG, SystemClock.elapsedRealtime());

    return;
  }


  /**
   * Used to STOP a Journey.
   */
  public void stop() {

    if (isStarted() == false) {
      throw new IllegalStateException("An attempt was made to STOP an UNSTARTED Journey");
    }
    if (isFinished()) {
      throw new IllegalStateException("An attempt was made to STOP a FINISHED Journey");
    }

    //set the statuses & start time...
    this.delegate.put(MapBasedJourneyKeys.IS_FINISHED_BOOL, Boolean.TRUE);
    this.delegate.put(MapBasedJourneyKeys.LAST_DATE_DATE, new Date());
    this.delegate.put(MapBasedJourneyKeys.LAST_CHRONO_LONG, SystemClock.elapsedRealtime());

    return;
  }
}
