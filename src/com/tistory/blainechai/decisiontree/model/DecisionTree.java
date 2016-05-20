package com.tistory.blainechai.decisiontree.model;

import java.util.*;

/**
 * Created by blainechai on 2016. 5. 6..
 */
public class DecisionTree extends Node {

    public String predict(Data data) {
        Node curNode = this;
        while (!curNode.isLeaf()) {
            curNode = (Node) curNode.get(data.get(curNode.getAttrName()));
        }
        return (String) curNode.getClassification();
    }
}

