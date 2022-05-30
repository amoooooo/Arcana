package net.arcanamod.blocks.bases;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.properties.Half;

import javax.annotation.ParametersAreNonnullByDefault;

// same BS as the sapling block - probably just going to AT these
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ATrapDoorBlock extends TrapDoorBlock {
	
	public ATrapDoorBlock(Block.Properties properties){
		super(properties);
		this.registerDefaultState(this.defaultBlockState()
				.setValue(FACING, Direction.NORTH)
				.setValue(OPEN, Boolean.FALSE)
				.setValue(HALF, Half.BOTTOM)
				.setValue(POWERED, Boolean.FALSE)
				.setValue(WATERLOGGED, Boolean.FALSE));
	}
}
