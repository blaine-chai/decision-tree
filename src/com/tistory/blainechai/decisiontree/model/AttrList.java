package com.tistory.blainechai.decisiontree.model;

import java.util.*;

/**
 * Created by blainechai on 2016. 5. 6..
 */
public class AttrList extends LinkedHashMap<String, ArrayList<String>> {

	private static final long serialVersionUID = 1L;

	public boolean addAttr(String k, String v) {
        if (this.containsKey(k)) {
            if (!this.get(k).contains(v)) {
                this.get(k).add(v);
                return true;
            }
        } else {
            ArrayList<String> value = new ArrayList<>();
            value.add(v);
            this.put(k, value);
        }
        return false;
    }
}
