package com.tistory.blainechai;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import com.tistory.blainechai.decisiontree.DTBuilder;
import com.tistory.blainechai.decisiontree.model.AttrList;
import com.tistory.blainechai.decisiontree.model.Data;
import com.tistory.blainechai.decisiontree.model.DecisionTree;
import com.tistory.blainechai.decisiontree.model.Partition;

/**
 * Created by blainechai on 2016. 5. 5..
 */
public class Main {
    public static void main(String[] args) {

        DecisionTree dt = new DecisionTree();
        DTBuilder dtBuilder = new DTBuilder();
        Partition dataSet = new Partition();
        Partition testDataSet = new Partition();
        ArrayList<String> labelList = new ArrayList<>();
        AttrList attrList = new AttrList();

        try {
            BufferedReader in = new BufferedReader(new FileReader(args[0]));
//            BufferedReader in = new BufferedReader(new FileReader("dt_train3.txt"));
            String s;

            s = in.readLine();
            StringTokenizer st = new StringTokenizer(s, "\t");

            while (st.hasMoreTokens()) {
                labelList.add(st.nextToken());
            }

            while ((s = in.readLine()) != null) {
                int count = 0;
                st = new StringTokenizer(s, "\t");
                Data data = new Data();
                while (st.hasMoreTokens()) {
                    String tmp = st.nextToken();
                    attrList.addAttr(labelList.get(count), tmp);
                    data.addAttr(labelList.get(count), tmp);
                    count++;
                }
                dataSet.add(data);
            }
        } catch (IOException e) {
            System.out.println("file is not found");
        }
        dt = dtBuilder.buildDecisionTree(dt, dataSet, attrList);

        try {
        	BufferedReader in = new BufferedReader(new FileReader(args[1]));
//            BufferedReader in = new BufferedReader(new FileReader("dt_test3.txt"));
            String s;

            s = in.readLine();
            StringTokenizer st = new StringTokenizer(s, "\t");

            while (st.hasMoreTokens()) {
                st.nextToken();
            }

            while ((s = in.readLine()) != null) {
                int count = 0;
                st = new StringTokenizer(s, "\t");
                Data data = new Data();
                while (st.hasMoreTokens()) {
                    String tmp = st.nextToken();
                    data.addAttr(labelList.get(count), tmp);
                    count++;
                }
                testDataSet.add(data);
            }
        } catch (IOException e) {
            System.out.println("file is not found");
        }

        for (Data data : testDataSet) {
            data.put(labelList.get(labelList.size() - 1), dt.predict(data));
        }

        try {
            BufferedWriter out = new BufferedWriter(new FileWriter("dt_result.txt"));
//            BufferedWriter out = new BufferedWriter(new FileWriter("dt_result3.txt"));
            for (int i = 0; i < labelList.size(); i++) {
                if (i < labelList.size() - 1) {
                    System.out.print(labelList.get(i) + "\t");
                    out.write(labelList.get(i) + "\t");
                } else {
                    out.write(labelList.get(i) + "\n");
                    System.out.print(labelList.get(i) + "\n");
                }
            }
            for (Data testData : testDataSet) {
                for (int i = 0; i < labelList.size(); i++) {
                    if (i < labelList.size() - 1) {
                        out.write(testData.get(labelList.get(i)) + "\t");
                        System.out.print(testData.get(labelList.get(i)) + "\t");
                    } else {
                        out.write(testData.get(labelList.get(i)) + "\n");
                        System.out.print(testData.get(labelList.get(i)) + "\n");
                    }
                }
            }
            out.close();
        } catch (IOException e) {
            System.err.println("file output Error");
        }
    }
}
