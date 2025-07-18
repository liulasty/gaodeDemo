package com.example.demo.jmsdemo;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Administrator
 */
public class FragmentGroupManager {

    private static final ConcurrentMap<String, FragmentGroup> GROUPS = new ConcurrentHashMap<>();

    public static FragmentGroup getOrCreateGroup(String groupId, int totalFragments) {
        return GROUPS.compute(groupId, (id, existing) ->
                existing != null ? existing : new FragmentGroup(groupId, totalFragments)
        );
    }

    public static void removeGroup(String groupId) {
        GROUPS.remove(groupId);
    }

    public static ConcurrentMap<String, FragmentGroup> getAllGroups() {
        return GROUPS;
    }
}
