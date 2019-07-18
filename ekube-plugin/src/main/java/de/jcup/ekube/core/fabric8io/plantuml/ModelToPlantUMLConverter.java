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

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import de.jcup.ekube.core.model.CurrentContextContainer;
import de.jcup.ekube.core.model.EKubeModel;
import de.jcup.ekube.core.model.NamespaceContainer;
import de.jcup.ekube.core.model.NodeContainer;
import de.jcup.ekube.core.model.PersistentVolumeClaimElement;
import de.jcup.ekube.core.model.PodContainer;
import de.jcup.ekube.core.model.ServiceContainer;
import io.fabric8.kubernetes.api.model.Node;
import io.fabric8.kubernetes.api.model.NodeSpec;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.Taint;

public class ModelToPlantUMLConverter {

    public String convert(EKubeModel model) {
        InternalContext ic = new InternalContext();
        ic.codeBuilder.addLine("@startuml");
        ic.codeBuilder.addEmptyLine();

        buildModel(model, ic);

        ic.codeBuilder.addEmptyLine();
        ic.codeBuilder.addLine("@enduml");
        return ic.codeBuilder.getCode();
    }

    private class InternalContext {
        CodeBuilder codeBuilder = new CodeBuilder();
        Map<String, String> nodeMapIdToName = new LinkedHashMap<>();
        Map<String, String> podMapIdToName = new LinkedHashMap<>();
        String masterNode = null;
        int nsPrefixNr;
        
        private String id(String id){
            return "ns"+nsPrefixNr+"_"+id;
        }
    }

    protected void buildModel(EKubeModel model, InternalContext ic) {
        if (model != null) {
            CurrentContextContainer context = model.getCurrentContext();
            ic.codeBuilder.addLine("title cluster:" + context.getCluster());
            buildNodes(ic, context);

            List<NamespaceContainer> namespaces = context.getNamespaces();
            for (NamespaceContainer namespace : namespaces) {
                ic.codeBuilder.addLine("package \"" + namespace.getName() + "\" {");
                buildNamespace(ic, namespace, 1);
                ic.codeBuilder.addLine("}");
            }
        }
    }

    protected void buildNodes(InternalContext ic, CurrentContextContainer context) {
        int nr = 0;
        for (NodeContainer node : context.getNodesContainer().getNodes()) {
            Object obj = node.getTechnicalObject();
            if (obj instanceof Node) {
                Node f8node = (Node) obj;
                String nodeId=null;
                if (!isIdentifiedAsMaster(ic, f8node)){
                    nodeId = "node" + (nr++);
                }else{
                    nodeId = "master";
                }
                ic.nodeMapIdToName.put(nodeId, node.getName());
                
                ic.codeBuilder.addLine("node " + nodeId + " [");
                ic.codeBuilder.addLine("   " + node.getName());
                ic.codeBuilder.addLine("]");

            }
        }
        /* fallback when no masterNode defined */
        if (ic.masterNode == null) {
            ic.codeBuilder.addLine("node master [");
            ic.codeBuilder.addLine("   undefined");
            ic.codeBuilder.addLine("]");
        }
        for (String nodeId : ic.nodeMapIdToName.keySet()) {
            if (nodeId.equals("master")) {
                continue;
            }
            ic.codeBuilder.addLine("master ~~> "+nodeId);
        }
    }

    protected boolean isIdentifiedAsMaster(InternalContext ic, Node f8node) {
        if (ic.masterNode == null) {
            NodeSpec spec = f8node.getSpec();
            List<Taint> taints = spec != null ? spec.getTaints() : Collections.emptyList();
            for (Taint taint : taints) {
                if (taint.getKey().equals("node-role.kubernetes.io/master")) {
                    ic.masterNode = f8node.getMetadata().getName();
                    return true;
                }
            }
        }
        return false;
    }

    private void buildNamespace(InternalContext ic, NamespaceContainer namespace, int indent) {
        ic.nsPrefixNr++;
        
        buildServicesForNamespace(ic, namespace, indent);
        buildPodsForNamespace(ic, namespace, indent);
        buildVolumesForNamespace(ic, namespace, indent);
        
        ic.codeBuilder.addLine(ic.id("pods")+" -[hidden]--> "+ic.id("pvcs"));
        ic.codeBuilder.addLine(ic.id("pvcs")+" -[hidden]--> "+ic.id("services"));

    }
    

    protected void buildPodsForNamespace(InternalContext ic, NamespaceContainer namespace, int indent) {
        List<PodContainer> podContainers = namespace.fetchPodsContainer().getPods();

        ic.codeBuilder.addLine(indent, "storage Pods as "+ic.id("pods")+" {");;
        int nr = 0;
        for (PodContainer podContainer : podContainers) {
            nr++;
            String podId = ic.id("pod" + nr);
            String podName = podContainer.getName();
            ic.codeBuilder.addLine(indent + 1, "entity \"" + podName + "\" as " + podId);
            ic.podMapIdToName.put(podId, podName);
            /* link to node */
            Object obj = podContainer.getTechnicalObject();
            if (obj instanceof Pod) {
                Pod pod = (Pod) obj;
                String nodeName = pod.getSpec().getNodeName();
                Set<Entry<String, String>> nodeEntrySet = ic.nodeMapIdToName.entrySet();
                for (Entry<String, String> nodeEntry : nodeEntrySet) {
                    if (nodeEntry.getValue().equals(nodeName)) {
                        ic.codeBuilder.addLine(indent + 1, nodeEntry.getKey() + " --> " + podId);
//                        break;
                    }
                }
            }
        }
        ic.codeBuilder.addLine(indent, "}");
    }

    protected void buildServicesForNamespace(InternalContext ic, NamespaceContainer namespace, int indent) {
        List<ServiceContainer> services = namespace.fetchServicesContainer().getServices();
        ic.codeBuilder.addLine(indent, "component Services as "+ic.id("services")+" {");
        int nr = 0;
        for (ServiceContainer service : services) {
            nr++;
            String serviceId = ic.id("service" + nr);
            ic.codeBuilder.addLine(indent + 1, "  entity \"" + service.getName() + "\" as "+serviceId);
        }
        ic.codeBuilder.addLine(indent, "}");
    }

    protected void buildVolumesForNamespace(InternalContext ic, NamespaceContainer namespace, int indent) {
        List<PersistentVolumeClaimElement> volumeClaims = namespace.fetchPersistentVolumeClaimsContainer().getVolumeClaims();
        ic.codeBuilder.addLine(indent, "storage \"Persistent Volume Claims\" as "+ic.id("pvcs")+" {");
        int nr = 0;
        for (PersistentVolumeClaimElement volumeClaim : volumeClaims) {
            nr++;
            String storeId=ic.id("store" + nr);
            ic.codeBuilder.addLine(indent + 1, "  storage \"" + volumeClaim.getName() + "\" as "+storeId);
        }
        ic.codeBuilder.addLine(indent, "}");
    }

}
