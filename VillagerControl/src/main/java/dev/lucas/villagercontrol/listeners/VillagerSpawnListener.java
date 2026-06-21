package dev.lucas.villagercontrol.listeners;

import dev.lucas.villagercontrol.config.ConfigValues;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityTransformEvent;

/**
 * Cuando villagers.enabled = false en config.yml:
 *  - Cancela el spawn de Villager, WanderingTrader y TraderLlama, sin importar la causa
 *    (natural, /summon, spawn egg, etc.) gracias a CreatureSpawnEvent.
 *  - Cancela la transformación de Zombie Villager -> Villager (curación con
 *    poción de debilidad + manzana dorada), evitando que "aparezca" un aldeano por esa vía.
 */
public class VillagerSpawnListener implements Listener {

    private final ConfigValues config;

    public VillagerSpawnListener(ConfigValues config) {
        this.config = config;
    }

    @EventHandler(ignoreCancelled = true)
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (config.villagersEnabled()) return;
        if (isControlledType(event.getEntityType())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityTransform(EntityTransformEvent event) {
        if (config.villagersEnabled()) return;

        // Caso típico: ZOMBIE_VILLAGER -> VILLAGER (curación)
        if (event.getEntityType() == EntityType.ZOMBIE_VILLAGER
                && event.getTransformedEntity() != null
                && event.getTransformedEntity().getType() == EntityType.VILLAGER) {
            event.setCancelled(true);
        }
    }

    private boolean isControlledType(EntityType type) {
        return type == EntityType.VILLAGER
                || type == EntityType.WANDERING_TRADER
                || type == EntityType.TRADER_LLAMA;
    }
}
