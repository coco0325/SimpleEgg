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
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * In keeping with tradition, Spigot's attempts to retrofit only create more
 * problems...
 * 
 * Damage is no longer stored on the itemstack, but the itemmeta. Fine, except
 * that's a lie, itemmeta doesn't store this property. It's stored in
 * Damageable. Which, per the Javadocs, is not implemented by itemmeta, OR any
 * of it's subinterfaces.
 * 
 * Even better, there's literally no other interface or class that implements
 * Damageable. Maybe it's implemented by the actual classes of CraftBukkit???
 * 
 * But, in natural trades I haven't even seen damaged items. So I guess for now
 * we'll just roll with this. But if the need ever comes about, hopefully there
 * is a more, permanent? Solution than this.
 */
public class ItemStackConverter {
    
    public static String toString(ItemStack itemStack) {
        String ret = new String(itemStack.getType().toString());
        int damage = 0;
        ret += "-" + itemStack.getAmount() + "-";
        
        if (!itemStack.hasItemMeta())
            return ret + damage;
        
        ItemMeta baseMeta = itemStack.getItemMeta();
        
        if (baseMeta instanceof Damageable)
            damage = ((Damageable) baseMeta).getDamage();
        
        ret += damage;
        
        if (baseMeta.hasDisplayName())
            ret += "-" + baseMeta.getDisplayName();
        
        ret += "-";
        
        if (baseMeta instanceof EnchantmentStorageMeta) {
            EnchantmentStorageMeta ESMeta = (EnchantmentStorageMeta) itemStack.getItemMeta();
            
            for (Enchantment enchantment : ESMeta.getStoredEnchants().keySet())
                ret += enchantment.getKey().getKey() + "/" + ESMeta.getStoredEnchants().get(enchantment) + "-";
        } else {
            for (Enchantment enchantment : baseMeta.getEnchants().keySet())
                ret += enchantment.getKey().getKey() + "/" + baseMeta.getEnchantLevel(enchantment) + "-";
        }
        
        return ret.substring(0, ret.lastIndexOf("-")); // Trim extra - off
    }
    
    public static ItemStack fromString(String string) {
        ItemStack ret = null;
        String[] itemStackParts = string.split("-");
        
        try {
            Material resultMaterial = Material.valueOf(itemStackParts[0]);
            int resultAmount = Integer.parseInt(itemStackParts[1]);
            int durability = Integer.parseInt(itemStackParts[2]);
            ret = new ItemStack(resultMaterial, resultAmount);
            ItemMeta baseMeta = Bukkit.getItemFactory().getItemMeta(resultMaterial);
            
            if (baseMeta instanceof Damageable)
                ((Damageable) baseMeta).setDamage(durability);
            
            if (baseMeta instanceof EnchantmentStorageMeta) {
                EnchantmentStorageMeta ESMeta = (EnchantmentStorageMeta) baseMeta;
                
                for (int i = 3; i < itemStackParts.length; i++) {
                    String[] enchantParts = itemStackParts[i].split("/");
                    ESMeta.addStoredEnchant(Enchantment.getByKey(NamespacedKey.minecraft(enchantParts[0])), Integer.parseInt(enchantParts[1]), true);
                }
                
                ret.setItemMeta(ESMeta);
            } else {
                for (int i = 3; i < itemStackParts.length; i++) {
                    String[] enchantParts = itemStackParts[i].split("/");
                    baseMeta.addEnchant(Enchantment.getByKey(NamespacedKey.minecraft(enchantParts[0])), Integer.parseInt(enchantParts[1]), true);
                }
                
                ret.setItemMeta(baseMeta);
            }
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Provided string cannot be converted to an ItemStack!", e);
        }
        
        return ret;
    }
}
