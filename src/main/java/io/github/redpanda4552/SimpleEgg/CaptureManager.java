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

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Guardian;
import org.bukkit.entity.Horse;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;
import org.bukkit.entity.Rabbit;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.SpawnEgg;

import io.github.redpanda4552.SimpleEgg.util.EggTrackerEntry;

public class CaptureManager {

	private SimpleEggMain plugin;
	
	public CaptureManager(SimpleEggMain plugin)
	{
		this.plugin = plugin;
	}
	
	/**
	 * Checks if there is an owner confliction in this EggTrackerEntry.
	 * @param entry - The EggTrackerEntry to test.
	 * @return True if a confliction is present, false if not.
	 */
	public boolean ownerConfliction(EggTrackerEntry entry)
	{
		if (entry.getEntity() instanceof Tameable)
		{
			Tameable tameable = (Tameable) entry.getEntity();
			
			if (tameable.getOwner() != null && tameable.getOwner() != entry.getPlayer())
			{
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Check if a Player has the materials required for a capture.
	 * @param entry - The EggTrackerEntry tied to this event.
	 * @return True if the Player has the necessary materials, or false if not.
	 */
	public boolean hasCaptureMaterials(EggTrackerEntry entry)
	{
		Player player = entry.getPlayer();
		Inventory inventory = player.getInventory();
		
		if (inventory.contains(plugin.consumedMaterial, plugin.consumedMaterialAmount))
		{
			return true;
		}
		return false;
	}
	
	/**
	 * Generates a SpawnEgg, assigns data to it and drops it where the mob used to be.
	 * @param entry - The EggTrackerEntry we are dealing with for this SpawnEgg.
	 */
	public void makeSpawnEgg(EggTrackerEntry entry)
	{
		Player player = entry.getPlayer();
		LivingEntity entity = entry.getEntity(); //No cast check; for it to have gotten this far, it must be an animal.
		Inventory inventory = player.getInventory();
		
		for (int i = 5; i != 0; i--)
		{
			if (inventory.getItem(inventory.first(plugin.consumedMaterial)).getAmount() > 1)
			{
				inventory.getItem(inventory.first(plugin.consumedMaterial)).setAmount(inventory.getItem(inventory.first(plugin.consumedMaterial)).getAmount() - 1);
			}
			else
			{
				inventory.remove(inventory.getItem(inventory.first(plugin.consumedMaterial)));
			}
		}

		ItemStack stack = new SpawnEgg(entity.getType()).toItemStack(1);
		ItemMeta meta = stack.getItemMeta();
		
		if (entity.getName() != null)
		{
			meta.setDisplayName(entity.getName());
		}
		else
		{
			meta.setDisplayName(entity.getType().toString());
		}
		
		//Now lets start packing the lore.
		ArrayList<String> lore = new ArrayList<String>();
		lore.add("Health: " + entity.getHealth() + "/" + entity.getMaxHealth());
		
		if (entity instanceof Ageable)
		{
			lore.add("Age (Ticks): " + ((Ageable) entity).getAge());
			
			if (entity instanceof Sheep)
			{
				lore.add("Color: " + ((Sheep) entity).getColor());
			}
			else if (entity instanceof Rabbit)
			{
				lore.add("Type: " + ((Rabbit) entity).getRabbitType().toString());
			}
			else if (entity instanceof Villager)
			{
				lore.add("Profession: " + ((Villager) entity).getProfession().toString());
			}
			else if (entity instanceof Tameable)
			{
				if (((Tameable) entity).getOwner() != null)
				{
					lore.add("Owner: " + ((Tameable) entity).getOwner().getUniqueId().toString());
				}
				else 
				{
					lore.add("Owner: None");
				}
				
				if (entity instanceof Horse)
				{
					Horse horse = (Horse) entity;
					
					if (horse.getInventory().getArmor() == null)
					{
						lore.add("Armor: None");
					}
					else if (horse.getInventory().getArmor().getType() == Material.IRON_BARDING)
					{
						lore.add("Armor: Iron");
					}
					else if (horse.getInventory().getArmor().getType() == Material.GOLD_BARDING)
					{
						lore.add("Armor: Gold");
					}
					else if (horse.getInventory().getArmor().getType() == Material.DIAMOND_BARDING)
					{
						lore.add("Armor: Diamond");
					}
					
					if (horse.getInventory().getSaddle() != null)
					{
						lore.add("Saddle: Yes");
					}
					else
					{
						lore.add("Saddle: No");
					}
					
					lore.add("Variant: " + horse.getVariant().toString());
					
					if (horse.getVariant() == Horse.Variant.HORSE)
					{
						lore.add("Color: " + horse.getColor().toString());
						lore.add("Style: " + horse.getStyle().toString());
					}
				}
				else if (entity instanceof Wolf)
				{
					if (((Wolf) entity).isAngry())
					{
						lore.add("Angry: Yes");
					}
					else
					{
						lore.add("Angry: No");
					}
					
					if (((Wolf) entity).isTamed())
					{
						lore.add("Collar: " + ((Wolf) entity).getCollarColor().toString());
					}
				}
				else if (entity instanceof Ocelot)
				{
					lore.add("Type: " + ((Ocelot) entity).getCatType());
				}
			}
		}
		else if (entity instanceof Slime)
		{
			lore.add("Size: " + ((Slime) entity).getSize());
		}
		else if (entity instanceof Creeper)
		{
			if (((Creeper) entity).isPowered())
			{
				lore.add("Charged: Yes");
			}
			else
			{
				lore.add("Charged: No");
			}
		}
		else if (entity instanceof Guardian)
		{
			if (((Guardian) entity).isElder())
			{
				lore.add("Elder: Yes");
			}
			else
			{
				lore.add("Elder: No");
			}
		}
		else if (entity instanceof Skeleton)
		{
			lore.add("Type: " + ((Skeleton) entity).getSkeletonType().toString());
		}
		else if (entity instanceof Zombie)
		{
			if (((Zombie) entity).isBaby())
			{
				lore.add("Baby: Yes");
			}
			else
			{
				lore.add("Baby: No");
			}
			
			if (((Zombie) entity).isVillager())
			{
				lore.add("Villager: Yes");
			}
			else
			{
				lore.add("Villager: No");
			}
			
			if (entity instanceof PigZombie)
			{
				lore.add("Anger Level: " + ((PigZombie) entity).getAnger());
			}
		}
		
		meta.setLore(lore);
		stack.setItemMeta(meta);
		entity.getWorld().dropItem(entity.getLocation(), stack);
		entity.remove();
		
	}
}
