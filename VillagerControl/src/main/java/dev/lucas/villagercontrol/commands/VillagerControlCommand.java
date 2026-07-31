package dev.lucas.villagercontrol.commands;

import dev.lucas.villagercontrol.config.ConfigValues;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class VillagerControlCommand implements CommandExecutor {

    private final ConfigValues config;

    public VillagerControlCommand(ConfigValues config) {
        this.config = config;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.isOp()) {
            sender.sendMessage(ChatColor.RED + "You need to be an admin to use this.");
            return true;
        }

        if (args.length == 0) { sendHelp(sender); return true; }

        switch (args[0].toLowerCase()) {
            case "reload" -> {
                config.reload();
                sender.sendMessage(ChatColor.GREEN + "VillagerControl: configuration reloaded.");
                sendStatus(sender);
            }
            case "status" -> sendStatus(sender);
            default -> sendHelp(sender);
        }
        return true;
    }

    private void sendStatus(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "--- VillagerControl Status ---");

        sender.sendMessage(ChatColor.YELLOW + "Villagers:");
        sender.sendMessage("  enabled: " + flag(config.villagersEnabled()));
        if (!config.villagersEnabled()) {
            sender.sendMessage("  block natural: "     + flag(config.villagerBlockNatural()));
            sender.sendMessage("  block summoned: "    + flag(config.villagerBlockSummoned()));
            sender.sendMessage("  block zombie-cure: " + flag(config.villagerBlockZombieCure()));
        }
        sender.sendMessage("  trades: " + flag(config.villagerTradesEnabled()));
        sender.sendMessage("  profession mode: " + ChatColor.AQUA + config.villagerProfessionMode());
        sender.sendMessage("  profession list: " + ChatColor.AQUA + config.villagerProfessionList());
        sender.sendMessage("  excluded worlds: " + ChatColor.AQUA + config.villagerExcludedWorlds());

        sender.sendMessage(ChatColor.YELLOW + "Wandering Traders:");
        sender.sendMessage("  enabled: " + flag(config.wanderingTraderEnabled()));
        if (!config.wanderingTraderEnabled()) {
            sender.sendMessage("  block natural: "  + flag(config.wanderingTraderBlockNatural()));
            sender.sendMessage("  block summoned: " + flag(config.wanderingTraderBlockSummoned()));
        }
        sender.sendMessage("  trades: " + flag(config.wanderingTraderTradesEnabled()));
        sender.sendMessage("  excluded worlds: " + ChatColor.AQUA + config.wanderingTraderExcludedWorlds());

        sender.sendMessage(ChatColor.YELLOW + "Trader Llamas:");
        sender.sendMessage("  enabled: " + flag(config.traderLlamaEnabled()));
        if (!config.traderLlamaEnabled()) {
            sender.sendMessage("  block natural: "  + flag(config.traderLlamaBlockNatural()));
            sender.sendMessage("  block summoned: " + flag(config.traderLlamaBlockSummoned()));
        }
        sender.sendMessage("  excluded worlds: " + ChatColor.AQUA + config.traderLlamaExcludedWorlds());
    }

    private String flag(boolean value) {
        return value ? (ChatColor.GREEN + "true") : (ChatColor.RED + "false");
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "--- VillagerControl ---");
        sender.sendMessage(ChatColor.YELLOW + "/vc reload" + ChatColor.GRAY + " - reload config.yml");
        sender.sendMessage(ChatColor.YELLOW + "/vc status" + ChatColor.GRAY + " - show current values");
    }
}
