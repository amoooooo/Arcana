package net.arcanamod.systems.research.impls;

import net.arcanamod.systems.research.EntrySection;
import net.minecraft.nbt.CompoundTag;

public class AspectCombosSection extends EntrySection{
	
	public static final String TYPE = "aspect_combos";
	
	public String getType(){
		return TYPE;
	}
	
	public CompoundTag getData(){
		return new CompoundTag();
	}
}