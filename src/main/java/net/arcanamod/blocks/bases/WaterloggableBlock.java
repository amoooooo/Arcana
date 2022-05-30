package net.arcanamod.blocks.bases;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@SuppressWarnings("deprecation")
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class WaterloggableBlock extends Block implements SimpleWaterloggedBlock {
	
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	
	public WaterloggableBlock(Properties properties){
		super(properties);
		registerDefaultState(this.stateDefinition.any().setValue(WATERLOGGED, Boolean.FALSE));
	}
	
	@Nonnull
	public BlockState getStateForPlacement(BlockPlaceContext context){
		BlockPos blockpos = context.getClickedPos();
		FluidState fluidstate = context.getLevel().getFluidState(blockpos);
		return defaultBlockState().setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);
	}
	
	/*public BlockState updatePostPlacement(BlockState state, Direction facing, BlockState facingState, LevelAccessor world, BlockPos currentPos, BlockPos facingPos){
		if(state.getValue(WATERLOGGED))
			world.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
		return super.updatePostPlacement(state, facing, facingState, world, currentPos, facingPos);
	}*/
	
	protected void fillStateContainer(StateDefinition.Builder<Block, BlockState> builder){
		builder.add(WATERLOGGED);
	}
	
	public FluidState getFluidState(BlockState state){
		return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}
}