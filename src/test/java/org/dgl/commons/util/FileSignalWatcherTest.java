package org.dgl.commons.util;

import java.io.File;
import java.io.IOException;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class FileSignalWatcherTest {
    private boolean signalReceived = false;
    private boolean signalExceptionReceived = false;
    private static String signalName = "file_signal_test";
    private static String signalName2 = "file_signal_test2";
    private final FileSignalWatcher signalWatcher = new FileSignalWatcher(path, new String[]{signalName, signalName2},
            this::fileSignalReceived, this::fileSignalExceptionReceived);
    private static final File path = new File(System.getProperty("java.io.tmpdir")
            + File.separator + "file_signal_watcher_test");

    @BeforeAll
    public static void checkPathInit() {
        if (!path.exists()) {
            path.mkdirs();
        }
    }

    @Test
    public void testStopWatching() throws IOException {
        //Checking exception when path is deleted
        signalWatcher.startWatching();
        assertTrue(signalWatcher.isWatching());
        assertFalse(signalExceptionReceived);
        assertTrue(path.delete());
        shortSleep();
        assertTrue(signalExceptionReceived);
        assertFalse(signalWatcher.isWatching());
    }

    @Test
    public void testExceptionWhenPathIsDeleted() throws IOException {
        //Checking exception when path is deleted
        signalWatcher.startWatching();
        assertTrue(signalWatcher.isWatching());
        assertFalse(signalExceptionReceived);
        assertTrue(path.delete());
        shortSleep();
        assertTrue(signalExceptionReceived);
        assertFalse(signalWatcher.isWatching());
    }

    @BeforeEach
    public void checkPath() {
        if (!path.exists()) {
            path.mkdirs();
        }
        if (checkSignalFileExists(path, signalName)) {
            deleteSignalFile(path, signalName);
        }
        signalExceptionReceived = false;
        signalReceived = false;
        assertTrue(path.exists());
        assertFalse(checkSignalFileExists(path, signalName));
        assertFalse(signalExceptionReceived);
    }

    @AfterEach
    public void generalTest() throws IOException {
        checkPath();
        assertTrue(signalWatcher.isDeleteSignalFileAfterRead());
        //Checking isWatching
        assertFalse(signalWatcher.isWatching());
        signalWatcher.startWatching();
        assertTrue(signalWatcher.isWatching());
        shortSleep();
        //Checking new signal1
        writeNewSignalFile(path, signalName);
        shortSleep();
        assertTrue(signalReceived);
        signalReceived = false;
        //Checking file deletion was done
        assertFalse(checkSignalFileExists(path, signalName));
        //Checking new signal2
        writeNewSignalFile(path, signalName2);
        shortSleep();
        assertTrue(signalReceived);
        signalReceived = false;
        //Checking file deletion was done
        assertFalse(checkSignalFileExists(path, signalName2));
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
        writeNewSignalFile(path, signalName2);
        shortSleep();
        assertTrue(signalReceived);
        signalReceived = false;
        shortSleep();
        assertFalse(checkSignalFileExists(path, signalName2));
        assertFalse(checkSignalFileExists(path, signalName));
    }

    @AfterAll
    public static void cleanup() {
        path.delete();
    }

    public void fileSignalReceived(String signalName) {
        if (signalName.equals(this.signalName) || signalName.equals(signalName2)) {
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
