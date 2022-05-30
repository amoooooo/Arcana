package net.arcanamod.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Random;

public class LightBlock extends Block {
	public LightBlock(Block.Properties properties) {
		super(properties);
	}

	@Override
	public void animateTick(BlockState stateIn, Level worldIn, BlockPos pos, Random rand) {
		super.animateTick(stateIn, worldIn, pos, rand);
		
		if (rand.nextInt(2) == 0)
			worldIn.addParticle(ParticleTypes.FLAME, pos.getX(), pos.getY(), pos.getZ(), 0.0D, 0.0D, 0.0D);
	}
}
