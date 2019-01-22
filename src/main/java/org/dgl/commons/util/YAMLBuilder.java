package org.dgl.commons.util;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.introspector.BeanAccess;

import java.io.*;

public class YAMLBuilder {

    public static String getYAMLString(Object object) {
        Yaml yaml = new Yaml();
        yaml.setBeanAccess(BeanAccess.FIELD);
        StringWriter writer = new StringWriter();
        yaml.dump(object, writer);
        return writer.toString();
    }

    public static Object getObject(String yamlString) {
        Yaml yaml = new Yaml();
        yaml.setBeanAccess(BeanAccess.FIELD);
        return yaml.load(yamlString);
    }

    public static <T> T getObject(String yamlString, Class<T> expectedObjectClass) {
        Yaml yaml = new Yaml();
        yaml.setBeanAccess(BeanAccess.FIELD);
        return yaml.loadAs(yamlString, expectedObjectClass);
    }

    public static String getYAMLStringFromFile(File file) throws IOException {
        return getStringFromFile(file);
    }

    public static Object getObjectFromFile(File file) throws IOException {
        return getObject(getYAMLStringFromFile(file));
    }

    public static <T> T getObjectFromFile(File file, Class<T> expectedObjectClass) throws IOException {
        return getObject(getStringFromFile(file), expectedObjectClass);
    }

    public static void saveToFile(File file, Object object) throws IOException {
        saveStringToFile(file, getYAMLString(object));
    }

    public static void saveToFile(File file, String yamlString) throws IOException {
        saveStringToFile(file, yamlString);
    }

    private static void saveStringToFile(File file, String string) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        writer.write(string);
        writer.close();
    }

    private static String getStringFromFile(File file) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        StringWriter writer = new StringWriter();
        String line;
        while ((line = reader.readLine()) != null) {
            writer.write(line+"\n");
        }
        reader.close();
        return writer.toString();
    }

}
