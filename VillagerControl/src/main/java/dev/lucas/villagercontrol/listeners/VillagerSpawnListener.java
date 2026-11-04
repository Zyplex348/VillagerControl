package dev.lucas.villagercontrol.listeners;

import dev.lucas.villagercontrol.config.ConfigValues;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
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
 *
 * Players are only told about a blocked spawn when it was caused by a
 * /summon command or a spawn egg. Natural spawns are blocked silently.
 */
public class VillagerSpawnListener implements Listener {

    private static final double NOTIFY_RADIUS = 6.0;

    private final ConfigValues config;

    public VillagerSpawnListener(ConfigValues config) {
        this.config = config;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        Entity entity = event.getEntity();
        String worldName = event.getLocation().getWorld().getName();
        CreatureSpawnEvent.SpawnReason reason = event.getSpawnReason();

        boolean block = switch (entity.getType()) {
            case VILLAGER -> shouldBlock(
                    config.villagersEnabled(), config.villagerExcludedWorlds(), worldName, reason,
                    config.villagerBlockNatural(), config.villagerBlockSummoned());
            case WANDERING_TRADER -> shouldBlock(
                    config.wanderingTraderEnabled(), config.wanderingTraderExcludedWorlds(), worldName, reason,
                    config.wanderingTraderBlockNatural(), config.wanderingTraderBlockSummoned());
            case TRADER_LLAMA -> shouldBlock(
                    config.traderLlamaEnabled(), config.traderLlamaExcludedWorlds(), worldName, reason,
                    config.traderLlamaBlockNatural(), config.traderLlamaBlockSummoned());
            default -> false;
        };

        if (!block) return;

        event.setCancelled(true);

        if (isSummonedCause(reason)) {
            notifyNearbyPlayers(event.getLocation(), config.msgSpawnBlocked());
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
            notifyNearbyPlayers(event.getEntity().getLocation(), config.msgZombieCureBlocked());
        }
    }

    private boolean shouldBlock(boolean enabled, Set<String> excludedWorlds, String worldName,
                                CreatureSpawnEvent.SpawnReason reason,
                                boolean blockNatural, boolean blockSummoned) {
        if (enabled) return false;
        if (excludedWorlds.contains(worldName)) return false;
        return isSummonedCause(reason) ? blockSummoned : blockNatural;
    }

    /** Summoned via command or spawn egg. Everything else counts as natural. */
    private boolean isSummonedCause(CreatureSpawnEvent.SpawnReason reason) {
        return reason == CreatureSpawnEvent.SpawnReason.COMMAND
                || reason == CreatureSpawnEvent.SpawnReason.SPAWNER_EGG;
    }

    private void notifyNearbyPlayers(Location location, String message) {
        if (message.isEmpty() || location.getWorld() == null) return;
        for (Player player : location.getWorld().getNearbyPlayers(location, NOTIFY_RADIUS)) {
            player.sendMessage(message);
        }
    }
}
