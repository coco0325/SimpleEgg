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
package io.github.redpanda4552.SimpleEgg.event;

import java.util.ArrayList;
import java.util.UUID;

import io.github.redpanda4552.SimpleEgg.CaptureManager;
import io.github.redpanda4552.SimpleEgg.SimpleEggMain;
import io.github.redpanda4552.SimpleEgg.util.*;

import org.bukkit.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Guardian;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Horse.Variant;
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
import org.bukkit.entity.Skeleton.SkeletonType;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerEggThrowEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.SpawnEgg;

public class EventListener implements Listener
{
	private SimpleEggMain plugin;
	
	private EggTracker eggTracker;
	private CaptureManager captureManager;
	
	private ChatColor a = ChatColor.AQUA;
	private ChatColor b = ChatColor.BLUE;
	private String pf = a + "[SimpleEgg]" + b + " ";
	
	public EventListener(SimpleEggMain plugin)
	{
		this.plugin = plugin;
		eggTracker = plugin.eggTracker;
		captureManager = plugin.captureManager;
	}
	
	/**
	 * By some witchcraft this event fires before PlayerEggThrowEvent.
	 * Don't ask questions, just accept that it works.
	 */
	@EventHandler
	public void onEggCollide(EntityDamageByEntityEvent event)
	{
		LivingEntity entity; Egg egg;
		
		if (event.getDamager() instanceof Egg)
		{
			egg = (Egg) event.getDamager();
		}
		else
		{
			return;
		}
		
		if (event.getEntity() instanceof LivingEntity)
		{
			entity = (LivingEntity) event.getEntity();
		}
		else
		{
			return;
		}
		
		eggTracker.addEntry(new EggTrackerEntry(null, entity, egg));
	}
	
	/**
	 * This fires after the damage event does, so this, somehow, works.
	 */
	@EventHandler
	public void eggCollide(PlayerEggThrowEvent event)
	{
		EggTrackerEntry entry;
		
		if (eggTracker.getEntry(event.getEgg()) != null)
		{
			eggTracker.getEntry(event.getEgg()).setPlayer(event.getPlayer());
			entry = eggTracker.getEntry(event.getEgg());
			event.setHatching(false);
		}
		else
		{
			return;
		}
		
		if (entry.getPlayer().hasPermission("SimpleEgg." + entry.getEntity().getType().toString().replaceAll("_", "").toLowerCase()))
		{
			if (!captureManager.ownerConfliction(entry))
			{
				if (captureManager.hasCaptureMaterials(entry))
				{
					captureManager.makeSpawnEgg(entry);
				}
				else
				{
					entry.getPlayer().sendMessage(pf + "You need " + a + plugin.consumedMaterialAmount + " " + plugin.consumedMaterialName + b + " to capture a mob.");
					refundEgg(entry.getPlayer());
				}
			}
			else
			{
				entry.getPlayer().sendMessage(pf + "You do not own this mob.");
				refundEgg(entry.getPlayer());
			}
		}
		else
		{
			entry.getPlayer().sendMessage(pf + "You do not have permission to capture this mob type.");
			refundEgg(entry.getPlayer());
		}
	}
	
