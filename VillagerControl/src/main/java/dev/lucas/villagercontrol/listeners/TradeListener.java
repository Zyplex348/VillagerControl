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
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.MerchantInventory;

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
        if (!isTradeBlocked(trader)) return;

        // Cancel for both hands, but only send the message once (main hand).
        event.setCancelled(true);
        if (event.getHand() == EquipmentSlot.HAND) {
            sendBlockedMessage(event.getPlayer());
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (!(event.getInventory() instanceof MerchantInventory merchantInventory)) return;
        if (!(event.getPlayer() instanceof Player player)) return;
        if (!(merchantInventory.getMerchant() instanceof AbstractVillager trader)) return;
        if (!isTradeBlocked(trader)) return;

        event.setCancelled(true);
        sendBlockedMessage(player);
    }

    /**
     * True if trading with this entity must be blocked according to the config.
     * Unknown AbstractVillager types (other plugins' custom traders) are never blocked.
     */
    private boolean isTradeBlocked(AbstractVillager trader) {
        String worldName = trader.getWorld().getName();

        if (trader instanceof Villager) {
            if (!config.villagersEnabled()) return false;        // doesn't exist, no point blocking trade
            if (config.villagerTradesEnabled()) return false;
            return !config.villagerExcludedWorlds().contains(worldName);
        }

        if (trader instanceof WanderingTrader) {
            if (!config.wanderingTraderEnabled()) return false;
            if (config.wanderingTraderTradesEnabled()) return false;
            return !config.wanderingTraderExcludedWorlds().contains(worldName);
        }

        return false;
    }

    private void sendBlockedMessage(Player player) {
        if (!config.msgTradeBlocked().isEmpty()) {
            player.sendMessage(config.msgTradeBlocked());
        }
    }
}
