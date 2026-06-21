package dev.lucas.villagercontrol.listeners;

import dev.lucas.villagercontrol.config.ConfigValues;
import org.bukkit.entity.AbstractVillager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEntityEvent;

/**
 * Cuando trades.enabled = false en config.yml, evita que se abra el menú de
 * comercio tanto de aldeanos normales como de Wandering Traders.
 *
 * Se cancela en dos puntos por seguridad:
 *  1) Al hacer clic derecho sobre el aldeano/errante (evita incluso la animación de apertura).
 *  2) Al abrirse efectivamente un inventario de tipo MERCHANT (red de seguridad
 *     por si algo más intenta abrir el comercio sin pasar por el clic, por ejemplo otro plugin).
 */
public class TradeListener implements Listener {

    private final ConfigValues config;

    public TradeListener(ConfigValues config) {
        this.config = config;
    }

    @EventHandler(ignoreCancelled = true)
    public void onInteract(PlayerInteractEntityEvent event) {
        if (config.tradesEnabled()) return;
        if (event.getRightClicked() instanceof AbstractVillager) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (config.tradesEnabled()) return;
        if (event.getInventory().getType() == InventoryType.MERCHANT
                && event.getPlayer() instanceof Player) {
            event.setCancelled(true);
        }
    }
}
