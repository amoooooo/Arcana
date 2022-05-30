package net.arcanamod.blocks;

import net.arcanamod.blocks.bases.HorizontalWaterloggableBlock;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.text.html.parser.Entity;

@SuppressWarnings("deprecation")
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class StatueBlock extends HorizontalWaterloggableBlock{
	
	protected static final VoxelShape SHAPE = box(1, 0, 1, 15, 7 + 16, 15);
	
	public StatueBlock(Properties properties){
		super(properties);
	}
	
	public boolean collisionExtendsVertically(BlockState state, BlockGetter world, BlockPos pos, Entity collidingEntity){
		return true;
	}
	
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context){
		return SHAPE;
	}
}
