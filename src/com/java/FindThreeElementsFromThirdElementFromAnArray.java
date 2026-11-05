package com.java;

import java.util.*;
import java.util.stream.*;

public class FindThreeElementsFromThirdElementFromAnArray {
    public static void main(String[] args) {
        List<Integer> nums = Arrays.asList(2,3,9,-4,7);

        nums = nums.stream()
                .skip(2)
                .limit(3)
                .sorted()
                .collect(Collectors.toList());

        System.out.println(nums);
    }
}
