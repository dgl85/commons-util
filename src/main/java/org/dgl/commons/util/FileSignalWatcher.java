package org.dgl.commons.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.function.Consumer;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;

public class FileSignalWatcher {

    private final File path;
    private final String signalName;
    private final Consumer<String> callback;
    private final Consumer<String> exceptionCallback;
    private WatchService watcher;
    private WatchKey watcherKey;
    private boolean deleteSignalFileAfterRead = true; //Default
    private boolean watching = false;

    public FileSignalWatcher(File path, String signalName, Consumer<String> callback,
                             Consumer<String> exceptionCallback) {
        if (!path.isDirectory()) {
            throw new IllegalArgumentException();
        }
        this.path = path;
        this.signalName = signalName;
        this.callback = callback;
        this.exceptionCallback = exceptionCallback;
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

    private void deleteSignalFile() {
        int counter = 0;
        while (!new File(path.getAbsolutePath() + File.separator + signalName).delete()) {
            counter++; //This block is hideous but sometimes necessary
            if (counter > 100) { //Completely arbitrary
                exceptionCallback.accept("Signal file could not be deleted");
                break;
            }
        }
    }

    private void watch() {
        while (watching) {
            try {
                watcherKey = watcher.take();
            }
            catch (ClosedWatchServiceException | InterruptedException e) {
                if (isWatching()) {
                    stopWatching();
                    exceptionCallback.accept(e.getMessage());
                }
            }
            if (watcherKey != null && isWatching()) { //Watching may be false by now, since watcher.take is blocking
                for (WatchEvent<?> event : watcherKey.pollEvents()) {
                    if (event.context().toString().equals(signalName)) {
                        if (deleteSignalFileAfterRead) {
                            deleteSignalFile();
                        }
                        callback.accept(signalName);
                    }
                }
                if (!watcherKey.reset()) { //Key no longer valid. Inaccessible path
                    if (isWatching()) {
                        stopWatching();
                        exceptionCallback.accept("Watcher key no longer valid");
                    }
                }
            }
        }
    }
}
