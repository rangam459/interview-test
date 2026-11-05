package com.java;

import java.util.*;

public class StringManipulations {

    public static void main(String[] args) {
        //check palindrome
        System.out.println("String saaas is a palindrome? : " + isPalindrome("saaas"));

        //First non-repeating char
        System.out.println("First non-repeating char : " + firstNonRepeatingChar("swiss"));
    }

    static boolean isPalindrome(String s) {
        return s.equals(new StringBuilder(s).reverse().toString());
    }

    static Character firstNonRepeatingChar(String s) {
        int[] counts = new int[128];
        for (char c : s.toCharArray()) {
            counts[c]++;
        }
        for (char c : s.toCharArray()) {
            if (counts[c] == 1)
                return c;
        }
        return null;
//        Map<Character, Integer> freq = new LinkedHashMap<>();
//        for (char c : s.toCharArray()) freq.merge(c, 1, Integer::sum);
//        return freq.entrySet().stream()
//                .filter(e -> e.getValue() == 1)
//                .map(Map.Entry::getKey)
//                .findFirst().orElse(null);
    }
}
