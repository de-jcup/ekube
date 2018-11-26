package de.jcup.ekube.core.model;

import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class ConfigMapElement extends AbstractEKubeElement implements EKubeStatusElement {

	private String status;
	private Map<String, String> data = new TreeMap<>();

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setData(Map<String, String> data) {
		this.data.clear();
		this.data.putAll(data);
	}
	
	public Map<String, String> getData() {
		return data;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Config map:");
		sb.append(getLabel());
		sb.append("\n------------------------------\n");
		for (Entry<String,String> entry: data.entrySet()){
			sb.append(entry.getKey());
			sb.append("\t\t\t:\t\t\t");
			sb.append(entry.getValue());
			sb.append("\n");
		}
		return sb.toString();
	}
}
