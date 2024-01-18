package dev.covector.customarrows.bow;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import dev.covector.customarrows.item.ItemManager;

public class BowListener implements Listener
{
    @EventHandler
    public void onBowPull(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        ItemStack itemMain = player.getInventory().getItemInMainHand();
        ItemStack itemOff = player.getInventory().getItemInOffHand();
        if (itemMain.getType() != Material.BOW && itemMain.getType() != Material.CROSSBOW && itemOff.getType() != Material.BOW && itemOff.getType() != Material.CROSSBOW) {
            return;
        }


        if (!ItemManager.isCustomBow(itemMain) && !ItemManager.isCustomBow(itemOff)) {
            return;
        }

        player.getInventory().remove(Material.ARROW);
        for (int i = 35; i >= 0; i--) {
            ItemStack item = player.getInventory().getItem(i);
            if (item == null) {
                player.getInventory().setItem(i, new ItemStack(Material.ARROW, 64));
                break;
            }
        }
    }
}
