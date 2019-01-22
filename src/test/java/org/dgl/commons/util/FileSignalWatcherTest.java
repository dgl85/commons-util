package org.dgl.commons.util;

import java.io.File;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class FileSignalWatcherTest {
    private boolean signalReceived = false;
    private boolean signalExceptionReceived = false;
    private String signalName = "file_signal_test";

    @Test
    public void generalTest() throws IOException {
        File path = new File(System.getProperty("java.io.tmpdir")+File.separator+"file_signal_watcher_test");
        if (!path.exists()) {
            path.mkdirs();
        }

        FileSignalWatcher signalWatcher = new FileSignalWatcher(path, signalName, this::fileSignalReceived,
                this::fileSignalExceptionReceived);
        if (checkSignalFileExists(path, signalName)) {
            deleteSignalFile(path, signalName);
        }

        assertFalse(checkSignalFileExists(path, signalName));
        assertFalse(signalExceptionReceived);
        assertTrue(signalWatcher.isDeleteSignalFileAfterRead());
        //Checking isWatching
        assertFalse(signalWatcher.isWatching());
        signalWatcher.startWatching();
        assertTrue(signalWatcher.isWatching());
        shortSleep();
        //Checking new signal
        writeNewSignalFile(path, signalName);
        shortSleep();
        assertTrue(signalReceived);
        signalReceived = false;
        //Checking file deletion was done
        assertFalse(checkSignalFileExists(path, signalName));
        //Checking setDeleteSignalFileAfterRead on false
        signalWatcher.setDeleteSignalFileAfterRead(false);
        writeNewSignalFile(path, signalName);
        shortSleep();
        assertTrue(signalReceived);
        signalReceived = false;
        assertTrue(checkSignalFileExists(path, signalName));
        deleteSignalFile(path, signalName);
        shortSleep();
        //Checking setDeleteSignalFileAfterRead back on true
        signalWatcher.setDeleteSignalFileAfterRead(true);
        writeNewSignalFile(path, signalName);
        shortSleep();
        assertTrue(signalReceived);
        signalReceived = false;
        shortSleep();
        assertFalse(checkSignalFileExists(path, signalName));
        //Checking stopWatching
        signalWatcher.stopWatching();
        assertFalse(signalWatcher.isWatching());
        writeNewSignalFile(path, signalName);
        shortSleep();
        assertFalse(signalReceived);
        deleteSignalFile(path, signalName);

        //Watching again after close. Repeating everything
        if (checkSignalFileExists(path, signalName)) {
            deleteSignalFile(path, signalName);
        }
        assertFalse(checkSignalFileExists(path, signalName));
        assertFalse(signalWatcher.isWatching());
        signalWatcher.startWatching();
        assertTrue(signalWatcher.isWatching());
        shortSleep();
        writeNewSignalFile(path, signalName);
        shortSleep();
        assertTrue(signalReceived);
        signalReceived = false;
        assertFalse(checkSignalFileExists(path, signalName));
        signalWatcher.setDeleteSignalFileAfterRead(false);
        writeNewSignalFile(path, signalName);
        shortSleep();
        assertTrue(signalReceived);
        signalReceived = false;
        assertTrue(checkSignalFileExists(path, signalName));
        deleteSignalFile(path, signalName);
        shortSleep();
        signalWatcher.setDeleteSignalFileAfterRead(true);
        writeNewSignalFile(path, signalName);
        shortSleep();
        assertTrue(signalReceived);
        signalReceived = false;
        shortSleep();
        assertFalse(checkSignalFileExists(path, signalName));

        //Checking exception when path is deleted
        assertFalse(signalExceptionReceived);
        assertTrue(path.delete());
        shortSleep();
        assertTrue(signalExceptionReceived);
        assertFalse(signalWatcher.isWatching());

        //Watching again after exception. Repeating everything
        if (!path.exists()) {
            path.mkdirs();
        }
        if (checkSignalFileExists(path, signalName)) {
            deleteSignalFile(path, signalName);
        }
        assertFalse(checkSignalFileExists(path, signalName));
        assertFalse(signalWatcher.isWatching());
        signalWatcher.startWatching();
        assertTrue(signalWatcher.isWatching());
        shortSleep();
        writeNewSignalFile(path, signalName);
        shortSleep();
        assertTrue(signalReceived);
        signalReceived = false;
        assertFalse(checkSignalFileExists(path, signalName));
        signalWatcher.setDeleteSignalFileAfterRead(false);
        writeNewSignalFile(path, signalName);
        shortSleep();
        assertTrue(signalReceived);
        signalReceived = false;
        assertTrue(checkSignalFileExists(path, signalName));
        deleteSignalFile(path, signalName);
        shortSleep();
        signalWatcher.setDeleteSignalFileAfterRead(true);
        writeNewSignalFile(path, signalName);
        shortSleep();
        assertTrue(signalReceived);
        signalReceived = false;
        shortSleep();
        assertFalse(checkSignalFileExists(path, signalName));
        assertTrue(path.delete());
    }

    public void fileSignalReceived(String signalName) {
        if (signalName.equals(signalName)) {
            signalReceived = true;
        }
    }
    public void fileSignalExceptionReceived(String exception) {
        signalExceptionReceived = true;
    }

    private void shortSleep() {
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {}
    }

    private void writeNewSignalFile(File path, String signalName) throws IOException {
        new File(path.getAbsolutePath() + File.separator + signalName).createNewFile();
    }

    private boolean checkSignalFileExists(File path, String signalName) {
        return new File(path.getAbsolutePath() + File.separator + signalName).exists();
    }

    private void deleteSignalFile(File path, String signalName) {
        new File(path.getAbsolutePath() + File.separator + signalName).delete();
    }
}
