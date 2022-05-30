package net.arcanamod.blocks.pipes;

import net.arcanamod.aspects.Aspect;
import net.arcanamod.aspects.AspectStack;
import net.arcanamod.aspects.Aspects;
import net.arcanamod.aspects.handlers.AspectHandler;
import net.arcanamod.blocks.tiles.ArcanaTiles;
import net.arcanamod.containers.PumpMenu;
import net.arcanamod.items.CrystalItem;
import net.arcanamod.items.EnchantedFilterItem;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class PumpBlockEntity extends TubeBlockEntity implements Nameable {
	
	protected static int PULL_AMOUNT = 4;
	protected static int PULL_TIME = 5;
	protected static float SPECK_SPEED = 0.5f;
	
	public boolean suppressedByRedstone = false;
	public ItemStackHandler inventory = new ItemStackHandler(2); // filter, crystal
	public Direction direction;
	
	// pull aspects from containers and convert them into specks
	public PumpBlockEntity(BlockPos pWorldPosition, BlockState pBlockState){
		super(ArcanaTiles.ASPECT_PUMP_TE.get(), pWorldPosition, pBlockState);
		this.direction = Direction.UP;
	}
	
	public PumpBlockEntity(Direction direction, BlockPos pWorldPosition, BlockState pBlockState){
		super(ArcanaTiles.ASPECT_PUMP_TE.get(), pWorldPosition, pBlockState);
		this.direction = direction;
	}
	
	@SuppressWarnings("ConstantConditions")
	public void tick(){
		super.tick();
		// pull new specks
		if(!suppressedByRedstone && specks.size() < 10 && getLevel().getGameTime() % PULL_TIME == 0){
			BlockEntity from = getLevel().getBlockEntity(getBlockPos().relative(direction.getOpposite()));
			AspectHandler handler = AspectHandler.getFrom(from);
			if(handler != null){
				boolean hasFilter = !filter().isEmpty() && filter().getItem() instanceof EnchantedFilterItem;
				int effBoost = hasFilter ? ((EnchantedFilterItem)filter().getItem()).efficiencyBoost : 0;
				AspectStack stack;
				if(filteredTo() == Aspects.EMPTY)
					stack = handler.drainAny(PULL_AMOUNT + effBoost);
				else
					stack = new AspectStack(filteredTo(), handler.drain(filteredTo(), PULL_AMOUNT + effBoost));
				if(!stack.isEmpty()){
					int speedBoost = hasFilter ? ((EnchantedFilterItem)filter().getItem()).speedBoost : 0;
					addSpeck(new AspectSpeck(stack, SPECK_SPEED + speedBoost * 0.1f, direction, 0));
				}
			}
		}
	}
	
	protected Optional<Direction> redirect(AspectSpeck speck, boolean canPass){
		return (!suppressedByRedstone && (crystal().isEmpty() || filteredTo() == speck.payload.getAspect()))
				? Optional.of(direction)
				: Optional.empty();
	}
	
	public ItemStack filter(){
		return inventory.getStackInSlot(0);
	}
	
	public ItemStack crystal(){
		return inventory.getStackInSlot(1);
	}
	
	public Aspect filteredTo(){
		return crystal().getItem() instanceof CrystalItem ? ((CrystalItem)crystal().getItem()).aspect : Aspects.EMPTY;
	}
	
	@SuppressWarnings("unchecked")
	@Nonnull
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side){
		if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
			return (LazyOptional<T>)LazyOptional.of(() -> inventory);
		return super.getCapability(cap, side);
	}
	
	public Component getName(){
		return new TranslatableComponent("block.arcana.essentia_pump");
	}

	@Override
	public boolean hasCustomName() {
		return Nameable.super.hasCustomName();
	}

	@Override
	public Component getDisplayName() {
		return this.getName();
	}

	@org.jetbrains.annotations.Nullable
	@Override
	public Component getCustomName() {
		return Nameable.super.getCustomName();
	}

	@Nullable
	public AbstractContainerMenu createMenu(int id, Inventory playerInv, Player player){
		return new PumpMenu(id, this, playerInv);
	}
	
	public void load(CompoundTag nbt){
		super.load(nbt);
		inventory.deserializeNBT(nbt.getCompound("items"));
		suppressedByRedstone = nbt.getBoolean("suppressed");
	}
	
	public void saveAdditional(CompoundTag compound){
		super.saveAdditional(compound);
		CompoundTag nbt = new CompoundTag();
		nbt.put("items", inventory.serializeNBT());
		nbt.putBoolean("suppressed", suppressedByRedstone);
	}
}