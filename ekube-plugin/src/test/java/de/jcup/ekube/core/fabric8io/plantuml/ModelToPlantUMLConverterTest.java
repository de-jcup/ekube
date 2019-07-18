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

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import de.jcup.ekube.core.model.EKubeModel;
import de.jcup.ekube.core.model.NamespaceContainer;
import de.jcup.ekube.core.model.NodeContainer;
import de.jcup.ekube.core.model.NodesContainer;
import io.fabric8.kubernetes.api.model.Node;

public class ModelToPlantUMLConverterTest {

    ModelToPlantUMLConverter converterToTest;

    @Before
    public void before() {
        converterToTest = new ModelToPlantUMLConverter();
    }

    @Test
    public void model_null_converter_returns_NOT_empty_string() {
        EKubeModel model = new EKubeModel();
        /* execute */
        String plantUML = converterToTest.convert(model);
        
        /* test */
        assertFalse(plantUML.isEmpty());
    }

    @Test
    public void model_empty_converter_returns_NOT_empty_string() {
        EKubeModel model = new EKubeModel();
        /* execute */
        String plantUML = converterToTest.convert(model);
        
        /* test */
        assertFalse(plantUML.isEmpty());
    }
    
    @Test
    public void model_one_namespace__converter_returns_string_containing_a_cloud_with_name_of_ns() {
        /* prepare */
        EKubeModel model = new EKubeModel();
        NamespaceContainer ns = new NamespaceContainer("ui1", null);
        ns.setName("name1");
        model.getCurrentContext().add(ns);

        /* execute */
        String plantUML = converterToTest.convert(model);
        
        /* test */
        assertTrue(plantUML.contains("cloud \"name1\""));
    }
    
    @Test
    public void model_one_node__converter_returns_string_containing_a_name_of_node() {
        /* prepare */
        EKubeModel model = new EKubeModel();
        NodesContainer nodes = model.getCurrentContext().getNodesContainer();
        Node f8node = new Node();
        NodeContainer node = new NodeContainer("uid1", f8node);
        node.setName("node1");
        nodes.addOrReuseExisting(node);
        
        /* execute */
        String plantUML = converterToTest.convert(model);
        
        /* test */
        assertTrue(plantUML.contains("node \"node1\""));
    }
    
    @Test
    public void model_complex_example1() {
        /* prepare */
        EKubeModel model = new EKubeModel();
        NodesContainer nodes = model.getCurrentContext().getNodesContainer();
        
        Node f8node = new Node();
        NodeContainer node = new NodeContainer("uid1", f8node);
        node.setName("nodename1");
        nodes.addOrReuseExisting(node);
        
        NamespaceContainer ns = new NamespaceContainer("ui1", null);
        ns.setName("name1");
        model.getCurrentContext().add(ns);
        
        /* execute */
        String plantUML = converterToTest.convert(model);
        
        /* test */
        System.out.println(plantUML);
        assertTrue(plantUML.contains("node \"node1\""));
    }

}
