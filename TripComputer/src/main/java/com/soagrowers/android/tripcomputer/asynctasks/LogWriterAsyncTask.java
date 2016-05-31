package com.soagrowers.android.tripcomputer.asynctasks;

import android.content.Context;
import android.os.AsyncTask;

import com.soagrowers.android.utils.Assert;
import com.soagrowers.android.utils.FileUtils;
import com.soagrowers.android.utils.Log;
import com.soagrowers.android.utils.StringUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;


/**
 * Created by Ben on 11/02/14.
 */
public class LogWriterAsyncTask extends AsyncTask<Object, Void, Boolean> {

    private static final String TAG = LogWriterAsyncTask.class.getCanonicalName();
    private final Context context;
    private final String logFileFolder;
    private final String logFileName;
    private Log log;


    public LogWriterAsyncTask(Context context, String logFileFolder, String logFileName) {
        this.context = context;
        this.logFileFolder = logFileFolder;
        this.logFileName = logFileName;
        this.log = Log.getInstance(context);
    }

    /**
     * Override this method to perform a computation on a background thread. The
     * specified parameters are the parameters passed to {@link #execute}
     * by the caller of this task.
     * <p/>
     * This method can call {@link #publishProgress} to publish updates
     * on the UI thread.
     *
     * @param params The parameters of the task.
     * @return A result, defined by the subclass of this task.
     * @see #onPreExecute()
     * @see #onPostExecute
     * @see #publishProgress
     */
    //@DebugLog SLOW!!
    @Override
    protected Boolean doInBackground(Object... params) {

        try {
            //Get the LogFile (if required)...
            File logFile = this.getLogFile();

            //Unpack the params...
            List<String> logLines = (List<String>) params[0];
            log.v(TAG, logLines.size() + " Lines to be written to the logFile");

            //write the LogMessage to the LogFile...
            this.appendToLog(logFile, logLines);

            //return a successful result...
            return Boolean.TRUE;
        } catch (Exception ex) {
            //There was an unexpected exception.
            //Nothing can be done, just quit silently...
            return Boolean.FALSE;
        }
    }

    /**
     * Append the list of LogLines to the LogFile given.
     *
     * @param logFile
     * @param logLines
     */
    //@DebugLog - SLOW!!!
    private void appendToLog(File logFile, List<String> logLines) {

        Assert.notNull(logFile);
        Assert.notNull(logLines);

        //If there are lines to be written...
        if (logLines.size() > 0) {

            try {
                //get an appending FileWriter...
                FileWriter fileWriter = new FileWriter(logFile, true);
                PrintWriter pw = new PrintWriter(fileWriter);
                android.util.Log.v(TAG, "Writing to LogFile: " + logFile.getName() + " " + String.valueOf(logLines.size()) + " lines");

                //Write each logLine to the file...
                for (String logLine : logLines) {

                    //formulate the LogLine Text...
                    pw.append(logLine);

                    //wrap the log to the next line
                    pw.append(StringUtils.NEW_LINE);

                    //Flush the line to the LogFile...
                    pw.flush();
                }

                //Close the PrintWriter
                pw.close();
                //Close the FileWriter
                fileWriter.close();

            } catch (Exception ioe) {
                //if there is a problem, try and continue...
                log.e(TAG, "There was a problem appending to the logfile.", ioe);
            }
        }
    }

    /**
     * Set up the LogFile on the External storage (if available)
     *
     * @return
     */

    private File getLogFile() throws IOException {

        log.v(TAG, "External Storage Readable: " + FileUtils.isExternalStorageReadable());
        log.v(TAG, "External Storage Writable: " + FileUtils.isExternalStorageWritable());

        //Fast fail...
        if (!FileUtils.isExternalStorageReadable() || !FileUtils.isExternalStorageWritable()) {
            log.w(TAG, "External storage is unusable. Can't write a Log File.");
            throw new IOException("Storage NOT Writable!");
        }

        //Get the external files dir (but NOT a specific type like PICTURES)...
        File root = context.getExternalFilesDir(null);
        log.v(TAG, "File Root AbsolutePath: " + root.getAbsolutePath());
        log.v(TAG, "Root CanWrite: " + root.canWrite());

        if (root.canWrite()) {
            try {
                //Get the LogFiles Folder...
                File tripLogFolder = new File(root, logFileFolder);
                if (!tripLogFolder.exists()) {
                    //Create it if it doesn't exist.
                    tripLogFolder.mkdir();
                }

                //Log out some details to the LogCat...
                log.v(TAG, "LogFolder AbsolutePath: " + tripLogFolder.getAbsolutePath());
                log.v(TAG, "LogFolder Name: " + tripLogFolder.getName());
                log.v(TAG, "LogFolder CanWrite: " + tripLogFolder.canWrite());
                //Create the file...
                File logFile = new File(tripLogFolder, logFileName);

                //return the file for further writing...
                return logFile;

            } catch (Exception ex) {
                //if there is a problem, try and continue...
                log.e(TAG, "There was a problem writing the logfile.", ex);
                throw new IOException("Unable to append to the LogFile: " + ex.getMessage());
            }
        } else {
            throw new IOException("Unable to write to the root folder");
        }
    }
}
