package dev.covector.customarrows;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.Plugin;

import dev.covector.customarrows.bow.*;
import dev.covector.customarrows.arrow.*;
import dev.covector.customarrows.item.*;
import org.bukkit.NamespacedKey;

public class CustomArrowsPlugin extends JavaPlugin
{
    public static CustomArrowsPlugin plugin;
    public static ArrowListener arrowListener;
    public static BowListener bowListener;

    @Override
    public void onEnable() {
        plugin = this;
        bowListener = new BowListener();
        Bukkit.getPluginManager().registerEvents(bowListener, this);
        ItemManager itemManager = new ItemManager();
        this.getCommand("ca").setExecutor(new GiveCommand());
        NamespacedKey key = new NamespacedKey(this, "arrow-types");
        arrowListener = new ArrowListener(key);
        Bukkit.getPluginManager().registerEvents(arrowListener, this);
        ArrowRegistry.register();
        getLogger().info("Custom Arrows Plugin Activated!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Custom Arrows Plugin Deactivated!");
    }
}
