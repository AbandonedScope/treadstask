package com.mahanko.threadstask.reader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CustomFileReader {
    public static List<String> readFile(String path) {
        List<String> result = new ArrayList<>();
        try(FileReader fileReader = new FileReader(path);
            BufferedReader reader = new BufferedReader(fileReader)) {
            String lineOfFile;
            while ((lineOfFile = reader.readLine()) != null)
            {
                result.add(lineOfFile);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
