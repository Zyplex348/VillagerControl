package dev.lucas.villagercontrol.listeners;

import dev.lucas.villagercontrol.VillagerControl;
import dev.lucas.villagercontrol.config.ConfigValues;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.VillagerAcquireTradeEvent;

import java.util.Set;

/**
 * Enforces the profession blacklist/whitelist.
 *
 * When a villager acquires its first trade (i.e., it has just chosen a profession
 * by claiming a workstation), we check if that profession is allowed.
 * If not, the villager is removed from the world.
 *
 * Note: NITWIT and NONE are assigned at spawn time and won't fire this event,
 * so they are handled at spawn level by VillagerSpawnListener indirectly —
 * the profession filter here covers professions acquired via workstations.
 * For NITWIT/NONE filtering at spawn, add them to the list and the plugin
 * will remove those villagers the moment they try to acquire their first trade
 * or they can be blocked at spawn with villagers.enabled = false.
 */
public class ProfessionListener implements Listener {

    private final VillagerControl plugin;
    private final ConfigValues config;

    public ProfessionListener(VillagerControl plugin, ConfigValues config) {
        this.plugin = plugin;
        this.config = config;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onVillagerAcquireTrade(VillagerAcquireTradeEvent event) {
        if (!(event.getEntity() instanceof Villager villager)) return;
        if (!config.villagersEnabled()) return;  // already blocked at spawn level

        String worldName = villager.getWorld().getName();
        if (config.villagerExcludedWorlds().contains(worldName)) return;

        Villager.Profession profession = villager.getProfession();
        Set<Villager.Profession> list = config.villagerProfessionList();
        boolean isListed = list.contains(profession);

        boolean shouldRemove = switch (config.villagerProfessionMode()) {
            case "blacklist" -> isListed;      // listed = forbidden
            case "whitelist" -> !isListed;     // not listed = forbidden
            default -> false;
        };

        if (shouldRemove) {
            event.setCancelled(true);
            // Remove on next tick to avoid concurrent modification issues
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                villager.remove();
                // Notify nearby players
                villager.getNearbyEntities(8, 8, 8).forEach(e -> {
                    if (e instanceof Player player && !config.msgProfessionBlocked().isEmpty()) {
                        player.sendMessage(config.msgProfessionBlocked());
                    }
                });
            });
        }
    }
}
