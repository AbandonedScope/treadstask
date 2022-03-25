package com.mahanko.threadstask.reader;

import com.mahanko.threadstask.exception.CustomThreadException;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CustomFileReader {
    private static final Logger logger = LogManager.getLogger();

    public  List<String> readFile(String path) throws CustomThreadException {
        List<String> result = new ArrayList<>();
        try(FileReader fileReader = new FileReader(path);
            BufferedReader reader = new BufferedReader(fileReader)) {
            String lineOfFile;
            while ((lineOfFile = reader.readLine()) != null)
            {
                result.add(lineOfFile);
            }
        } catch (IOException e) {
            logger.log(Level.ERROR, e);
            throw new CustomThreadException(e);
        }

        return result;
    }
}
