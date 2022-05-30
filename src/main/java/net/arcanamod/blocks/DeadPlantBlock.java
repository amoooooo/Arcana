package net.arcanamod.blocks;

import net.arcanamod.systems.taint.Taint;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.IForgeShearable;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.PlantType;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Random;

public class DeadPlantBlock extends DeadBlock implements IPlantable, BonemealableBlock, IForgeShearable{
	public DeadPlantBlock(Block parent) {
		super(parent);
	}

	// Growable methods

	@Nullable
	private BonemealableBlock getGrowable(){
		return parentBlock instanceof BonemealableBlock ? (BonemealableBlock)parentBlock : null;
	}

	public boolean isValidBonemealTarget(BlockGetter world, BlockPos pos, BlockState state, boolean isClient){
		return getGrowable() != null && getGrowable().isValidBonemealTarget(world, pos, state, isClient);
	}

	public boolean isBonemealSuccess(Level world, Random rand, BlockPos pos, BlockState state){
		return getGrowable() != null && getGrowable().isBonemealSuccess(world, rand, pos, state);
	}

	public void performBonemeal(ServerLevel world, Random rand, BlockPos pos, BlockState state){
		if(getGrowable() != null)
			getGrowable().performBonemeal(world, rand, pos, state);
	}

	// Plantable methods

	@Nullable
	private IPlantable getPlantable(){
		return parentBlock instanceof IPlantable ? (IPlantable)parentBlock : null;
	}

	public PlantType getPlantType(BlockGetter world, BlockPos pos){
		return getPlantable() != null ? getPlantable().getPlantType(world, pos) : PlantType.PLAINS;
	}

	public BlockState getPlant(BlockGetter world, BlockPos pos){
		return getPlantable() != null ? switchBlock(getPlantable().getPlant(world, pos), this) : defaultBlockState();
	}
	
	// Fix BushBlock
	// TODO: AT instead of reflection
	// func_200014_a_(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/IBlockReader;Lnet/minecraft/util/math/BlockPos;)Z
	private static final Method isValidGround = ObfuscationReflectionHelper.findMethod(BushBlock.class, "func_200014_a_", BlockState.class, BlockGetter.class, BlockPos.class);

	private static boolean invokeIsValidGround(BushBlock block, BlockState state, BlockGetter reader, BlockPos pos){
		isValidGround.setAccessible(true);
		boolean ret = false;
		try{
			ret = (boolean)isValidGround.invoke(block, state, reader, pos);
		}catch(IllegalAccessException | InvocationTargetException e){
			e.printStackTrace();
		}
		isValidGround.setAccessible(false);
		return ret;
	}

	public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos){
		BlockState bushBaseState = world.getBlockState(pos.below());
		return super.canSurvive(state, world, pos)
				|| (parentBlock instanceof BushBlock && Taint.getPureOfBlock(bushBaseState.getBlock()) != null && invokeIsValidGround((BushBlock)parentBlock, switchBlock(bushBaseState, Taint.getPureOfBlock(bushBaseState.getBlock())), world, pos))
				|| (parentBlock instanceof BushBlock && Taint.getLivingOfBlock(bushBaseState.getBlock()) != null && invokeIsValidGround((BushBlock)parentBlock, switchBlock(bushBaseState, Taint.getLivingOfBlock(bushBaseState.getBlock())), world, pos));
	}
}
