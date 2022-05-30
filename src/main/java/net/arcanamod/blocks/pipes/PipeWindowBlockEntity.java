package net.arcanamod.blocks.pipes;

import net.arcanamod.blocks.tiles.ArcanaTiles;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;

public class PipeWindowBlockEntity extends TubeBlockEntity {
	
	private long lastTransferTime = -1;
	
	public PipeWindowBlockEntity(BlockPos pWorldPosition, BlockState pBlockState){
		super(ArcanaTiles.ASPECT_WINDOW_TE.get(), pWorldPosition, pBlockState);
	}
	
	public void deserializeNBT(CompoundTag nbt){
		lastTransferTime = nbt.getInt("lastTransferTime");
	}
	
	public CompoundTag serializeNBT(){
		// save if enabled
		CompoundTag nbt = new CompoundTag();
		nbt.putLong("lastTransferTime", lastTransferTime);
		return nbt;
	}
	
	public long getLastTransferTime(){
		return lastTransferTime;
	}
	
	public void addSpeck(AspectSpeck speck){
		super.addSpeck(speck);
		if(!speck.payload.isEmpty())
			lastTransferTime = getLevel().getGameTime();
	}
}