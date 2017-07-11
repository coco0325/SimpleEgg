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

import org.apache.commons.lang.StringUtils;
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
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Parrot;
import org.bukkit.entity.Pig;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Rabbit;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Sittable;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Snowman;
import org.bukkit.entity.Spellcaster;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;
import org.bukkit.entity.ZombieVillager;
import org.bukkit.inventory.MerchantRecipe;

import io.github.redpanda4552.SimpleEgg.Main;

public class LorePacker {

    private ArrayList<String> lore;
    
    /**
     * Assembles an ArrayList of the properties for the specified Entity that
     * is to be used for a spawn egg. All instanceof checks are done internally
     * by the LorePackager, so no type checking is required prior to calling
     * this method. Null Entities will throw an IllegalArgumentException. <b>The
     * actual ArrayList is returned by {@link #getLore() LorePacker.getLore()}.
     * </b>
     * @param livingEntity - The Entity to assemble a lore for.
     * @return An ArrayList of Strings
     * @throws IllegalArgumentException If entity parameter is null
     */
    public LorePacker(LivingEntity livingEntity) throws IllegalArgumentException {
        if (livingEntity == null) {
            throw new IllegalArgumentException("Can't assemble lore for a null entity!");
        }
        
        lore = new ArrayList<String>();
        // This needs to always be on top of an egg's lore
        lore.add("Identifier: SimpleEgg." + livingEntity.getType().getEntityClass().getSimpleName() + "." + Main.getSelf().getDescription().getVersion());
        lore.addAll(livingEntity(livingEntity));
        
        if (livingEntity instanceof Ageable) {
            lore.addAll(ageable((Ageable) livingEntity));
            
            if (livingEntity instanceof Sheep) {
                lore.addAll(sheep((Sheep) livingEntity));
            } else if (livingEntity instanceof Pig) {
                lore.addAll(pig((Pig) livingEntity));
            } else if (livingEntity instanceof Rabbit) {
                lore.addAll(rabbit((Rabbit) livingEntity));
            } else if (livingEntity instanceof Villager) {
                lore.addAll(villager((Villager) livingEntity));
            } else if (livingEntity instanceof Tameable) {
                lore.addAll(tameable((Tameable) livingEntity));
                
                if (livingEntity instanceof AbstractHorse) {
                    lore.addAll(abstractHorse((AbstractHorse) livingEntity));
                    
                    if (livingEntity instanceof Horse) {
                        lore.addAll(horse((Horse) livingEntity));
                    } else if (livingEntity instanceof ChestedHorse) {
                        lore.addAll(chestedHorse((ChestedHorse) livingEntity));
                        
                        if (livingEntity instanceof Llama) {
                            lore.addAll(llama((Llama) livingEntity));
                        }
                    }
                } else if (livingEntity instanceof Sittable) {
                    lore.addAll(sittable((Sittable) livingEntity));
                    
                    if (livingEntity instanceof Wolf) {
                        lore.addAll(wolf((Wolf) livingEntity));
                    } else if (livingEntity instanceof Ocelot) {
                        lore.addAll(ocelot((Ocelot) livingEntity));
                    } else if (livingEntity instanceof Parrot) {
                        lore.addAll(parrot((Parrot) livingEntity));
                    }
                }
            }
        } else if (livingEntity instanceof Slime) {
            lore.addAll(slime((Slime) livingEntity));
        } else if (livingEntity instanceof Creeper) {
            lore.addAll(creeper((Creeper) livingEntity));
        } else if (livingEntity instanceof Zombie) {
            lore.addAll(zombie((Zombie) livingEntity));
            
            if (livingEntity instanceof PigZombie) {
                lore.addAll(pigZombie((PigZombie) livingEntity));
            } else if (livingEntity instanceof ZombieVillager) {
                lore.addAll(zombieVillager((ZombieVillager) livingEntity));
            }
        } else if (livingEntity instanceof Spellcaster) {
            lore.addAll(spellCaster((Spellcaster) livingEntity));
        } else if (livingEntity instanceof IronGolem) {
            lore.addAll(ironGolem((IronGolem) livingEntity));
        } else if (livingEntity instanceof Snowman) {
            lore.addAll(snowman((Snowman) livingEntity));
        }
    }
    
