package com.java;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

record Employee(String name, String dept, double salary) {}

public class JavaStreams {

    public static void main(String[] args) {
        List<Employee> employees = List.of(
                new Employee("Megha","Engineering", 95000),
                new Employee("Avinash", "Engineering", 150000),
                new Employee("Yashwanth", "Sales",97000)
        );

        //1. Group by department
        Map<String, List<Employee>> empByDept = employees.stream().collect(
                Collectors.groupingBy(Employee::dept)
        );
        System.out.println("Employees grouped by department: " + empByDept);

        //2. Average salary per department
        Map<String, Double> avgSalaryPerDept = employees.stream()
                .collect(Collectors.groupingBy(Employee::dept, Collectors.averagingDouble(Employee::salary)));
        System.out.println("Employees grouped by average salary per department: " + avgSalaryPerDept);

        //3.Top 2 highest paid names only
        List<String> topNames = employees.stream()
                .sorted(Comparator.comparingDouble(Employee::salary).reversed())
                .limit(2)
                .map(Employee::name)
                .toList();
        System.out.println("Top 2 highest paid Employees: " + topNames);

        //Flatmap example
        List<List<Integer>> nested = List.of(List.of(1,2), List.of(3,4));
        List<Integer> flat = nested.stream()
                .flatMap(List::stream)
                .toList();
        System.out.println("Nested list: " + nested + " Flat list: " + flat);

        Stream<Integer> stream = flat.stream();
        stream.forEach(System.out::println);  // 1st terminal op — OK
        stream.forEach(System.out::println);  // 2nd call on SAME stream
    }


}
