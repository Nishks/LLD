package com.conceptandcoding.LowLevelDesign.DesignLogger.handlers;

import org.springframework.boot.logging.LogLevel;

public class WarnHandler extends LogHandler{

    protected boolean canHandle(LogLevel level){
        return level == LogLevel.WARN;
    }
}
