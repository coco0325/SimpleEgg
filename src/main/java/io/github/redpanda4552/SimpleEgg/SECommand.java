package io.github.redpanda4552.SimpleEgg;

import org.bukkit.command.CommandExecutor;

public abstract class SECommand implements CommandExecutor {

	protected SimpleEggMain plugin;
	
	public SECommand(SimpleEggMain plugin) {
		this.plugin = plugin;
	}
}
