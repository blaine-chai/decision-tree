package com.tistory.blainechai.decisiontree;

import java.util.*;

import com.tistory.blainechai.decisiontree.model.AttrList;
import com.tistory.blainechai.decisiontree.model.Data;
import com.tistory.blainechai.decisiontree.model.DecisionTree;
import com.tistory.blainechai.decisiontree.model.Partition;
import com.tistory.blainechai.decisiontree.model.Node;

/**
 * Created by blainechai on 2016. 5. 18..
 */
public class DTBuilder {

    public DecisionTree buildDecisionTree(DecisionTree dt, Partition dataSet, AttrList attrList) {
        Partition p = new Partition(dataSet);
        ArrayList<String> labelList = new ArrayList<>(attrList.keySet());
        ArrayList<String> unusedAttrList = new ArrayList<>(labelList);
        dt.setAttrName(selectAttribute(p, unusedAttrList, labelList, attrList));
        Queue<Node> nodeQueue = new LinkedList<>();
        Queue<ArrayList<String>> unusedAttrQueue = new LinkedList<>();
        Queue<Partition> partitionQueue = new LinkedList<>();
        Node curNode = dt;
        do {
            String className;
            if (unusedAttrList.size() <= 1) {
                curNode.setClassification(this.getMostClass(p, attrList, labelList));
            } else if ((className = this.getSameClass(p, labelList)) != null) {
                curNode.setClassification(className);
            } else {
                String selectedAttr = selectAttribute(p, unusedAttrList, labelList, attrList);
                curNode.setAttrName(selectedAttr);
                ArrayList<Partition> partitionList = this.getPartitionList(p, selectedAttr, attrList);

                for (int i = 0; i < partitionList.size(); i++) {
                    Node tmpNode = new Node();
                    if (partitionList.get(i).isEmpty()) {
                        tmpNode.setClassification(this.getMostClass(p, attrList, labelList));
                        curNode.put(attrList.get(selectedAttr).get(i), tmpNode);
                    } else {
                        ArrayList<String> tmpUnusedAttrs = new ArrayList<>(unusedAttrList);
                        tmpUnusedAttrs.remove(selectedAttr);
                        curNode.put(attrList.get(selectedAttr).get(i), tmpNode);
                        nodeQueue.offer(tmpNode);
                        unusedAttrQueue.offer(tmpUnusedAttrs);
                        partitionQueue.offer(new Partition(partitionList.get(i)));
                    }
                }
            }
        } while ((curNode = nodeQueue.poll()) != null
                && (p = partitionQueue.poll()) != null
                && (unusedAttrList = unusedAttrQueue.poll()) != null);
        return dt;
    }

    private ArrayList<Partition> getPartitionList(Partition partition, String selectedAttr, AttrList attrList) {
        ArrayList<String> attrs = attrList.get(selectedAttr);
        LinkedHashMap<String, Partition> pMap = new LinkedHashMap<>();
        for (String attr : attrs) {
            pMap.put(attr, new Partition());
        }

        for (Data data : partition) {
            pMap.get(data.get(selectedAttr)).add(data);
        }
        return new ArrayList<>(pMap.values());
    }


    private String selectAttribute(Partition partition, ArrayList<String> unusedAttrList, ArrayList<String> labelList, AttrList attrList) {
        double maxGain = 0;
        String selectedAttr = "";


        ArrayList<Integer> numbers = new ArrayList<>();
        ArrayList<String> classList = attrList.get(labelList.get(labelList.size() - 1));

        for (String className : classList) {
            int tmpSum = 0;
            for (Data data : partition) {
                if (data.get(labelList.get(labelList.size() - 1)).equals(className))
                    tmpSum++;
            }
            numbers.add(tmpSum);
        }
        for (int i = 0; i < unusedAttrList.size() - 1; i++) {
            String attr = unusedAttrList.get(i);
            ArrayList<String> attrs = attrList.get(attr);
            double curGain = calculateI(numbers, partition.size()) - calculateIAttr(attr, attrs, partition, attrList, labelList);
            if (curGain >= maxGain) {
                maxGain = curGain;
                selectedAttr = attr;
            }
        }
        return selectedAttr;
    }

