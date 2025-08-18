package com.conceptandcoding.LowLevelDesign.DesignLogger.appenders;

import com.conceptandcoding.LowLevelDesign.DesignLogger.formatter.LogFormatter;
import com.conceptandcoding.LowLevelDesign.DesignLogger.model.LogMessage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class FileAppender implements LogAppender{
    private final LogFormatter formatter;
    private final BufferedWriter writer;

    public FileAppender(LogFormatter formatter, String fileName) {
        this.formatter = formatter;
        try{
            this.writer = new BufferedWriter(new FileWriter(fileName, true));
        } catch (IOException e) {
            throw new RuntimeException("Failed to open log File ", e);
        }
    }

    @Override
    public synchronized void append(LogMessage message) {
        try {
            writer.write(formatter.format(message));
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized void close(){
        try{
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
