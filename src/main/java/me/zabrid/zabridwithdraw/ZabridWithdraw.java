package me.zabrid.zabridwithdraw;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public final class ZabridWithdraw extends JavaPlugin {

    private static ZabridWithdraw instance;
    private static Economy econ = null;
    private ConfigManager configManager;

    @Override
    public void onEnable() {

        if (!setupEconomy()) {
            getLogger().severe("Disabled due to no Vault dependency found!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        getLogger().info("Vault economy hooked successfully!");
        getCommand("withdraw").setExecutor(new WithdrawCommand());
        getCommand("zabridwithdrawreload").setExecutor(new ReloadCommand());

        getServer().getPluginManager().registerEvents(new BanknoteRedeem(), this);

        configManager = new ConfigManager(this);

        instance = this;
    }

    @Override
    public void onDisable() {
        if (configManager != null) {
            configManager.saveConfig();
        }
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }

        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }

        econ = rsp.getProvider();
        return econ != null;
    }

    public static ZabridWithdraw getInstance() {
        return instance;
    }

    public static Economy getEconomy() {
        return econ;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

}
