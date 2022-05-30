package net.arcanamod.blocks.pipes;

import net.arcanamod.aspects.handlers.AspectHandlerCapability;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@SuppressWarnings("deprecation")
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class TubeBlock extends PipeBlock implements EntityBlock {
	
	public TubeBlock(BlockBehaviour.Properties properties){
		super(.1875f, properties);
		this.registerDefaultState(this.stateDefinition.any()
				.setValue(NORTH, Boolean.FALSE)
				.setValue(EAST, Boolean.FALSE)
				.setValue(SOUTH, Boolean.FALSE)
				.setValue(WEST, Boolean.FALSE)
				.setValue(UP, Boolean.FALSE)
				.setValue(DOWN, Boolean.FALSE));
	}
	
	private boolean isVisHolder(BlockGetter world, BlockPos pos){
		Block block = world.getBlockState(pos).getBlock();
		BlockEntity tile = world.getBlockEntity(pos);
		return (tile != null && tile.getCapability(AspectHandlerCapability.ASPECT_HANDLER).isPresent()) || block instanceof TubeBlock;
	}
	
	// Blockstate stuff
	
	public BlockState getStateForPlacement(BlockPlaceContext context){
		return this.makeConnections(context.getLevel(), context.getClickedPos());
	}
	
	public BlockState makeConnections(BlockGetter world, BlockPos pos){
		return this.defaultBlockState()
				.setValue(DOWN, isVisHolder(world, pos.below()))
				.setValue(UP, isVisHolder(world, pos.above()))
				.setValue(NORTH, isVisHolder(world, pos.north()))
				.setValue(EAST, isVisHolder(world, pos.east()))
				.setValue(SOUTH, isVisHolder(world, pos.south()))
				.setValue(WEST, isVisHolder(world, pos.west()));
	}
	
	/**
	 * Update the provided state given the provided neighbor facing and neighbor state, returning a new state.
	 * For example, fences make their connections to the passed in state if possible, and wet concrete powder immediately
	 * returns its solidified counterpart.
	 * Note that this method should ideally consider only the specific face passed in.
	 */
	public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor world, BlockPos currentPos, BlockPos facingPos){
		boolean flag = isVisHolder(world, facingPos);
		return state.setValue(PROPERTY_BY_DIRECTION.get(facing), flag);
	}
	
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder){
		builder.add(NORTH, EAST, SOUTH, WEST, UP, DOWN);
	}
	
//	public boolean allowsMovement(BlockState state, BlockGetter worldIn, BlockPos pos, PathType type){
//		return false;
//	}
	
//	@Override
//	public boolean hasBlockEntity(BlockState state){
//		return true;
//	}
	
	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state){
		return new TubeBlockEntity(pos, state);
	}
}