package com.iunames.utilities;

import java.util.List;

public class ApiUtilities {
    public static boolean hasDuplicates(List list){
        boolean hasDuplicates = false;
        for (int i = 0; i < list.size(); i++) {
            for (int j = 0; j < list.size(); j++) {
                if (list.get(i).equals(list.get(j)) && i != j) {
                    hasDuplicates = true;
                    System.out.println("Duplicate: "+list.get(i));
                    break;
                }
            }
        }
        return hasDuplicates;
    }
}
