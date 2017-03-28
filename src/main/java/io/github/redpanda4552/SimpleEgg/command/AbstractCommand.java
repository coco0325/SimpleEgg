package io.github.redpanda4552.SimpleEgg.command;

import org.bukkit.command.CommandExecutor;

import io.github.redpanda4552.SimpleEgg.Main;

/**
 * This class exists only because otherwise there are issues with "plugin" being null
 * and by some weirdness that I do not yet understand, this works.
 */
public abstract class AbstractCommand implements CommandExecutor {

	protected Main plugin;
	
	public AbstractCommand(Main plugin) {
		this.plugin = plugin;
	}
}
