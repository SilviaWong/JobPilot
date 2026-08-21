package com.jobpilot.jobpilotserver.strategy.algorithm;

import java.util.HashMap;
import java.util.Map;

public class CompanyDisjointSet {
    private final Map<String, String> parent = new HashMap<>();

    public String find(String i) {
        if (i == null) return null;
        parent.putIfAbsent(i, i);
        if (parent.get(i).equals(i)) {
            return i;
        }
        String root = find(parent.get(i));
        parent.put(i, root);
        return root;
    }

    public void union(String alias, String fullName) {
        if (alias == null || fullName == null || alias.trim().isEmpty() || fullName.trim().isEmpty()) {
            return;
        }
        String rootAlias = find(alias);
        String rootFullName = find(fullName);
        if (!rootAlias.equals(rootFullName)) {
            parent.put(rootAlias, rootFullName);
        }
    }
}