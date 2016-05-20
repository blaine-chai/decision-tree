package com.tistory.blainechai.decisiontree.model;

import java.util.LinkedHashMap;

/**
 * Created by blainechai on 2016. 5. 8..
 */
public class Data<K, V> extends LinkedHashMap<K,V> {
	private static final long serialVersionUID = 1L;

	public Object addAttr(K key, V value) {
        return this.put(key, value);
    }
}