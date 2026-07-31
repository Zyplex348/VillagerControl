package dev.lucas.villagercontrol;

import dev.lucas.villagercontrol.commands.VillagerControlCommand;
import dev.lucas.villagercontrol.config.ConfigValues;
import dev.lucas.villagercontrol.listeners.ProfessionListener;
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
        getServer().getPluginManager().registerEvents(new ProfessionListener(this, configValues), this);

        getCommand("villagercontrol").setExecutor(new VillagerControlCommand(configValues));

        getLogger().info("VillagerControl v2.0 enabled.");
        getLogger().info("Villagers: " + configValues.villagersEnabled()
                + " | Wandering Traders: " + configValues.wanderingTraderEnabled()
                + " | Trader Llamas: " + configValues.traderLlamaEnabled());
    }

    @Override
    public void onDisable() {
        getLogger().info("VillagerControl disabled.");
    }
}
