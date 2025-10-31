package main.java.com.conceptandcoding.LowLevelDesign.DesignLogger.handlers;


import main.java.com.conceptandcoding.LowLevelDesign.DesignLogger.Enum.LogLevel;

public class DebugHandler extends LogHandler{

    protected boolean canHandle(LogLevel level){
        return level == LogLevel.DEBUG;
    }
}
