package com.java;

import java.util.*;
import java.util.stream.*;

public class GroupByObjectType {
    public static void main(String[] args) {
        List<Object> input = List.of("Hello World", 42, 3.14, "Java 8", 100, true, 2.718);

        Map<String, List<Object>> groupedObjectMap = input.stream()
                .collect(Collectors.groupingBy(obj -> obj.getClass().getSimpleName(),
                        LinkedHashMap::new, Collectors.toList()));

        List<String> dataTypes = List.of("Boolean", "Integer", "Double", "String");
        dataTypes.forEach(dataType -> {
            if (groupedObjectMap.containsKey(dataType))
                System.out.println(dataType + ": " + groupedObjectMap.get(dataType));
        });
    }
}
