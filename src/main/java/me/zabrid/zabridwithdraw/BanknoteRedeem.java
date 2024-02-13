package me.zabrid.zabridwithdraw;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.text.NumberFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BanknoteRedeem implements Listener {

    public static String color(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static String replacePlaceholder(String message, String placeholderRegex, String replacement) {
        Pattern pattern = Pattern.compile(placeholderRegex);
        Matcher matcher = pattern.matcher(message);
        return matcher.replaceAll(replacement);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {

        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item != null && event.getAction().toString().contains("RIGHT")) {
            String valueString = item.getItemMeta().getPersistentDataContainer().get(new org.bukkit.NamespacedKey("zabrid_withdraw", "value"), PersistentDataType.STRING);

            if (valueString != null) {
                int value = Integer.parseInt(valueString);

                int holdingAmount = item.getAmount();
                if (holdingAmount > value) {
                    item.setAmount(holdingAmount - value);
                } else {
                    player.getInventory().setItem(player.getInventory().getHeldItemSlot(), new ItemStack(Material.AIR));
                }

                Economy economy = ZabridWithdraw.getEconomy();
                if (economy == null) {
                    player.sendMessage("Vault economy service is not available.");
                    return;
                }

                ConfigManager configManager = ZabridWithdraw.getInstance().getConfigManager();

                Integer total_value = (value * holdingAmount);
                economy.depositPlayer(player, total_value);

                String formattedAmount = NumberFormat.getNumberInstance().format(holdingAmount);
                String formattedTotalValue = NumberFormat.getNumberInstance().format(total_value);
                String message = (String) configManager.getValue("messages.plugin_prefix") + configManager.getValue("messages.successfully_redeemed");
                message = replacePlaceholder(message, "\\{amount\\}", formattedAmount);
                message = replacePlaceholder(message, "\\{total_value\\}", formattedTotalValue);
                player.sendMessage(color(message));

            }
        }
    }
}
