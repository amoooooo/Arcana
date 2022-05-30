package net.arcanamod.world;

import net.arcanamod.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;

public final class WorldInteractions {
	private static final Logger LOGGER = LogManager.getLogger();

	protected Level world;

	public static HashMap<Block, Pair<Block,Block>> freezable = new HashMap<>();

	private WorldInteractions(Level world) {
		this.world = world;
	}

	public static WorldInteractions fromWorld(Level world){
		return new WorldInteractions(world);
	}

	public void freezeBlock(BlockPos position){
		Block targetedBlock = world.getBlockState(position).getBlock();
		if (freezable.containsKey(targetedBlock)) {
			Pair<Block,Block> replace = freezable.get(targetedBlock);
			world.setBlockAndUpdate(position, replace.getFirst().defaultBlockState());
			if (replace.getSecond() != Blocks.AIR){
				if (world.getBlockState(position.above()).isAir()){
					world.setBlockAndUpdate(position.above(), replace.getSecond().defaultBlockState());
				}
			}
		}
	}
}
