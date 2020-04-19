package backend.util;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.LogManager;
import java.util.logging.SimpleFormatter;

/**
 * This class is used to generate a logger that writes logs
 * of what happens during the programs runtime, which is useful
 * for things like debugging.
 */
public class Log {
    public static Logger logger = Logger.getAnonymousLogger();
    private FileHandler fh;
    /**
     * Used to store log filenames so that the program can select the latest
     */
    private static ArrayList<String> logs = new ArrayList<>();
    /**
     * The log that is currently being written to
     */
    private static String currentLog;

    static {
        try {
            currentLog = pickLog();
        } catch (URISyntaxException | UnsupportedEncodingException e) {
            logger.log(Level.SEVERE, "Log picklog");
        }
    }

    /**
     * This constructor creates a new logger, and makes sure the
     * that the logger writes to the correct log file.
     */
    public Log() {

        //the oldest of the three log files
        new File(currentLog).delete();
        try {
            File f = new File(currentLog);
            f.createNewFile();

            LogManager.getLogManager().reset();
            fh = new FileHandler(currentLog, true);
            fh.setLevel(Level.FINE);
            logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
            logger.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method adds the logs into an ArrayList which is then
     * sorted by the date last modified, so that the oldest log
     * can be chosen and returned.
     *
     * @return the filename of the oldest log
     */
    private static String pickLog() throws URISyntaxException, UnsupportedEncodingException {
        //three valid logs
        String logPath = directoryMaker.folderMaker("Logs");

        File log1 = new File(logPath + File.separator+ "Log1.log");
        File log2 = new File(logPath + File.separator + "Log2.log");
        File log3 = new File(logPath + File.separator + "Log3.log");

        logs.add(log1.getPath());
        logs.add(log2.getPath());
        logs.add(log3.getPath());
        //places the one that is the oldest last
        logs.sort((o1, o2) -> {
            File file1 = new File(o1);
            File file2 = new File(o2);
            //oldest last, so need to sort descending
            return (int) -(file1.lastModified() - file2.lastModified());
        });
        //chooses the last log, aka the oldest one
        currentLog = logs.get(logs.size() - 1);
        return currentLog;
    }

    /**
     * Writes new information to the current log file
     *
     * @param message the information to be written
     */
    public void logNewInfo(String message) {
        logger.log(Level.INFO, message + "\n");
    }

    /**
     * Writes a warning to the log file
     *
     * @param warning the warning to be written
     */
    public void logNewWarning(String warning) {
        logger.log(Level.WARNING, warning + "\n");
    }

    /**
     * Writes a new fatal error to the log file
     *
     * @param fatalWarning the fatal warning to be written, should
     *                     include he exception thrown if there is one
     */
    public void logNewFatalError(String fatalWarning) {
        logger.log(Level.SEVERE, fatalWarning);
    }
}
