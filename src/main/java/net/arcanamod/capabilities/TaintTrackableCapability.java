package net.arcanamod.capabilities;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static net.arcanamod.Arcana.arcLoc;

public class TaintTrackableCapability{

	public static Capability<TaintTrackable> TAINT_TRACKABLE_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});
	
	public static final ResourceLocation KEY = arcLoc("taint_trackable_capability");
	
	public static void init(){
		MinecraftForge.EVENT_BUS.addListener(TaintTrackableCapability::onRegisterCapabilities);
		MinecraftForge.EVENT_BUS.addGenericListener(Entity.class, TaintTrackableCapability::onAttachCapabilities);
	}

	public static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
		event.register(TaintTrackable.class);
	}

	public static void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
		if (event.getObject() instanceof LivingEntity) {
			TaintTrackableCapability.Provider provider = new TaintTrackableCapability.Provider();
			event.addCapability(KEY, provider);
			event.addListener(provider::invalidate);
		}
	}
	
	public static class Provider implements ICapabilitySerializable<CompoundTag>{
		
		private final TaintTrackable cap = new TaintTrackableImpl();
		private final LazyOptional<TaintTrackable> optionalHandler = LazyOptional.of(() -> cap);

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
			return TAINT_TRACKABLE_CAPABILITY.orEmpty(capability, optionalHandler);
		}
	}
}