    private double calculateI(ArrayList<Integer> numbers, int sum) {
        double infoD = 0;
        for (Integer tmpNum : numbers)
            if (tmpNum != 0)
                infoD -= ((double) tmpNum / (double) sum) * Math.log(((double) tmpNum / (double) sum)) / Math.log(2);
        return infoD;
    }

    private double calculateIAttr(String attrName, ArrayList<String> attrs, ArrayList<Data<String, String>> partition, AttrList attrList, ArrayList<String> labelList) {
        double infoD = 0;

        for (String attr : attrs) {
            ArrayList<String> classNameList = attrList.get(labelList.get(labelList.size() - 1));
            LinkedHashMap<String, Integer> classSumMap = new LinkedHashMap<>();
            for (String className : classNameList) {
                classSumMap.put(className, 0);
            }
            int tmpSum = 0;
            for (Data<String, String> data : partition) {
                if (data.get(attrName).equals(attr)) {
                    tmpSum++;
                    Integer tmp = classSumMap.get((String) data.get(labelList.get(labelList.size() - 1)));
                    tmp++;
                    classSumMap.put((String) data.get(labelList.get(labelList.size() - 1)), tmp);
                }
            }
            infoD += ((double) tmpSum / (double) partition.size()) * calculateI(new ArrayList<Integer>(classSumMap.values()), tmpSum);
        }
        return infoD;
    }

    public String selectAttrByRatio(Partition partition, ArrayList<String> unusedAttrList, ArrayList<String> labelList, AttrList attrList) {
        double maxRatio = 0;
        String selectedAttr = "";


        ArrayList<Integer> numbers = new ArrayList<>();
        ArrayList<String> classList = attrList.get(labelList.get(labelList.size() - 1));

        for (String className : classList) {
            int tmpSum = 0;
            for (Data data : partition) {
                if (data.get(labelList.get(labelList.size() - 1)).equals(className))
                    tmpSum++;
            }
            numbers.add(tmpSum);
        }
        for (int i = 0; i < unusedAttrList.size() - 1; i++) {
            String attr = unusedAttrList.get(i);
            ArrayList<String> attrs = attrList.get(attr);
            double curRatio = 0;
            if (calculateSplitInfo(attr, attrs, partition) != 0)
                curRatio = calculateI(numbers, partition.size()) - calculateIAttr(attr, attrs, partition, attrList, labelList) / calculateSplitInfo(attr, attrs, partition);
            if (curRatio >= maxRatio) {
                maxRatio = curRatio;
                selectedAttr = attr;
            }
        }
        return selectedAttr;
    }

    private double calculateSplitInfo(String attrName, ArrayList<String> attrs, ArrayList<Data<String, String>> partition) {
        double splitInfo = 0;
        LinkedHashMap<String, Integer> attrCount = new LinkedHashMap<>();
        for (String tmpAttr : attrs) {
            attrCount.put(tmpAttr, 0);
        }
        for (Data<String, String> data : partition) {
            Integer tmp = attrCount.get(data.get(attrName));
            tmp++;
            attrCount.put((String) data.get(attrName), tmp);
        }
        splitInfo += calculateI(new ArrayList<Integer>(attrCount.values()), partition.size());
//        System.out.println(attrName+": " + splitInfo);
        return splitInfo;
    }
    
