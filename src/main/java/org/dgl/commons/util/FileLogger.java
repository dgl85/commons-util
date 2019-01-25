package org.dgl.commons.util;

import java.io.*;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.concurrent.locks.ReentrantLock;

public class FileLogger {

    private BufferedWriter logWriter = null;
    private ReentrantLock writeLock = new ReentrantLock(true);
    private boolean addTimestamp = false;
    private DateTimeFormatter dateFormatter = new DateTimeFormatterBuilder().
            appendInstant(3).toFormatter(); //Default TS. Example: "2019-01-22T16:39:11.757Z"
    private final File logFile;

    public FileLogger(File logFile) {
        this.logFile = logFile;
        if (!logFile.getParentFile().exists()) {
            logFile.getParentFile().mkdirs();
        }
    }

    private void processLog(String message) throws IOException {
        writeLock.lock();
        try {
            if (logWriter == null) {
                logWriter = new BufferedWriter(new FileWriter(logFile, true));
            }
            logWriter.write(message);
            logWriter.flush();
        } finally {
            writeLock.unlock();
        }
    }

    public void log(String message) throws IOException {
        if (addTimestamp) {
            String TimestampS = dateFormatter.format(Instant.now());
            processLog(TimestampS + ": " + message);
        }
        else {
            processLog(message);
        }
    }

    public void logLine(String message) throws IOException {
        log(message+"\n");
    }

    public void logFile(String file) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(new File(file)));
        String line;
        while ((line = reader.readLine()) != null) {
            logLine(line);
        }
        reader.close();
    }

    public void close() {
        try {
            logWriter.close();
        } catch (IOException e) {}
    }

    public boolean isAddTimestamp() {
        return addTimestamp;
    }

    public FileLogger setAddTimestamp(boolean addTimestamp) {
        this.addTimestamp = addTimestamp;
        return this;
    }

    public DateTimeFormatter getDateFormatter() {
        return dateFormatter;
    }

    public FileLogger setDateFormatter(DateTimeFormatter dateFormatter) {
        this.dateFormatter = dateFormatter;
        return this;
    }
}
