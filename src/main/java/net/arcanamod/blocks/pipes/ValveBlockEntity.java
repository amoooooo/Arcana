package net.arcanamod.blocks.pipes;

import net.arcanamod.blocks.tiles.ArcanaTiles;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ValveBlockEntity extends TubeBlockEntity{
	
	private boolean enabled = true;
	private boolean suppressedByRedstone = false;
	private long lastChangedTick = -1;
	
	public ValveBlockEntity(BlockPos pWorldPosition, BlockState pBlockState){
		super(ArcanaTiles.ASPECT_VALVE_TE.get(), pWorldPosition, pBlockState);
	}
	
	public boolean enabled(){
		return enabled && !suppressedByRedstone;
	}
	
	public boolean enabledByHand(){
		return enabled;
	}
	
	public void setEnabledAndNotify(boolean enabled){
		this.enabled = enabled;
		notifyChange();
	}
	
	public boolean isSuppressedByRedstone(){
		return suppressedByRedstone;
	}
	
	public void setSuppressedByRedstone(boolean suppress){
		if(suppressedByRedstone != suppress){
			suppressedByRedstone = suppress;
			notifyChange();
		}
	}
	
	@SuppressWarnings("ConstantConditions")
	private void notifyChange(){
		lastChangedTick = level.getGameTime();
	}
	
	public long getLastChangedTick(){
		return lastChangedTick;
	}
	
	public void deserializeNBT(CompoundTag nbt){
		enabled = nbt.getBoolean("enabled");
		suppressedByRedstone = nbt.getBoolean("suppressed");
	}
	
	public CompoundTag serializeNBT(){
		// save if enabled
		CompoundTag nbt = new CompoundTag();
		nbt.putBoolean("enabled", enabled);
		nbt.putBoolean("suppressed", isSuppressedByRedstone());
		return nbt;
	}
	
	@Nonnull
	public CompoundTag getUpdateTag(){
		CompoundTag nbt = super.getUpdateTag();
		nbt.putBoolean("suppressed", isSuppressedByRedstone());
		return nbt;
	}
	
	public void handleUpdateTag(@Nonnull CompoundTag tag){
		super.handleUpdateTag(tag);
		setSuppressedByRedstone(tag.getBoolean("suppressed"));
	}
	
	@Nullable
	public ClientboundBlockEntityDataPacket getUpdatePacket(){
		return ClientboundBlockEntityDataPacket.create(this);
	}
	
	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt){
		handleUpdateTag(pkt.getTag());
	}
}