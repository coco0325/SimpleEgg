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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.ChestedHorse;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Horse;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Llama;
import org.bukkit.entity.Llama.Color;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Parrot;
import org.bukkit.entity.Parrot.Variant;
import org.bukkit.entity.Pig;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Rabbit;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Sittable;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Snowman;
import org.bukkit.entity.Spellcaster;
import org.bukkit.entity.Spellcaster.Spell;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;
import org.bukkit.entity.ZombieVillager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;

public class LoreExtractor {

    private HashMap<String, String> attributeMap;
    
    /**
     * Applies attributes stored in a lore ArrayList to a LivingEntity. In the
     * case of proper, untampered SimpleEggs, all instanceof checks should have
     * been previously handled by the LorePackager, so no type checking should
     * be required prior to calling this method, and by extension any type
     * compatibility issues will be an indication of tampering. Null Entities
     * will throw an IllegalArgumentException.
     * @param lore - The lore ArrayList to apply.
     * @param livingEntity - The Entity to assemble a lore for.
     * @throws IllegalArgumentException If entity parameter is null
     */
    public LoreExtractor(ArrayList<String> lore, LivingEntity livingEntity) throws IllegalArgumentException {
        if (lore == null || lore.isEmpty()) {
            throw new IllegalArgumentException("Can't apply null/empty lore!");
        }
        
        if (livingEntity == null) {
            throw new IllegalArgumentException("Can't apply lore to a null entity!");
        }
        
        attributeMap = new HashMap<String, String>();
        
        for (String str : lore) {
            str = ChatColor.stripColor(str);
            String[] strArr = str.split(": ");
            attributeMap.put(strArr[0], strArr[1]);
        }
        
        livingEntity(livingEntity);
        
        if (livingEntity instanceof Ageable) {
            ageable((Ageable) livingEntity);
            
            if (livingEntity instanceof Sheep) {
                sheep((Sheep) livingEntity);
            } else if (livingEntity instanceof Pig) {
                pig((Pig) livingEntity);
            } else if (livingEntity instanceof Rabbit) {
                rabbit((Rabbit) livingEntity);
            } else if (livingEntity instanceof Villager) {
                villager((Villager) livingEntity);
            } else if (livingEntity instanceof Tameable) {
                tameable((Tameable) livingEntity);
                
                if (livingEntity instanceof AbstractHorse) {
                    abstractHorse((AbstractHorse) livingEntity);
                    
                    if (livingEntity instanceof Horse) {
                        horse((Horse) livingEntity);
                    } else if (livingEntity instanceof ChestedHorse) {
                        chestedHorse((ChestedHorse) livingEntity);
                        
                        if (livingEntity instanceof Llama) {
                            llama((Llama) livingEntity);
                        }
                    }
                } else if (livingEntity instanceof Sittable) {
                    sittable((Sittable) livingEntity);
                    
                    if (livingEntity instanceof Wolf) {
                        wolf((Wolf) livingEntity);
                    } else if (livingEntity instanceof Ocelot) {
                        ocelot((Ocelot) livingEntity);
                    } else if (livingEntity instanceof Parrot) {
                        parrot((Parrot) livingEntity);
                    }
                }
            }
        } else if (livingEntity instanceof Slime) {
            slime((Slime) livingEntity);
        } else if (livingEntity instanceof Creeper) {
            creeper((Creeper) livingEntity);
        } else if (livingEntity instanceof Zombie) {
            zombie((Zombie) livingEntity);
            
            if (livingEntity instanceof PigZombie) {
                pigZombie((PigZombie) livingEntity);
            } else if (livingEntity instanceof ZombieVillager) {
                zombieVillager((ZombieVillager) livingEntity);
            }
        } else if (livingEntity instanceof Spellcaster) {
            spellCaster((Spellcaster) livingEntity);
        } else if (livingEntity instanceof IronGolem) {
            ironGolem((IronGolem) livingEntity);
        } else if (livingEntity instanceof Snowman) {
            snowman((Snowman) livingEntity);
        }
    }
    
    // Entity specific methods
    private void livingEntity(LivingEntity livingEntity) {
        livingEntity.setCustomName(attributeMap.get("Custom Name"));
        livingEntity.setHealth(Double.parseDouble(attributeMap.get("Health")));
        AttributeInstance attrInst;
        
        for (Attribute attribute : Attribute.values()) {
            attrInst = livingEntity.getAttribute(attribute);
            
            if (attrInst != null) {
                String attrName = attribute.toString();
                String[] components = attrName.split("_");
                String label = "";
                
                // Skip the first
                for (int i = 1; i < components.length; i++) {
                    label += StringUtils.capitalize(components[i].toLowerCase()) + " ";
                }
                
                double value = Double.parseDouble(attributeMap.get(label.trim()));
                attrInst.setBaseValue(value);
            }
        }
    }
    
    private void ageable(Ageable ageable) {
        ageable.setAge(Integer.parseInt(attributeMap.get("Age (Ticks)")));
    }
    
    private void sheep(Sheep sheep) {
        sheep.setColor(DyeColor.valueOf(attributeMap.get("Color")));
    }
    
    private void pig(Pig pig) {
        pig.setSaddle(Boolean.parseBoolean(attributeMap.get("Saddle")));
    }
    
    private void rabbit(Rabbit rabbit) {
        rabbit.setRabbitType(Rabbit.Type.valueOf(attributeMap.get("Type")));
    }
    
