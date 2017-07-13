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

import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;

/**
 * A compatibility layer to be used before the LoreExtractor on old eggs.
 * Any harsh changes to how mob data is stored in eggs will be corrected by this
 * prior to full application by the LoreExtractor.
 * 
 * In other words, when a SimpleEgg is from a previous plugin version, this
 * should be run before the LoreExtractor to ensure there are no data continuity
 * problems.
 */
public class EggUpdater {
    
    private ArrayList<String> newLore;

    /**
     * Run through an old egg's lore and re-map its values to a current egg's
     * lore. Result is retrievable with
     * {@link #getNewLore() EggUpdater.getNewLore()}.
     * @param oldLore - The old lore to re-map.
     * @param livingEntity - The LivingEntity produced by the spawn egg
     */
    public EggUpdater(ArrayList<String> oldLore, LivingEntity livingEntity) {
        newLore = new ArrayList<String>();
        HashMap<String, String> oldAttributeMap = new HashMap<String, String>();
        HashMap<String, String> newAttributeMap = new HashMap<String, String>();
        
        // Populate oldAttributeMap
        for (String string : oldLore) {
            String[] components = ChatColor.stripColor(string).split(": ");
            
            if (components.length == 2) {
                oldAttributeMap.put(components[0], components[1]);
            }
        }
        
        // Populate newAttributeMap with defaults
        ArrayList<String> defaultLore = new LorePacker(livingEntity).getLore();
        
        for (String string : defaultLore) {
            String[] components = ChatColor.stripColor(string).split(": ");
            
            if (components.length == 2) {
                newAttributeMap.put(components[0], components[1]);
            }
        }
        
        
        // Manual corrections for fields that have changed. As severe changes to
        // data storage are made, we will need to add if statements per version
        // with changes. Perhaps not the most robust, but for now it will work.
        
        // Changes made after v0.2.0; this version didn't have the identifier
        if (!oldLore.get(0).startsWith("Identifier")) {
            // Get health and max health from single line
            String healthLine = oldAttributeMap.get("Health");
            oldAttributeMap.remove("Health");
            
            if (healthLine != null) {
                String[] healthComponents = ChatColor.stripColor(healthLine).split("/");
                
                if (healthComponents.length == 2) {
                    try {
                        double maxHealth = Double.parseDouble(healthComponents[1]);
                        
                        if (livingEntity.getHealth() > maxHealth) {
                            livingEntity.setHealth(maxHealth);
                        }
                        
                        newAttributeMap.put("Max Health", healthComponents[1]);
                        newAttributeMap.put("Health", healthComponents[0]);
                    } catch (NumberFormatException e) {}
                }
            }
            
            // Movement Speed was stored as Speed
            String speedStr = oldAttributeMap.get("Speed");
            oldAttributeMap.remove("Speed");
            
            if (speedStr != null) {
                try {
                    Double.parseDouble(speedStr);
                    newAttributeMap.put("Movement Speed", speedStr);
                } catch (NumberFormatException e) {}
            }
            
            // Jump Strength was stored as Jump Power
            String jumpStr = oldAttributeMap.get("Jump Power");
            oldAttributeMap.remove("Jump Power");
            
            if (jumpStr != null) {
                try {
                    Double.parseDouble(jumpStr);
                    newAttributeMap.put("Jump Strength", jumpStr);
                } catch (NumberFormatException e) {}
            }
            
            // Horse Armor was stored as Armor (this is safe, these eggs don't
            // yet have the Spigot defined Armor attribute)
            String armorStr = oldAttributeMap.get("Armor");
            oldAttributeMap.remove("Armor");
            
            if (armorStr != null) {
                switch (armorStr) {
                    case "Iron":
                    case "Gold":
                    case "Diamond":
                        // No breaks above because we want them all to fall to this
                        newAttributeMap.put("Horse Armor", armorStr);
                    default:
                        break;
                }
            }
        }
        
        for (String key : oldAttributeMap.keySet()) {
            if (newAttributeMap.containsKey(key)) {
                newAttributeMap.put(key, oldAttributeMap.get(key));
            }
        }
        
        for (String key : newAttributeMap.keySet()) {
            newLore.add(Text.a + key + ": " + Text.b + newAttributeMap.get(key));
        }
    }
    
    public ArrayList<String> getNewLore() {
        return newLore;
    }
}
