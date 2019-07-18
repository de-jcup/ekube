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
 package de.jcup.ekube.core.fabric8io.plantuml;

public class CodeBuilder {

    private StringBuilder sb = new StringBuilder();
    
    public void addLine(String line){
       addLine(0,line);
    }
    
    public void addLine(int indent, String line){
        for (int i=0;i<indent;i++){
            sb.append("   ");
        }
        sb.append(line).append("\n");
    }
    public void addEmptyLine(){
        sb.append("\n");
    }

    public String getCode() {
        return sb.toString();
    }
}
