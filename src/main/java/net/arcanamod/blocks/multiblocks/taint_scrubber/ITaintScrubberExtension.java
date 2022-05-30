package net.arcanamod.blocks.multiblocks.taint_scrubber;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;


public interface ITaintScrubberExtension{
	
	/**
	 * Returns true if an extension is in a valid position.
	 *
	 * @param world
	 * 		World
	 * @param pos
	 * 		Position of extension
	 * @return If an extension is in a correct position.
	 */
	boolean isValidConnection(Level world, BlockPos pos);
	
	/**
	 * Called by a taint scrubber that this extension is connected to.
	 *
	 * @param world
	 * 		World
	 * @param pos
	 * 		Position of extension
	 */
	void sendUpdate(Level world, BlockPos pos);
	
	/**
	 * Runs extension action.
	 *
	 * @param world
	 * 		World
	 * @param pos
	 * 		Position of extension
	 */
	void run(Level world, BlockPos pos, CompoundTag compound);
	
	CompoundTag getShareableData(CompoundTag compound);
}