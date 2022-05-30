package net.arcanamod.blocks;

import net.arcanamod.blocks.tiles.VacuumBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@SuppressWarnings({"deprecation"})
public class VacuumBlock extends Block implements EntityBlock {
 
	public VacuumBlock(Properties properties){
		super(properties);
	}
	
	@Nonnull
    @Override
	public RenderShape getRenderShape(@Nonnull BlockState state){
		return RenderShape.INVISIBLE;
	}
	
	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state){
		return new VacuumBlockEntity(pos, state);
	}
	
//	@Override
//	public boolean hasTileEntity(BlockState state){
//		return true;
//	}
}
