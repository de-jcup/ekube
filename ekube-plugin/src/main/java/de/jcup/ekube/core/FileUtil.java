/*
 * Copyright 2019 Albert Tregnaghi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 */
 package de.jcup.ekube.core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FileUtil {
    
    public static void writeTextFile(String content, File file) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            bw.write(content);
        }
    }
    
    public static String readTextFile(File file) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
           String line = null;
           while ((line = br.readLine())!=null) {
               if (sb.length()>0) {
                   sb.append("\n");
               }
               sb.append(line);
           }
           return sb.toString();
        }
    }

}
