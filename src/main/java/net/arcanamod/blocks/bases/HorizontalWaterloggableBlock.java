package net.arcanamod.blocks.bases;

import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;

import javax.annotation.Nonnull;

@SuppressWarnings("deprecation")
public class HorizontalWaterloggableBlock extends WaterloggableBlock{
	
	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	
	public HorizontalWaterloggableBlock(Properties properties){
		super(properties);
		this.registerDefaultState(this.stateDefinition.any()
				.setValue(WATERLOGGED, Boolean.FALSE)
				.setValue(FACING, Direction.NORTH));
	}
	
	@Nonnull
	public BlockState getStateForPlacement(@Nonnull BlockPlaceContext context){
		return super.getStateForPlacement(context).setValue(FACING, context.getHorizontalDirection());
	}
	
	protected void fillStateContainer(@Nonnull StateDefinition.Builder<Block, BlockState> builder){
		super.fillStateContainer(builder);
		builder.add(FACING);
	}
	
	@Nonnull
	public BlockState rotate(@Nonnull BlockState state, @Nonnull Rotation rot) {
		return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
	}
	
	@Nonnull
	public BlockState mirror(@Nonnull BlockState state, @Nonnull Mirror mirrorIn){
		return state.rotate(mirrorIn.getRotation(state.getValue(FACING)));
	}
}
