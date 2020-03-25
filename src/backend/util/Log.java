package backend.util;
import java.io.File;
import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Log{
    public Logger logger = Logger.getAnonymousLogger();
    FileHandler fh;

    public Log(String file_name){
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

    public void logNewInfo(String message){
        logger.log(Level.INFO,message);
    }


    public void logNewWarning(String warning){
        logger.log(Level.WARNING,warning);
    }

    public void logNewFatalError(String fatalWarning){
        logger.log(Level.SEVERE,fatalWarning);
    }
}
