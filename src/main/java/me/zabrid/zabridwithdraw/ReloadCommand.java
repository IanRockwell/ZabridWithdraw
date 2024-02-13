package me.zabrid.zabridwithdraw;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ReloadCommand implements CommandExecutor {

    public static String color(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        long startTime = System.currentTimeMillis(); // Record start time

        ConfigManager configManager = ZabridWithdraw.getInstance().getConfigManager();
        configManager.reloadConfig();

        long endTime = System.currentTimeMillis(); // Record end time
        long elapsedTime = endTime - startTime; // Calculate elapsed time in milliseconds

        sender.sendMessage(color("&6[ZabridWithdraw] &aConfig reloaded in " + elapsedTime + " milliseconds.")); // Send message with elapsed time
        return true;

    }
}
