package net.arcanamod.blocks.bases;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * SaplingBlock's constructor is protected, for some reason.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ASaplingBlock extends SaplingBlock {
	
	public ASaplingBlock(AbstractTreeGrower tree, Block.Properties properties){
		super(tree, properties);
	}
}