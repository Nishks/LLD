package com.conceptandcoding.LowLevelDesign.DesignLogger.handlers;

import com.conceptandcoding.LowLevelDesign.DesignLogger.appenders.LogAppender;
import com.conceptandcoding.LowLevelDesign.DesignLogger.model.LogMessage;
import org.springframework.boot.logging.LogLevel;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class LogHandler {
    protected LogHandler next;
    protected final List<LogAppender> appenders = new CopyOnWriteArrayList<>();

    public void subscriber(LogAppender observer){
        appenders.add(observer);
    }

    public void setNext(LogHandler next) {
        this.next = next;
    }

    public void notifyObservers(LogMessage message){
        for(LogAppender appender: appenders)
            appender.append(message);
    }

    public void handle(LogMessage message){
        if(canHandle(message.getLevel()))
            notifyObservers(message);
        else if(next != null){
            next.handle(message);
        }
    }

    protected abstract boolean canHandle(LogLevel level);

}
