package net.arcanamod.blocks;

import net.arcanamod.Arcana;
import net.arcanamod.ArcanaSounds;
import net.arcanamod.blocks.bases.GroupedBlock;
import net.arcanamod.capabilities.TaintTrackable;
import net.arcanamod.systems.taint.Taint;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.SpreadingSnowyDirtBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.lighting.BlockLightEngine;
import net.minecraftforge.common.FarmlandWaterManager;
import net.minecraftforge.common.IPlantable;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Random;

import static net.minecraft.world.level.block.FarmBlock.MOISTURE;
import static net.minecraft.world.level.block.SnowyDirtBlock.SNOWY;
import static net.minecraftforge.common.ForgeHooks.onFarmlandTrample;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class TaintedBlock extends DelegatingBlock implements GroupedBlock{
	
	public static final BooleanProperty UNTAINTED = Taint.UNTAINTED;
	
	@Deprecated() // Use Taint#taintedOf instead
	public TaintedBlock(Block block){
		super(block, ArcanaSounds.TAINT);
		Taint.addTaintMapping(block, this);
	}
	
	public MutableComponent getTranslatedName(){
		return new TranslatableComponent("arcana.status.tainted", super.getTranslatedName());
	}
	
	public boolean isRandomlyTicking(BlockState state){
		return true;
	}
	
	protected void fillStateContainer(StateDefinition.Builder<Block, BlockState> builder){
		super.fillStateContainer(builder);
		builder.add(UNTAINTED);
	}
	
	public BlockState getStateForPlacement(BlockPlaceContext context){
		BlockState placement = super.getStateForPlacement(context);
		return placement != null ? placement.setValue(UNTAINTED, true) : null;
	}
	
	public void randomTick(BlockState state, ServerLevel world, BlockPos pos, Random random){
		// Tainted Farmland yet again
		boolean continueTick = true;
		if(parentBlock == Blocks.FARMLAND){
			if(!state.canSurvive(world, pos)){
				world.setBlockAndUpdate(pos, pushEntitiesUp(world.getBlockState(pos), ArcanaBlocks.TAINTED_SOIL.get().defaultBlockState().setValue(UNTAINTED, state.getValue(UNTAINTED)), world, pos));
				continueTick = false;
			}else if(!hasWater(world, pos) && !world.isRainingAt(pos.above()))
				if(state.getValue(MOISTURE) == 0)
					if(!hasCrops(world, pos)){
						world.setBlockAndUpdate(pos, pushEntitiesUp(world.getBlockState(pos), ArcanaBlocks.TAINTED_SOIL.get().defaultBlockState().setValue(UNTAINTED, state.getValue(UNTAINTED)), world, pos));
						continueTick = false;
					}
		}
		// Tainted grass path decays into tainted soil
		if(parentBlock == Blocks.DIRT_PATH){
			if(!state.canSurvive(world, pos))
				world.setBlockAndUpdate(pos, pushEntitiesUp(world.getBlockState(pos), ArcanaBlocks.TAINTED_SOIL.get().defaultBlockState().setValue(UNTAINTED, state.getValue(UNTAINTED)), world, pos));
			continueTick = false;
		}
		// And tainted grass decays into tainted soil, and spreads.
		// Should also cover mycelium.
		if(parentBlock instanceof SpreadingSnowyDirtBlock){
			if(!isLocationUncovered(state, world, pos)){
				if(!world.isAreaLoaded(pos, 3))
					return; // Forge: prevent loading unloaded chunks when checking neighbor's light and spreading
				world.setBlockAndUpdate(pos, ArcanaBlocks.TAINTED_SOIL.get().defaultBlockState().setValue(UNTAINTED, state.getValue(UNTAINTED)));
			}else if(world.getLightEmission(pos.above()) >= 9){
				BlockState blockstate = defaultBlockState();
				for(int i = 0; i < 4; ++i){
					BlockPos blockpos = pos.offset(random.nextInt(3) - 1, random.nextInt(5) - 3, random.nextInt(3) - 1);
					if(world.getBlockState(blockpos).getBlock() == ArcanaBlocks.TAINTED_SOIL.get() && isLocationValidForGrass(blockstate, world, blockpos))
						world.setBlockAndUpdate(blockpos, blockstate.setValue(SNOWY, world.getBlockState(blockpos.above()).getBlock() == Blocks.SNOW).setValue(UNTAINTED, state.getValue(UNTAINTED)));
				}
			}
			continueTick = false;
		}
		if(continueTick)
			super.randomTick(state, world, pos, random);
		Taint.tickTaintedBlock(state, world, pos, random);
	}
	
	// Tainted Cactus and Sugar Cane
	public boolean canSustainPlant(BlockState state, BlockGetter world, BlockPos pos, Direction facing, IPlantable plantable){
		// BlockState plant = plantable.getPlant(world, pos.offset(facing));
		return super.canSustainPlant(state, world, pos, facing, plantable);
				/*|| ((parentBlock == Blocks.GRASS_BLOCK || parentBlock == Blocks.DIRT || parentBlock == Blocks.COARSE_DIRT || parentBlock == Blocks.PODZOL || parentBlock == Blocks.FARMLAND)
						&& plantable instanceof BushBlock)
				|| (parentBlock == Blocks.CACTUS
						&& (plant.getBlock() == Blocks.CACTUS *//*|| plant.getBlock() == ArcanaBlocks.TAINTED_CACTUS*//*))
				|| (parentBlock == Blocks.SUGAR_CANE
						&& (plant.getBlock() == Blocks.SUGAR_CANE *//*|| plant.getBlock() == ArcanaBlocks.TAINTED_SUGAR_CANE*//*));*/
	}
	
	// Make farmland turn to tainted soil
	public void fallOn(Level world, BlockState state, BlockPos pos, Entity entity, float fallDistance){
		if(parentBlock == Blocks.FARMLAND){
			// Forge: Move logic to Entity#canTrample
			if(!world.isClientSide && onFarmlandTrample(world, pos, Blocks.DIRT.defaultBlockState(), fallDistance, entity))
				world.setBlockAndUpdate(pos, pushEntitiesUp(world.getBlockState(pos), ArcanaBlocks.TAINTED_SOIL.get().defaultBlockState(), world, pos));
			entity.causeFallDamage(fallDistance, 1.0F, DamageSource.FALL);
		}else
			super.fallOn(world, state, pos, entity, fallDistance);
	}
	
	@Nullable
	@Override
	public CreativeModeTab getGroup(){
		return Arcana.TAINT;
	}
	
	@SuppressWarnings("deprecation")
	public void entityInside(BlockState state, Level world, BlockPos pos, Entity entity){
		super.entityInside(state, world, pos, entity);
		startTracking(entity);
	}
	
	public void stepOn(Level world, BlockPos pos, BlockState state, Entity entity){
		super.stepOn(world, pos, state, entity);
		startTracking(entity);
	}
	
	private void startTracking(Entity entity){
		if(entity instanceof LivingEntity){
			// Start tracking taint biome for entity
			TaintTrackable trackable = TaintTrackable.getFrom((LivingEntity)entity);
			if(trackable != null)
				trackable.setTracking(true);
		}
	}
	
	// Private stuff in FarmlandBlock
	// TODO: AT this
	private boolean hasCrops(LevelReader worldIn, BlockPos pos){
		BlockState state = worldIn.getBlockState(pos.above());
		return state.getBlock() instanceof IPlantable && canSustainPlant(state, worldIn, pos, Direction.UP, (IPlantable)state.getBlock());
	}
	
	private static boolean hasWater(LevelReader worldIn, BlockPos pos){
		for(BlockPos blockpos : BlockPos.betweenClosed(pos.offset(-4, 0, -4), pos.offset(4, 1, 4)))
			if(worldIn.getFluidState(blockpos).is(FluidTags.WATER))
				return true;
		return FarmlandWaterManager.hasBlockWaterTicket(worldIn, pos);
	}
	
	// Private stuff in SpreadableSnowyDirtBlock
	private static boolean isLocationUncovered(BlockState state, LevelReader world, BlockPos pos){
		BlockPos blockpos = pos.above();
		BlockState blockstate = world.getBlockState(blockpos);
		if(blockstate.getBlock() == Blocks.SNOW && blockstate.getValue(SnowLayerBlock.LAYERS) == 1)
			return true;
		else{
			int i = BlockLightEngine.getLightBlockInto(world, state, pos, blockstate, blockpos, Direction.UP, blockstate.getLightBlock(world, blockpos));
			return i < world.getMaxLightLevel();
		}
	}
	
	private static boolean isLocationValidForGrass(BlockState state, LevelReader world, BlockPos pos){
		BlockPos blockpos = pos.above();
		return isLocationUncovered(state, world, pos) && !world.getFluidState(blockpos).is(FluidTags.WATER);
	}
}