package dev.lucas.villagercontrol.listeners;

import dev.lucas.villagercontrol.VillagerControl;
import dev.lucas.villagercontrol.config.ConfigValues;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.VillagerCareerChangeEvent;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Enforces the profession blacklist/whitelist.
 *
 * A villager triggers VillagerCareerChangeEvent when it claims a workstation
 * (reason EMPLOYED) or loses its job (reason LOSING_JOB). We only look at
 * EMPLOYED: if the new profession is not allowed, the change is cancelled and
 * the villager simply stays unemployed. The villager is NOT removed.
 *
 * NONE and NITWIT are never "acquired" through this event, so they are
 * ignored here even if they are in the list.
 */
public class ProfessionListener implements Listener {

    private static final long MESSAGE_COOLDOWN_MS = 5_000L;

    private final VillagerControl plugin;
    private final ConfigValues config;

    // Villagers retry to claim the workstation constantly, so we limit the messages.
    private final Map<UUID, Long> lastMessage = new ConcurrentHashMap<>();

    public ProfessionListener(VillagerControl plugin, ConfigValues config) {
        this.plugin = plugin;
        this.config = config;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onCareerChange(VillagerCareerChangeEvent event) {
        if (event.getReason() != VillagerCareerChangeEvent.ChangeReason.EMPLOYED) return;
        if (!config.villagersEnabled()) return; // villagers already blocked at spawn level

        Villager villager = event.getEntity();
        if (config.villagerExcludedWorlds().contains(villager.getWorld().getName())) return;

        Villager.Profession profession = event.getProfession();
        if (profession == Villager.Profession.NONE || profession == Villager.Profession.NITWIT) return;

        boolean isListed = config.villagerProfessionList().contains(profession);

        boolean forbidden = switch (config.villagerProfessionMode()) {
            case "blacklist" -> isListed;   // listed = forbidden
            case "whitelist" -> !isListed;  // not listed = forbidden
            default -> false;
        };

        if (!forbidden) return;

        event.setCancelled(true);
        notifyNearby(villager);
    }

    private void notifyNearby(Villager villager) {
        String message = config.msgProfessionBlocked();
        if (message.isEmpty()) return;

        long now = System.currentTimeMillis();
        Long last = lastMessage.get(villager.getUniqueId());
        if (last != null && now - last < MESSAGE_COOLDOWN_MS) return;

        if (lastMessage.size() > 200) {
            lastMessage.values().removeIf(time -> now - time >= MESSAGE_COOLDOWN_MS);
        }
        lastMessage.put(villager.getUniqueId(), now);

        for (var entity : villager.getNearbyEntities(8, 8, 8)) {
            if (entity instanceof Player player) {
                player.sendMessage(message);
            }
        }
    }
}
