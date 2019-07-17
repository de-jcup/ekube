package de.jcup.ekube.core;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileUtil {
    
    public static void writeTextFile(String content, File file) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            bw.write(content);
        }
    }

}
