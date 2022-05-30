package net.arcanamod.systems.research.impls;

import net.arcanamod.capabilities.Researcher;
import net.arcanamod.systems.research.Parent;
import net.arcanamod.systems.research.Requirement;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import static net.arcanamod.Arcana.arcLoc;

public class ResearchCompletedRequirement extends Requirement{
	
	public static final ResourceLocation TYPE = arcLoc("research_completed");
	
	protected Parent req;
	
	public ResearchCompletedRequirement(String req){
		this.req = Parent.parse(req);
	}
	
	public boolean satisfied(Player player){
		return req.satisfiedBy(Researcher.getFrom(player));
	}
	
	public void take(Player player){
		// no-op
	}
	
	public ResourceLocation type(){
		return TYPE;
	}
	
	public CompoundTag data(){
		CompoundTag compound = new CompoundTag();
		compound.putString("requirement", req.asString());
		return compound;
	}
}