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

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.ChestedHorse;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Evoker;
import org.bukkit.entity.Horse;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Llama;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;
import org.bukkit.entity.Rabbit;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;
import org.bukkit.entity.ZombieVillager;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SpawnEggMeta;

import io.github.redpanda4552.SimpleEgg.util.EggTrackerEntry;

public class CaptureManager {

	private Main plugin;
	
	private final ChatColor a = ChatColor.AQUA;
    private final ChatColor b = ChatColor.BLUE;
    private final String tag = a + "[SimpleEgg]" + b + " ";
	
	public CaptureManager(Main plugin) {
		this.plugin = plugin;
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
	 * Check if a Player has the materials required for a capture.
	 * @param entry - The EggTrackerEntry tied to this event.
	 * @return True if the Player has the necessary materials, or false if not.
	 */
	public boolean hasCaptureMaterials(EggTrackerEntry entry) {
		Player player = entry.getPlayer();
		Inventory inventory = player.getInventory();
		
		if (inventory.contains(plugin.consumedMaterial, plugin.consumedMaterialAmount)) {
			return true;
		}
		
		return false;
	}
	
	/**
	 * Generates a SpawnEgg, assigns data to it and drops it where the mob used to be.
	 * @param entry - The EggTrackerEntry we are dealing with for this SpawnEgg.
	 */
	public void makeSpawnEgg(EggTrackerEntry entry) {
		Player player = entry.getPlayer();
		LivingEntity entity = entry.getEntity();
		Inventory inventory = player.getInventory();
		
		for (int i = plugin.consumedMaterialAmount; i != 0; i--) {
			if (inventory.getItem(inventory.first(plugin.consumedMaterial)).getAmount() > 1) {
				inventory.getItem(inventory.first(plugin.consumedMaterial)).setAmount(inventory.getItem(inventory.first(plugin.consumedMaterial)).getAmount() - 1);
			} else {
				inventory.remove(inventory.getItem(inventory.first(plugin.consumedMaterial)));
			}
		}

		ItemStack stack = new ItemStack(Material.MONSTER_EGG);
		SpawnEggMeta meta = (SpawnEggMeta) stack.getItemMeta();
		meta.setSpawnedType(entity.getType());
		String name = entity.getType().getEntityClass().getSimpleName();
		
		if (entity.getCustomName() != null) {
			name += ": " + entity.getCustomName();
		}
		
		meta.setDisplayName(name);
		ArrayList<String> lore = new ArrayList<String>();
		lore.add("Health: " + entity.getHealth() + "/" + entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
		
		if (entity instanceof Ageable) {
			lore.add("Age (Ticks): " + ((Ageable) entity).getAge());
			
			if (entity instanceof Sheep) {
				lore.add("Color: " + ((Sheep) entity).getColor());
			} else if (entity instanceof Rabbit) {
				lore.add("Type: " + ((Rabbit) entity).getRabbitType().toString());
			} else if (entity instanceof Villager) {
				lore.add("Profession: " + ((Villager) entity).getProfession().toString());
			} else if (entity instanceof Tameable) {
				if (((Tameable) entity).getOwner() != null) {
					lore.add("Owner: " + ((Tameable) entity).getOwner().getUniqueId().toString());
				} else {
					lore.add("Owner: None");
				}
				
				if (entity instanceof AbstractHorse) {
				    lore.add("Jump Power: " + ((AbstractHorse) entity).getJumpStrength());
					
				    if (entity instanceof Horse) {
				        Horse horse = (Horse) entity;
				        
				        if (horse.getInventory().getArmor() == null) {
	                        lore.add("Armor: None");
	                    } else if (horse.getInventory().getArmor().getType() == Material.IRON_BARDING) {
	                        lore.add("Armor: Iron");
	                    } else if (horse.getInventory().getArmor().getType() == Material.GOLD_BARDING) {
	                        lore.add("Armor: Gold");
	                    } else if (horse.getInventory().getArmor().getType() == Material.DIAMOND_BARDING) {
	                        lore.add("Armor: Diamond");
	                    }
	                    
	                    if (horse.getInventory().getSaddle() != null) {
	                        lore.add("Saddle: Yes");
	                    } else {
	                        lore.add("Saddle: No");
	                    }
	                    
	                    lore.add("Color: " + horse.getColor().toString());
	                    lore.add("Style: " + horse.getStyle().toString());
				    } else if (entity instanceof ChestedHorse) {
				        lore.add("Carrying Chest: " + ((ChestedHorse) entity).isCarryingChest());
				        
				        if (entity instanceof Llama) {
				            Llama llama = (Llama) entity;
	                        lore.add("Color: " + llama.getColor().toString());
	                        lore.add("Strength: " + llama.getStrength());
	                        
	                        if (llama.getInventory().getDecor() != null) {
	                            llama.getWorld().dropItemNaturally(llama.getLocation(), llama.getInventory().getDecor());
	                        }
	                    }
				    }
				} else if (entity instanceof Wolf) {
					if (((Wolf) entity).isAngry()) {
						lore.add("Angry: Yes");
					} else {
						lore.add("Angry: No");
					}
					
					if (((Wolf) entity).isTamed()) {
						lore.add("Collar: " + ((Wolf) entity).getCollarColor().toString());
					}
				} else if (entity instanceof Ocelot) {
					lore.add("Type: " + ((Ocelot) entity).getCatType());
					
					if (((Ocelot) entity).isSitting()) {
					    lore.add("Sitting: Yes");
					} else {
					    lore.add("Sitting: No");
					}
				}
			}
		} else if (entity instanceof Slime) {
			lore.add("Size: " + ((Slime) entity).getSize());
		} else if (entity instanceof Creeper) {
			if (((Creeper) entity).isPowered()) {
				lore.add("Charged: Yes");
			} else {
				lore.add("Charged: No");
			}
		} else if (entity instanceof Zombie) {
			if (((Zombie) entity).isBaby()) {
				lore.add("Baby: Yes");
			} else {
				lore.add("Baby: No");
			}
			
			if (entity instanceof PigZombie) {
				lore.add("Anger Level: " + ((PigZombie) entity).getAnger());
			} else if (entity instanceof ZombieVillager) {
			    lore.add("Profession: " + ((ZombieVillager) entity).getVillagerProfession().toString());
			}
		} else if (entity instanceof Evoker) {
		    lore.add("Active spell: " + ((Evoker) entity).getCurrentSpell().toString());
		}
		
		meta.setLore(lore);
		stack.setItemMeta(meta);
		entity.getWorld().dropItem(entity.getLocation(), stack);
		entity.remove();
		player.sendMessage(tag + "Mob captured successfully!");
	}
}
