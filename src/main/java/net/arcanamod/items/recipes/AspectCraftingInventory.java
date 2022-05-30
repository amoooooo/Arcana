package net.arcanamod.items.recipes;

import net.arcanamod.containers.slots.WandSlot;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Set;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class AspectCraftingInventory extends CraftingContainer{
	
	private final WandSlot wandSlot;
	private final Container deferred;
	private final AbstractContainerMenu eventHandler; // just store this twice
	private final Player crafter;
	
	public AspectCraftingInventory(AbstractContainerMenu eventHandler, WandSlot wandSlot, int width, int height, Container deferred, Player crafter){
		super(eventHandler, width, height);
		this.eventHandler = eventHandler;
		this.wandSlot = wandSlot;
		this.deferred = deferred;
		this.crafter = crafter;
	}

	public WandSlot getWandSlot() {
		return wandSlot;
	}
	
	public int getSizeInventory(){
		return deferred.getContainerSize();
	}
	
	public ItemStack removeItemNoUpdate(int index){
		return deferred.removeItemNoUpdate(index);
	}
	
	public ItemStack removeItem(int index, int count) {
		ItemStack itemstack = deferred.removeItem(index, count);
		if (!itemstack.isEmpty()) {
			this.eventHandler.slotsChanged(this);
		}
		return itemstack;
	}
	
	public void setInventorySlotContents(int index, ItemStack stack){
		deferred.setItem(index, stack);
		this.eventHandler.slotsChanged(this);
	}
	
	public void markDirty(){
		deferred.setChanged();
	}
	
	public void clearContent(){
		deferred.clearContent();
	}
	
	public int getMaxStackSize(){
		return deferred.getMaxStackSize();
	}
	
	public void openInventory(Player player){}
	
	public void closeInventory(Player player){}
	
	public boolean canPlaceItem(int index, ItemStack stack){
		return deferred.canPlaceItem(index, stack);
	}
	
	public int count(Item item){
		return deferred.countItem(item);
	}
	
	public boolean hasAny(Set<Item> set){
		return deferred.hasAnyOf(set);
	}
	
	public boolean isEmpty(){
		return deferred.isEmpty();
	}
	
	public boolean stillValid(Player player){
		return deferred.stillValid(player);
	}
	
	public ItemStack getItem(int index){
		return deferred.getItem(index);
	}
	
	public Player getCrafter() {
		return crafter;
	}
}