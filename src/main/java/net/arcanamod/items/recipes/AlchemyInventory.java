package net.arcanamod.items.recipes;

import net.arcanamod.aspects.Aspect;
import net.arcanamod.aspects.AspectStack;
import net.arcanamod.blocks.tiles.CrucibleBlockEntity;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class AlchemyInventory implements Container {
	
	ItemStack stack = ItemStack.EMPTY;
	CrucibleBlockEntity crucible;
	Player crafter;
	
	public AlchemyInventory(CrucibleBlockEntity crucible, Player crafter) {
		this.crucible = crucible;
		this.crafter = crafter;
	}
	
	public int getContainerSize(){
		return 1;
	}
	
	public boolean isEmpty(){
		return stack.isEmpty() && crucible.getAspectStackMap().isEmpty();
	}
	
	public ItemStack getItem(int index){
		return index == 0 ? stack : ItemStack.EMPTY;
	}
	
	public ItemStack removeItem(int index, int count){
		return index == 0 ? stack.split(count) : ItemStack.EMPTY;
	}
	
	public ItemStack removeItemNoUpdate(int index){
		ItemStack result = ItemStack.EMPTY;
		if(index == 0){
			result = stack;
			stack = ItemStack.EMPTY;
		}
		return result;
	}
	
	public void setItem(int index, ItemStack stack){
		if(index == 0)
			this.stack = stack;
	}
	
	public void setChanged(){
		crucible.setChanged();
	}
	
	public boolean stillValid(Player player){
		return true;
	}
	
	public Map<Aspect, AspectStack> getAspectMap(){
		return crucible.getAspectStackMap();
	}
	
	public Player getCrafter(){
		return crafter;
	}
	
	public void clearContent(){
		stack = ItemStack.EMPTY;
		// eww
		crucible.getAspectStackMap().clear();
	}
}
