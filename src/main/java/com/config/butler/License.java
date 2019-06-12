package com.config.butler;


import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.RandomStringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static java.nio.charset.Charset.forName;

public class License {
    private static String dir = "Required";
    private static File keyFile = new File(dir + "/auth.key");
    private static String poison = "wuXs3UlDTKwOoBegKLMc";
    FireStore cloud = new FireStore();

    public boolean isValid() {
        return readKey().equals(cloud.getDocument("key"));
    }

    public boolean isPoisoned() {
        return readKey().equals(poison);
    }

    /*
    Write the generated Key to a keyFile.
     */
    public String writeKey() {
        String key = generateKey();
        File directory = new File(dir);
        if (!directory.exists()) {
            directory.mkdir();
        }

        try {
            keyFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            FileUtils.writeStringToFile(keyFile, key, forName("UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return key;
    }

    /*
    Read the Key From a File
     */
    public String readKey() {
        String metadata = null;
        FileInputStream inputStream;
        try {
            inputStream = new FileInputStream(keyFile);
            metadata = IOUtils.toString(inputStream);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return metadata;
    }

    private String generateKey() {
        return RandomStringUtils.randomAlphanumeric(500);
    }

    public void revokeLicense() {
        File directory = new File(dir);
        if (!directory.exists()) {
            directory.mkdir();
        }

        try {
            keyFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            FileUtils.writeStringToFile(keyFile, poison, forName("UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void checkPoint() {

    }


}
