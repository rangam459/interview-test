package com.java;

import java.util.*;
import java.util.stream.*;
public class RemoveDuplicatesFromProducts {


    public static void main(String[] args) {
        List<Product> products = List.of(
                new Product("a", "a is used for eating"),
                new Product("a", "a is used for eating"),
                new Product("a", "a is used for eating"),
                new Product("b", "b is used for cleaning"),
                new Product("b", "b is used for cleaning"));

        List<Product> uniqueProducts = products.stream()
                .distinct()
                .toList();

//        List<Product> uniqueProducts = new ArrayList<>(
//                products.stream()
//                        .collect(Collectors.toMap(
//                                Product::getType,          // key
//                                p -> p,                     // value
//                                (first, duplicate) -> first, // merge function: keep first seen
//                                LinkedHashMap::new           // preserve insertion order
//                        ))
//                        .values()
//        );
        System.out.println("Products = " + products + "\nUnique products = " + uniqueProducts);
    }

}

class Product {
    private String type;
    private String description;

    Product(String type, String description) {
        this.type = type;
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product p)) return false;
        return Objects.equals(type, p.type) && Objects.equals(description, p.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, description);
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