    public ArrayList<String> getLore() {
        for (int i = 0; i < lore.size(); i++) {
            String[] halves = lore.get(i).split(":");
            lore.set(i, String.format("%s%s:%s%s", Text.a, halves[0], Text.b, halves[1]));
        }
        
        return lore;
    }
    
    // Entity specific methods
    private ArrayList<String> livingEntity(LivingEntity livingEntity) {
        ArrayList<String> ret = new ArrayList<String>();
        
        if (livingEntity.getCustomName() != null) {
            ret.add("Custom Name: " + livingEntity.getCustomName());
        }
        
        ret.add("Health: " + livingEntity.getHealth());
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
                
                ret.add(label.trim() + ": " + attrInst.getBaseValue());
            }
        }
        
        return ret;
    }
    
    private ArrayList<String> ageable(Ageable ageable) {
        ArrayList<String> ret = new ArrayList<String>();
        ret.add("Age (Ticks): " + ageable.getAge());
        return ret;
    }
    
    private ArrayList<String> sheep(Sheep sheep) {
        ArrayList<String> ret = new ArrayList<String>();
        ret.add("Color: " + sheep.getColor());
        return ret;
    }
    
    private ArrayList<String> pig(Pig pig) {
        ArrayList<String> ret = new ArrayList<String>();
        ret.add("Saddle: " + pig.hasSaddle());
        return ret;
    }
    
    private ArrayList<String> rabbit(Rabbit rabbit) {
        ArrayList<String> ret = new ArrayList<String>();
        ret.add("Type: " + rabbit.getRabbitType().toString());
        return ret;
    }
    
    private ArrayList<String> villager(Villager villager) {
        ArrayList<String> ret = new ArrayList<String>();
        ret.add("Profession: " + villager.getProfession().toString());
        ret.add("Riches: " + villager.getRiches());
        int i = 1;
        
        for (MerchantRecipe merchantRecipe : villager.getRecipes()) {
            ret.add("Recipe" + i + ": " + MerchantRecipeConverter.toString(merchantRecipe));
            i++;
        }
        
        return ret;
    }
    
    private ArrayList<String> tameable(Tameable tameable) {
        ArrayList<String> ret = new ArrayList<String>();
        
        if (tameable.getOwner() != null) {
            ret.add("Owner: " + tameable.getOwner().getUniqueId().toString());
        } else {
            ret.add("Owner: None");
        }
        
        return ret;
    }
    
    private ArrayList<String> abstractHorse(AbstractHorse abstractHorse) {
        ArrayList<String> ret = new ArrayList<String>();
        return ret;
    }
    
    private ArrayList<String> horse(Horse horse) {
        ArrayList<String> ret = new ArrayList<String>();
        
        if (horse.getInventory().getArmor() == null) {
            ret.add("Armor: None");
        } else if (horse.getInventory().getArmor().getType() == Material.IRON_BARDING) {
            ret.add("Armor: Iron");
        } else if (horse.getInventory().getArmor().getType() == Material.GOLD_BARDING) {
            ret.add("Armor: Gold");
        } else if (horse.getInventory().getArmor().getType() == Material.DIAMOND_BARDING) {
            ret.add("Armor: Diamond");
        }
        
        if (horse.getInventory().getSaddle() != null) {
            ret.add("Saddle: Yes");
        } else {
            ret.add("Saddle: No");
        }
        
        ret.add("Color: " + horse.getColor().toString());
        ret.add("Style: " + horse.getStyle().toString());
        return ret;
    }
    
    private ArrayList<String> chestedHorse(ChestedHorse chestedHorse) {
        ArrayList<String> ret = new ArrayList<String>();
        ret.add("Carrying Chest: " + chestedHorse.isCarryingChest());
        return ret;
    }
    
    private ArrayList<String> llama(Llama llama) {
        ArrayList<String> ret = new ArrayList<String>();
        ret.add("Color: " + llama.getColor().toString());
        ret.add("Strength: " + llama.getStrength());
        
        if (llama.getInventory().getDecor() != null) {
            ret.add("Decor: " + ItemStackConverter.toString(llama.getInventory().getDecor()));
        }
        
        return ret;
    }
    
    private ArrayList<String> sittable(Sittable sittable) {
        ArrayList<String> ret = new ArrayList<String>();
        
        if (sittable.isSitting()) {
            ret.add("Sitting: Yes");
        } else {
            ret.add("Sitting: No");
        }
        
        return ret;
    }
    
    private ArrayList<String> wolf(Wolf wolf) {
        ArrayList<String> ret = new ArrayList<String>();

        if (wolf.isAngry()) {
            ret.add("Angry: Yes");
        } else {
            ret.add("Angry: No");
        }
        
        if (wolf.isTamed()) {
            ret.add("Collar: " + wolf.getCollarColor().toString());
        }
        
        return ret;
    }
    
    private ArrayList<String> ocelot(Ocelot ocelot) {
        ArrayList<String> ret = new ArrayList<String>();
        ret.add("Type: " + ocelot.getCatType());
        return ret;
    }
    
    private ArrayList<String> parrot(Parrot parrot) {
        ArrayList<String> ret = new ArrayList<String>();
        ret.add("Variant: " + parrot.getVariant().toString());
        return ret;
    }
    
    private ArrayList<String> slime(Slime slime) {
        ArrayList<String> ret = new ArrayList<String>();
        ret.add("Size: " + slime.getSize());
        return ret;
    }
    
    private ArrayList<String> creeper(Creeper creeper) {
        ArrayList<String> ret = new ArrayList<String>();
        
        if (creeper.isPowered()) {
            ret.add("Charged: Yes");
        } else {
            ret.add("Charged: No");
        }
        
        return ret;
    }
    
    private ArrayList<String> zombie(Zombie zombie) {
        ArrayList<String> ret = new ArrayList<String>();
        
        if (zombie.isBaby()) {
            ret.add("Baby: Yes");
        } else {
            ret.add("Baby: No");
        }
        
        return ret;
    }
    
    private ArrayList<String> pigZombie(PigZombie pigZombie) {
        ArrayList<String> ret = new ArrayList<String>();
        ret.add("Anger Level: " + pigZombie.getAnger());
        return ret;
    }
    
    private ArrayList<String> zombieVillager(ZombieVillager zombieVillager) {
        ArrayList<String> ret = new ArrayList<String>();
        ret.add("Profession: " + zombieVillager.getVillagerProfession().toString());
        return ret;
    }
    
    private ArrayList<String> spellCaster(Spellcaster spellCaster) {
        ArrayList<String> ret = new ArrayList<String>();
        ret.add("Active Spell: " + spellCaster.getSpell().toString());
        return ret;
    }
    
    private ArrayList<String> ironGolem(IronGolem ironGolem) {
        ArrayList<String> ret = new ArrayList<String>();

        if (ironGolem.isPlayerCreated()) {
            ret.add("Player Created: Yes");
        } else {
            ret.add("Player Created: No");
        }
        
        return ret;
    }
    
    private ArrayList<String> snowman(Snowman snowman) {
        ArrayList<String> ret = new ArrayList<String>();
        
        // Is always returning true, maybe Spigot doesn't implement to API spec?
        if (snowman.isDerp()) {
            ret.add("Derp: Yes");
        } else {
            ret.add("Derp: No");
        }
        
        return ret;
    }
}
