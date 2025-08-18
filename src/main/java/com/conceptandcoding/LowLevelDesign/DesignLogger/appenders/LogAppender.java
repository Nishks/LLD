package com.conceptandcoding.LowLevelDesign.DesignLogger.appenders;

import com.conceptandcoding.LowLevelDesign.DesignLogger.model.LogMessage;

public interface LogAppender {
    void append(LogMessage message);
}
