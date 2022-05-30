package net.arcanamod.blocks.tiles;

import net.arcanamod.aspects.AspectUtils;
import net.arcanamod.aspects.handlers.AspectBattery;
import net.arcanamod.aspects.handlers.AspectHandlerCapability;
import net.arcanamod.aspects.handlers.AspectHolder;
import net.arcanamod.containers.AspectCrystallizerMenu;
import net.arcanamod.items.CrystalItem;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class AspectCrystallizerBlockEntity extends BaseContainerBlockEntity implements WorldlyContainer {
	
	protected NonNullList<ItemStack> items = NonNullList.withSize(1, ItemStack.EMPTY);
	private static final int[] SLOT_OTHER = new int[]{};
	private static final int[] SLOTS_DOWN = new int[]{0};
	
	public static final int MAX_PROGRESS = 80;
	
	public AspectBattery vis = new AspectBattery(/*1, 100*/);
	public int progress = 0;
	
	public AspectCrystallizerBlockEntity(BlockPos pWorldPosition, BlockState pBlockState){
		super(ArcanaTiles.ASPECT_CRYSTALLIZER_TE.get(), pWorldPosition, pBlockState);
		vis.initHolders(100, 1);
	}
	
	public void tick(){
		AspectHolder holder = vis.getHolder(0);
		if(holder.getStack().getAmount() > 0
				&& ((getItem(0).getItem() instanceof CrystalItem && ((CrystalItem)getItem(0).getItem()).aspect == holder.getStack().getAspect() && getItem(0).getCount() < 64)
				|| ((getItem(0).isEmpty())))){
			if(progress >= MAX_PROGRESS){
				progress = 0;
				if(getItem(0).isEmpty())
					setItem(0, new ItemStack(AspectUtils.aspectCrystalItems.get(holder.getStack().getAspect())));
				else
					getItem(0).grow(1);
				holder.drain(1, false);
			}
			progress++;
		}else if(progress > 0)
			progress--;
	}
	
	public void saveAdditional(CompoundTag compound){
		ContainerHelper.saveAllItems(compound, items);
		CompoundTag aspectsNbt = vis.serializeNBT();
		compound.put("aspects", aspectsNbt);
		compound.putInt("progress", progress);
		super.saveAdditional(compound);
	}
	
	public void load(CompoundTag compound){
		ContainerHelper.loadAllItems(compound, items);
		vis.deserializeNBT(compound.getCompound("aspects"));
		progress = compound.getInt("progress");
		super.load(compound);
	}
	
	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap){
		if(cap == AspectHandlerCapability.ASPECT_HANDLER)
			return vis.getCapability(AspectHandlerCapability.ASPECT_HANDLER).cast();
		return LazyOptional.empty();
	}
	
	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side){
		return getCapability(cap);
	}
	
	protected Component getDefaultName(){
		return new TranslatableComponent("container.aspect_crystallizer");
	}
	
	protected AbstractContainerMenu createMenu(int id, Inventory player){
		return new AspectCrystallizerMenu(id, this, player);
	}
	
	public int getContainerSize(){
		return 3;
	}
	
	public boolean isEmpty(){
		return items.stream().allMatch(ItemStack::isEmpty);
	}
	
	public ItemStack getItem(int index){
		return items.get(index);
	}
	
	public ItemStack removeItem(int index, int count){
		return ContainerHelper.removeItem(items, index, count);
	}
	
	public ItemStack removeItemNoUpdate(int index){
		return ContainerHelper.takeItem(items, index);
	}
	
	public void setItem(int index, ItemStack stack){
		items.set(index, stack);
	}
	
	public boolean stillValid(Player player){
		if(level == null || level.getBlockEntity(worldPosition) != this)
			return false;
		return player.distanceToSqr(worldPosition.getX() + .5, worldPosition.getY() + .5, worldPosition.getZ() + .5) <= 64;
	}
	
	public void clearContent(){
		items.clear();
	}
	
	public int[] getSlotsForFace(Direction side){
		return side == Direction.DOWN ? SLOTS_DOWN : SLOT_OTHER;
	}

	@Override
	public boolean canPlaceItemThroughFace(int index, ItemStack itemStack, @Nullable Direction direction){
		return canPlaceItem(index, itemStack);
	}

	@Override
	public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction){
		return index == 0;
	}

	public CompoundTag getUpdateTag(){
		return new CompoundTag();
	}
}