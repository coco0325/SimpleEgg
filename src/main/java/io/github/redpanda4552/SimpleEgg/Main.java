/**
 * This file is part of SimpleEgg, licensed under the MIT License (MIT)
 * 
 * Copyright (c) 2015 Brian Wood
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package io.github.redpanda4552.SimpleEgg;

import java.util.logging.Logger;

import org.bukkit.Material;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import io.github.redpanda4552.SimpleEgg.UpdateNotifier.UpdateResult;
import io.github.redpanda4552.SimpleEgg.command.CommandSimpleEgg;
import io.github.redpanda4552.SimpleEgg.listeners.ListenerEggEvents;
import io.github.redpanda4552.SimpleEgg.listeners.ListenerJoin;
import io.github.redpanda4552.SimpleEgg.util.ExpenseHandler;
import io.github.redpanda4552.SimpleEgg.util.Text;
import net.milkbowl.vault.economy.Economy;

public class Main extends JavaPlugin {

    public Logger log;
    
    private CaptureManager captureManager;
    private EggTracker eggTracker;
    private ExpenseHandler expenseHandler;
    private Economy vaultEconomy = null;
    private String updateName = null;
    
    // For the update notifier only
    private static Main main;
    
    public static Main getSelf() {
        return main;
    }
    
    public void onEnable() {
        log = getLogger();
        main = this;
        saveDefaultConfig();
        
        // Configuration settings (that have failsafes)
        String exchangeMode = getConfig().getString("exchange-mode");
        boolean eggRefund = getConfig().getBoolean("egg-refund");
        double vaultCost = getConfig().getDouble("vault-cost");
        String consumedItemName = getConfig().getString("consumed-item-name");
        int consumedItemAmount = getConfig().getInt("consumed-item-amount");
        
        if (exchangeMode.equalsIgnoreCase("vault") && setupEconomy()) {
            log.info("Starting in Vault mode");
            expenseHandler = new ExpenseHandler(this, getConfig().getDouble("vault-cost"));
            getCommand("simpleegg").setExecutor(new CommandSimpleEgg(this, eggRefund, vaultEconomy.currencyNamePlural(), vaultCost));
        } else {
            log.info("Starting in Item mode");
            Material consumedItem;
            
            // getString() will work without fail; need to check manually
            try {
                consumedItem = Material.valueOf(getConfig().getString("consumed-item"));
            } catch (IllegalArgumentException e) {
                log.warning("Config entry 'consumed-item' is not a valid Bukkit Material type! Defaulting to Diamonds.");
                consumedItem = Material.valueOf(getConfig().getDefaults().getString("consumed-item"));
            }
            
            expenseHandler = new ExpenseHandler(consumedItem, consumedItemName, consumedItemAmount);
            getCommand("simpleegg").setExecutor(new CommandSimpleEgg(this, eggRefund, consumedItemName, consumedItemAmount));
        }
        
        captureManager = new CaptureManager(this);
        eggTracker = new EggTracker();
        getServer().getPluginManager().registerEvents(new ListenerJoin(main), this);
        getServer().getPluginManager().registerEvents(new ListenerEggEvents(this), this);
        runUpdateChecker();
    }
    
    public void onDisable() {
        
    }
    
    public void reload() {
        
    }
    
    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") != null) {
            RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
            
            if (rsp != null) {
                vaultEconomy = rsp.getProvider();
                
                if (vaultEconomy != null) {
                    return true;
                } else {
                    log.warning("Failed to get Vault economy; aborting Vault mode.");
                }
            } else {
                log.warning("No provider for Vault; is a Vault-compatible economy plugin present? Aborting Vault mode.");
            }
        } else {
            log.info("Vault not present; aborting Vault mode.");
        }
        
        return false;
    }
    
    /**
     * Update notifier; only notifies, does not download.
     * Utilizes Gravity's Updater class.
     */
    private void runUpdateChecker() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (getConfig().getBoolean("update-checks") == true) {
                    UpdateNotifier updater = new UpdateNotifier(main, 88959, main.getFile(), UpdateNotifier.UpdateType.NO_DOWNLOAD, false);
                    
                    if (updater.getResult() == UpdateResult.UPDATE_AVAILABLE) {
                        updateName = updater.getLatestName();
                        log.info(String.format("%s%s is available at %s", Text.a, updateName, "https://dev.bukkit.org/projects/simpleegg"));
                    }
                }    
            }
        }.runTaskAsynchronously(main);
    }
    
    public CaptureManager getCaptureManager() {
        return captureManager;
    }
    
    public EggTracker getEggTracker() {
        return eggTracker;
    }
    
    public ExpenseHandler getExpenseHandler() {
        return expenseHandler;
    }
    
    public Economy getVaultEconomy() {
        return vaultEconomy;
    }
    
    public String getUpdateName() {
        return updateName;
    }
}
