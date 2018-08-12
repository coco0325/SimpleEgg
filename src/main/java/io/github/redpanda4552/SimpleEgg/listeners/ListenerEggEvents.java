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
package io.github.redpanda4552.SimpleEgg.listeners;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Egg;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerEggThrowEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import io.github.redpanda4552.SimpleEgg.CaptureManager;
import io.github.redpanda4552.SimpleEgg.EggTracker;
import io.github.redpanda4552.SimpleEgg.EggTrackerEntry;
import io.github.redpanda4552.SimpleEgg.ExpenseHandler;
import io.github.redpanda4552.SimpleEgg.Main;
import io.github.redpanda4552.SimpleEgg.util.EggUpdater;
import io.github.redpanda4552.SimpleEgg.util.LoreExtractor;
import io.github.redpanda4552.SimpleEgg.util.Text;

public class ListenerEggEvents extends AbstractListener {
    
    private EggTracker eggTracker;
    private ExpenseHandler expenseHandler;
    private CaptureManager captureManager;
    
    public ListenerEggEvents(Main plugin) {
        super(plugin);
        eggTracker = plugin.getEggTracker();
        expenseHandler = plugin.getExpenseHandler();
        captureManager = plugin.getCaptureManager();
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
                if (expenseHandler.hasMaterials(entry.getPlayer())) {
                    captureManager.makeSpawnEgg(entry);
                } else {
                    entry.getPlayer().sendMessage(Text.tag + "You need " + Text.a + expenseHandler.requiredMaterials() + Text.b + " to capture a mob.");
                    refundEgg(entry.getPlayer());
                }
            } else {
                entry.getPlayer().sendMessage(Text.tag + "You do not own this mob.");
                refundEgg(entry.getPlayer());
            }
        } else {
            entry.getPlayer().sendMessage(Text.tag + "You do not have permission to capture this mob type.");
            refundEgg(entry.getPlayer());
        }
        
        eggTracker.removeEntry(entry);
    }
    
    @EventHandler
    public void eggUse(PlayerInteractEvent event) {
        ItemStack stack = event.getItem();
        
        if (stack == null)
            return;
        
        ItemMeta meta = stack.getItemMeta();
        
        if (meta == null)
            return;

        if (event.getItem() != null && event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            // Don't cancel the event and process SimpleEgg behaviors, if the
            // player is trying to insert this egg into a spawner.
            if (event.getClickedBlock().getType() == Material.SPAWNER)
                return;
            
            ArrayList<String> lore = (ArrayList<String>) meta.getLore();
            
            // Use a more specific identifier line, instead of the health line
            if (isVersionCurrent(meta)) {
                event.setCancelled(true);
                LivingEntity livingEntity = (LivingEntity) event.getPlayer().getWorld().spawnEntity(new Location(event.getPlayer().getWorld(), event.getClickedBlock().getX(), event.getClickedBlock().getY() + 1, event.getClickedBlock().getZ()), EntityType.valueOf(stack.getType().toString().replace("_SPAWN_EGG", "")));
                consumeSimpleEgg(stack);
                
                // The legacy code for the display name is arbitrary. New eggs
                // will have a custom name field in the lore body, if present,
                // and the display name is now just for show and not saved data.
                new LoreExtractor(lore, livingEntity);
            } else if (isSimpleEgg(meta)) {
                event.setCancelled(true);
                LivingEntity livingEntity = (LivingEntity) event.getPlayer().getWorld().spawnEntity(new Location(event.getPlayer().getWorld(), event.getClickedBlock().getX(), event.getClickedBlock().getY() + 1, event.getClickedBlock().getZ()), EntityType.valueOf(stack.getType().toString().replace("_SPAWN_EGG", "")));
                consumeSimpleEgg(stack);
                
                ArrayList<String> updatedLore = new EggUpdater(lore, livingEntity).getNewLore();
                new LoreExtractor(updatedLore, livingEntity);
            } else if (meta.hasLore() && lore.get(0).startsWith("Health: ")) {
                event.setCancelled(true);
                LivingEntity livingEntity = (LivingEntity) event.getPlayer().getWorld().spawnEntity(new Location(event.getPlayer().getWorld(), event.getClickedBlock().getX(), event.getClickedBlock().getY() + 1, event.getClickedBlock().getZ()), EntityType.valueOf(stack.getType().toString().replace("_SPAWN_EGG", "")));
                consumeSimpleEgg(stack);
                
                // If the ItemStack name contains ": ", that means the mob has a custom name.
                // Below we will use ": " as a delimeter to split and remove the preceding mob type,
                // as well as the ": ", isolating the actual name.
                if (meta.getDisplayName().contains(": ")) {
                    String customName = meta.getDisplayName();
                    customName = customName.replaceFirst(meta.getDisplayName().split(": ")[0], "");
                    customName = customName.replaceFirst(": ", "");
                    livingEntity.setCustomName(customName);
                }
                
                ArrayList<String> updatedLore = new EggUpdater(lore, livingEntity).getNewLore();
                new LoreExtractor(updatedLore, livingEntity);
            }
        }
    }
    
    @EventHandler
    public void eggUseOnEntity(PlayerInteractEntityEvent event) {
        ItemStack stack = event.getPlayer().getInventory().getItemInMainHand();
        ItemMeta meta = stack.getItemMeta();
        
        if (isSimpleEgg(meta)) {
            event.getPlayer().sendMessage(Text.tag + "You cannot use a SimpleEgg to make babies out of other adult mobs.");
            event.setCancelled(true);
        }
    }
    
    private void refundEgg(Player player) {
        if (plugin.getConfig().getBoolean("egg-refund") && player.getGameMode() != GameMode.CREATIVE) {
            player.getWorld().dropItem(player.getLocation(), new ItemStack(Material.EGG, 1));
        }
    }
    
    private void consumeSimpleEgg(ItemStack stack) {
        stack.setAmount(stack.getAmount() - 1);
    }
    
    /**
     * Check if an egg's identifier line exists, regardless of version.
     * @return True if it has an identifier of any version, false otherwise.
     */
    private boolean isSimpleEgg(ItemMeta meta) {
        return meta != null && meta.hasLore() && ChatColor.stripColor(meta.getLore().get(0)).startsWith("Identifier: SimpleEgg.");
    }
    
    /**
     * Check if an egg's identifier line exists and the version on it matches
     * the current plugin version.
     * @return True if the egg is current, false otherwise.
     */
    private boolean isVersionCurrent(ItemMeta meta) {
        if (isSimpleEgg(meta)) {
            if (ChatColor.stripColor(meta.getLore().get(0)).endsWith(Main.getSelf().getDescription().getVersion())) {
                return true;
            }
        }
        
        return false;
    }
}
