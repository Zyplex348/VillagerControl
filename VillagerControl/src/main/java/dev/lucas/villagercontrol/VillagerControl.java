package dev.lucas.villagercontrol;

import dev.lucas.villagercontrol.commands.VillagerControlCommand;
import dev.lucas.villagercontrol.config.ConfigValues;
import dev.lucas.villagercontrol.listeners.TradeListener;
import dev.lucas.villagercontrol.listeners.VillagerSpawnListener;
import org.bukkit.plugin.java.JavaPlugin;

public class VillagerControl extends JavaPlugin {

    private ConfigValues configValues;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        this.configValues = new ConfigValues(this);

        getServer().getPluginManager().registerEvents(new VillagerSpawnListener(configValues), this);
        getServer().getPluginManager().registerEvents(new TradeListener(configValues), this);

        var executor = new VillagerControlCommand(configValues);
        getCommand("villagercontrol").setExecutor(executor);

        getLogger().info("VillagerControl activado. villagers.enabled=" + configValues.villagersEnabled()
                + " trades.enabled=" + configValues.tradesEnabled());
    }

    @Override
    public void onDisable() {
        getLogger().info("VillagerControl desactivado.");
    }
}
