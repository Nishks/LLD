package main.java.com.conceptandcoding.LowLevelDesign.DesignLogger.handlers;


import main.java.com.conceptandcoding.LowLevelDesign.DesignLogger.Enum.LogLevel;
import main.java.com.conceptandcoding.LowLevelDesign.DesignLogger.appenders.LogAppender;
import main.java.com.conceptandcoding.LowLevelDesign.DesignLogger.model.LogMessage;

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
