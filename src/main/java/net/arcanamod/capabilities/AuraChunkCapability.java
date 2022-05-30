package net.arcanamod.capabilities;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static net.arcanamod.Arcana.arcLoc;

public class AuraChunkCapability{

	public static Capability<AuraChunk> NODE_CHUNK_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});
	
	public static final ResourceLocation KEY = arcLoc("node_chunk_capability");
	
	public static void init(){
		MinecraftForge.EVENT_BUS.addListener(AuraChunkCapability::onRegisterCapabilities);
		MinecraftForge.EVENT_BUS.addGenericListener(LevelChunk.class, AuraChunkCapability::onAttachCapabilities);
	}

	public static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
		event.register(AuraChunk.class);
	}

	public static void onAttachCapabilities(AttachCapabilitiesEvent<LevelChunk> event) {
		AuraChunkCapability.Provider provider = new AuraChunkCapability.Provider();
		event.addCapability(KEY, provider);
		event.addListener(provider::invalidate);
	}
	
	public static class Provider implements ICapabilitySerializable<CompoundTag>{
		
		private final AuraChunk cap = new AuraChunkImpl();
		private final LazyOptional<AuraChunk> optionalHandler = LazyOptional.of(() -> cap);

		public void invalidate() {
			optionalHandler.invalidate();
		}
		
		public CompoundTag serializeNBT(){
			return cap.serializeNBT();
		}
		
		public void deserializeNBT(CompoundTag nbt){
			cap.deserializeNBT(nbt);
		}
		
		@Nonnull
		public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction side){
			return NODE_CHUNK_CAPABILITY.orEmpty(capability, optionalHandler);
		}
	}
}