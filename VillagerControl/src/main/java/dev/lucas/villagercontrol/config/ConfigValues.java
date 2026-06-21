package dev.lucas.villagercontrol.config;

import dev.lucas.villagercontrol.VillagerControl;

/**
 * Lee config.yml y expone los dos toggles del plugin.
 * Llamar a reload() para releer el archivo en caliente.
 */
public class ConfigValues {

    private final VillagerControl plugin;
    private boolean villagersEnabled;
    private boolean tradesEnabled;

    public ConfigValues(VillagerControl plugin) {
        this.plugin = plugin;
        reload();
    }

    public void reload() {
        plugin.reloadConfig();
        var cfg = plugin.getConfig();
        this.villagersEnabled = cfg.getBoolean("villagers.enabled", true);
        this.tradesEnabled = cfg.getBoolean("trades.enabled", true);
    }

    public boolean villagersEnabled() {
        return villagersEnabled;
    }

    public boolean tradesEnabled() {
        return tradesEnabled;
    }
}
