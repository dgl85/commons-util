package org.dgl.commons.util;

import org.junit.jupiter.api.Test;
import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class YAMLBuilderTest {

    @Test
    public void generalTest() throws IOException {
        File path = new File(System.getProperty("java.io.tmpdir")
                + File.separator + "yaml_builder_test");
        if (!path.exists()) {
            path.mkdirs();
        }
        File testFile = new File(path.getAbsolutePath() + File.separator + "test.txt");
        if (testFile.exists()) {
            testFile.delete();
        }
        assertFalse(testFile.exists());
        YAMLBuilderTestStructure testStructure = new YAMLBuilderTestStructure();
        testStructure.setName("Syd");
        testStructure.setLastName("Barret");
        testStructure.setAddress("Dark side of the moon");
        testStructure.setPostalCode(7799);
        testStructure.setChildrenAge(new int[]{2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31});
        testStructure.setMarried(false);

        //Direct
        assertTrue(compareStructures(testStructure,
                (YAMLBuilderTestStructure)YAMLBuilder.getObject(YAMLBuilder.getYAMLString(testStructure))));

        assertTrue(compareStructures(testStructure,
            YAMLBuilder.getObject(YAMLBuilder.getYAMLString(testStructure), YAMLBuilderTestStructure.class)));


        //File based from object
        YAMLBuilder.saveToFile(testFile, testStructure);
        assertTrue(testFile.exists());
        assertTrue(compareStructures(testStructure,
                (YAMLBuilderTestStructure)YAMLBuilder.getObjectFromFile(testFile)));
        testFile.delete();
        assertFalse(testFile.exists());

        YAMLBuilder.saveToFile(testFile, testStructure);
        assertTrue(testFile.exists());
        assertTrue(compareStructures(testStructure,
                YAMLBuilder.getObjectFromFile(testFile, YAMLBuilderTestStructure.class)));
        testFile.delete();
        assertFalse(testFile.exists());


        //File based from string
        YAMLBuilder.saveToFile(testFile, YAMLBuilder.getYAMLString(testStructure));
        assertTrue(testFile.exists());
        assertTrue(compareStructures(testStructure,
                (YAMLBuilderTestStructure)YAMLBuilder.getObjectFromFile(testFile)));
        testFile.delete();
        assertFalse(testFile.exists());

        YAMLBuilder.saveToFile(testFile, YAMLBuilder.getYAMLString(testStructure));
        assertTrue(testFile.exists());
        assertTrue(compareStructures(testStructure,
                YAMLBuilder.getObjectFromFile(testFile, YAMLBuilderTestStructure.class)));
        testFile.delete();
        assertFalse(testFile.exists());


        path.delete();
    }

    private boolean compareStructures(YAMLBuilderTestStructure structure1, YAMLBuilderTestStructure structure2) {
        if (!structure1.getName().equals(structure2.getName())) {
            return false;
        }
        if (!structure1.getLastName().equals(structure2.getLastName())) {
            return false;
        }
        if (!structure1.getAddress().equals(structure2.getAddress())) {
            return false;
        }
        if (structure1.getPostalCode() != structure2.getPostalCode()) {
            return false;
        }
        int[] ages1 = structure1.getChildrenAge();
        int[] ages2 = structure2.getChildrenAge();
        if (ages1.length != ages2.length) {
            return false;
        }
        for (int i = 0; i < ages1.length; i++) {
            if (ages1[i] != ages2[i]) {
                return false;
            }
        }
        if (structure1.isMarried() != structure2.isMarried()) {
            return false;
        }
        return true;
    }
}
