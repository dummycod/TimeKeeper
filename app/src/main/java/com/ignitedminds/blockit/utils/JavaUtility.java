package com.ignitedminds.blockit.utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class JavaUtility {

    public static LinkedHashMap<String, Long>
    sortByValue(HashMap<String, Long> hm) {
        // Create a list from elements of HashMap
        List<Map.Entry<String, Long>> list
                = new LinkedList<>(
                hm.entrySet());

        // Sort the list using lambda expression
        Collections.sort(
                list,
                (i1,
                 i2) -> i2.getValue().compareTo(i1.getValue()));

        // put data from sorted list to hashmap
        LinkedHashMap<String, Long> temp
                = new LinkedHashMap();
        for (Map.Entry<String, Long> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
    }
}
