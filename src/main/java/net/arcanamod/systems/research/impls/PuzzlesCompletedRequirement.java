package net.arcanamod.systems.research.impls;

import net.arcanamod.capabilities.Researcher;
import net.arcanamod.systems.research.Requirement;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import static net.arcanamod.Arcana.arcLoc;

public class PuzzlesCompletedRequirement extends Requirement{
	
	public static final ResourceLocation TYPE = arcLoc("puzzles_completed");
	
	public boolean satisfied(Player player){
		return Researcher.getFrom(player).getPuzzlesCompleted() >= getAmount();
	}
	
	public void take(Player player){
		// no-op
	}
	
	public ResourceLocation type(){
		return TYPE;
	}
	
	public CompoundTag data(){
		return new CompoundTag();
	}
}