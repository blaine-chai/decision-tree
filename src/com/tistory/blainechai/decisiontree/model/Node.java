package com.tistory.blainechai.decisiontree.model;

import java.util.LinkedHashMap;

public class Node extends LinkedHashMap {

	private String attrName = null;

	public void setClassification(String classification) {
		this.put("class", classification);
	}

	public String getClassification() {
		return (String) this.get("class");
	}

	public boolean isLeaf() {
		return attrName == null;
	}

	public String getAttrName() {
		return attrName;
	}

	public void setAttrName(String attrName) {
		this.attrName = attrName;
	}
}