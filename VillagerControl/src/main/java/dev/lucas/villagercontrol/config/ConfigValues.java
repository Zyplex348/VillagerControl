package dev.lucas.villagercontrol.config;

import dev.lucas.villagercontrol.VillagerControl;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Villager;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

/**
 * Central configuration reader for VillagerControl.
 * Call reload() to re-read config.yml without restarting.
 *
 * Thread safety (needed on Folia, where several threads run at the same time):
 * every field is volatile and every set is immutable. reload() builds the new
 * values first and only then swaps them in, so other threads never see a set
 * that is half empty or being modified.
 */
public class ConfigValues {

    private final VillagerControl plugin;

    // --- Villagers ---
    private volatile boolean villagersEnabled;
    private volatile boolean villagerBlockNatural;
    private volatile boolean villagerBlockSummoned;
    private volatile boolean villagerBlockZombieCure;
    private volatile boolean villagerTradesEnabled;
    private volatile String  villagerProfessionMode = "blacklist";   // "blacklist" or "whitelist"
    private volatile Set<Villager.Profession> villagerProfessionList = Set.of();
    // Plain profession names (e.g. "LIBRARIAN"), only used to display the list nicely.
    private volatile Set<String> villagerProfessionNames = Set.of();
    private volatile Set<String> villagerExcludedWorlds = Set.of();

    // --- Wandering Traders ---
    private volatile boolean wanderingTraderEnabled;
    private volatile boolean wanderingTraderBlockNatural;
    private volatile boolean wanderingTraderBlockSummoned;
    private volatile boolean wanderingTraderTradesEnabled;
    private volatile Set<String> wanderingTraderExcludedWorlds = Set.of();

    // --- Trader Llamas ---
    private volatile boolean traderLlamaEnabled;
    private volatile boolean traderLlamaBlockNatural;
    private volatile boolean traderLlamaBlockSummoned;
    private volatile Set<String> traderLlamaExcludedWorlds = Set.of();

    // --- Messages ---
    private volatile String msgTradeBlocked = "";
    private volatile String msgZombieCureBlocked = "";
    private volatile String msgSpawnBlocked = "";

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

        Set<Villager.Profession> professions = new HashSet<>();
        Set<String> professionNames = new TreeSet<>();
        for (String name : cfg.getStringList("villagers.professions.list")) {
            try {
                professions.add(Villager.Profession.valueOf(name.toUpperCase()));
                professionNames.add(name.toUpperCase());
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Invalid profession in config: " + name);
            }
        }
        villagerProfessionList = Collections.unmodifiableSet(professions);
        villagerProfessionNames = Collections.unmodifiableSet(professionNames);
        villagerExcludedWorlds = immutableCopy(cfg.getStringList("villagers.excluded-worlds"));

        // --- Wandering Traders ---
        wanderingTraderEnabled          = cfg.getBoolean("wandering-trader.enabled", true);
        wanderingTraderBlockNatural     = cfg.getBoolean("wandering-trader.spawn-control.natural", true);
        wanderingTraderBlockSummoned    = cfg.getBoolean("wandering-trader.spawn-control.summoned", true);
        wanderingTraderTradesEnabled    = cfg.getBoolean("wandering-trader.trades.enabled", true);
        wanderingTraderExcludedWorlds   = immutableCopy(cfg.getStringList("wandering-trader.excluded-worlds"));

        // --- Trader Llamas ---
        traderLlamaEnabled          = cfg.getBoolean("trader-llama.enabled", true);
        traderLlamaBlockNatural     = cfg.getBoolean("trader-llama.spawn-control.natural", true);
        traderLlamaBlockSummoned    = cfg.getBoolean("trader-llama.spawn-control.summoned", true);
        traderLlamaExcludedWorlds   = immutableCopy(cfg.getStringList("trader-llama.excluded-worlds"));

        // --- Messages ---
        msgTradeBlocked         = color(cfg.getString("messages.trade-blocked", ""));
        msgZombieCureBlocked    = color(cfg.getString("messages.zombie-cure-blocked", ""));
        msgSpawnBlocked         = color(cfg.getString("messages.spawn-blocked", "&cThat mob is disabled on this server."));
    }

    private static Set<String> immutableCopy(Collection<String> values) {
        return Collections.unmodifiableSet(new HashSet<>(values));
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
    public Set<String> villagerProfessionNames() { return villagerProfessionNames; }
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
    public String msgSpawnBlocked()         { return msgSpawnBlocked; }
}
