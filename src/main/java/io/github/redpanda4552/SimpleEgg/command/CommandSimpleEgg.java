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

    private boolean eggRefund;
    private String costStr;
    
    public CommandSimpleEgg(Main plugin, boolean eggRefund, String name, double amount) {
        super(plugin);
        this.eggRefund = eggRefund;
        costStr = String.format("%s%s%s%s", Text.a, amount, name != null && !name.isEmpty() ? " " : "", name);
    }
    
    public CommandSimpleEgg(Main plugin, boolean eggRefund, String name, int amount) {
        super(plugin);
        this.eggRefund = eggRefund;
        costStr = String.format("%s%s %s", Text.a, amount, name);
    }
    
    private String[] getHelpDialogue() {
        return new String[] {
            String.format("%s==SimpleEgg v%s by pandubz ==", Text.a, plugin.getDescription().getVersion()),
            String.format("%sThe alias for %s/simpleegg %sis %s/se %s.", Text.b, Text.a, Text.b, Text.a, Text.b),
            String.format("%sEgg Refunding %son failed captures is %s%s%s.", Text.a, Text.b, Text.a, eggRefund == true ? "Enabled" : "Disabled", Text.b),
            String.format("%sTo capture a mob, throw an %sEgg %sat it.", Text.b, Text.a, Text.b),
            String.format("%sEach capture will cost %s%s%s.", Text.b, Text.a, costStr, Text.b)
        };
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("reload")) {
                plugin.reload();
            }
        }
        
        sender.sendMessage(getHelpDialogue());
        return true;
    }
}
