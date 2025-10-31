package main.java.com.conceptandcoding.LowLevelDesign.DesignLogger.formatter;


import main.java.com.conceptandcoding.LowLevelDesign.DesignLogger.model.LogMessage;

public interface LogFormatter {
    String format(LogMessage message);
}
