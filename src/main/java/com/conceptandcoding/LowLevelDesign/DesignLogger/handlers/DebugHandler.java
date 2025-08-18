package com.conceptandcoding.LowLevelDesign.DesignLogger.handlers;

import org.springframework.boot.logging.LogLevel;

public class DebugHandler extends LogHandler{

    protected boolean canHandle(LogLevel level){
        return level == LogLevel.DEBUG;
    }

}
