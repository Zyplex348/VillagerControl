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
        if (!config.villagersEnabled()) return;

        String worldName = villager.getWorld().getName();
        if (config.villagerExcludedWorlds().contains(worldName)) return;

        Villager.Profession profession = villager.getProfession();
        Set<Villager.Profession> list = config.villagerProfessionList();
        boolean isListed = list.contains(profession);

        boolean shouldRemove = switch (config.villagerProfessionMode()) {
            case "blacklist" -> isListed;
            case "whitelist" -> !isListed;
            default -> false;
        };

        if (shouldRemove) {
            event.setCancelled(true);
            villager.getScheduler().run(plugin, task -> {
                villager.remove();
                villager.getNearbyEntities(8, 8, 8).forEach(e -> {
                    if (e instanceof Player player && !config.msgProfessionBlocked().isEmpty()) {
                        player.sendMessage(config.msgProfessionBlocked());
                    }
                });
            }, null);
        }
    }
}