/*Input
"abcdefgh","abcdefghijkl","abcdef","abcdefghijklmnop","abcd"

Output
"ponmlkjihgfedcba","lkjihgfedcba","hgfedcba","fedcba","dcba"

1. Collection of String inputs into a list
2. call functional interface with the list as input parameter
3. Implement functional interface into the same class
- Desc sorting of the list using stream(high length string - lowest length string)
- reverse the every element into same position. After reversing the o/p should match
with the given output list*/

package com.java;

import java.util.*;
import java.util.stream.Collectors;

public class ReverseStringWithFuncInterfaceSortedByDescLength implements StringProcessor{
    @Override
    public List<String> process(List<String> input) {
        return input.stream()
                .sorted(Comparator.comparingInt(String::length).reversed())
                .map(s -> new StringBuilder(s).reverse().toString())
                .collect(Collectors.toList());

    }

    public static void main(String[] args) {
        List<String> input = Arrays.asList("abcdefgh","abcdefghijkl","abcdef","abcdefghijklmnop","abcd");
        StringProcessor processor = new ReverseStringWithFuncInterfaceSortedByDescLength();
        List<String> output = processor.process(input);
        System.out.println(output);
    }
}

@FunctionalInterface
interface StringProcessor{
    List<String> process(List<String> process);
}
