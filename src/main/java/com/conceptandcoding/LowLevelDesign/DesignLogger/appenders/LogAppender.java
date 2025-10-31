package main.java.com.conceptandcoding.LowLevelDesign.DesignLogger.appenders;


import main.java.com.conceptandcoding.LowLevelDesign.DesignLogger.model.LogMessage;

public interface LogAppender {
    void append(LogMessage message);
}
