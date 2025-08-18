package com.conceptandcoding.LowLevelDesign.DesignLogger.handlers;

import org.springframework.boot.logging.LogLevel;

public class ErrorHandler extends LogHandler{

    protected boolean canHandle(LogLevel level){
        return level == LogLevel.ERROR;
    }

}
