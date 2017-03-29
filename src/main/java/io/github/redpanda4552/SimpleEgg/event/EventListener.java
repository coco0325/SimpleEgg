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
import java.util.HashMap;
import java.util.UUID;

import io.github.redpanda4552.SimpleEgg.CaptureManager;
import io.github.redpanda4552.SimpleEgg.Main;
import io.github.redpanda4552.SimpleEgg.util.*;

import org.bukkit.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.ChestedHorse;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Evoker;
import org.bukkit.entity.Evoker.Spell;
import org.bukkit.entity.Horse;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Llama;
import org.bukkit.entity.Llama.Color;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;
import org.bukkit.entity.Rabbit;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;
import org.bukkit.entity.ZombieVillager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerEggThrowEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SpawnEggMeta;

public class EventListener implements Listener {
	private Main plugin;
	
	private EggTracker eggTracker;
	private CaptureManager captureManager;
	
	private final ChatColor a = ChatColor.AQUA;
	private final ChatColor b = ChatColor.BLUE;
	private final String tag = a + "[SimpleEgg]" + b + " ";
	
	public EventListener(Main plugin) {
		this.plugin = plugin;
		eggTracker = plugin.eggTracker;
		captureManager = plugin.captureManager;
	}
	
	/**
	 * By some witchcraft this event fires before PlayerEggThrowEvent.
	 * Don't ask questions, just accept that it works.
	 */
	@EventHandler
	public void onEggCollide(EntityDamageByEntityEvent event) {
		LivingEntity entity; Egg egg;
		
		if (event.getDamager() instanceof Egg) {
			egg = (Egg) event.getDamager();
		} else {
			return;
		}
		
		if (event.getEntity() instanceof LivingEntity) {
			entity = (LivingEntity) event.getEntity();
		} else {
			return;
		}
		
		eggTracker.addEntry(new EggTrackerEntry(null, entity, egg));
	}
	
	/**
	 * This fires after the damage event does, so this, somehow, works.
	 */
	@EventHandler
	public void eggCollide(PlayerEggThrowEvent event) {
		if (eggTracker.getEntry(event.getEgg()) != null) {
		    eggTracker.getEntry(event.getEgg()).setPlayer(event.getPlayer());
			event.setHatching(false);
		} else {
			return;
		}
		
		// The player is undefined before we set it above. So to make sure the
		// local var is in fact a true copy, we will define it post assignment.
		EggTrackerEntry entry = eggTracker.getEntry(event.getEgg());
		
		if (entry.getPlayer().hasPermission("SimpleEgg." + entry.getEntity().getType().toString().replaceAll("_", "").toLowerCase())) {
			if (!captureManager.ownerConfliction(entry)) {
				if (captureManager.hasCaptureMaterials(entry)) {
					captureManager.makeSpawnEgg(entry);
				} else {
					entry.getPlayer().sendMessage(tag + "You need " + a + plugin.consumedMaterialAmount + " " + plugin.consumedMaterialName + b + " to capture a mob.");
					refundEgg(entry.getPlayer());
				}
			} else {
				entry.getPlayer().sendMessage(tag + "You do not own this mob.");
				refundEgg(entry.getPlayer());
			}
		} else {
			entry.getPlayer().sendMessage(tag + "You do not have permission to capture this mob type.");
			refundEgg(entry.getPlayer());
		}
		
		eggTracker.removeEntry(entry);
	}
	
