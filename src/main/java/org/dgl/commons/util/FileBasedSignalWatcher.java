package org.dgl.commons.util;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.*;
import java.util.List;
import java.util.function.Consumer;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;

public class FileBasedSignalWatcher {

    private final File path;
    private final String[] signalRegexs;
    private final Consumer<String> callback;
    private final Consumer<Exception> exceptionCallback;
    private boolean deleteSignalFileAfterRead = true; //Default
    private WatchService watcher;
    private WatchKey watcherKey;
    private boolean watching = false;

    public FileBasedSignalWatcher(File path, String[] signalRegexs, Consumer<String> callback,
            Consumer<Exception> exceptionCallback) {
        if (!path.isDirectory()) {
            throw new IllegalArgumentException();
        }
        this.path = path;
        this.signalRegexs = signalRegexs;
        this.callback = callback;
        this.exceptionCallback = exceptionCallback;
    }

    public FileBasedSignalWatcher(File path, String signalRegex, Consumer<String> callback,
            Consumer<Exception> exceptionCallback) {
        this(path, new String[]{signalRegex}, callback, exceptionCallback);
    }

    public void startWatching() throws IOException {
        if (watching) {
            return;
        }
        watcher = FileSystems.getDefault().newWatchService();
        watcherKey = Paths.get(path.getAbsolutePath()).register(watcher, ENTRY_CREATE);
        watching = true;
        (new Thread(() -> watch())).start();
    }

    public void stopWatching() {
        try {
            watcherKey.cancel();
            watcher.close();
        } catch (IOException e) {}
        watching = false;
    }

    public boolean isWatching() {
        return watching;
    }

    public boolean isDeleteSignalFileAfterRead() {
        return deleteSignalFileAfterRead;
    }

    public void setDeleteSignalFileAfterRead(boolean deleteSignalFileAfterRead) {
        this.deleteSignalFileAfterRead = deleteSignalFileAfterRead;
    }

    private void deleteSignalFile(String signalName) {

        File lockFile = new File(path.getAbsolutePath() + File.separator + signalName);
        //We wait for file creation to be done before attempting to delete
        try {
            RandomAccessFile rFile = new RandomAccessFile(lockFile, "rw");
            rFile.getChannel().tryLock();
            rFile.close();
        } catch (IOException e) {}
        if (!lockFile.delete()) {
            exceptionCallback.accept(new IllegalStateException("Signal file could not be deleted"));
        }
    }

    private void watch() {
        while (watching) {
            try {
                watcherKey = watcher.take();
            } catch (ClosedWatchServiceException | InterruptedException e) {
                stopWithPossibleException(e);
            }
            if (watcherKey != null && isWatching()) { //Watching may be false by now, since watcher.take is blocking
                processPollEvents(watcherKey.pollEvents());
                if (!watcherKey.reset()) { //Key no longer valid. Inaccessible path
                    stopWithPossibleException(new IllegalStateException("Watcher key no longer valid"));
                }
            }
        }
    }

    private void processPollEvents(List<WatchEvent<?>> events) {
        for (WatchEvent<?> event : events) {
            String eventName = event.context().toString();
            for (String signalRegex : signalRegexs) {
                if (eventName.matches(signalRegex)) {
                    processNewSignal(eventName);
                }
            }
        }
    }

    private void processNewSignal(String signalName) {
        if (deleteSignalFileAfterRead) {
            deleteSignalFile(signalName);
        }
        callback.accept(signalName);
    }

    private void stopWithPossibleException(Exception possibleException) {
        if (isWatching()) {
            stopWatching();
            exceptionCallback.accept(possibleException); //Exception is created only if we are currently watching
        }
    }
}
