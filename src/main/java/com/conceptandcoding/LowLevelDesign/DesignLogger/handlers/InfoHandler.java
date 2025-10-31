package main.java.com.conceptandcoding.LowLevelDesign.DesignLogger.handlers;

import main.java.com.conceptandcoding.LowLevelDesign.DesignLogger.Enum.LogLevel;

public class InfoHandler extends LogHandler{

    protected boolean canHandle(LogLevel level){
        return level == LogLevel.INFO;
    }

}
