package net.arcanamod.systems.research.impls;

import com.google.gson.JsonObject;
import net.arcanamod.Arcana;
import net.arcanamod.aspects.handlers.AspectHandler;
import net.arcanamod.containers.ResearchTableMenu;
import net.arcanamod.containers.slots.AspectSlot;
import net.arcanamod.systems.research.Puzzle;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class Fieldwork extends Puzzle{
	
	private static final ResourceLocation ICON = new ResourceLocation(Arcana.MODID, "textures/gui/research/fieldwork.png");
	public static final String TYPE = "fieldwork";
	
	public void load(JsonObject data, ResourceLocation file){
		// no-op
	}
	
	public String type(){
		return TYPE;
	}
	
	public CompoundTag getData(){
		// no-op
		return new CompoundTag();
	}
	
	public String getDefaultDesc(){
		return "requirement.fieldwork";
	}
	
	public ResourceLocation getDefaultIcon(){
		return ICON;
	}
	
	public List<Puzzle.SlotInfo> getItemSlotLocations(Player player){
		return Collections.emptyList();
	}
	
	public List<AspectSlot> getAspectSlots(Supplier<AspectHandler> returnInv){
		return Collections.emptyList();
	}
	
	public boolean validate(List<AspectSlot> aspectSlots, List<Slot> itemSlots, Player player, ResearchTableMenu container){
		return false;
	}
}