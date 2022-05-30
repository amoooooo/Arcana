package net.arcanamod.blocks.multiblocks.taint_scrubber;

import net.arcanamod.blocks.ArcanaBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;

import javax.annotation.Nonnull;

@SuppressWarnings("deprecation")
public class BoosterTaintScrubberExtensionBlock extends Block implements ITaintScrubberExtension{
	
	public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
	
	public BoosterTaintScrubberExtensionBlock(Block.Properties properties){
		super(properties);
		this.registerDefaultState(this.getStateDefinition().any().setValue(FACING, Direction.NORTH));
	}
	
	@Override
	public boolean isValidConnection(Level world, BlockPos pos){
		if(world.getBlockState(pos.north()).getBlock().equals(ArcanaBlocks.TAINT_SCRUBBER_MK2.get()))
			return true;
		if(world.getBlockState(pos.south()).getBlock().equals(ArcanaBlocks.TAINT_SCRUBBER_MK2.get()))
			return true;
		if(world.getBlockState(pos.west()).getBlock().equals(ArcanaBlocks.TAINT_SCRUBBER_MK2.get()))
			return true;
		return world.getBlockState(pos.east()).getBlock().equals(ArcanaBlocks.TAINT_SCRUBBER_MK2.get());
	}
	
	/**
	 * It is performed if this block is found by TaintScrubber.
	 *
	 * @param world
	 * 		World
	 * @param pos
	 * 		Position of extension
	 */
	@Override
	public void sendUpdate(Level world, BlockPos pos){
	
	}
	
	@Override
	public void run(Level world, BlockPos pos, CompoundTag compound){}
	
	@Override
	public CompoundTag getShareableData(CompoundTag compound){
		compound.putInt("speed", compound.getInt("speed") + 1);
		return compound;
	}
	
	public BlockState getStateForPlacement(BlockPlaceContext context){
		return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection());
	}
	
	/**
	 * Returns the blockstate with the given rotation from the passed blockstate. If inapplicable, returns the passed
	 * blockstate.
	 */
	@Nonnull
	public BlockState rotate(BlockState state, Rotation rot){
		return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
	}
	
	/**
	 * Returns the blockstate with the given mirror of the passed blockstate. If inapplicable, returns the passed
	 * blockstate.
	 */
	@Nonnull
	public BlockState mirror(BlockState state, Mirror mirrorIn){
		return state.rotate(mirrorIn.getRotation(state.getValue(FACING)));
	}
	
	protected void createBlockStateDefinition(@Nonnull StateDefinition.Builder<Block, BlockState> builder){
		super.createBlockStateDefinition(builder);
		builder.add(FACING);
	}
}