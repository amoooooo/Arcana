package net.arcanamod.blocks.tiles;

import net.arcanamod.ArcanaConfig;
import net.arcanamod.aspects.Aspect;
import net.arcanamod.aspects.AspectStack;
import net.arcanamod.aspects.handlers.AspectBattery;
import net.arcanamod.aspects.handlers.AspectHandlerCapability;
import net.arcanamod.aspects.handlers.AspectHolder;
import net.arcanamod.blocks.ArcanaBlocks;
import net.arcanamod.blocks.pipes.AspectSpeck;
import net.arcanamod.blocks.pipes.TubeBlockEntity;
import net.arcanamod.client.render.particles.AspectHelixParticleOption;
import net.arcanamod.containers.AlembicMenu;
import net.arcanamod.items.EnchantedFilterItem;
import net.arcanamod.world.AuraView;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import static net.arcanamod.aspects.Aspects.EMPTY;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class AlembicBlockEntity extends BaseContainerBlockEntity {
	
	// 50 of 5 aspects
	// TODO: see usage of ALEMBIC_BASE_DISTILL_EFFICIENCY
	public AspectBattery aspects = new AspectBattery(/*5, 50*/);
	public boolean suppressedByRedstone = false;
	public ItemStackHandler inventory = new ItemStackHandler(2);
	public int burnTicks = 0;
	public int maxBurnTicks = 0;
	
	protected int crucibleLevel = -1;
	protected boolean stacked = false;
	
	public AlembicBlockEntity(BlockPos pWorldPosition, BlockState pBlockState){
		super(ArcanaTiles.ALEMBIC_TE.get(), pWorldPosition, pBlockState);
		for(int i = 0; i < 5; i++){
			aspects.initHolders(50, 5);
			aspects.getHolders().forEach(h -> h.setCanInsert(false));
		}
	}
	
	@SuppressWarnings("deprecation")
	public void tick(){
		if(isOn() && level != null){
			// scan for boiling crucibles
			// 1-4 blocks of air, +3 of other alembics by default
			int maxAirs = ArcanaConfig.MAX_ALEMBIC_AIR.get(), maxAlembics = ArcanaConfig.MAX_ALEMBIC_STACK.get();
			// if alembics are present, don't show particles
			crucibleLevel = -1;
			boolean pocket = false;
			int stack = 0;
			int airs = -1;
			for(int i = getBlockPos().getY() - 1; i > getBlockPos().getY() - (maxAirs + maxAlembics + 1); i--){
				int passes = (getBlockPos().getY() - 1) - i;
				BlockPos pos = new BlockPos(getBlockPos().getX(), i, getBlockPos().getZ());
				BlockState state = level.getBlockState(pos);
				airs = passes - stack;
				if(state.getBlock() == ArcanaBlocks.ALEMBIC.get()){
					// air followed by alembic is invalid
					if(pocket)
						break;
					stack++;
				}
				// only three alembics
				if(stack > maxAlembics)
					break;
				if(state.isAir()){
					// up to 3 alembics + 4 air blocks
					if(airs > maxAirs)
						break;
					pocket = true;
				}
				if(level.getBlockEntity(pos) instanceof CrucibleBlockEntity){
					// found it
					// if we haven't gotten any air, don't save (it's invalid)
					if(airs > 0)
						crucibleLevel = i;
					break;
				}
			}
			stacked = stack > 0;
			if(crucibleLevel != -1){
				if(burnTicks == 0){
					ItemStack fuel = fuel();
					// TODO: Smelting or sth else?
					int newTicks = ForgeHooks.getBurnTime(fuel, RecipeType.SMELTING) / 2; //furnaces need significantly longer to work
					if(newTicks > 0){
						burnTicks = newTicks;
						maxBurnTicks = newTicks;
						if(fuel.getCount() == 1)
							inventory.setStackInSlot(1, fuel.getContainerItem());
						else
							fuel.setCount(fuel.getCount() - 1);
						setChanged();
					}
				}
				if(burnTicks > 0){
					BlockPos cruciblePos = new BlockPos(getBlockPos().getX(), crucibleLevel, getBlockPos().getZ());
					CrucibleBlockEntity te = (CrucibleBlockEntity)level.getBlockEntity(cruciblePos);
					if(te != null && te.getAspectStackMap().size() > 0){
						Aspect aspect = EMPTY;
						// find an aspect stack we can actually pull
						AspectHolder adding = null;
						for(AspectHolder holder : aspects.getHolders()){
							if(holder.getCapacity() - holder.getStack().getAmount() > 0){
								adding = holder;
								Aspect maybe = te.getAspectStackMap().values().stream().filter(stack1 -> stack1.getAspect() == holder.getStack().getAspect() || holder.getStack().isEmpty()).findFirst().map(AspectStack::getAspect).orElse(EMPTY);
								if(maybe != EMPTY){
									aspect = maybe;
									break;
								}
							}
						}
						
						if(aspect != EMPTY){
							AspectStack aspectStack = te.getAspectStackMap().get(aspect);
							if(!stacked)
								level.addParticle(new AspectHelixParticleOption(aspect, 20 * airs + 15, level.random.nextInt(180), new Vec3(0, 1, 0)), cruciblePos.getX() + .5 + level.random.nextFloat() * .1, cruciblePos.getY() + .7, cruciblePos.getZ() + .5 + level.random.nextFloat() * .1, 0, 0, 0);
							// pick a random aspect, take from it, and store them in our actual aspect handler
							int freeTicks = filter().isEmpty() ? 0 : ((EnchantedFilterItem)filter().getItem()).speedBoost;
							if(level.getGameTime() % (ArcanaConfig.ALEMBIC_DISTILL_TIME.get() - freeTicks * 2) == 0){
								float diff = Math.min(aspectStack.getAmount(), 1);
								AspectStack newStack = new AspectStack(aspectStack.getAspect(), aspectStack.getAmount() - 1);
								if(!newStack.isEmpty())
									te.getAspectStackMap().put(aspect, newStack);
								else
									te.getAspectStackMap().remove(aspect);
								
								int efficiencyBoost = filter().isEmpty() ? 0 : ((EnchantedFilterItem)filter().getItem()).efficiencyBoost;
								// -1: 0.6 multiplier, 0.4 flux
								// +0: 0.7 multiplier, 0.3 flux
								// +1: 0.8 multiplier, 0.3 flux
								// +2: 0.8 multiplier, 0.2 flux
								// +3: 0.9 multiplier, 0.1 flux
								float effMultiplier = 0.7f;
								float fluxMultiplier = 0.3f;
								switch(efficiencyBoost){
									case -1:
										effMultiplier = 0.6f; fluxMultiplier = 0.4f; break;
									case 1:
										effMultiplier = 0.8f; fluxMultiplier = 0.3f; break;
									case 2:
										effMultiplier = 0.8f; fluxMultiplier = 0.2f; break;
									case 3:
										effMultiplier = 0.9f; fluxMultiplier = 0.1f; break;
								}
								adding.setCanInsert(true);
								adding.insert(new AspectStack(aspectStack.getAspect(), diff * effMultiplier), false);
								adding.setCanInsert(false);
								AuraView.SIDED_FACTORY.apply(level).addFluxAt(getBlockPos(), diff * fluxMultiplier);
							}
						}
					}
					// then push them out into the total pipe system from sides
					if(level.getGameTime() % 5 == 0)
						for(Direction dir : Direction.Plane.HORIZONTAL){
							BlockEntity tubeTe = level.getBlockEntity(worldPosition.relative(dir));
							if(tubeTe instanceof TubeBlockEntity){
								TubeBlockEntity aspectTube = (TubeBlockEntity) tubeTe;
								AspectHolder holder = aspects.findFirstFullHolder();
								// try not to add specks that can't transfer
								if(aspectTube.getSpecks().size() < 6 && holder != null && holder.getStack().getAmount() >= 0.5){
									AspectStack speck = aspects.drainAny(ArcanaConfig.MAX_ALEMBIC_ASPECT_OUT.get());
									if(!speck.isEmpty())
										aspectTube.addSpeck(new AspectSpeck(speck, 0.8f, dir, 0));
								}
							}
						}
					// aspects can be pulled from the top when pulling becomes a thing but that doesn't matter here
				}
				if(burnTicks > 0)
					burnTicks--;
			}
		}
	}
	
	public boolean isOn(){
		return !suppressedByRedstone;
	}
	
	public void load(CompoundTag compound){
		super.load(compound);
		aspects.deserializeNBT(compound.getCompound("aspects"));
		suppressedByRedstone = compound.getBoolean("suppressed");
		inventory.deserializeNBT(compound.getCompound("items"));
		burnTicks = compound.getInt("burnTicks");
		maxBurnTicks = compound.getInt("maxBurnTicks");
	}
	
	public void saveAdditional(CompoundTag compound){
		super.saveAdditional(compound);
		CompoundTag nbt = new CompoundTag();
		nbt.put("aspects", aspects.serializeNBT());
		nbt.putBoolean("suppressed", suppressedByRedstone);
		nbt.put("items", inventory.serializeNBT());
		nbt.putInt("burnTicks", burnTicks);
		nbt.putInt("maxBurnTicks", maxBurnTicks);
	}
	
	public ItemStack filter(){
		return inventory.getStackInSlot(0);
	}
	
	public ItemStack fuel(){
		return inventory.getStackInSlot(1);
	}
	
	@SuppressWarnings("unchecked") // bad generics checkers
	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side){
		if(cap == AspectHandlerCapability.ASPECT_HANDLER)
			return aspects.getCapability(AspectHandlerCapability.ASPECT_HANDLER).cast();
		if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
			return (LazyOptional<T>)LazyOptional.of(() -> inventory);
		return super.getCapability(cap, side);
	}
	
	public Component getDisplayName(){
		return new TranslatableComponent("block.arcana.alembic");
	}

	@Override
	protected Component getDefaultName() {
		return null;
	}

	@Nullable
	public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player){
		return new AlembicMenu(id, this, inventory);
	}

	@Override
	protected AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory) {
		return null;
	}

	public CompoundTag getUpdateTag(){
		return new CompoundTag();
	}

	@Override
	public int getContainerSize() {
		return 0;
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	@Override
	public ItemStack getItem(int pIndex) {
		return null;
	}

	@Override
	public ItemStack removeItem(int pIndex, int pCount) {
		return null;
	}

	@Override
	public ItemStack removeItemNoUpdate(int pIndex) {
		return null;
	}

	@Override
	public void setItem(int pIndex, ItemStack pStack) {

	}

	@Override
	public boolean stillValid(Player pPlayer) {
		return false;
	}

	@Override
	public void clearContent() {

	}
}