package me.zabrid.zabridwithdraw;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.text.NumberFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WithdrawCommand implements CommandExecutor {

    public static String color(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static String replacePlaceholder(String message, String placeholderRegex, String replacement) {
        Pattern pattern = Pattern.compile(placeholderRegex);
        Matcher matcher = pattern.matcher(message);
        return matcher.replaceAll(replacement);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        ConfigManager configManager = ZabridWithdraw.getInstance().getConfigManager();

        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be executed by players.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            String message = (String) configManager.getValue("messages.plugin_prefix") + configManager.getValue("messages.invalid_usage");
            player.sendMessage(color(message));
            return true;
        }

        int amount;
        try {
            amount = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            String message = (String) configManager.getValue("messages.plugin_prefix") + configManager.getValue("messages.invalid_number");
            player.sendMessage(color(message));
            return true;
        }

        Integer minimum = (Integer) configManager.getValue("settings.min_withdraw_amount");
        if (amount < minimum) {
            String formattedMinimum = NumberFormat.getNumberInstance().format(minimum);
            String message = (String) configManager.getValue("messages.plugin_prefix") + configManager.getValue("messages.below_minimum");
            message = replacePlaceholder(message, "\\{minimum\\}", formattedMinimum);
            player.sendMessage(color(message));
            return true;
        }

        Integer maximum = (Integer) configManager.getValue("settings.max_withdraw_amount");
        if (amount > maximum) {
            String formattedMaximum = NumberFormat.getNumberInstance().format(maximum);
            String message = (String) configManager.getValue("messages.plugin_prefix") + configManager.getValue("messages.above_maximum");
            message = replacePlaceholder(message, "\\{maximum\\}", formattedMaximum);
            player.sendMessage(color(message));
            return true;
        }

        Economy economy = ZabridWithdraw.getEconomy();
        if (economy == null) {
            player.sendMessage("Vault economy service is not available.");
            return true;
        }

        if (economy.getBalance(player) < amount) {
            String formattedBalance = NumberFormat.getNumberInstance().format(Math.round(economy.getBalance(player)));
            String message = (String) configManager.getValue("messages.plugin_prefix") + configManager.getValue("messages.insufficient_balance");
            message = replacePlaceholder(message, "\\{balance\\}", formattedBalance);
            player.sendMessage(color(message));
            return true;
        }

        economy.withdrawPlayer(player, amount);

        ItemStack item = GenerateBanknote.generateBanknote(player.getName(), amount);

        player.getInventory().addItem(item);

        String formattedValue = NumberFormat.getNumberInstance().format(amount);
        String message = (String) configManager.getValue("messages.plugin_prefix") + configManager.getValue("messages.successfully_withdrew");
        message = replacePlaceholder(message, "\\{value\\}", formattedValue);
        player.sendMessage(color(message));

        return true;
    }
}