package net.arcanamod.systems.research.impls;

import net.arcanamod.capabilities.Researcher;
import net.arcanamod.network.Connection;
import net.arcanamod.systems.research.Puzzle;
import net.arcanamod.systems.research.Requirement;
import net.arcanamod.systems.research.ResearchBooks;
import net.arcanamod.systems.research.ResearchEntry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import static net.arcanamod.Arcana.arcLoc;

public class PuzzleRequirement extends Requirement{
	
	public static final ResourceLocation TYPE = arcLoc("puzzle");
	
	protected ResourceLocation puzzleId;
	
	public PuzzleRequirement(ResourceLocation puzzleId){
		this.puzzleId = puzzleId;
	}
	
	public boolean satisfied(Player player){
		return Researcher.getFrom(player).isPuzzleCompleted(ResearchBooks.puzzles.get(puzzleId));
	}
	
	public void take(Player player){
		// no-op
	}
	
	public ResourceLocation type(){
		return TYPE;
	}
	
	public CompoundTag data(){
		CompoundTag compound = new CompoundTag();
		compound.putString("puzzle", puzzleId.toString());
		return compound;
	}
	
	public boolean onClick(ResearchEntry entry, Player player){
		Puzzle puzzle = ResearchBooks.puzzles.get(puzzleId);
		if(!(puzzle instanceof Fieldwork || satisfied(player)))
			Connection.sendGetNoteHandler(puzzleId, entry.key().toString());
		return false;
	}
	
	public ResourceLocation getPuzzleId(){
		return puzzleId;
	}
}