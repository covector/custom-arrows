package dev.covector.customarrows.arrow;

import org.bukkit.Bukkit;

import java.util.HashMap;

public class ParamTester {
    private static HashMap<String, Integer> params = new HashMap<String, Integer>();

    public static int test(String name, int defaultValue) {
        if (!params.containsKey(name)) {
            params.put(name, defaultValue);
        }
        Bukkit.broadcastMessage("Tested " + name + " to " + params.get(name));
        return params.get(name);
    }

    public static void set(String name, int value) {
        params.put(name, value);
        Bukkit.broadcastMessage("Set " + name + " to " + value);
    }
}