	@EventHandler
	public void eggUse(PlayerInteractEvent event) {
		if (event.getItem() != null && event.getItem().getItemMeta() instanceof SpawnEggMeta && event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			ItemStack stack = event.getItem();
			SpawnEggMeta meta = (SpawnEggMeta) stack.getItemMeta();
			ArrayList<String> lore = (ArrayList<String>) meta.getLore();

			// Check the first line for health, to see if we have a SimpleEgg.
			if (meta.hasLore() && lore.get(0).startsWith("Health: ")) {
				event.setCancelled(true);
				LivingEntity entity = (LivingEntity) event.getPlayer().getWorld().spawnEntity(new Location(event.getPlayer().getWorld(), event.getClickedBlock().getX(), event.getClickedBlock().getY() + 1, event.getClickedBlock().getZ()), meta.getSpawnedType());
				
				if (stack.getAmount() > 1) {
					stack.setAmount(stack.getAmount() - 1);
				} else {
					event.getPlayer().getInventory().remove(stack);
				}
				
				// If the ItemStack name contains ": ", that means the mob has a custom name.
				// Below we will use ": " as a delimeter to split and remove the preceding mob type,
				// as well as the ": ", isolating the actual name.
				if (meta.getDisplayName().contains(": ")) {
				    String customName = meta.getDisplayName();
				    customName = customName.replaceFirst(meta.getDisplayName().split(": ")[0], "");
				    customName = customName.replaceFirst(": ", "");
				    entity.setCustomName(customName);
				}
				
				HashMap<String, String> attributeMap = new HashMap<String, String>();
				
				for (String str : lore) {
				    String[] strArr = str.split(": ");
				    attributeMap.put(strArr[0], strArr[1]);
				}
				
				entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(Double.parseDouble(lore.get(0).split(" ")[1].split("/")[1]));
				entity.setHealth(Double.parseDouble(lore.get(0).split(" ")[1].split("/")[0]));
				
				if (entity instanceof Ageable) {
					((Ageable) entity).setAge(Integer.parseInt(attributeMap.get("Age (Ticks)")));
					
					if (entity instanceof Sheep) {
						((Sheep) entity).setColor(DyeColor.valueOf(attributeMap.get("Color")));
					} else if (entity instanceof Rabbit) {
						((Rabbit) entity).setRabbitType(Rabbit.Type.valueOf(attributeMap.get("Type")));
					} else if (entity instanceof Villager) {
						((Villager) entity).setProfession(Profession.valueOf(attributeMap.get("Profession")));
					} else if (entity instanceof Tameable) {
						if (!attributeMap.get("Owner").equals("None")) {
							((Tameable) entity).setOwner(Bukkit.getPlayer(UUID.fromString(attributeMap.get("Owner"))));
							((Tameable) entity).setTamed(true);
						}
						
						if (entity instanceof AbstractHorse) {
						    ((AbstractHorse) entity).setJumpStrength(Double.parseDouble(attributeMap.get("Jump Power")));
						    
						    if (entity instanceof Horse) {
						        if (attributeMap.get("Armor").equals("Iron")) {
	                                ((Horse) entity).getInventory().setArmor(new ItemStack(Material.IRON_BARDING, 1));
	                            } else if (attributeMap.get("Armor").equals("Gold")) {
	                                ((Horse) entity).getInventory().setArmor(new ItemStack(Material.GOLD_BARDING, 1));
	                            } else if (attributeMap.get("Armor").equals("Diamond")) {
	                                ((Horse) entity).getInventory().setArmor(new ItemStack(Material.DIAMOND_BARDING, 1));
	                            }
	                            
	                            if (attributeMap.get("Saddle").equals("Yes")) {
	                                ((Horse) entity).getInventory().setSaddle(new ItemStack(Material.SADDLE, 1));
	                            }
	                            
	                            ((Horse) entity).setColor(Horse.Color.valueOf(attributeMap.get("Color")));
                                ((Horse) entity).setStyle(Horse.Style.valueOf(attributeMap.get("Style")));
						    } else if (entity instanceof ChestedHorse) {
						        if (Boolean.parseBoolean(attributeMap.get("Carrying Chest"))) {
						            ((ChestedHorse) entity).setCarryingChest(true);
						        } else {
						            ((ChestedHorse) entity).setCarryingChest(false);
						        }
						        
						        if (entity instanceof Llama) {
	                                ((Llama) entity).setColor(Color.valueOf(attributeMap.get("Color")));
	                                ((Llama) entity).setStrength(Integer.parseInt(attributeMap.get("Strength")));
	                            }
						    }
						} else if (entity instanceof Wolf) {
							if (attributeMap.get("Angry").equals("Yes")) {
								((Wolf) entity).setAngry(true);
							} else {
								((Wolf) entity).setAngry(false);
							}
							
							if (((Wolf) entity).isTamed()) {
								((Wolf) entity).setCollarColor(DyeColor.valueOf(attributeMap.get("Collar")));
							}
						} else if (entity instanceof Ocelot) {
							((Ocelot) entity).setCatType(Ocelot.Type.valueOf(attributeMap.get("Type")));
							((Ocelot) entity).setSitting(Boolean.parseBoolean(attributeMap.get("Sitting")));
						}
					}
				} else if (entity instanceof Slime) {
					Slime slime = (Slime) entity;
					slime.setSize(Integer.parseInt(attributeMap.get("Size")));
				} else if (entity instanceof Creeper) {
					if (attributeMap.get("Charged").equals("Yes")) {
						((Creeper) entity).setPowered(true);
					} else {
						((Creeper) entity).setPowered(false);
					}
				} else if (entity instanceof Zombie) {
					if (attributeMap.get("Baby").equals("Yes")) {
						((Zombie) entity).setBaby(true);
					} else {
						((Zombie) entity).setBaby(false);
					}
					
					if (entity instanceof PigZombie) {
					    ((PigZombie) entity).setAnger(Integer.parseInt(attributeMap.get("Anger Level")));
					} else if (entity instanceof ZombieVillager) {
					    ((ZombieVillager) entity).setVillagerProfession(Profession.valueOf(attributeMap.get("Profession")));
					}
				} else if (entity instanceof Evoker) {
				    ((Evoker) entity).setCurrentSpell(Spell.valueOf(attributeMap.get("Active Spell")));
				}
			}
		}
	}
	
	@EventHandler
	public void eggUseOnEntity(PlayerInteractEntityEvent event) {
		ItemStack stack = event.getPlayer().getInventory().getItemInMainHand();
		ItemMeta meta = stack.getItemMeta();
		
		if (meta instanceof SpawnEggMeta) {
			if (meta != null && meta.getLore() != null) {
				if (meta.getLore().size() >= 1 && meta.getLore().get(0).startsWith("Health: ")) {
					event.getPlayer().sendMessage(tag + "You cannot use a SimpleEgg to make babies out of other adult mobs.");
					event.setCancelled(true);
				}
			}
		}
	}
	
	private void refundEgg(Player player) {
		if (plugin.getConfig().getBoolean("egg-refund")) {
			player.getWorld().dropItem(player.getLocation(), new ItemStack(Material.EGG, 1));
		}
	}
}
