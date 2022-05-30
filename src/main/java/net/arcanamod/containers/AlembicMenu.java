package net.arcanamod.containers;

import net.arcanamod.blocks.tiles.AlembicBlockEntity;
import net.arcanamod.items.EnchantedFilterItem;
import net.arcanamod.util.ItemStackHandlerAsInventory;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class AlembicMenu extends AbstractContainerMenu {
	
	public AlembicBlockEntity te;
	
	public AlembicMenu(int id, AlembicBlockEntity te, Inventory playerInventory){
		super(ArcanaContainers.ALEMBIC.get(), id);
		this.te = te;
		ItemStackHandlerAsInventory in = new ItemStackHandlerAsInventory(te.inventory, te::setChanged);
		// Filter @ 14,14
		addSlot(new Slot(in, 0, 14, 14){
			public boolean mayPlace(ItemStack stack){
				return stack.getItem() instanceof EnchantedFilterItem;
			}
		});
		// Fuel @ 14,101
		addSlot(new Slot(in, 1, 14, 101){
			public boolean isItemValid(ItemStack stack){
				return /*stack.getItem() instanceof EnchantedFilterItem*/true;
			}
		});
		addPlayerSlots(playerInventory);
	}
	
	public boolean stillValid(Player player){
		return true;
	}
	
	private void addPlayerSlots(Container playerInventory){
		for(int i = 0; i < 3; ++i)
			for(int j = 0; j < 9; ++j)
				addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 140 + i * 18));
		
		for(int k = 0; k < 9; ++k)
			addSlot(new Slot(playerInventory, k, 8 + k * 18, 198));
	}
	
	public ItemStack quickMoveStack(Player player, int index) {
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = slots.get(index);
		if(slot != null && slot.hasItem()){
			ItemStack itemstack1 = slot.getItem();
			itemstack = itemstack1.copy();
			if(index < 9){
				if(!moveItemStackTo(itemstack1, 2, 37, true))
					return ItemStack.EMPTY;
			}else if(!moveItemStackTo(itemstack1, 0, 2, false))
				return ItemStack.EMPTY;
			if(itemstack1.isEmpty())
				slot.set(ItemStack.EMPTY);
			else
				slot.setChanged();
			if(itemstack1.getCount() == itemstack.getCount())
				return ItemStack.EMPTY;
			slot.onTake(player, itemstack1);
		}
		
		return itemstack;
	}
}