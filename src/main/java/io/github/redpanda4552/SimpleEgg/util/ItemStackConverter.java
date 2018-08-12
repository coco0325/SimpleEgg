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

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemStackConverter {
    
    public static String toString(ItemStack itemStack) {
        String ret = new String(itemStack.getType().toString());
        ret += "-" + itemStack.getAmount() + "-" + itemStack.getDurability();
        
        if (!itemStack.hasItemMeta())
            return ret;
        
        if (itemStack.getItemMeta().hasDisplayName())
            ret += "-" + itemStack.getItemMeta().getDisplayName();
        
        ret += "-";
        
        if (itemStack.getItemMeta() instanceof EnchantmentStorageMeta) {
            EnchantmentStorageMeta meta = (EnchantmentStorageMeta) itemStack.getItemMeta();
            
            for (Enchantment enchantment : meta.getStoredEnchants().keySet())
                ret += enchantment.getKey().getKey() + "/" + meta.getStoredEnchants().get(enchantment) + "-";
        } else {
            ItemMeta meta = itemStack.getItemMeta();
            
            for (Enchantment enchantment : meta.getEnchants().keySet())
                ret += enchantment.getKey().getKey() + "/" + meta.getEnchantLevel(enchantment) + "-";
        }
        
        return ret.substring(0, ret.lastIndexOf("-")); // Trim extra - off
    }
    
    public static ItemStack fromString(String string) {
        ItemStack ret = null;
        String[] itemStackParts = string.split("-");
        
        try {
            Material resultMaterial = Material.valueOf(itemStackParts[0]);
            int resultAmount = Integer.parseInt(itemStackParts[1]);
            short durability = Short.parseShort(itemStackParts[2]);
            ret = new ItemStack(resultMaterial, resultAmount, durability);
            
            if (resultMaterial == Material.ENCHANTED_BOOK) {
                EnchantmentStorageMeta meta = (EnchantmentStorageMeta) Bukkit.getItemFactory().getItemMeta(resultMaterial);
                
                for (int i = 3; i < itemStackParts.length; i++) {
                    String[] enchantParts = itemStackParts[i].split("/");
                    meta.addStoredEnchant(Enchantment.getByKey(NamespacedKey.minecraft(enchantParts[0])), Integer.parseInt(enchantParts[1]), true);
                }
                
                ret.setItemMeta(meta);
            } else {
                ItemMeta meta = Bukkit.getItemFactory().getItemMeta(resultMaterial);
                
                for (int i = 3; i < itemStackParts.length; i++) {
                    String[] enchantParts = itemStackParts[i].split("/");
                    meta.addEnchant(Enchantment.getByKey(NamespacedKey.minecraft(enchantParts[0])), Integer.parseInt(enchantParts[1]), true);
                }
                
                ret.setItemMeta(meta);
            }
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Provided string cannot be converted to an ItemStack!", e);
        }
        
        return ret;
    }
}
