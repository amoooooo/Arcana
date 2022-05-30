package net.arcanamod.blocks.tiles;

import io.netty.buffer.Unpooled;
import net.arcanamod.aspects.VisShareable;
import net.arcanamod.aspects.handlers.AspectBattery;
import net.arcanamod.aspects.handlers.AspectHandler;
import net.arcanamod.aspects.handlers.AspectHandlerCapability;
import net.arcanamod.blocks.ArcanaBlocks;
import net.arcanamod.containers.ResearchTableMenu;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;

import static net.arcanamod.blocks.multiblocks.research_table.ResearchTableComponentBlock.PAPER;
import static net.arcanamod.blocks.multiblocks.research_table.ResearchTableCoreBlock.FACING;
import static net.arcanamod.blocks.multiblocks.research_table.ResearchTableCoreBlock.INK;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ResearchTableBlockEntity extends BaseContainerBlockEntity {
	ArrayList<BlockPos> visContainers = new ArrayList<>();
	AspectBattery battery = new AspectBattery(/*Integer.MAX_VALUE, 100*/);

	public boolean batteryIsDirty = true;

	public ResearchTableBlockEntity(BlockPos pWorldPosition, BlockState pBlockState){
		super(ArcanaTiles.RESEARCH_TABLE_TE.get(), pWorldPosition, pBlockState);
	}

	// Three slots for wand (OR any AspectHandler, but research games can only be performed with a wand), ink, note
	// up to 9 for crafting guesswork for arcane crafting
	// up to, idk, 12 for arcane infusion
	// golemancy will be weird
	// so its ~15 max?

	// slots 0-2 are always there, the rest are reserved for the games themselves

	protected ItemStackHandler items = new ItemStackHandler(14) {
		protected void onContentsChanged(int slot){
			super.onContentsChanged(slot);
			setChanged();
		}
	};

	public AspectBattery getVisBattery(){
		return getVisShareablesAsBattery();
	}

	//TODO: There is better way to do it
	private AspectBattery getVisShareablesAsBattery() {
		battery.clear();
		BlockPos.MutableBlockPos blockPos = new BlockPos.MutableBlockPos(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ());
		for(int x = 0; x < 9; x++){
			for(int y = 0; y < 7; y++){
				for(int z = 0; z < 9; z++){
					blockPos.set(getBlockPos());
					blockPos.move(x - 4, y - 2, z - 4);
					if(level.getBlockState(blockPos).hasBlockEntity()){
						BlockEntity tileEntityInBox = level.getBlockEntity(blockPos);
						if(tileEntityInBox != null)
							if(tileEntityInBox instanceof VisShareable)
								if(((VisShareable)tileEntityInBox).isVisShareable() && ((VisShareable)tileEntityInBox).isManual()){
									AspectBattery vis = (AspectBattery)AspectHandler.getFrom(tileEntityInBox);
									if(vis != null){
										visContainers.add(new BlockPos(blockPos)); // Removing reference
										AspectBattery.merge(battery, vis);
									}
								}
					}
				}
			}
		}
		return battery;
	}

	@Override
	public void load(CompoundTag compound){
		super.load(compound);
		if(compound.contains("items"))
			items.deserializeNBT(compound.getCompound("items"));
	}

	@Override
	public void saveAdditional(CompoundTag compound){
		compound.put("items", items.serializeNBT());
		super.saveAdditional(compound);
	}

	@Override
	protected Component getDefaultName(){
		return new TextComponent("research_table");
	}

	@Override
	protected AbstractContainerMenu createMenu(int id, Inventory player){
		FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer(8, 8));
		buffer.writeBlockPos(worldPosition);
		return new ResearchTableMenu(id, player, buffer);
	}

	@Nonnull
	public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing){
		if(capability == AspectHandlerCapability.ASPECT_HANDLER){
			AspectBattery battery = getVisBattery();
			return battery.getCapability(AspectHandlerCapability.ASPECT_HANDLER).cast();
		}
		return super.getCapability(capability, facing).cast();
	}

	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap){
		return this.getCapability(cap, null);
	}

	public ItemStack visItem(){
		return items.getStackInSlot(0);
	}

	public ItemStack ink(){
		return items.getStackInSlot(1);
	}

	@Nonnull
	public ItemStack note(){
		return items.getStackInSlot(2);
	}

	@Override
	public int getContainerSize(){
		return items.getSlots();
	}

	@Override
	public boolean isEmpty(){
		return false;
	}

	@Override
	public ItemStack getItem(int index){
		return items.getStackInSlot(index);
	}

	@Override
	public ItemStack removeItem(int index, int count){
		ItemStack stack0 = items.getStackInSlot(index);
		ItemStack stack1 = items.getStackInSlot(index).copy();
		stack0.shrink(count);
		stack1.setCount(count);
		return stack1; //TODO: Check of works fine (custom impl)
	}

	@Override
	public ItemStack removeItemNoUpdate(int index){
		ItemStack stack = this.items.getStackInSlot(index).copy();
		items.getStackInSlot(index).setCount(0);
		return stack;
	}

	@Override
	public void setItem(int index, ItemStack stack){
		items.setStackInSlot(index, stack);
		if(stack.getCount() > this.getMaxStackSize())
			stack.setCount(this.getMaxStackSize());
		if (level != null) {
			level.setBlockAndUpdate(worldPosition, level.getBlockState(worldPosition).setValue(INK, !ink().isEmpty()));
			Direction facing = level.getBlockState(worldPosition).getValue(FACING);
			BlockPos componentPos = worldPosition.offset(-facing.getStepZ(), facing.getStepY(), facing.getStepX());
			if (level.getBlockState(componentPos).getBlock() == ArcanaBlocks.RESEARCH_TABLE_COMPONENT.get()) {
				level.setBlockAndUpdate(componentPos, level.getBlockState(componentPos).setValue(PAPER, !note().isEmpty()));
			}
		}
	}

	@Override
	public boolean stillValid(Player player){
		if(this.level.getBlockEntity(this.worldPosition) != this)
			return false;
		return player.distanceToSqr((double)this.worldPosition.getX() + 0.5D, (double)this.worldPosition.getY() + 0.5D, (double)this.worldPosition.getZ() + 0.5D) <= 64.0D;
	}

	@Override
	public void clearContent(){
		for(int i = 0; i < items.getSlots() - 1; i++)
			items.getStackInSlot(i).setCount(0);
	}
}