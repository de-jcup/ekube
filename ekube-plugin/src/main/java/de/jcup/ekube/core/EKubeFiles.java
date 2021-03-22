/*
 * Copyright 2020 Albert Tregnaghi
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

import java.io.File;

public class EKubeFiles {
    
    public static File getUserHome() {
        File userHomeFile = new File(System.getProperty("user.home"));
        return userHomeFile;
    }
    
    public static File getEKubeHome() {
        File userHomeFile = new File(getUserHome(), ".ekube");
        return userHomeFile;
    }
    
    
    public static File getEKubeTempFolder() {
        File userHomeFile = new File(getEKubeHome(), "tmp");
        return userHomeFile;
    }

    public static File getDefaultKubeConfigFile() {
        return new File(getUserHome(), ".kube/config");
    }
    
}
