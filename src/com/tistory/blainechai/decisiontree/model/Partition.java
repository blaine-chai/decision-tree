package com.tistory.blainechai.decisiontree.model;

import java.util.ArrayList;

/**
 * Created by blainechai on 2016. 5. 13..
 */
public class Partition extends ArrayList<Data<String, String>> {
	private static final long serialVersionUID = 1L;
	
    public Partition() {
        super();
    }

    public Partition(ArrayList<Data<String, String>> partition) {
        super(partition);
    }
}
