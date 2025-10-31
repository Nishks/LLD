package main.java.com.conceptandcoding.LowLevelDesign.DesignLogger;


import main.java.com.conceptandcoding.LowLevelDesign.DesignLogger.Enum.LogLevel;
import main.java.com.conceptandcoding.LowLevelDesign.DesignLogger.appenders.LogAppender;
import main.java.com.conceptandcoding.LowLevelDesign.DesignLogger.handlers.*;

public class LogHandlerConfiguration {

    private static final LogHandler debug = new DebugHandler();
    private static final LogHandler info = new InfoHandler();
    private static final LogHandler error = new ErrorHandler();
    private static final LogHandler warn = new WarnHandler();

    public static LogHandler build(){
        debug.setNext(info);
        info.setNext(warn);
        warn.setNext(error);
        return debug;
    }

    public static void addAppenderForLevel(LogLevel level, LogAppender appender){
        switch (level){
            case DEBUG -> debug.subscriber(appender);
            case INFO -> info.subscriber(appender);
            case WARN -> warn.subscriber(appender);
            case ERROR -> error.subscriber(appender);
        }
    }
}
