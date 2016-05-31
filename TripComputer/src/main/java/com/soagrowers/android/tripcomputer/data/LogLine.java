package com.soagrowers.android.tripcomputer.data;

import android.text.format.DateFormat;

import com.soagrowers.android.utils.StringUtils;

import java.util.Arrays;
import java.util.Calendar;


/**
 * Created by Ben on 12/02/14.
 */
public final class LogLine {

    private static final String TIMECODE_FORMAT = "yyyyMMddHHmmss";
    private final String logLine;


    private LogLine(String tag, String message, String priority, Throwable ex) {

        //formulate the LogLine Text...
        StringBuilder pw = new StringBuilder();
        pw.append(DateFormat.format(TIMECODE_FORMAT, Calendar.getInstance().getTime()).toString());
        pw.append(StringUtils.SPACE);
        pw.append(priority);
        pw.append(StringUtils.SPACE);
        pw.append(tag);
        pw.append(StringUtils.COLON);
        pw.append(StringUtils.SPACE);
        pw.append(message);
        if (null != ex) {
            pw.append(StringUtils.SPACE);
            pw.append(Arrays.toString(ex.getStackTrace()));
        }

        //wrap the log to the next line
        pw.append(StringUtils.NEW_LINE);

        //store the logline
        this.logLine = pw.toString();
    }

    public String getLogLine() {
        return logLine;
    }
}
