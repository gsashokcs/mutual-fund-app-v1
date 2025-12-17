package com.mutualfund.exception;

import java.util. LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Test {
    public static void main(String[] args) {
        String example = "test";
        
        // Method 1: Find all non-repeating characters
        String nonRepeatingChars = example. chars()
            .mapToObj(c -> (char) c)
            .collect(Collectors. groupingBy(Function.identity(), 
                     LinkedHashMap::new, 
                     Collectors.counting()))
            .entrySet()
            .stream()
            .filter(entry -> entry.getValue() == 1)
            .map(Map.Entry::getKey)
            .map(String::valueOf)
            .collect(Collectors.joining());
        
        System.out. println("Non-repeating characters:  " + nonRepeatingChars); // Output: "es"
        
        // Method 2: Find first non-repeating character
        Character firstNonRepeating = example.chars()
            .mapToObj(c -> (char) c)
            .collect(Collectors.groupingBy(Function.identity(), 
                     LinkedHashMap:: new, 
                     Collectors.counting()))
            .entrySet()
            .stream()
            .filter(entry -> entry. getValue() == 1)
            .map(Map.Entry::getKey)
            .findFirst()
            .orElse(null);
        
        System.out.println("First non-repeating character: " + firstNonRepeating); // Output: "e"
    }
}