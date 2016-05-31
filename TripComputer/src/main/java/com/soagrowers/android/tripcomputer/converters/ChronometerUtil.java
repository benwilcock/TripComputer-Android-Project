package com.soagrowers.android.tripcomputer.converters;

import android.content.Context;
import android.os.SystemClock;
import android.widget.Chronometer;

import com.google.common.base.Optional;
import com.soagrowers.android.tripcomputer.data.MapBasedJourney;
import com.soagrowers.android.utils.Assert;

import static com.soagrowers.android.tripcomputer.data.MapBasedJourneyKeys.FIRST_CHRONO_LONG;
import static com.soagrowers.android.tripcomputer.data.MapBasedJourneyKeys.LAST_CHRONO_LONG;

/**
 * Created by Ben on 03/10/2014.
 */
public class ChronometerUtil extends AbstractToStringStrategy<Chronometer>{

  public ChronometerUtil(Context mContext) {
    super(mContext);
  }

  public Optional<String> toString(MapBasedJourney journey, Chronometer chronometer) {
    setJourney(journey);
    chronometer = configure(getJourney(), chronometer);
    return Optional.of(chronometer.getText().toString());
  }

  public Chronometer configure(MapBasedJourney journey, Chronometer chronometer){

    Assert.notNull(chronometer);
    setJourney(journey);

    if (getJourney().isRunning()
      && getJourney().isPresent(FIRST_CHRONO_LONG)) {
      //use the present chronoBase value
      Long chrono_base = (Long) getJourney().get(FIRST_CHRONO_LONG).get();
      chronometer.setBase(chrono_base);
    }

    if (getJourney().isFinished()
      && getJourney().isPresent(LAST_CHRONO_LONG)) {
      //trip has stopped
      //How long was the trip?
      Long chrono_start = (Long) getJourney().get(FIRST_CHRONO_LONG).get();
      Long chrono_stop = (Long) getJourney().get(LAST_CHRONO_LONG).get();
      long tripLength = chrono_stop - chrono_start;

      //take the Trip length from the current time and use that
      long currentChronoBase = SystemClock.elapsedRealtime() - tripLength;
      //set the chronometer's base.
      chronometer.setBase(currentChronoBase);
    }

    return chronometer;
  }

  @Override
  public Optional<String> toString(Chronometer chronometer) {
    return Optional.of(super.toString());
  }
}
