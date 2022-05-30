package net.arcanamod.blocks.bases;

import net.minecraft.world.item.CreativeModeTab;

import javax.annotation.Nullable;

public interface GroupedBlock{
	
	@Nullable
	CreativeModeTab getGroup();
}