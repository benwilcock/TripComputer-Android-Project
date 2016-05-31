package com.soagrowers.android.tripcomputer.data;

import android.content.Context;
import android.widget.Chronometer;

import com.google.android.gms.location.DetectedActivity;
import com.google.common.base.Optional;
import com.soagrowers.android.tripcomputer.R;
import com.soagrowers.android.tripcomputer.converters.AltitudeToStringStrategy;
import com.soagrowers.android.tripcomputer.converters.AverageSpeedToStringStrategy;
import com.soagrowers.android.tripcomputer.converters.BasicToStringStrategy;
import com.soagrowers.android.tripcomputer.converters.ChronometerUtil;
import com.soagrowers.android.tripcomputer.converters.CostToStringStrategy;
import com.soagrowers.android.tripcomputer.converters.DateToStringStrategy;
import com.soagrowers.android.tripcomputer.converters.DetectedActivityConfidenceToStringStrategy;
import com.soagrowers.android.tripcomputer.converters.DetectedActivityTypeToToStringStrategy;
import com.soagrowers.android.tripcomputer.converters.DistanceToStringStrategy;
import com.soagrowers.android.tripcomputer.converters.HeadingToStringStrategy;
import com.soagrowers.android.tripcomputer.converters.LocationAccuracyToStringStrategy;
import com.soagrowers.android.tripcomputer.converters.LocationQuantityToStringStrategy;
import com.soagrowers.android.utils.StringUtils;

import static com.soagrowers.android.tripcomputer.data.MapBasedJourneyKeys.CURRENT_ACTIVITY_CONFIDENCE_INT;
import static com.soagrowers.android.tripcomputer.data.MapBasedJourneyKeys.CURRENT_ACTIVITY_TYPE_INT;
import static com.soagrowers.android.tripcomputer.data.MapBasedJourneyKeys.FIRST_DATE_DATE;
import static com.soagrowers.android.tripcomputer.data.MapBasedJourneyKeys.LAST_DATE_DATE;
import static com.soagrowers.android.tripcomputer.data.MapBasedJourneyKeys.TOTAL_DISTANCE_FLT;

/**
 * Created by Ben on 15/08/2014.
 */
public class MapBasedJourneyUiWrapper {

  private final DetectedActivityTypeToToStringStrategy activityTypeToText;
  private final DetectedActivityConfidenceToStringStrategy activityConfidenceToText;
  private final DistanceToStringStrategy totalDistanceToText;
  private final DateToStringStrategy firstDateToText;
  private final DateToStringStrategy lastDateToText;
  private final AverageSpeedToStringStrategy averageSpeedToText;
  private final CostToStringStrategy totalCostToText;
  private final AltitudeToStringStrategy altitudeToText;
  private final HeadingToStringStrategy headingToText;
  private final LocationQuantityToStringStrategy locationQtyToText;
  private final LocationAccuracyToStringStrategy locationAccToText;
  private final BasicToStringStrategy latToText;
  private final BasicToStringStrategy longToText;
  private final ChronometerUtil chronoUtil;
  private Optional<MapBasedJourney> journey = Optional.absent();
  private Optional<Context> context = Optional.absent();

  /**
   * Constructor for setting helpers etc...
   *
   * @param newJourney
   * @param context
   */
  public MapBasedJourneyUiWrapper(MapBasedJourney newJourney, Context context) {
    this.journey = Optional.of(newJourney);
    this.context = Optional.of(context);
    activityTypeToText = new DetectedActivityTypeToToStringStrategy(context);
    activityConfidenceToText = new DetectedActivityConfidenceToStringStrategy(context);
    totalDistanceToText = new DistanceToStringStrategy(context, TOTAL_DISTANCE_FLT);
    firstDateToText = new DateToStringStrategy(context, FIRST_DATE_DATE, R.string.format_dateandtime);
    lastDateToText = new DateToStringStrategy(context, LAST_DATE_DATE, R.string.format_dateandtime);
    altitudeToText = new AltitudeToStringStrategy(context);
    averageSpeedToText = new AverageSpeedToStringStrategy(context);
    headingToText = new HeadingToStringStrategy(context);
    totalCostToText = new CostToStringStrategy(context);
    locationQtyToText = new LocationQuantityToStringStrategy(context);
    locationAccToText = new LocationAccuracyToStringStrategy(context);
    latToText = new BasicToStringStrategy(context, MapBasedJourneyKeys.CURRENT_LATITUDE_DBL);
    longToText = new BasicToStringStrategy(context, MapBasedJourneyKeys.CURRENT_LONGITUDE_DBL);
    chronoUtil = new ChronometerUtil(context);
  }

  /**
   * Use this method to do a check for the presence of a particular key before asking for it
   *
   * @param key
   * @return
   */
  public boolean isPresent(Object key) {
    return getJourney().get(key).isPresent();
  }

  protected MapBasedJourney getJourney() {
    return journey.get();
  }

  protected Context getContext() {
    return context.get();
  }

  /**
   * Map detected activity types to strings
   *
   * @return A user-readable name for the type
   * @deprecated Need to split
   */
  public Optional<String> getCurrentActivityTypeAsString() {

    // Unpack the optionals for the data we're after
    Optional o = getJourney().get(CURRENT_ACTIVITY_TYPE_INT);
    Optional p = getJourney().get(CURRENT_ACTIVITY_CONFIDENCE_INT);

    if (o.isPresent() && p.isPresent()) {

      StringBuilder sb = new StringBuilder();
      Integer activityType = (Integer) o.get();
      Integer confidence = (Integer) p.get();

      DetectedActivity activity = new DetectedActivity(activityType, confidence);
      sb.append(activityTypeToText.toString(activity));
      sb.append(StringUtils.SPACE);
      sb.append(activityConfidenceToText.toString(activity));
      return Optional.of(sb.toString());

    } else {
      return Optional.absent();
    }
  }

  public Optional<String> getCurrentLongitudeAsString() {
    return longToText.toString(getJourney());
  }

  public Optional<String> getCurrentLatitudeAsString() {
    return latToText.toString(getJourney());
  }

  public Optional<String> getStartTimeAsString() {
    return firstDateToText.toString(getJourney());
  }

  public Optional<String> getStopTimeAsString() {
    return lastDateToText.toString(getJourney());
  }

  public Optional<String> getTotalDistanceAsString() {
    return totalDistanceToText.toString(getJourney());
  }

  public Optional<String> getAverageSpeedAsString() {
    return averageSpeedToText.toString(getJourney());
  }

  public Optional<String> getCostAsString() {
    return totalCostToText.toString(getJourney());
  }

  public Chronometer configureChronometer(Chronometer chronometer) {
    return chronoUtil.configure(getJourney(), chronometer);
  }

  public Optional<String> getLocationAccuracyAsString() {
    return locationAccToText.toString(getJourney());
  }

  public Optional<String> getLocationQuantityAsString() {
    return locationQtyToText.toString(getJourney());
  }

  public Optional<String> getHeadingAsString() {
    return headingToText.toString(getJourney());
  }

  public Optional<String> getCurrentAltitudeAsString() {
    return altitudeToText.toString(getJourney());
  }
}
