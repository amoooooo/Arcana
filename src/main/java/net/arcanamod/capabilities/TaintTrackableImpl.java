package net.arcanamod.capabilities;

import net.minecraft.nbt.CompoundTag;

import javax.annotation.Nonnull;

public class TaintTrackableImpl implements TaintTrackable{
	
	boolean tracking = false, inTaintBiome = false;
	int timeInTaintBiome = 0;
	
	public boolean isTracking(){
		return tracking;
	}
	
	public void setTracking(boolean tracking){
		this.tracking = tracking;
	}
	
	public boolean isInTaintBiome(){
		return inTaintBiome;
	}
	
	public void setInTaintBiome(boolean inTaintBiome){
		this.inTaintBiome = inTaintBiome;
	}
	
	public int getTimeInTaintBiome(){
		return timeInTaintBiome;
	}
	
	public void setTimeInTaintBiome(int timeInTaintBiome){
		this.timeInTaintBiome = timeInTaintBiome;
	}
	
	public void addTimeInTaintBiome(int timeInTaintBiome){
		this.timeInTaintBiome += timeInTaintBiome;
	}
	
	public CompoundTag serializeNBT(){
		CompoundTag nbt = new CompoundTag();
		nbt.putBoolean("tracking", isTracking());
		nbt.putBoolean("inTaintBiome", inTaintBiome);
		nbt.putInt("timeInTaintBiome", timeInTaintBiome);
		return nbt;
	}
	
	public void deserializeNBT(@Nonnull CompoundTag data){
		tracking = data.getBoolean("tracking");
		inTaintBiome = data.getBoolean("inTaintBiome");
		timeInTaintBiome = data.getInt("timeInTaintBiome");
	}
}