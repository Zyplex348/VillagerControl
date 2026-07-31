package dev.lucas.villagercontrol.config;

import dev.lucas.villagercontrol.VillagerControl;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Villager;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Central configuration reader for VillagerControl.
 * Call reload() to re-read config.yml without restarting.
 */
public class ConfigValues {

    private final VillagerControl plugin;

    // --- Villagers ---
    private boolean villagersEnabled;
    private boolean villagerBlockNatural;
    private boolean villagerBlockSummoned;
    private boolean villagerBlockZombieCure;
    private boolean villagerTradesEnabled;
    private String  villagerProfessionMode;   // "blacklist" or "whitelist"
    private final Set<Villager.Profession> villagerProfessionList = new HashSet<>();
    private final Set<String> villagerExcludedWorlds = new HashSet<>();

    // --- Wandering Traders ---
    private boolean wanderingTraderEnabled;
    private boolean wanderingTraderBlockNatural;
    private boolean wanderingTraderBlockSummoned;
    private boolean wanderingTraderTradesEnabled;
    private final Set<String> wanderingTraderExcludedWorlds = new HashSet<>();

    // --- Trader Llamas ---
    private boolean traderLlamaEnabled;
    private boolean traderLlamaBlockNatural;
    private boolean traderLlamaBlockSummoned;
    private final Set<String> traderLlamaExcludedWorlds = new HashSet<>();

    // --- Messages ---
    private String msgTradeBlocked;
    private String msgZombieCureBlocked;
    private String msgProfessionBlocked;

    public ConfigValues(VillagerControl plugin) {
        this.plugin = plugin;
        reload();
    }

    public void reload() {
        plugin.reloadConfig();
        FileConfiguration cfg = plugin.getConfig();

        // --- Villagers ---
        villagersEnabled            = cfg.getBoolean("villagers.enabled", true);
        villagerBlockNatural        = cfg.getBoolean("villagers.spawn-control.natural", true);
        villagerBlockSummoned       = cfg.getBoolean("villagers.spawn-control.summoned", true);
        villagerBlockZombieCure     = cfg.getBoolean("villagers.spawn-control.zombie-cure", true);
        villagerTradesEnabled       = cfg.getBoolean("villagers.trades.enabled", true);
        villagerProfessionMode      = cfg.getString("villagers.professions.mode", "blacklist").toLowerCase();

        villagerProfessionList.clear();
        for (String name : cfg.getStringList("villagers.professions.list")) {
            try {
                villagerProfessionList.add(Villager.Profession.valueOf(name.toUpperCase()));
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Invalid profession in config: " + name);
            }
        }

        villagerExcludedWorlds.clear();
        villagerExcludedWorlds.addAll(cfg.getStringList("villagers.excluded-worlds"));

        // --- Wandering Traders ---
        wanderingTraderEnabled          = cfg.getBoolean("wandering-trader.enabled", true);
        wanderingTraderBlockNatural     = cfg.getBoolean("wandering-trader.spawn-control.natural", true);
        wanderingTraderBlockSummoned    = cfg.getBoolean("wandering-trader.spawn-control.summoned", true);
        wanderingTraderTradesEnabled    = cfg.getBoolean("wandering-trader.trades.enabled", true);

        wanderingTraderExcludedWorlds.clear();
        wanderingTraderExcludedWorlds.addAll(cfg.getStringList("wandering-trader.excluded-worlds"));

        // --- Trader Llamas ---
        traderLlamaEnabled          = cfg.getBoolean("trader-llama.enabled", true);
        traderLlamaBlockNatural     = cfg.getBoolean("trader-llama.spawn-control.natural", true);
        traderLlamaBlockSummoned    = cfg.getBoolean("trader-llama.spawn-control.summoned", true);

        traderLlamaExcludedWorlds.clear();
        traderLlamaExcludedWorlds.addAll(cfg.getStringList("trader-llama.excluded-worlds"));

        // --- Messages ---
        msgTradeBlocked         = color(cfg.getString("messages.trade-blocked", ""));
        msgZombieCureBlocked    = color(cfg.getString("messages.zombie-cure-blocked", ""));
        msgProfessionBlocked    = color(cfg.getString("messages.profession-blocked", ""));
    }

    private String color(String s) {
        if (s == null || s.isEmpty()) return "";
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    // ---- Villagers ----
    public boolean villagersEnabled()           { return villagersEnabled; }
    public boolean villagerBlockNatural()       { return villagerBlockNatural; }
    public boolean villagerBlockSummoned()      { return villagerBlockSummoned; }
    public boolean villagerBlockZombieCure()    { return villagerBlockZombieCure; }
    public boolean villagerTradesEnabled()      { return villagerTradesEnabled; }
    public String  villagerProfessionMode()     { return villagerProfessionMode; }
    public Set<Villager.Profession> villagerProfessionList() { return villagerProfessionList; }
    public Set<String> villagerExcludedWorlds() { return villagerExcludedWorlds; }

    // ---- Wandering Traders ----
    public boolean wanderingTraderEnabled()         { return wanderingTraderEnabled; }
    public boolean wanderingTraderBlockNatural()    { return wanderingTraderBlockNatural; }
    public boolean wanderingTraderBlockSummoned()   { return wanderingTraderBlockSummoned; }
    public boolean wanderingTraderTradesEnabled()   { return wanderingTraderTradesEnabled; }
    public Set<String> wanderingTraderExcludedWorlds() { return wanderingTraderExcludedWorlds; }

    // ---- Trader Llamas ----
    public boolean traderLlamaEnabled()         { return traderLlamaEnabled; }
    public boolean traderLlamaBlockNatural()    { return traderLlamaBlockNatural; }
    public boolean traderLlamaBlockSummoned()   { return traderLlamaBlockSummoned; }
    public Set<String> traderLlamaExcludedWorlds() { return traderLlamaExcludedWorlds; }

    // ---- Messages ----
    public String msgTradeBlocked()         { return msgTradeBlocked; }
    public String msgZombieCureBlocked()    { return msgZombieCureBlocked; }
    public String msgProfessionBlocked()    { return msgProfessionBlocked; }
}
