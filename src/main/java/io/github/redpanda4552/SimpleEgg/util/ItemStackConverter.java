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
