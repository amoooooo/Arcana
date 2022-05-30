package net.arcanamod.aspects.handlers;

import net.arcanamod.Arcana;
import net.arcanamod.capabilities.Researcher;
import net.arcanamod.capabilities.ResearcherCapability;
import net.arcanamod.capabilities.ResearcherImpl;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class AspectHandlerCapability {

	public static Capability<AspectHandler> ASPECT_HANDLER = CapabilityManager.get(new CapabilityToken<>(){});

	public static final ResourceLocation KEY = new ResourceLocation(Arcana.MODID, "new_aspect_handler_capability");

	public static void init(){
		MinecraftForge.EVENT_BUS.addListener(AspectHandlerCapability::onRegisterCapabilities);
		MinecraftForge.EVENT_BUS.addGenericListener(Entity.class, AspectHandlerCapability::onAttachCapabilities);
	}

	public static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
		event.register(AspectHandler.class);
	}

	public static void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
		AspectHandlerCapability.Provider provider = new AspectHandlerCapability.Provider();
		event.addCapability(KEY, provider);
		event.addListener(provider::invalidate);
	}

	public static class Provider implements ICapabilitySerializable<CompoundTag> {

		private final AspectHandler cap = new AspectBattery();
		private final LazyOptional<AspectHandler> optionalHandler = LazyOptional.of(() -> cap);

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
			return ASPECT_HANDLER.orEmpty(capability, optionalHandler);
		}
	}
}
