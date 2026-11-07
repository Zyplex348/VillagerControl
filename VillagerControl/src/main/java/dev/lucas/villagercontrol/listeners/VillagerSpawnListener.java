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
 *
 * Only uses Bukkit/Spigot API calls that exist from 1.20.5 to 26.1.x, so it
 * runs on Spigot, Paper, Purpur and Folia. Entity types are compared with ==
 * instead of a switch, to stay safe if EntityType changes shape in a future version.
 */
public class VillagerSpawnListener implements Listener {

    private static final double NOTIFY_RADIUS = 6.0;

    private final ConfigValues config;

    public VillagerSpawnListener(ConfigValues config) {
        this.config = config;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        EntityType type = event.getEntity().getType();
        String worldName = event.getLocation().getWorld().getName();
        CreatureSpawnEvent.SpawnReason reason = event.getSpawnReason();

        boolean block;
        if (type == EntityType.VILLAGER) {
            block = shouldBlock(
                    config.villagersEnabled(), config.villagerExcludedWorlds(), worldName, reason,
                    config.villagerBlockNatural(), config.villagerBlockSummoned());
        } else if (type == EntityType.WANDERING_TRADER) {
            block = shouldBlock(
                    config.wanderingTraderEnabled(), config.wanderingTraderExcludedWorlds(), worldName, reason,
                    config.wanderingTraderBlockNatural(), config.wanderingTraderBlockSummoned());
        } else if (type == EntityType.TRADER_LLAMA) {
            block = shouldBlock(
                    config.traderLlamaEnabled(), config.traderLlamaExcludedWorlds(), worldName, reason,
                    config.traderLlamaBlockNatural(), config.traderLlamaBlockSummoned());
        } else {
            return;
        }

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

    /** getNearbyEntities exists on every platform (getNearbyPlayers is Paper-only). */
    private void notifyNearbyPlayers(Location location, String message) {
        if (message.isEmpty() || location.getWorld() == null) return;
        for (Entity nearby : location.getWorld().getNearbyEntities(
                location, NOTIFY_RADIUS, NOTIFY_RADIUS, NOTIFY_RADIUS)) {
            if (nearby instanceof Player player) {
                player.sendMessage(message);
            }
        }
    }
}
