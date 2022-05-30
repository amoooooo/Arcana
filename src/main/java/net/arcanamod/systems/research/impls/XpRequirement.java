package net.arcanamod.systems.research.impls;

import net.arcanamod.systems.research.Requirement;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import static net.arcanamod.Arcana.arcLoc;

public class XpRequirement extends Requirement{
	
	public static final ResourceLocation TYPE = arcLoc("xp");
	
	public boolean satisfied(Player player){
		return player.experienceLevel >= getAmount();
	}
	
	public void take(Player player){
		player.experienceLevel -= getAmount();
	}
	
	public ResourceLocation type(){
		return TYPE;
	}
	
	public CompoundTag data(){
		return new CompoundTag();
	}
}