package org.scenario.cloudsimplus.network;

import java.util.ArrayList;
import java.util.List;


public class NetworkLoadGraphNode {
	private String label;
	private int level;
	private int weight;
	private List<NetworkLoadGraphNode> children;
	  
	  public NetworkLoadGraphNode(String label, int weight, int level) {
		  this.label = label;
		  this.weight = weight;
		  this.level = level;
		  children = new ArrayList<>();
	  }

	public String getLabel() {
		return label;
	}
	
	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	public List<NetworkLoadGraphNode> getChildren() {
		return children;
	}	
	
}
