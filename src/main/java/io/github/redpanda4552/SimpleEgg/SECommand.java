package io.github.redpanda4552.SimpleEgg;

import org.bukkit.command.CommandExecutor;

/**
 * This class exists only because otherwise there are issues with "plugin" being null
 * and by some weirdness that I do not yet understand, this works.
 */
public abstract class SECommand implements CommandExecutor {

	protected SimpleEggMain plugin;
	
	public SECommand(SimpleEggMain plugin) {
		this.plugin = plugin;
	}
}