    public String selectAttrByGini(Partition partition, ArrayList<String> unusedAttrList, ArrayList<String> labelList, AttrList attrList) {
    	double minGini = 0;
        String selectedAttr = "";
        for (int i=0; i< unusedAttrList.size()-1;i++) {
        	String attr = unusedAttrList.get(i);
        	double tmpGini = calculateAttrGini(attr, attrList.get(attr), partition, unusedAttrList, labelList, attrList);
        	if (unusedAttrList.indexOf(attr) == 0){
	        	minGini = tmpGini;
	        	selectedAttr = attr;
        	} else if(tmpGini<minGini){
        			minGini = calculateAttrGini(attr, attrList.get(attr), partition, unusedAttrList, labelList, attrList);
    	        	selectedAttr = attr;	
        	}
//        	System.out.println(attr+": " + tmpGini);
        }
//        System.out.println(selectedAttr);
        return selectedAttr;
    }
    
    
    private double calculateAttrGini(String attrName, ArrayList<String> attrs, Partition partition, ArrayList<String> unusedAttrList, ArrayList<String> labelList, AttrList attrList){
    	double giniAttr = 0;
    	ArrayList<Partition> pList = getPartitionList(partition, attrName, attrList);
		for (Partition p : pList){
			giniAttr += ((double)p.size()/(double)partition.size()) * calculateGini(p, labelList, attrList);
		}
    	return giniAttr;
    }
    
    private double calculateGini(Partition partition, ArrayList<String> labelList, AttrList attrList){
    	double gini = 1;
    	ArrayList<Integer> numbers = new ArrayList<>();
        ArrayList<String> classList = attrList.get(labelList.get(labelList.size() - 1));
    	for (String className : classList) {
            int tmpSum = 0;
            for (Data data : partition) {
                if (data.get(labelList.get(labelList.size() - 1)).equals(className))
                    tmpSum++;
            }
            numbers.add(tmpSum);
        }
    	for (int tmp:numbers) {
    		gini -= ((double)tmp/(double)partition.size())*((double)tmp/(double)partition.size());
    	}
    	return gini;
    }

    private String getSameClass(Partition dataSet, ArrayList<String> labelList) {

        String className = (String) dataSet.get(0).get(labelList.get(labelList.size() - 1));
        for (Data<String, String> data : dataSet) {
            if (!((String) data.get(labelList.get(labelList.size() - 1))).equals(className)) {
                return null;
            }
        }
        return className;
    }


    private String getMostClass(Partition partition, AttrList attrList, ArrayList<String> labelList) {
        ArrayList<String> classNameList = attrList.get(labelList.get(labelList.size() - 1));
        LinkedHashMap<String, Integer> classSumMap = new LinkedHashMap<>();
        for (String className : classNameList) {
            classSumMap.put(className, 0);
        }
        for (Data<String, String> data : partition) {
            Integer tmp = classSumMap.get((String) data.get(labelList.get(labelList.size() - 1)));
            tmp++;
            classSumMap.put((String) data.get(labelList.get(labelList.size() - 1)), tmp);
        }
        Integer maxCount = 0;
        int maxIdx = 0;
        ArrayList<Integer> classList = new ArrayList<>(classSumMap.values());
        for (int i = 0; i < classList.size(); i++) {
            if (maxCount <= classList.get(i)) {
                maxCount = classList.get(i);
                maxIdx = i;
            }
        }
        return attrList.get(labelList.get(labelList.size() - 1)).get(maxIdx);
    }
    
    private ArrayList<ArrayList<String>> powerSet(ArrayList<String> originalSet) {
        int size = originalSet.size();
        int numberOfSubSets = (int) Math.pow(2, size);
        ArrayList<ArrayList<String>> setList = new ArrayList<>();
        ArrayList<String> originalList = new ArrayList<>(originalSet);
        for (int i = 1; i < numberOfSubSets / 2; i++) {
            String bin = getPaddedBinString(i, size);
            ArrayList<String> set = getSet(bin, originalList);
            setList.add(set);
        }
        return setList;
    }
    
    private ArrayList<String> getSet(String bin, ArrayList<String> origValues) {
    	ArrayList<String> result = new ArrayList<>();
        for (int i = 0; i <= bin.length() - 1; i++) {
            if (bin.charAt(i) == '1') {
                String val = origValues.get(i);
                result.add(val);
            }
        }
        return result;
    }

    private String getPaddedBinString(int i, int size) {
        String bin = Integer.toBinaryString(i);
        bin = String.format("%0" + size + "d", Integer.parseInt(bin));
        return bin;
    }
}
