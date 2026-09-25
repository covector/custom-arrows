package dev.covector.customarrows;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import dev.covector.customarrows.arrow.ArrowListener;
import dev.covector.customarrows.arrow.ArrowRegistry;
import dev.covector.customarrows.arrow.CustomArrow;
import dev.covector.customarrows.bow.BowListener;
import dev.covector.customarrows.item.GiveCommand;

public class CustomArrowsPlugin extends JavaPlugin
{
    public static CustomArrowsPlugin plugin;
    public static ArrowListener arrowListener;
    public static BowListener bowListener;

    // namespaces
    public NamespacedKey arrowTypesKey = new NamespacedKey(this, "arrow-types");
    public NamespacedKey piercedEntitiesKey = new NamespacedKey(this, "pierced-entities");
    public NamespacedKey pierceRemaining = new NamespacedKey(this, "pierce-remaining");

    @Override
    public void onEnable() {
        // set singleton reference to this
        plugin = this;

        // bow listener
        bowListener = new BowListener();
        Bukkit.getPluginManager().registerEvents(bowListener, this);

        // command handler
        this.getCommand("ca").setExecutor(new GiveCommand());
        this.getCommand("ca").setTabCompleter(new GiveCommand());

        // arrow listener
        arrowListener = new ArrowListener();
        Bukkit.getPluginManager().registerEvents(arrowListener, this);
        ArrowRegistry.registerAll();

        // all registered
        getLogger().info("Custom Arrows Plugin Activated!");
    }

    @Override
    public void onDisable() {
        // all event listener
        HandlerList.unregisterAll(this);

        // unregister arrows
        ArrowRegistry.unregisterAll();

        // command listener
        if (this.getCommand("ca") != null) {
            this.getCommand("ca").setExecutor(null);
            this.getCommand("ca").setTabCompleter(null); // Good practice if you added one later
        }

        // all unregistered
        getLogger().info("Custom Arrows Plugin Deactivated!");
    }

    public void registerArrow(CustomArrow customArrow) {
        ArrowRegistry.registerArrow(customArrow);
    }   
}
