package org.example.repository.until;

import java.util.Collections;


public class QueryUntil {
    private QueryUntil(){}
    public static String generatePlaceholders(int count) {
        return String.join(", ", Collections.nCopies(count, "?"));
    }
}
