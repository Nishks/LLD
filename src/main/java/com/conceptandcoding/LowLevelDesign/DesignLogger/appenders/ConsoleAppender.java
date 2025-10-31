package main.java.com.conceptandcoding.LowLevelDesign.DesignLogger.appenders;

import main.java.com.conceptandcoding.LowLevelDesign.DesignLogger.formatter.LogFormatter;
import main.java.com.conceptandcoding.LowLevelDesign.DesignLogger.model.LogMessage;

public class ConsoleAppender implements LogAppender {

    private final LogFormatter formatter;

    public ConsoleAppender(LogFormatter formatter) {
        this.formatter = formatter;
    }

    @Override
    public void append(LogMessage message) {
        System.out.println(formatter.format(message));
    }
}
