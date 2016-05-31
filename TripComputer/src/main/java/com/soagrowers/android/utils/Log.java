package com.soagrowers.android.utils;

import android.content.Context;
import android.text.format.DateFormat;

import com.soagrowers.android.tripcomputer.R;
import com.soagrowers.android.tripcomputer.asynctasks.LogWriterAsyncTask;
import com.soagrowers.android.tripcomputer.data.Constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;


/**
 * Created by Ben on 09/12/13.
 */
public final class Log {

    public static final int VERBOSE = 0;
    public static final int DEBUG = 1;
    public static final int INFO = 2;
    public static final int WARN = 3;
    public static final int ERROR = 4;

    public static final String V = "V:";
    public static final String D = "D:";
    public static final String I = "I:";
    public static final String W = "W:";
    public static final String E = "E:";
    public static final String STRIP_PACKAGE_NAME = "com.soagrowers.android";
    private static final String TIMECODE_FORMAT = "yyyyMMddHHmmss";

    private static final int LINES_TO_KEEP = 100;
    private final String logFileFolder;
    private final String logFileName;
    private final String fileDateFormat;
    private final String fileDate;
    private List<String> logLines = new ArrayList<String>();

    private static int logLevel = 2;
    private static Log instance;
    private static final String TAG = Log.class.getSimpleName();
    private static Context context;

    private Log(Context theContext) {
        this.context = theContext;
        logLevel = theContext.getResources().getInteger(R.integer.logLevel);
        this.d(TAG, "The Log has been successfully instantiated.");
        this.logFileFolder = Constants.LOG_FILE_FOLDER_NAME;
        this.logFileName = Constants.LOG_FILE_NAME_PREFIX;
        this.fileDateFormat = context.getResources().getString(R.string.format_log_file_datetime);
        this.fileDate = DateFormat.format(fileDateFormat, Calendar.getInstance().getTime()).toString();
    }

    public static Log getInstance(Context theContext) {

        //The context is NULL and the instance is also NULL
        //this is bad.
        if (null == theContext && instance == null) {
            String message = "Can't initialise the LOG - Bad Context";
            RuntimeException e = new InstantiationException(message);
            android.util.Log.e(TAG, message, e);
            //throw e;
        }

        //The context is NULL but the instance is OK
        if (null == theContext && null != instance) {
            return instance;
        }

        //The context is OK but the instance is NULL
        //So create an instance
        if (null != theContext && null == instance) {

            instance = new Log(theContext);
            logLevel = theContext.getResources().getInteger(R.integer.logLevel);
            return instance;
        }

        //The context is OK and the instance is already full
        //Refresh the context
        context = theContext;
        //Return the instance
        return instance;
    }

    public static Log getInstance() {
        return getInstance(null);
    }

    public void v(String tag, String message) {
        if (logLevel == VERBOSE) {
            this.appendToLog(tag, message, V, null);
            android.util.Log.v(tag, message);
        }
    }

    public void d(String tag, String message) {
        if (logLevel <= DEBUG) {
            this.appendToLog(tag, message, D, null);
            android.util.Log.d(tag, message);
        }
    }

    public void d(String tag, String message, Throwable e) {
        if (logLevel <= DEBUG) {
            this.appendToLog(tag, message, D, e);
            android.util.Log.d(tag, message, e);
        }
    }

    public void i(String tag, String message) {
        if (logLevel <= INFO) {
            this.appendToLog(tag, message, I, null);
            android.util.Log.i(tag, message);
        }
    }

    public void w(String tag, String message) {
        if (logLevel <= WARN) {
            this.appendToLog(tag, message, W, null);
            android.util.Log.w(tag, message);
        }
    }

    public void e(String tag, String message) {
        if (logLevel <= ERROR) {
            this.appendToLog(tag, message, E, null);
            android.util.Log.e(tag, message);
        }
    }

    public void e(String tag, String message, Throwable e) {
        if (logLevel <= ERROR) {
            this.appendToLog(tag, message, E, e);
            android.util.Log.e(tag, message, e);
        }
    }

    /**
     * Add a LogLine to the LogLine store...
     *
     * @param tag
     * @param message
     * @param priority
     * @param ex
     */
    private void appendToLog(String tag, String message, String priority, Throwable ex) {
        Assert.notNull(tag);
        Assert.notNull(message);
        Assert.notNull(priority);

        if (context.getResources().getBoolean(R.bool.feature_logToFile)) {

            //If the Priority is not 'VERBOSE'...
            if (!priority.equals(Log.V)) {

                //formulate the LogLine Text...
                StringBuilder pw = new StringBuilder();
                pw.append(DateFormat.format(TIMECODE_FORMAT, Calendar.getInstance().getTime()).toString());
                pw.append(StringUtils.SPACE);
                pw.append(priority);
                pw.append(StringUtils.SPACE);
                pw.append(tag.replace(STRIP_PACKAGE_NAME, StringUtils.EMPTY_STRING));
                pw.append(StringUtils.COLON);
                pw.append(StringUtils.SPACE);
                pw.append(message);
                if (null != ex) {
                    pw.append(StringUtils.SPACE);
                    pw.append(Arrays.toString(ex.getStackTrace()));
                }

                this.logLines.add(pw.toString());
            }

            //Check if a flush is required...
            if (this.logLines.size() >= LINES_TO_KEEP) {
                flushLog();
            }
        }
    }

    /**
     * Write the stored logLines to the Log (if the LogToFile feature is enabled)...
     */
    public void flushLog() {

        //If this feature is enabled in this build...
        if (context.getResources().getBoolean(R.bool.feature_logToFile)) {

            android.util.Log.v(TAG, "Sending the LogLines to the LogFile.");
            List<String> copyOfLogLines = new ArrayList<String>(LINES_TO_KEEP);
            copyOfLogLines.addAll(logLines);

            //Create an AsyncTask to handle the processing
            LogWriterAsyncTask logWriter = new LogWriterAsyncTask(context, logFileFolder, logFileName + "-" + fileDate + ".txt");
            logWriter.execute(copyOfLogLines);

            //reset the LogLines now they've been used up...
            this.logLines.clear();
        }
    }
}
