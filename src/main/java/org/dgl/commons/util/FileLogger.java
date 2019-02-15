package org.dgl.commons.util;

import java.io.*;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.concurrent.locks.ReentrantLock;

public class FileLogger {

    private final File logFile;
    private final ReentrantLock writeLock = new ReentrantLock(true);
    private BufferedWriter logWriter = null;
    private boolean addTimestamp = false;
    private long rotateWhenBiggerThanBytes = 0; //Disabled by default
    private DateTimeFormatter dateFormatter = new DateTimeFormatterBuilder().
            appendInstant(3).toFormatter(); //Default TS. Example: "2019-01-22T16:39:11.757Z"

    public FileLogger(File logFile) {
        this.logFile = logFile;
        if (!logFile.getParentFile().exists()) {
            logFile.getParentFile().mkdirs();
        }
    }

    private void writeLog(String message) throws IOException {
        writeLock.lock();
        try {
            if (logWriter == null) {
                logWriter = new BufferedWriter(new FileWriter(logFile, true));
            }
            logWriter.write(message);
            logWriter.flush();
            rotateIfNeeded();
        } finally {
            writeLock.unlock();
        }
    }

    private void rotateIfNeeded() {
        if (rotateWhenBiggerThanBytes > 0 && logFile.length() > rotateWhenBiggerThanBytes) {
            try {
                logWriter.close();
            } catch (IOException e) {}
            String originalName = logFile.getAbsolutePath();
            int extensionIndex = originalName.lastIndexOf(".");
            String extension = originalName.substring(extensionIndex);
            String rotatedName = originalName.substring(0, extensionIndex + 1) + System.currentTimeMillis() + extension;
            logFile.renameTo(new File(rotatedName));
            try {
                logWriter = new BufferedWriter(new FileWriter(logFile, true));
            } catch (IOException e) {
                e.printStackTrace();
                logWriter = null;
            }
        }
    }

    public void log(String message) throws IOException {
        if (addTimestamp) {
            String TimestampS = dateFormatter.format(Instant.now());
            writeLog(TimestampS + ": " + message);
        } else {
            writeLog(message);
        }
    }

    public void logLine(String message) throws IOException {
        log(message + "\n");
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

    public long getRotateWhenBiggerThanBytes() {
        return rotateWhenBiggerThanBytes;
    }

    /**
     * FileLogger will rotate log file when size is bigger than rotateWhenBiggerThanBytes
     *
     * @param rotateWhenBiggerThanBytes <=0 to ignore file rotations
     * @return
     */
    public FileLogger setRotateWhenBiggerThanBytes(long rotateWhenBiggerThanBytes) {
        this.rotateWhenBiggerThanBytes = rotateWhenBiggerThanBytes;
        return this;
    }
}
