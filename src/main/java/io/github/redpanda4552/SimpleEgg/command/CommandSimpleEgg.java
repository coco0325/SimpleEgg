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
package io.github.redpanda4552.SimpleEgg.command;

import io.github.redpanda4552.SimpleEgg.Main;
import io.github.redpanda4552.SimpleEgg.util.Text;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class CommandSimpleEgg extends AbstractCommand {

	public CommandSimpleEgg(Main plugin) {
		super(plugin);
	}
	
	// Note to future self. These aren't static fields, you can't make these
	// Strings final. You almost did it once. Don't try again.
	private String[] help = {
		Text.a + "== SimpleEgg v" + plugin.getDescription().getVersion() + " by pandubz ==",
		Text.b + "The alias for " + Text.a + "/simpleegg" + Text.b + " is " + Text.a + "/se" + Text.b + ".",
        Text.a + "Egg Refunding " + Text.b + "on failed captures is " + Text.a + (plugin.getConfig().getBoolean("egg-refund") == true ? "Enabled" : "Disabled") + Text.b + ".",
        Text.b + "To capture a mob, throw an " + Text.a + "Egg" + Text.b + " at it. You will need to have " + Text.a + plugin.consumedMaterialAmount + " " + plugin.consumedMaterialName +
        Text.b + " in your inventory. The spawn egg will be dropped where the mob was, and the items will be removed from your inventory."
	};
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
	    sender.sendMessage(help);
		return true;
	}
}
