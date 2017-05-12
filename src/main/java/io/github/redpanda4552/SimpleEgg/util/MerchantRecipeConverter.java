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

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;

public class MerchantRecipeConverter {

    /**
     * Returns a String representation of a MerchantRecipe. Spaces are used as
     * delimeters between attributes. Utilizes
     * {@link ItemStackConverter ItemStackConverter} for the ItemStacks.
     * </b>
     * @param merchantRecipe - The MerchantRecipe to stringify.
     * @return A String that can be stored and passed in to
     * {@link #fromString(String) fromString(String)} later.
     * @throws IllegalArgumentException If MerchantRecipe is null.
     */
    public static String toString(MerchantRecipe merchantRecipe) throws IllegalArgumentException {
        if (merchantRecipe == null) {
            throw new IllegalArgumentException("Cannot stringify a null MerchantRecipe!");
        }
        
        String ret = new String();
        ret += ItemStackConverter.toString(merchantRecipe.getResult()) + " ";
        
        for (ItemStack itemStack : merchantRecipe.getIngredients()) {
            ret += ItemStackConverter.toString(itemStack) + ",";
        }
        
        ret += " " + merchantRecipe.getUses();
        ret += " " + merchantRecipe.getMaxUses();
        ret += " " + merchantRecipe.hasExperienceReward();
        return ret;
    }
    
    /**
     * Takes in a String produced by
     * {@link #toString(MerchantRecipe) toString(MerchantRecipe)} and converts
     * it to a MerchantRecipe.
     * @param string - The String to convert.
     * @return A MerchantRecipe with the attributes specified in the String.
     * @throws IllegalArgumentException If the String has an unexpected number
     * of attributes.
     */
    public static MerchantRecipe fromString(String string) throws IllegalArgumentException {
        String[] attributes = string.split(" ");
        
        if (attributes.length != 5) {
            throw new IllegalArgumentException("Input string has an unexpected number of attributes!");
        }
        
        String resultString = attributes[0], ingredientString = attributes[1], usesString = attributes[2], maxUsesString = attributes[3], experienceString = attributes[4];
        
        ItemStack result = itemStackListFromString(resultString).get(0);
        ArrayList<ItemStack> ingredients = itemStackListFromString(ingredientString);
        int uses = Integer.parseInt(usesString);
        int maxUses = Integer.parseInt(maxUsesString);
        boolean experience = Boolean.parseBoolean(experienceString);
        MerchantRecipe ret = new MerchantRecipe(result, uses, maxUses, experience);
        ret.setIngredients(ingredients);
        return ret;
    }
    
    /**
     * Takes in a String and converts it to an array of ItemStacks.
     * @param string - The string to parse.
     * @return An array of ItemStacks.
     */
    private static ArrayList<ItemStack> itemStackListFromString(String string) throws IllegalArgumentException {
        if (string == null || string.isEmpty()) {
            throw new IllegalArgumentException("Cannot make ItemStack from null/empty string!");
        }
        
        ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
        String[] itemStackStrings = string.split(",");
        
        for (String itemStackString : itemStackStrings) {
            if (itemStackString.isEmpty()) {
                continue;
            }
            
            ret.add(ItemStackConverter.fromString(itemStackString));
        }
        
        return ret;
    }
}
