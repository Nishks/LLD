package com.conceptandcoding.LowLevelDesign.DesignLogger.model;

import org.springframework.boot.logging.LogLevel;

public class LogMessage {
    private String message;
    private Long time;
    private LogLevel level;

    public LogMessage(String message, Long time, LogLevel level){
        this.level = level;
        this.time = time;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public LogLevel getLevel() {
        return level;
    }

    public void setLevel(LogLevel level) {
        this.level = level;
    }
}
