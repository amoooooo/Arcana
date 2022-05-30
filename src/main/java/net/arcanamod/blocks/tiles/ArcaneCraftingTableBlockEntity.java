package net.arcanamod.blocks.tiles;

import net.arcanamod.containers.ArcaneCraftingTableMenu;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ArcaneCraftingTableBlockEntity extends BaseContainerBlockEntity {

	protected NonNullList<ItemStack> items = NonNullList.withSize(11, ItemStack.EMPTY);

	public ArcaneCraftingTableBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
		super(ArcanaTiles.ARCANE_WORKBENCH_TE.get(), pWorldPosition, pBlockState);
	}

	@Override
	protected Component getDefaultName() {
		return new TranslatableComponent("container.arcane_workbench");
	}

	@Override
	protected AbstractContainerMenu createMenu(int id, Inventory player) {
		return new ArcaneCraftingTableMenu(id,player,this);
	}

	/**
	 * Returns the number of slots in the inventory.
	 */
	@Override
	public int getContainerSize() {
		return this.items.size();
	}

	@Override
	public boolean isEmpty() {
		for(ItemStack itemstack : this.items) {
			if (!itemstack.isEmpty()) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Returns the stack in the given slot.
	 */
	@Override
	public ItemStack getItem(int index) {
		return this.items.get(index);
	}

	/**
	 * Removes up to a specified number of items from an inventory slot and returns them in a new stack.
	 */
	@Override
	public ItemStack removeItem(int index, int count) {
		return ContainerHelper.removeItem(this.items, index, count);
	}

	/**
	 * Removes a stack from the given slot and returns it.
	 */
	@Override
	public ItemStack removeItemNoUpdate(int index) {
		return ContainerHelper.takeItem(this.items, index);
	}

	/**
	 * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
	 */
	@Override
	public void setItem(int index, ItemStack stack) {
		this.items.set(index, stack);
		if (stack.getCount() > this.getMaxStackSize()) {
			stack.setCount(this.getMaxStackSize());
		}
	}

	/**
	 * Don't rename this method to canInteractWith due to conflicts with Container
	 *
	 * @param player
	 */
	@Override
	public boolean stillValid(Player player) {
		return true;
	}

	@Override
	public void clearContent() {

	}

	@Override
	public void saveAdditional(CompoundTag nbt) {
		super.saveAdditional(nbt);
		ContainerHelper.saveAllItems(nbt, items);
	}

	@Override
	public void load(CompoundTag nbt) {
		super.load(nbt);
		ContainerHelper.loadAllItems(nbt, items);
	}
}