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
import org.bukkit.entity.Egg;

public class EggTracker {

	
	private ArrayList<EggTrackerEntry> entries;
	
	public EggTracker() {
		this.entries = new ArrayList<EggTrackerEntry>();
	}
	
	/**
	 * Find the EggTrackerEntry from an Egg.
	 * @param egg - The Egg to look for.
	 * @return The EggTrackerEntry matching the given Egg, or null if one is not found.
	 */
	public EggTrackerEntry getEntry(Egg egg) {
		for (EggTrackerEntry entry : this.entries) {
			if (entry.getEgg() == egg) {
				return entry;
			}
		}
		return null;
	}
	
	/**
	 * Get all the EggTrackerEntry objects from the EggTracker.
	 * @return EggTrackerEntry ArrayList.
	 */
	public ArrayList<EggTrackerEntry> getEntries() {
		return this.entries;
	}
	
	/**
	 * Add an EggTrackerEntry to the EggTracker.
	 * @param entry - The EggTrackerEntry to add.
	 */
	public void addEntry(EggTrackerEntry entry) {
		this.entries.add(entry);
	}
	
	/**
	 * Remove an EggTrackerEntry from the EggTracker.
	 * @param entry - The entry to remove.
	 * @return True if removed, false if entry is null or not in the list.
	 */
	public boolean removeEntry(EggTrackerEntry entry) {
	    // Null check is probably extraneous but it doesn't hurt anything
		if (entry != null && this.entries.contains(entry)) {
			this.entries.remove(entry);
			return true;
		}
		return false;
	}
}
