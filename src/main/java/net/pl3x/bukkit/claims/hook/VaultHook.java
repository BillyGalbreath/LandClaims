package net.pl3x.bukkit.claims.hook;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultHook {
    private static final VaultHook instance = new VaultHook();

    public static VaultHook getInstance() {
        return instance;
    }

    private VaultHook() {
    }

    private Economy economy = null;

    public Economy getEconomy() {
        return economy;
    }

    public boolean setupEconomy() {
        RegisteredServiceProvider<Economy> economyProvider = Bukkit.getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
        }
        return (economy != null);
    }
}
