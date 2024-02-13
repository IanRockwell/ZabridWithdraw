package me.zabrid.zabridwithdraw;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Boolean.parseBoolean;

public class GenerateBanknote {

    public static String color(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static String replacePlaceholder(String message, String placeholderRegex, String replacement) {
        Pattern pattern = Pattern.compile(placeholderRegex);
        Matcher matcher = pattern.matcher(message);
        return matcher.replaceAll(replacement);
    }

    public static ItemStack generateBanknote(String signer, Integer value) {

        ConfigManager configManager = ZabridWithdraw.getInstance().getConfigManager();

        String material_string = (String) configManager.getValue("banknote_item.material");
        ItemStack item = new ItemStack(Material.matchMaterial(material_string));
        ItemMeta meta = item.getItemMeta();

        String item_name = (String) configManager.getValue("banknote_item.name");
        meta.setDisplayName(color(item_name));

        String formattedValue = NumberFormat.getNumberInstance().format(value);

        ArrayList<String> lore = new ArrayList<>();
        ArrayList<String> originalLore = (ArrayList<String>) configManager.getValue("banknote_item.lore");
        for (String line : originalLore) {
            String formattedLine = replacePlaceholder(line, "\\{signer\\}", signer);
            formattedLine = replacePlaceholder(formattedLine, "\\{value\\}", formattedValue);
            formattedLine = color(formattedLine);
            lore.add(formattedLine);
        }
        meta.setLore(lore);

        if (parseBoolean(configManager.getValue("banknote_item.glowing").toString())) {
            meta.addEnchant(org.bukkit.enchantments.Enchantment.DURABILITY, 1, true);
        }

        meta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ENCHANTS);

        meta.getPersistentDataContainer().set(new org.bukkit.NamespacedKey("zabrid_withdraw", "value"), PersistentDataType.STRING, value.toString());

        item.setItemMeta(meta);
        return item;
    }

}
