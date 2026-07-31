package dev.lucas.villagercontrol.listeners;

import dev.lucas.villagercontrol.config.ConfigValues;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityTransformEvent;

import java.util.Set;

/**
 * Handles blocking of villager/wandering trader/trader llama spawning
 * based on entity type, world, and spawn cause.
 * Also blocks zombie villager curing via EntityTransformEvent.
 */
public class VillagerSpawnListener implements Listener {

    private final ConfigValues config;

    public VillagerSpawnListener(ConfigValues config) {
        this.config = config;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        Entity entity = event.getEntity();
        String worldName = event.getLocation().getWorld().getName();
        CreatureSpawnEvent.SpawnReason reason = event.getSpawnReason();

        switch (entity.getType()) {
            case VILLAGER -> {
                if (config.villagersEnabled()) return;
                if (config.villagerExcludedWorlds().contains(worldName)) return;
                if (shouldBlockCause(reason, config.villagerBlockNatural(), config.villagerBlockSummoned())) {
                    event.setCancelled(true);
                }
            }
            case WANDERING_TRADER -> {
                if (config.wanderingTraderEnabled()) return;
                if (config.wanderingTraderExcludedWorlds().contains(worldName)) return;
                if (shouldBlockCause(reason, config.wanderingTraderBlockNatural(), config.wanderingTraderBlockSummoned())) {
                    event.setCancelled(true);
                }
            }
            case TRADER_LLAMA -> {
                if (config.traderLlamaEnabled()) return;
                if (config.traderLlamaExcludedWorlds().contains(worldName)) return;
                if (shouldBlockCause(reason, config.traderLlamaBlockNatural(), config.traderLlamaBlockSummoned())) {
                    event.setCancelled(true);
                }
            }
        }
    }

    /**
     * Blocks zombie villager curing (ZOMBIE_VILLAGER -> VILLAGER transform)
     * when villagers.spawn-control.zombie-cure is true and villagers.enabled is false.
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityTransform(EntityTransformEvent event) {
        if (config.villagersEnabled()) return;
        if (!config.villagerBlockZombieCure()) return;

        if (event.getEntityType() == EntityType.ZOMBIE_VILLAGER
                && event.getTransformedEntity().getType() == EntityType.VILLAGER) {

            String worldName = event.getEntity().getWorld().getName();
            if (config.villagerExcludedWorlds().contains(worldName)) return;

            event.setCancelled(true);

            // Notify nearby players who may be attempting the cure
            event.getEntity().getNearbyEntities(5, 5, 5).forEach(e -> {
                if (e instanceof Player player && !config.msgZombieCureBlocked().isEmpty()) {
                    player.sendMessage(config.msgZombieCureBlocked());
                }
            });
        }
    }

    /**
     * Maps spawn reason to natural/summoned category and checks against config flags.
     */
    private boolean shouldBlockCause(CreatureSpawnEvent.SpawnReason reason,
                                     boolean blockNatural, boolean blockSummoned) {
        return switch (reason) {
            // Summoned via command or spawn egg
            case COMMAND, SPAWNER_EGG -> blockSummoned;
            // Everything else (natural, chunk gen, village, etc.) is "natural"
            default -> blockNatural;
        };
    }
}
