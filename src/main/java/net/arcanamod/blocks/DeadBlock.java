package net.arcanamod.blocks;

import net.arcanamod.Arcana;
import net.arcanamod.blocks.bases.GroupedBlock;
import net.arcanamod.systems.taint.Taint;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.Random;

public class DeadBlock extends DelegatingBlock implements GroupedBlock {

	@Deprecated() // Use Taint#deadOf instead
	public DeadBlock(Block block){
		super(block);
		Taint.addDeadMapping(block, this);
	}
	/**
	 * Called periodically clientside on blocks near the player to show effects (like furnace fire particles). Note that
	 * this method is unrelated to randomTick and needsRandomTick, and will always be called regardless
	 * of whether the block can receive random update ticks
	 */
	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState stateIn, Level worldIn, BlockPos pos, Random rand) {
		super.animateTick(stateIn, worldIn, pos, rand);
		if (stateIn.getBlock()==ArcanaBlocks.DEAD_GRASS_BLOCK.get())
			if (rand.nextInt(4) == 0) {
				worldIn.addParticle(ParticleTypes.MYCELIUM, (double)pos.getX() + (double)rand.nextFloat(), (double)pos.getY() + 1.1D, (double)pos.getZ() + (double)rand.nextFloat(), 0.0D, 0.0D, 0.0D);
			}
		//TODO: If mod ported to 1.16 change it to minecraft:ash
	}

	@Nullable
	@Override
	public CreativeModeTab getGroup(){
		return Arcana.TAINT;
	}
}