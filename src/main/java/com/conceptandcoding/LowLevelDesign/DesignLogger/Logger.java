package com.conceptandcoding.LowLevelDesign.DesignLogger;

import com.conceptandcoding.LowLevelDesign.DesignLogger.handlers.LogHandler;
import com.conceptandcoding.LowLevelDesign.DesignLogger.model.LogMessage;
import org.springframework.boot.logging.LogLevel;

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
