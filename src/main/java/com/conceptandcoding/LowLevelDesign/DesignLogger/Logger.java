package main.java.com.conceptandcoding.LowLevelDesign.DesignLogger;


import main.java.com.conceptandcoding.LowLevelDesign.DesignLogger.Enum.LogLevel;
import main.java.com.conceptandcoding.LowLevelDesign.DesignLogger.handlers.LogHandler;
import main.java.com.conceptandcoding.LowLevelDesign.DesignLogger.model.LogMessage;

public class Logger {
    private static final Logger INSTANCE = new Logger();

    private final LogHandler handleChain;

    private Logger() {
        this.handleChain = LogHandlerConfiguration.build();
    }

    public static Logger getInstance(){
        return INSTANCE;
    }

    public void log(LogLevel level, String message){
        LogMessage msg = new LogMessage(message, System.currentTimeMillis(), level);
        handleChain.handle(msg);
    }

    public void debug(String message){
        log(LogLevel.DEBUG, message);
    }

    public void warn(String message){
        log(LogLevel.WARN, message);
    }

    public void info(String message){
        log(LogLevel.INFO, message);
    }

    public void error(String message){
        log(LogLevel.ERROR, message);
    }
}
