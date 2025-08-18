package com.conceptandcoding.LowLevelDesign.DesignLogger.formatter;

import com.conceptandcoding.LowLevelDesign.DesignLogger.model.LogMessage;

public interface LogFormatter {
    String format(LogMessage message);
}
