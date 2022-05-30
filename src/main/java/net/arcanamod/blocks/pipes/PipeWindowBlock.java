package net.arcanamod.blocks.pipes;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@SuppressWarnings("deprecation")
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class PipeWindowBlock extends TubeBlock{
	
	public PipeWindowBlock(Properties properties){
		super(properties);
	}
	
	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState){
		return new PipeWindowBlockEntity(pPos, pState);
	}

	@Override
	public boolean hasAnalogOutputSignal(BlockState state){
		return true;
	}
	
	@Override
	public int getAnalogOutputSignal(BlockState block, Level world, BlockPos pos){
		BlockEntity te = world.getBlockEntity(pos);
		if(te instanceof PipeWindowBlockEntity){
			PipeWindowBlockEntity window = (PipeWindowBlockEntity) te;
			int elapsed = (int)(te.getLevel().getGameTime() - window.getLastTransferTime());
			return elapsed > 12 ? 0 : 15;
		}
		return 0;
	}
}