package net.arcanamod.blocks.tiles;

import net.arcanamod.aspects.VisShareable;
import net.arcanamod.aspects.handlers.AspectBattery;
import net.arcanamod.aspects.handlers.AspectHandler;
import net.arcanamod.aspects.handlers.AspectHandlerCapability;
import net.arcanamod.aspects.handlers.AspectHolder;
import net.arcanamod.items.PhialItem;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DispenserMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class AspectBookshelfBlockEntity extends RandomizableContainerBlockEntity implements VisShareable{
	private NonNullList<ItemStack> stacks = NonNullList.withSize(9, ItemStack.EMPTY);
	AspectBattery vis = new AspectBattery(/*9, 8*/);
	private double lastVis;
	public Direction rotation;
	
	public AspectBookshelfBlockEntity(BlockPos pWorldPosition, BlockState pBlockState){
		super(ArcanaTiles.ASPECT_SHELF_TE.get(), pWorldPosition, pBlockState);
		vis.initHolders(8, 9);
	}
	
	public AspectBookshelfBlockEntity(Direction rotation, BlockPos pWorldPosition, BlockState pBlockState){
		super(ArcanaTiles.ASPECT_SHELF_TE.get(), pWorldPosition, pBlockState);
		this.rotation = rotation;
		vis.initHolders(8, 9);
	}
	
	@Override
	public boolean canPlaceItem(int index, ItemStack stack){
		return this.getItem(index).isEmpty();
	}
	
	public int getVisTotal(){
		int vis = 0;
		for(ItemStack stack : stacks)
			if(stack.getItem() instanceof PhialItem)
				vis += ((PhialItem)stack.getItem()).getAspectStacks(stack).get(0).getAmount();
		return vis;
	}
	
	public int getContainerSize(){
		return 9;
	}
	
	public int getMaxStackSize(){
		return 1;
	}
	
	protected Component getDefaultName(){
		return new TranslatableComponent("container.aspectbookshelf");
	}
	
	protected AbstractContainerMenu createMenu(int id, Inventory player){
		return new DispenserMenu(id, player, this);
	}
	
	public int getRedstoneOut(){
		float vis;
		vis = getVisTotal();
		return (int)((vis / 72F) * 15);
	}
	
	protected NonNullList<ItemStack> getItems(){
		return this.stacks;
	}
	
	protected void setItems(NonNullList<ItemStack> itemsIn){
		this.stacks = itemsIn;
	}

	public void tick(){
		double newVis = vis.countHolders();
		if(level != null && lastVis != newVis && !level.isClientSide){
			level.updateNeighborsAt(worldPosition, level.getBlockState(worldPosition).getBlock());
		}
		lastVis = newVis;
	}
	
	public void saveAdditional(CompoundTag compound){
		super.load(compound);
		CompoundTag aspectsNbt = vis.serializeNBT();
		compound.put("aspects", aspectsNbt);
		if(!this.tryLoadLootTable(compound)){
			ContainerHelper.saveAllItems(compound, this.stacks);
		}
	}
	
	public void load(CompoundTag compound){
		vis.deserializeNBT(compound.getCompound("aspects"));
		this.stacks = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
		if(!this.tryLoadLootTable(compound)){
			ContainerHelper.loadAllItems(compound, this.stacks);
		}
		super.load(compound);
	}
	
	public AspectBattery updateBatteryAndReturn(){
		vis.initHolders(8, 9);
		for(int i = stacks.size() - 1; i >= 0; i--){
			if(stacks.get(i).getItem() instanceof PhialItem){
				AspectBattery aspectBattery = (AspectBattery)AspectHandler.getFrom(stacks.get(i));
				AspectHolder target;
				if(aspectBattery != null){
					target = aspectBattery.getHolder(0);
					vis.getHolders().set(i, target);
				}
			}else if(vis.hasHolder(i))
				vis.getHolders().remove(i);
		}
		return vis;
	}
	
	
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap){
		if(cap == AspectHandlerCapability.ASPECT_HANDLER){
			return updateBatteryAndReturn().getCapability(AspectHandlerCapability.ASPECT_HANDLER).cast();
		}else{
			return updateBatteryAndReturn().getCapability(null, null);
		}
	}
	
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side){
		return this.getCapability(cap);
	}
	
	public boolean addPhial(ItemStack stack, int slot){
		if(this.stacks.get(slot).isEmpty()){
			stack = stack.copy();
			stack.setCount(1);
			this.setItem(slot, stack);
			return true;
		}
		return false;
	}
	
	public ItemStack removePhial(int slot){
		if(!this.stacks.get(slot).isEmpty()){
			ItemStack removedPhial = this.stacks.get(slot);
			this.stacks.set(slot, ItemStack.EMPTY);
			return removedPhial;
		}
		return ItemStack.EMPTY;
	}
	
	@Override
	public void setItem(int index, ItemStack stack){
		this.setChanged();
		this.getLevel().sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
		super.setItem(index, stack);
	}
	
	@Override
	@Nullable
	public ClientboundBlockEntityDataPacket getUpdatePacket(){
		CompoundTag nbtTagCompound = new CompoundTag();
		saveAdditional(nbtTagCompound);
		return ClientboundBlockEntityDataPacket.create(this);
	}
	
	@Override
	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt){
		load(pkt.getTag());
	}
	
	@Override
	public CompoundTag getUpdateTag(){
		CompoundTag nbtTagCompound = new CompoundTag();
		saveAdditional(nbtTagCompound);
		return nbtTagCompound;
	}
	
	@Override
	public void handleUpdateTag(CompoundTag tag){
		this.load(tag);
	}
	
	@Override
	public boolean isVisShareable(){
		return true;
	}
	
	@Override
	public boolean isManual(){
		return true;
	}
	
	@Override
	public boolean isSecure(){
		return false;
	}
}