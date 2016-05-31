package com.soagrowers.android.utils;

import android.content.Context;
import android.text.format.DateFormat;

import com.soagrowers.android.tripcomputer.R;
import com.soagrowers.android.tripcomputer.data.Constants;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.Date;


/**
 * Created by Ben on 11/02/14.
 */
public final class JourneyWriter implements Runnable {

  private static final String TAG = JourneyWriter.class.getSimpleName();
  private final Context context;
  private final String journeyFileFolder;
  private final String journeyFileName;
  private final String format;
  private final String content;
  private final Log log;
  private boolean journeyFileOk;
  private File journeyFile;

  /**
   * Construct
   *
   * @param theContext
   */
  public JourneyWriter(Context theContext, String theContent) {
    Assert.notNull(theContent);
    Assert.notNull(theContext);

    this.context = theContext;
    this.content = theContent;

    this.log = Log.getInstance(theContext);
    this.format = context.getResources().getString(R.string.format_log_file_datetime);
    this.journeyFileFolder = Constants.JOURNEY_FILE_FOLDER_NAME;
    this.journeyFileName = Constants.JOURNEY_FILE_NAME_PREFIX;
  }

  /**
   * Starts executing the active part of the class' code. This method is
   * called when a thread is started that has been created with a class which
   * implements {@code Runnable}.
   */
  @Override
  public void run() {
    if (this.openFile()) {
      this.writeJourney();
      this.closeFile();
    }
  }

  /**
   * Set up the LogFile
   *
   * @return
   */

  private boolean openFile() {
    Assert.notNull(context);
    log.d(TAG, "External Storage Readable: " + FileUtils.isExternalStorageReadable());
    log.d(TAG, "External Storage Writable: " + FileUtils.isExternalStorageWritable());

    //Fast fail...
    if (!FileUtils.isExternalStorageReadable() || !FileUtils.isExternalStorageWritable()) {
      log.w(TAG, "External storage is unusable. Can't write a Journey File.");
      journeyFileOk = false;
      return journeyFileOk;
    }

    //Get the external files dir (but NOT a specific type like PICTURES)...
    File root = context.getExternalFilesDir(null);
    log.d(TAG, "Root AbsolutePath: " + root.getAbsolutePath());
    log.d(TAG, "Root CanWrite: " + root.canWrite());

    if (root.canWrite()) {
      try {
        File journeyFolder = new File(root, this.journeyFileFolder);
        if (!journeyFolder.exists()) {
          journeyFolder.mkdir();
        }

        log.d(TAG, "JourneyFolder AbsolutePath: " + journeyFolder.getAbsolutePath());
        log.d(TAG, "JourneyFolder Name: " + journeyFolder.getName());
        log.d(TAG, "JourneyFolder CanWrite: " + journeyFolder.canWrite());

        //set up some items to make the journeyFileName
        Date today = Calendar.getInstance().getTime();
        String reportDate = DateFormat.format(format, today).toString();
        //Create the file...
        this.journeyFile = new File(journeyFolder, this.journeyFileName + "-" + reportDate + ".txt");
        log.i(TAG, "JourneyFile Name: " + journeyFile.getName());

        //everything went OK...
        journeyFileOk = true;
      } catch (Exception ex) {
        //if there is a problem, try and continue...
        log.e(TAG, "There was a problem writing the Journey to a file.", ex);
        journeyFileOk = false;
      }
    }

    //return the result...
    return journeyFileOk;
  }


  private void writeJourney() {
    Assert.notNull(content);

    //If the LOG is working and the Priority is not 'VERBOSE'
    if (this.journeyFileOk) {
      try {
        //get a File appending FileWriter...
        FileWriter fileWriter = new FileWriter(journeyFile, false);
        PrintWriter pw = new PrintWriter(fileWriter);

        //formulate the File Line...
        pw.append(content);
        pw.append(StringUtils.NEW_LINE);

        //Flush the line to the LogFile...
        pw.flush();
        pw.close();
      } catch (Exception ioe) {
        //if there is a problem, try and continue...
        log.e(TAG, "There was a problem appending to the Journey file.", ioe);
      }
    }
  }


  private void closeFile() {
    if (journeyFileOk) {
      journeyFileOk = false;
    }
  }
}
