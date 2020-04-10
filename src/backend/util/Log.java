package backend.util;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Log{
    public static Logger logger = Logger.getAnonymousLogger();
    FileHandler fh;
    /**
     * used to store log filenames so that the program can select the latest
     */
    private static ArrayList<String> logs = new ArrayList<>();
    /**
     * the log that is currently being written to
     */
    private static String currentLog = pickLog();

    /**
     * Constructs a new logger
     */
    public Log(){
        //the oldest of the three log files
        new File(currentLog).delete();
        try{
            File f = new File(currentLog);
            f.createNewFile();

            LogManager.getLogManager().reset();
            fh=new FileHandler(currentLog,true);
            fh.setLevel(Level.FINE);
            logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
            logger.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * picks the oldest log
     * @return the filename of the oldest log
     */
    private static String pickLog(){
        //three valid logs
        logs.add("Logs/Log1.log");
        logs.add("Logs/Log2.log");
        logs.add("Logs/Log3.log");
        //places the one that is the oldest last
        logs.sort((o1, o2) -> {
            File file1 = new File(o1);
            File file2 = new File(o2);
            //oldest last, so need to sort descending
            return (int) -(file1.lastModified() - file2.lastModified());
        });
        //chooses the last log, aka the oldest one
        currentLog = logs.get(logs.size()-1);
        return currentLog;
    }

    /**
     * writes new info to the current log file
     * @param message the info you want to save in the log
     */
    public void logNewInfo(String message){
        logger.log(Level.INFO,message);
    }

    /**
     * logs new warning to the log file
     * @param warning the warning you want to save
     */
    public void logNewWarning(String warning){
        logger.log(Level.WARNING,warning);
    }

    /**
     * logs a new fatal error to the log file
     * @param fatalWarning the fatal warning you want to save, should include the exception that was likely thrown to achieve a fatal error
     */
    public void logNewFatalError(String fatalWarning){
        logger.log(Level.SEVERE,fatalWarning);
    }
}
