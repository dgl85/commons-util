package org.dgl.commons.util;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

/**
 * Works on Windows and POSIX systems, however on POSIX the lock file can still be deleted while opened/locked
 */
public class FileBasedLock {

    private final File lockFile;
    private FileLock fileLock;
    private FileChannel channel;

    public FileBasedLock(File lockFile) {
        this.lockFile = lockFile;
    }

    public boolean acquireLock() {
        boolean success = true;
        try {
            channel = new RandomAccessFile(lockFile, "rw").getChannel();
            fileLock = channel.tryLock();
            if (fileLock == null) {
                success = false;
            }
        } catch (IOException e) {
            success = false;
        } finally {
            return success;
        }
    }

    public void releaseLock() {
        try {
            fileLock.release();
            channel.close();
        } catch (IOException e) {}
    }
}
