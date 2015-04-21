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

import io.github.redpanda4552.SimpleEgg.command.CSimpleEgg;
import io.github.redpanda4552.SimpleEgg.event.EventListener;
import io.github.redpanda4552.SimpleEgg.util.EggTracker;

import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class SimpleEggMain extends JavaPlugin
{

	public Logger log;
	
	public CaptureManager captureManager;
	public EggTracker eggTracker;
	
	public Material consumedMaterial;
	public String consumedMaterialName;
	public int consumedMaterialAmount;
	
	public void onEnable()
	{
		log = getLogger();
		saveDefaultConfig();
		
		try
		{
			consumedMaterial = Material.valueOf(getConfig().getString("consumed-item"));
		}
		catch (IllegalArgumentException e)
		{
			log.warning("Config entry 'consumed-item' is not a valid Bukkit Material type! Defaulting to Diamonds.");
			consumedMaterial = Material.DIAMOND;
		}
		
		try
		{
			Integer.parseInt(getConfig().getString("consumed-item-amount"));
			consumedMaterialAmount = getConfig().getInt("consumed-item-amount");
		}
		catch (NumberFormatException e)
		{
			log.warning("Config entry 'consumed-item-amount' is not an integer! Defaulting to 5.");
		}
		
		consumedMaterialName = getConfig().getString("consumed-item-name");
		
		captureManager = new CaptureManager(this);
		eggTracker = new EggTracker();
		getServer().getPluginManager().registerEvents(new EventListener(this), this);
		getCommand("simpleegg").setExecutor(new CSimpleEgg(this));
	}
	
	public void onDisable() //As of now, nothing to do at disable, really.
	{
		
	}
	
	/**
	 * Send a message to a player with color and tag.
	 * @param player - The player to send a message to.
	 * @param message - The message to send.
	 */
	public void send(Player player, String message)
	{
		player.sendMessage(ChatColor.RED + "[SimpleEgg] " + ChatColor.LIGHT_PURPLE + message);
	}
}
