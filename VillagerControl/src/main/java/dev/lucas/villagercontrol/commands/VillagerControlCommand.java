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
            sender.sendMessage(ChatColor.RED + "Necesitás ser admin para usar esto.");
            return true;
        }

        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "reload" -> {
                config.reload();
                sender.sendMessage(ChatColor.GREEN + "VillagerControl: configuración recargada.");
                sendStatus(sender);
            }
            case "status" -> sendStatus(sender);
            default -> sendHelp(sender);
        }
        return true;
    }

    private void sendStatus(CommandSender sender) {
        sender.sendMessage(ChatColor.AQUA + "villagers.enabled = " + config.villagersEnabled());
        sender.sendMessage(ChatColor.AQUA + "trades.enabled = " + config.tradesEnabled());
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "--- VillagerControl ---");
        sender.sendMessage(ChatColor.YELLOW + "/vc reload" + ChatColor.GRAY + " - recargar config.yml");
        sender.sendMessage(ChatColor.YELLOW + "/vc status" + ChatColor.GRAY + " - ver valores actuales");
    }
}
