package net.arcanamod.blocks.bases;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class PillarBlock extends Block {
	public static final BooleanProperty UP = BooleanProperty.create("up");
	public static final BooleanProperty DOWN = BooleanProperty.create("down");
	
	public PillarBlock(Properties properties){
		super(properties);
		this.registerDefaultState(this.stateDefinition.any()
				.setValue(UP, Boolean.FALSE)
				.setValue(DOWN, Boolean.FALSE));
	}
	
	@Nonnull
	public BlockState getStateForPlacement(BlockPlaceContext context){
		BlockPos blockpos = context.getClickedPos();
		Level world = context.getLevel();
		return defaultBlockState()
				.setValue(UP, world.getBlockState(blockpos.above()).getBlock() instanceof PillarBlock)
				.setValue(DOWN, world.getBlockState(blockpos.below()).getBlock() instanceof PillarBlock);
	}
	
	/*@Override
	public boolean isFlammable(BlockState state, IBlockReader world, BlockPos pos, Direction face){
		return false;
	}
	
	@SuppressWarnings("deprecation")
	@Nonnull
	@Override
	public BlockState updatePostPlacement(BlockState state, Direction facing, BlockState facingState, IWorld world, BlockPos pos, BlockPos facingPos){
		UpdatePillars(((World)world), pos);
		return super.updatePostPlacement(state, facing, facingState, world, pos, facingPos);
	}*/
	
	public void UpdatePillars(Level worldIn, BlockPos pos){
		if(worldIn.getBlockState(pos.below()).getBlock() instanceof PillarBlock)
			worldIn.setBlockAndUpdate(pos.below(), worldIn.getBlockState(pos.below()).setValue(UP, worldIn.getBlockState(pos).getBlock() instanceof PillarBlock).setValue(DOWN, worldIn.getBlockState(pos.below().below()).getBlock() instanceof PillarBlock));
		if(worldIn.getBlockState(pos.above()).getBlock() instanceof PillarBlock)
			worldIn.setBlockAndUpdate(pos.above(), worldIn.getBlockState(pos.above()).setValue(UP, worldIn.getBlockState(pos.above().above()).getBlock() instanceof PillarBlock).setValue(DOWN, worldIn.getBlockState(pos).getBlock() instanceof PillarBlock));
	}
	
	@Override
	public void wasExploded(Level worldIn, BlockPos pos, Explosion explosionIn){
		UpdatePillars(worldIn, pos);
		super.wasExploded(worldIn, pos, explosionIn);
	}
	
	@Override
	public void destroy(LevelAccessor worldIn, BlockPos pos, BlockState state){
		UpdatePillars(((Level)worldIn), pos);
		super.destroy(worldIn, pos, state);
	}
	
	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder){
		builder.add(UP);
		builder.add(DOWN);
		super.createBlockStateDefinition(builder);
	}
}