	@EventHandler
	public void eggUse(PlayerInteractEvent event)
	{
		if (event.getItem() != null && event.getItem().getData() instanceof SpawnEgg && event.getAction() == Action.RIGHT_CLICK_BLOCK)
		{
			ItemStack stack = event.getItem();
			SpawnEgg spawnEgg = (SpawnEgg) event.getItem().getData();
			ItemMeta meta = stack.getItemMeta();
			ArrayList<String> lore = (ArrayList<String>) meta.getLore();

			//Check the first line for health, to see if we have a SimpleEgg.
			if (meta.hasLore() && lore.get(0).startsWith("Health: "))
			{
				event.setCancelled(true);
				LivingEntity entity = (LivingEntity) event.getPlayer().getWorld().spawnEntity(new Location(event.getPlayer().getWorld(), event.getClickedBlock().getX(), event.getClickedBlock().getY() + 1, event.getClickedBlock().getZ()), spawnEgg.getSpawnedType());
				
				if (stack.getAmount() > 1)
				{
					stack.setAmount(stack.getAmount() - 1);
				}
				else
				{
					event.getPlayer().getInventory().remove(stack);
				}
				
				if (meta.getDisplayName().equalsIgnoreCase(entity.getType().toString()))
				{
					entity.setCustomName(null);
				}
				else
				{
					entity.setCustomName(meta.getDisplayName());
				}
				
				entity.setMaxHealth(Double.parseDouble(lore.get(0).split(" ")[1].split("/")[1]));
				entity.setHealth(Double.parseDouble(lore.get(0).split(" ")[1].split("/")[0]));
				
				/*
				 * In "Yes or No" cases, we will check if true and otherwise set to false.
				 * While it is expected that a spawned in Guardian will never be an elder,
				 * or a spawned in Creeper will never be charged, this is Vanilla, and may
				 * be changed by another plugin. So for safety, we will make sure normal
				 * mobs spawn in as such.
				 */
				
				//Passive mobs with extra data
				if (entity instanceof Ageable)
				{
					((Ageable) entity).setAge(Integer.parseInt(lore.get(1).split(" ")[2])); //Array index is 2 because the text has two spaces.
					
					if (entity instanceof Sheep)
					{
						((Sheep) entity).setColor(DyeColor.valueOf(lore.get(2).split(" ")[1]));
					}
					else if (entity instanceof Rabbit)
					{
						((Rabbit) entity).setRabbitType(Rabbit.Type.valueOf(lore.get(2).split(" ")[1]));
					}
					else if (entity instanceof Villager)
					{
						((Villager) entity).setProfession(Profession.valueOf(lore.get(2).split(" ")[1]));
					}
					else if (entity instanceof Tameable)
					{
						if (!lore.get(2).split(" ")[1].equalsIgnoreCase("None"))
						{
							((Tameable) entity).setOwner(Bukkit.getPlayer(UUID.fromString(lore.get(2).split(" ")[1])));
						}
						
						if (entity instanceof Horse)
						{
							if (lore.get(3).split(" ")[1].equalsIgnoreCase("Iron"))
							{
								((Horse) entity).getInventory().setArmor(new ItemStack(Material.IRON_BARDING, 1));
							}
							else if (lore.get(3).split(" ")[1].equalsIgnoreCase("Gold"))
							{
								((Horse) entity).getInventory().setArmor(new ItemStack(Material.GOLD_BARDING, 1));
							}
							else if (lore.get(3).split(" ")[1].equalsIgnoreCase("Diamond"))
							{
								((Horse) entity).getInventory().setArmor(new ItemStack(Material.DIAMOND_BARDING, 1));
							}
							
							if (lore.get(4).split(" ")[1].equalsIgnoreCase("Yes"))
							{
								((Horse) entity).getInventory().setSaddle(new ItemStack(Material.SADDLE, 1));
							}
							else
							{
								((Horse) entity).getInventory().setSaddle(null);
							}
							
							((Horse) entity).setVariant(Horse.Variant.valueOf(lore.get(5).split(" ")[1]));
							
							if (((Horse) entity).getVariant() == Variant.HORSE)
							{
								((Horse) entity).setColor(Horse.Color.valueOf(lore.get(6).split(" ")[1]));
								((Horse) entity).setStyle(Horse.Style.valueOf(lore.get(7).split(" ")[1]));
							}
						}
						else if (entity instanceof Wolf)
						{
							if (lore.get(3).split(" ")[1].equalsIgnoreCase("Yes"))
							{
								((Wolf) entity).setAngry(true);
							}
							else
							{
								((Wolf) entity).setAngry(false);
							}
							
							if (((Wolf) entity).isTamed())
							{
								((Wolf) entity).setCollarColor(DyeColor.valueOf(lore.get(4).split(" ")[1]));
							}
						}
						else if (entity instanceof Ocelot)
						{
							((Ocelot) entity).setCatType(Ocelot.Type.valueOf(lore.get(3).split(" ")[1]));
						}
					}
				}
				
				//Hostiles with extra data
				else if (entity instanceof Slime)
				{
					Slime slime = (Slime) entity;
					slime.setSize(Integer.parseInt(lore.get(1).split(" ")[1]));
				}
				else if (entity instanceof Creeper)
				{
					if (lore.get(1).split(" ")[1].equalsIgnoreCase("Yes"))
					{
						((Creeper) entity).setPowered(true);
					}
					else
					{
						((Creeper) entity).setPowered(false);
					}
				}
				else if (entity instanceof Guardian)
				{
					if (lore.get(1).split(" ")[1].equalsIgnoreCase("Yes"))
					{
						((Guardian) entity).setElder(true);
					}
					else
					{
						((Guardian) entity).setElder(false);
					}
				}
				else if (entity instanceof Skeleton)
				{
					((Skeleton) entity).setSkeletonType(SkeletonType.valueOf(lore.get(1).split(" ")[1]));
				}
				else if (entity instanceof Zombie)
				{
					if (lore.get(1).split(" ")[1].equalsIgnoreCase("Yes"))
					{
						((Zombie) entity).setBaby(true);
					}
					else
					{
						((Zombie) entity).setBaby(false);
					}
					
					if (lore.get(2).split(" ")[1].equalsIgnoreCase("Yes"))
					{
						((Zombie) entity).setVillager(true);
					}
					else
					{
						((Zombie) entity).setVillager(false);
					}
					
					if (entity instanceof PigZombie)
					{
						PigZombie pigzombie = (PigZombie) entity;
						pigzombie.setAnger(Integer.parseInt(lore.get(3).split(" ")[2])); //Array index is 2 because the text has two spaces.
					}
				}
			}
		}
	}
	
	@EventHandler
	public void eggUseOnEntity(PlayerInteractEntityEvent event)
	{
		ItemStack stack = event.getPlayer().getItemInHand();
		ItemMeta meta = stack.getItemMeta();
		if (stack.getData() instanceof SpawnEgg)
		{
			if (meta != null && meta.getLore() != null)
			{
				if (meta.getLore().size() >= 1 && meta.getLore().get(0).startsWith("Health: "))
				{
					event.getPlayer().sendMessage(pf + "You cannot use a SimpleEgg to make babies out of other adult mobs.");
					event.setCancelled(true);
				}
			}
		}
	}
	
	private void refundEgg(Player player)
	{
		if (plugin.getConfig().getBoolean("egg-refund")) {
			player.getWorld().dropItem(player.getLocation(), new ItemStack(Material.EGG, 1));
		}
	}
}
