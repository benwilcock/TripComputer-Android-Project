package com.soagrowers.android.tripcomputer.data;

import java.util.Date;
import java.util.UUID;

/**
 * Created by Ben on 12/12/13.
 */
public interface ImmutableJourney {

    public UUID getId();

    public boolean isStarted();

    public boolean isFinished();

    public boolean isRunning();

    public String getCurrentPlace();

    public String getFirstPlace();

    public String getLastPlace();

    public float getTotalDistance();

    public Date getStartedDate();

    public Date getStoppedDate();

    public long getChronometerBase();

    public long getChronometerStop();

    public long getTotalTime();

    public double getCurrentLatitude();

    public double getCurrentLongitude();

    public int getUnusedLocationCount();

    public int getUsedLocationCount();

    public int getTotalLocationCount();

    public float getAccuracyThreshold();

    public float getCurrentAccuracy();

    public float getTotalAccuracy();

    public int getDistanceUnits();

    public float getChargeValue();

    public float getCurrentHeading();

    public double getCurrentAltitude();

    public int getCurrentActivityType();

    public int getCurrentActivityConfidence();
}