    private void villager(Villager villager) {
        // For some reason, villagers inherit their mob type as a name, despite
        // having no name set. Furthermore, the original call of this seems on
        // the LivingEntity seems to have no effect, so we must do so to the
        // Villager interface here. Perhaps Villager overrides the LivingEntity
        // interface's method and we just don't know this?
        villager.setCustomName(attributeMap.get("Custom Name"));
        villager.setProfession(Profession.valueOf(attributeMap.get("Profession")));
        villager.setRiches(Integer.parseInt(attributeMap.get("Riches")));
        ArrayList<MerchantRecipe> merchantRecipes = new ArrayList<MerchantRecipe>();
        
        for (int i = 1; i < attributeMap.size(); i++) {
            if (attributeMap.containsKey("Recipe" + i)) {
                merchantRecipes.add(MerchantRecipeConverter.fromString(attributeMap.get("Recipe" + i)));
            } else {
                break;
            }
        }
        
        villager.setRecipes(merchantRecipes);
    }
    
    private void tameable(Tameable tameable) {
        if (!attributeMap.get("Owner").equals("None")) {
            tameable.setOwner(Bukkit.getPlayer(UUID.fromString(attributeMap.get("Owner"))));
            tameable.setTamed(true);
        }
    }
    
    private void abstractHorse(AbstractHorse abstractHorse) {
        
    }
    
    private void horse(Horse horse) {
        if (attributeMap.get("Armor").equals("Iron")) {
            horse.getInventory().setArmor(new ItemStack(Material.IRON_BARDING, 1));
        } else if (attributeMap.get("Armor").equals("Gold")) {
            horse.getInventory().setArmor(new ItemStack(Material.GOLD_BARDING, 1));
        } else if (attributeMap.get("Armor").equals("Diamond")) {
            horse.getInventory().setArmor(new ItemStack(Material.DIAMOND_BARDING, 1));
        }
        
        if (attributeMap.get("Saddle").equals("Yes")) {
            horse.getInventory().setSaddle(new ItemStack(Material.SADDLE, 1));
        }
        
        horse.setColor(Horse.Color.valueOf(attributeMap.get("Color")));
        horse.setStyle(Horse.Style.valueOf(attributeMap.get("Style")));
    }
    
    private void chestedHorse(ChestedHorse chestedHorse) {
        if (Boolean.parseBoolean(attributeMap.get("Carrying Chest"))) {
            chestedHorse.setCarryingChest(true);
        } else {
            chestedHorse.setCarryingChest(false);
        }
    }
    
    private void llama(Llama llama) {
        llama.setColor(Color.valueOf(attributeMap.get("Color")));
        llama.setStrength(Integer.parseInt(attributeMap.get("Strength")));
        
        if (attributeMap.containsKey("Decor")) {
            llama.getInventory().setDecor(ItemStackConverter.fromString(attributeMap.get("Decor")));
        }
    }
    
    private void sittable(Sittable sittable) {
        if (attributeMap.get("Sitting").equals("Yes")) {
            sittable.setSitting(true);
        } else {
            sittable.setSitting(false);
        }
    }
    
    private void wolf(Wolf wolf) {
        if (attributeMap.get("Angry").equals("Yes")) {
            wolf.setAngry(true);
        } else {
            wolf.setAngry(false);
        }
        
        if (attributeMap.containsKey("Collar")) {
            wolf.setCollarColor(DyeColor.valueOf(attributeMap.get("Collar")));
        }
    }
    
    private void ocelot(Ocelot ocelot) {
        ocelot.setCatType(Ocelot.Type.valueOf(attributeMap.get("Type")));
    }
    
    private void parrot(Parrot parrot) {
        parrot.setVariant(Variant.valueOf(attributeMap.get("Variant")));
        
    }
    
    private void slime(Slime slime) {
        slime.setSize(Integer.parseInt(attributeMap.get("Size")));
    }
    
    private void creeper(Creeper creeper) {
        if (attributeMap.get("Charged").equals("Yes")) {
            creeper.setPowered(true);
        } else {
            creeper.setPowered(false);
        }
    }
    
    private void zombie(Zombie zombie) {
        if (attributeMap.get("Baby").equals("Yes")) {
            zombie.setBaby(true);
        } else {
            zombie.setBaby(false);
        }
    }
    
    private void pigZombie(PigZombie pigZombie) {
        pigZombie.setAnger(Integer.parseInt(attributeMap.get("Anger Level")));
    }
    
    private void zombieVillager(ZombieVillager zombieVillager) {
        zombieVillager.setVillagerProfession(Profession.valueOf(attributeMap.get("Profession")));
    }
    
    private void spellCaster(Spellcaster spellCaster) {
        spellCaster.setSpell(Spell.valueOf(attributeMap.get("Active Spell")));
    }
    
    private void ironGolem(IronGolem ironGolem) {
        String playerCreatedStr = attributeMap.get("Player Created");
        
        if (playerCreatedStr.equals("Yes")) {
            ironGolem.setPlayerCreated(true);
        } else {
            ironGolem.setPlayerCreated(false);
        }
    }
    
    private void snowman(Snowman snowman) {
        String derpStr = attributeMap.get("Derp");
        
        if (derpStr.equals("Yes")) {
            snowman.setDerp(true);
        } else {
            snowman.setDerp(false);
        }
    }
}
