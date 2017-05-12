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

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ItemStackConverter {
    
    @SuppressWarnings("deprecation")
    public static String toString(ItemStack itemStack) {
        String ret = new String(itemStack.getType().toString());
        ret += "-" + itemStack.getAmount() + "-" + itemStack.getDurability() + "-" + itemStack.getData().getData();
        return ret;
    }
    
    @SuppressWarnings("deprecation")
    public static ItemStack fromString(String string) {
        ItemStack ret = null;
        String[] itemStackParts = string.split("-");
        
        try {
            Material resultMaterial = Material.valueOf(itemStackParts[0]);
            int resultAmount = Integer.parseInt(itemStackParts[1]);
            short durability = Short.parseShort(itemStackParts[2]);
            byte data = Byte.parseByte(itemStackParts[3]);
            ret = new ItemStack(resultMaterial, resultAmount, durability, data);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Provided string cannot be converted to an ItemStack!", e);
        }
        
        return ret;
    }
}
