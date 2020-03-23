package backend;
import java.io.File;
import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Log{
    private static Logger logger = Logger.getAnonymousLogger();
    private static FileHandler fh;

    public static void saveLog(String file_name){
        boolean fileRemade = false;
        try{
            File f = new File(file_name);
            if(f.exists()){
                f.delete();
                fileRemade=true;
            }
            if(!f.exists()||fileRemade){
                f.createNewFile();
            }
            LogManager.getLogManager().reset();
            fh=new FileHandler(file_name,true);
            fh.setLevel(Level.FINE);
            logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
            logger.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void logNewInfo(String message){
        logger.log(Level.INFO,message);
    }

    
    public static void logNewWarning(String warning){
        logger.log(Level.WARNING,warning);
    }

    public static void logNewFatalError(String fatalwarning){
        logger.log(Level.SEVERE,fatalwarning);
    }
}