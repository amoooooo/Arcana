package net.arcanamod.blocks.tiles;

import net.arcanamod.ArcanaConfig;
import net.arcanamod.aspects.Aspect;
import net.arcanamod.aspects.AspectStack;
import net.arcanamod.aspects.AspectUtils;
import net.arcanamod.aspects.ItemAspectRegistry;
import net.arcanamod.blocks.ArcanaBlocks;
import net.arcanamod.blocks.CrucibleBlock;
import net.arcanamod.items.recipes.AlchemyInventory;
import net.arcanamod.items.recipes.AlchemyRecipe;
import net.arcanamod.world.AuraView;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static net.arcanamod.blocks.CrucibleBlock.FULL;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class CrucibleBlockEntity extends BlockEntity{
	
	// Not an aspect handler; cannot be drawn directly from, and has infinite size
	// Should decay or something - to avoid very large NBT, at least.
	Map<Aspect, AspectStack> aspectStackMap = new HashMap<>();
	boolean boiling = false;
	
	public CrucibleBlockEntity(BlockPos pWorldPosition, BlockState pBlockState){
		super(ArcanaTiles.CRUCIBLE_TE.get(), pWorldPosition, pBlockState);
	}
	
	public Map<Aspect, AspectStack> getAspectStackMap(){
		return aspectStackMap;
	}
	
	public void tick(){
		BlockState below = level.getBlockState(worldPosition.below());
		FluidState fluidState = level.getFluidState(worldPosition.below());
		// TODO: use a block+fluid tag
		boiling = hasWater() && (below.getBlock() == Blocks.FIRE || below.getBlock() == Blocks.MAGMA_BLOCK || below.getBlock() == ArcanaBlocks.NITOR.get() || fluidState.getType() == Fluids.FLOWING_LAVA || fluidState.getType() == Fluids.LAVA);
		
		// check for items
		// if there are items that have aspects, boil them :)
		if(!level.isClientSide() && isBoiling()){
			List<ItemEntity> items = level.getEntitiesOfClass(ItemEntity.class, CrucibleBlock.INSIDE.bounds().move(getBlockPos()));
			for(ItemEntity item : items){
				ItemStack stack = item.getItem();
				// check if that item makes a recipe
				// requires a player; droppers and such will never craft alchemy recipes
				// yet; we could make a knowledgeable dropper or such later
				boolean melt = true;
				if(item.getThrower() != null && level.getPlayerByUUID(item.getThrower()) != null){
					Player thrower = level.getPlayerByUUID(item.getThrower());
					AlchemyInventory inventory = new AlchemyInventory(this, thrower);
					inventory.setItem(0, stack);
					Optional<AlchemyRecipe> optionalRecipe = level.getRecipeManager().getRecipeFor(AlchemyRecipe.ALCHEMY, inventory, level);
					if(optionalRecipe.isPresent()){
						melt = false;
						AlchemyRecipe recipe = optionalRecipe.get();
						if(stack.getCount() == 1)
							item.remove(Entity.RemovalReason.DISCARDED);
						else
							stack.shrink(1);
						ItemStack result = recipe.assemble(inventory);
						if(!thrower.addItem(result))
							thrower.drop(result, false);
						for(AspectStack aspectStack : recipe.getAspects()){
							Aspect aspect = aspectStack.getAspect();
							AspectStack newStack = new AspectStack(aspect, aspectStackMap.get(aspect).getAmount() - aspectStack.getAmount());
							if(!newStack.isEmpty())
								aspectStackMap.put(aspect, newStack);
							else
								aspectStackMap.remove(aspect);
						}
						setChanged();
						level.sendBlockUpdated(worldPosition, level.getBlockState(worldPosition), level.getBlockState(worldPosition), 2);
					}
				}
				if(melt){
					List<AspectStack> aspects = ItemAspectRegistry.get(stack);
					if(aspects.size() > 0){
						item.remove(Entity.RemovalReason.DISCARDED);
						level.playSound(null, worldPosition, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 1.0F, 1.0F);
						setChanged();
						level.sendBlockUpdated(worldPosition, level.getBlockState(worldPosition), level.getBlockState(worldPosition), 2);
						for(AspectStack aspect : aspects)
							if(aspect.getAmount() != 0)
								aspectStackMap.put(aspect.getAspect(), new AspectStack(aspect.getAspect(), aspect.getAmount() * stack.getCount() + (aspectStackMap.containsKey(aspect.getAspect()) ? aspectStackMap.get(aspect.getAspect()).getAmount() : 0)));
					}
				}
			}
		}
	}
	
	public void empty(){
		// release aspects as flux
		AuraView.SIDED_FACTORY.apply(level).addFluxAt(getBlockPos(), (float)(aspectStackMap.values().stream().mapToDouble(AspectStack::getAmount).sum() * ArcanaConfig.ASPECT_DUMPING_WASTE.get()));
		aspectStackMap.clear();
		// block handles changing state
	}
	
	private boolean hasWater(){
		return getLevel().getBlockState(getBlockPos()).getValue(FULL);
	}
	
	public boolean isBoiling(){
		return boiling;
	}
	
	public void load(CompoundTag compound){
		super.load(compound);
		ListTag aspects = compound.getList("aspects", Tag.TAG_COMPOUND);
		aspectStackMap.clear();
		for(Tag inbt : aspects){
			CompoundTag aspectNbt = ((CompoundTag)inbt);
			Aspect aspect = AspectUtils.getAspectByName(aspectNbt.getString("aspect"));
			int amount = aspectNbt.getInt("amount");
			aspectStackMap.put(aspect, new AspectStack(aspect, amount));
		}
	}
	
	public void saveAdditional(CompoundTag compound){
		super.saveAdditional(compound);
		ListTag aspects = new ListTag();
		for(AspectStack stack : aspectStackMap.values()){
			CompoundTag stackNbt = new CompoundTag();
			stackNbt.putString("aspect", stack.getAspect().name());
			stackNbt.putFloat("amount", stack.getAmount());
			aspects.add(stackNbt);
		}
		compound.put("aspects", aspects);
	}
	
	public CompoundTag getUpdateTag(){
		return new CompoundTag();
	}
	
	public void handleUpdateTag(CompoundTag tag){
		load(tag);
	}
	
	public ClientboundBlockEntityDataPacket getUpdatePacket(){
		return ClientboundBlockEntityDataPacket.create(this);
	}
	
	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt){
		handleUpdateTag(pkt.getTag());
	}
}