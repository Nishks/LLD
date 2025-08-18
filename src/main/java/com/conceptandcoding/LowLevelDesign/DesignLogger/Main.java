package com.conceptandcoding.LowLevelDesign.DesignLogger;

import com.conceptandcoding.LowLevelDesign.DesignLogger.appenders.ConsoleAppender;
import com.conceptandcoding.LowLevelDesign.DesignLogger.appenders.FileAppender;
import com.conceptandcoding.LowLevelDesign.DesignLogger.formatter.PlainTextFormatter;
import org.springframework.boot.logging.LogLevel;

public class Main {
    public static void main(String[] args) {
        Logger logger = Logger.getInstance();

        LogHandlerConfiguration.addAppenderForLevel(
                LogLevel.INFO,
                new ConsoleAppender(new PlainTextFormatter())
        );

        LogHandlerConfiguration.addAppenderForLevel(
                LogLevel.ERROR,
                new ConsoleAppender(new PlainTextFormatter())
        );

        LogHandlerConfiguration.addAppenderForLevel(
                LogLevel.ERROR,
                new FileAppender(new PlainTextFormatter(), "logs.txt")
        );

        // use
        logger.info("This is some key information"); // CONSOLE
        logger.error("There's an error"); // CONSOLE + FILE
    }
}
