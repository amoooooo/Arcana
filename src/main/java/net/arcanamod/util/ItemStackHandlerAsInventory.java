package net.arcanamod.util;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ItemStackHandlerAsInventory implements Container {
	
	private ItemStackHandler handler;
	public Runnable onDirty;
	
	public ItemStackHandlerAsInventory(@Nonnull ItemStackHandler handler, @Nullable Runnable onDirty){
		this.handler = handler;
		this.onDirty = onDirty;
	}
	
	public int getContainerSize(){
		return handler.getSlots();
	}
	
	// todo: implement
	@Override
	public boolean isEmpty(){
		return false;
	}

	@Override
	public ItemStack getItem(int index){
		return handler.getStackInSlot(index);
	}
	
	@Override
	public ItemStack removeItem(int index, int count){
		return handler.extractItem(index, count, false);
	}
	
	@Override
	public ItemStack removeItemNoUpdate(int index){
		return handler.extractItem(index, handler.getStackInSlot(index).getCount(), false);
	}

	@Override
	public void setItem(int index, ItemStack stack){
		handler.setStackInSlot(index, stack);
	}

	@Override
	public void setChanged(){
		if(onDirty != null)
			onDirty.run();
	}

	@Override
	public boolean stillValid(Player player){
		return true;
	}

	@Override
	public void clearContent(){
		for(int i = 0; i < handler.getSlots() - 1; i++)
			handler.getStackInSlot(i).setCount(0);
	}
}
