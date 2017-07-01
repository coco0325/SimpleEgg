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

import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SpawnEggMeta;

import io.github.redpanda4552.SimpleEgg.util.ExpenseHandler;
import io.github.redpanda4552.SimpleEgg.util.LorePacker;
import io.github.redpanda4552.SimpleEgg.util.Text;

public class CaptureManager {

    private ExpenseHandler expenseHandler;
	
	public CaptureManager(Main plugin) {
	    expenseHandler = plugin.getExpenseHandler();
	}
	
	/**
	 * Checks if there is an owner confliction in this EggTrackerEntry.
	 * @param entry - The EggTrackerEntry to test.
	 * @return True if a confliction is present, false if not.
	 */
	public boolean ownerConfliction(EggTrackerEntry entry) {
		if (entry.getEntity() instanceof Tameable) {
			Tameable tameable = (Tameable) entry.getEntity();
			
			// Order in the below statements is important. Owner override must be first, otherwise it will never be hit.
			if (tameable.getOwner() != null) {
			    if (entry.getPlayer().hasPermission("SimpleEgg.owner-override")) {
	                if (entry.getPlayer().hasPermission("SimpleEgg.steal")) {
	                    tameable.setOwner(entry.getPlayer());
	                }
	                
	                return false;
	            }
			} else if (entry.getPlayer().hasPermission("SimpleEgg.auto-tame")) {
			    tameable.setOwner(entry.getPlayer());
			}
			
			if (tameable.getOwner() != null && tameable.getOwner() != entry.getPlayer()) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Makes a call to the ExpenseHandler, generates a SpawnEgg, assigns data to
	 * it and drops it where the mob used to be.
	 * @param entry - The EggTrackerEntry we are dealing with for this SpawnEgg.
	 */
	public void makeSpawnEgg(EggTrackerEntry entry) {
		Player player = entry.getPlayer();
		LivingEntity livingEntity = entry.getEntity();
		expenseHandler.execute(player);
		ItemStack stack = new ItemStack(Material.MONSTER_EGG);
		SpawnEggMeta meta = (SpawnEggMeta) stack.getItemMeta();
		meta.setSpawnedType(livingEntity.getType());
		String name = livingEntity.getType().getEntityClass().getSimpleName();
		
		if (livingEntity.getCustomName() != null) {
			name += ": " + livingEntity.getCustomName();
		}
		
		meta.setDisplayName(name);
		meta.setLore(new LorePacker(livingEntity).getLore());
        stack.setItemMeta(meta);
        livingEntity.getWorld().dropItem(livingEntity.getLocation(), stack);
        livingEntity.remove();
        player.sendMessage(Text.tag + "Mob captured successfully!");
	}
}
