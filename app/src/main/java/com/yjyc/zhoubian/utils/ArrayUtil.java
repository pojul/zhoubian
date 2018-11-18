package com.yjyc.zhoubian.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ArrayUtil {

    public static int findMax(int[] lastPositions) {
        int max = lastPositions[0];
        for (int value : lastPositions) {
            if (value > max) {
                max = value;
            }
        }
        return max;
    }

    public static String toCommaSplitStr(List<String> strs){
        if(strs == null || strs.size() <= 0){
            return "";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("");
        for(int i = 0; i < strs.size(); i++){
            if(i > 0){
                sb.append(",");
            }
            sb.append(strs.get(i));
        }
        return sb.toString();
    }

    public static String toSpaceSplitStr(List<String> strs){
        if(strs == null || strs.size() <= 0){
            return "";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("");
        for(int i = 0; i < strs.size(); i++){
            if(i > 0){
                sb.append(" ");
            }
            sb.append(strs.get(i));
        }
        return sb.toString();
    }

    public static List<String> toCommaSplitList(String str){
        List<String> arrays = new ArrayList<>();
        if(str == null){
            return arrays;
        }
        String[] strs = str.split(",");
        for (int i = 0; i < strs.length; i++) {
            if(strs[i] == null || "".equals(strs[i])){
                continue;
            }
            arrays.add(strs[i]);
        }
        return arrays;
    }

    public static boolean containsStringVal(List<String> strs, String str){
        if(strs == null || str == null){
            return false;
        }
        for (int i = 0; i < strs.size(); i++) {
            if(strs.get(i).equals(str)){
                return true;
            }
        }
        return false;
    }

    public static HashMap<String, List<String>> getSelfLabel(List<String> allLabel, List<String> labels){
        HashMap<String, List<String>> maps = new HashMap<String, List<String>>();
        List<String> selfLabels = new ArrayList<>();
        List<String> normalLabels = new ArrayList<>();
        for (int i = 0; i < labels.size(); i++) {
            String label = labels.get(i);
            if(label == null){
                continue;
            }
            if(containsStringVal(allLabel, label)){
                normalLabels.add(label);
            }else{
                selfLabels.add(label);
            }
        }
        maps.put("selfLabels", selfLabels);
        maps.put("normalLabels", normalLabels);
        return maps;
    }

    public static boolean hasIntersecte(List<String> labels1, List<String> labels2){
        for (int i = 0; i < labels2.size(); i++) {
            if(containsStringVal(labels1, labels2.get(i))){
                return true;
            }
        }
        return false;
    }


}
