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
package io.github.redpanda4552.SimpleEgg.util;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

import io.github.redpanda4552.SimpleEgg.Main;
import net.milkbowl.vault.economy.Economy;

public class ExpenseHandler {

    public enum ExchangeMode {
        ITEM, VAULT;
    }
    
    private Material itemMaterial;
    private int itemAmount;
    private double vaultAmount;
    private ExchangeMode exchangeMode;
    private Economy economy;
    
    public ExpenseHandler(Material itemMaterial, int itemAmount) {
        this.itemMaterial = itemMaterial;
        this.itemAmount = itemAmount;
        exchangeMode = ExchangeMode.ITEM;
    }
    
    public ExpenseHandler(Main main, double vaultAmount) {
        this.vaultAmount = vaultAmount;
        exchangeMode = ExchangeMode.VAULT;
        economy = main.getVaultEconomy();
    }
    
    /**
     * Remove the cost materials, either items or vault currency, from a player
     */
    public boolean execute(Player player) {
        if (hasMaterials(player)) {
            if (exchangeMode == ExchangeMode.ITEM) {
                PlayerInventory inventory = player.getInventory();
                
                for (int i = itemAmount; i != 0; i--) {
                    if (inventory.getItem(inventory.first(itemMaterial)).getAmount() > 1) {
                        inventory.getItem(inventory.first(itemMaterial)).setAmount(inventory.getItem(inventory.first(itemMaterial)).getAmount() - 1);
                    } else {
                        inventory.remove(inventory.getItem(inventory.first(itemMaterial)));
                        return true;
                    }
                }
                
                // No return here - if something happens mid-iteration that
                // causes the for loop to halt, we want a false return so we
                // know something bad happened
            } else if (exchangeMode == ExchangeMode.VAULT) {
                economy.withdrawPlayer(player, vaultAmount);
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Test if a Player has the materials, either items or vault currency,
     * needed for a capture.
     */
    public boolean hasMaterials(Player player) {
        if (exchangeMode == ExchangeMode.ITEM) {
            return player.getInventory().contains(itemMaterial, itemAmount);
        } else if (exchangeMode == ExchangeMode.VAULT) {
            return economy.has(player, vaultAmount);
        }
        
        return false;
    }
    
    /**
     * Returns an uncolored, player-friendly, non-technical string stating the
     * materials, item or vault based, required for a capture.
     */
    public String requiredMaterials() {
        StringBuilder ret = new StringBuilder();
        
        if (exchangeMode == ExchangeMode.ITEM) {
            ret.append(itemAmount).append(" ").append(itemMaterial);
        } else if (exchangeMode == ExchangeMode.VAULT) {
            ret.append(vaultAmount).append(" ").append(economy.currencyNamePlural());
        }
        
        return ret.toString();
    }
}
