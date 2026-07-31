package dev.lucas.villagercontrol.listeners;

import dev.lucas.villagercontrol.config.ConfigValues;
import org.bukkit.entity.AbstractVillager;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.entity.WanderingTrader;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEntityEvent;

/**
 * Blocks trading with villagers and wandering traders based on config.
 * Cancelled at two points: on right-click interaction AND on inventory open,
 * as a safety net against other plugins forcing the menu open.
 */
public class TradeListener implements Listener {

    private final ConfigValues config;

    public TradeListener(ConfigValues config) {
        this.config = config;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onInteract(PlayerInteractEntityEvent event) {
        if (!(event.getRightClicked() instanceof AbstractVillager trader)) return;

        String worldName = trader.getWorld().getName();
        Player player = event.getPlayer();

        if (trader instanceof Villager) {
            if (!config.villagersEnabled()) return;           // doesn't exist, no point blocking trade
            if (config.villagerTradesEnabled()) return;
            if (config.villagerExcludedWorlds().contains(worldName)) return;
        } else if (trader instanceof WanderingTrader) {
            if (!config.wanderingTraderEnabled()) return;
            if (config.wanderingTraderTradesEnabled()) return;
            if (config.wanderingTraderExcludedWorlds().contains(worldName)) return;
        }

        event.setCancelled(true);
        if (!config.msgTradeBlocked().isEmpty()) {
            player.sendMessage(config.msgTradeBlocked());
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (event.getInventory().getType() != InventoryType.MERCHANT) return;
        if (!(event.getPlayer() instanceof Player player)) return;

        // Check the holder to determine type
        var holder = event.getInventory().getHolder();
        if (!(holder instanceof AbstractVillager trader)) return;

        String worldName = trader.getWorld().getName();

        if (trader instanceof Villager) {
            if (!config.villagersEnabled()) return;
            if (config.villagerTradesEnabled()) return;
            if (config.villagerExcludedWorlds().contains(worldName)) return;
        } else if (trader instanceof WanderingTrader) {
            if (!config.wanderingTraderEnabled()) return;
            if (config.wanderingTraderTradesEnabled()) return;
            if (config.wanderingTraderExcludedWorlds().contains(worldName)) return;
        }

        event.setCancelled(true);
        if (!config.msgTradeBlocked().isEmpty()) {
            player.sendMessage(config.msgTradeBlocked());
        }
    }
}
