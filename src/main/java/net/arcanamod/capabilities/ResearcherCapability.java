package net.arcanamod.capabilities;

import net.arcanamod.event.WorldTickHandler;
import net.arcanamod.network.Connection;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static net.arcanamod.Arcana.arcLoc;

public class ResearcherCapability{

	public static Capability<Researcher> RESEARCHER_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});
	
	public static final ResourceLocation KEY = arcLoc("researcher_capability");
	
	public static void init(){
		MinecraftForge.EVENT_BUS.addListener(ResearcherCapability::onRegisterCapabilities);
		MinecraftForge.EVENT_BUS.addGenericListener(Entity.class, ResearcherCapability::onAttachCapabilities);
		MinecraftForge.EVENT_BUS.addListener(ResearcherCapability::playerClone);
	}

	public static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
		event.register(Researcher.class);
	}

	public static void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
		if (event.getObject() instanceof Player) {
			ResearcherCapability.Provider provider = new ResearcherCapability.Provider();
			event.addCapability(KEY, provider);
			event.addListener(provider::invalidate);
		}
	}

	public static void playerClone(PlayerEvent.Clone event) {
		Researcher.getFrom(event.getPlayer()).deserializeNBT(Researcher.getFrom(event.getOriginal()).serializeNBT());
		WorldTickHandler.onTick.add(world -> Connection.sendSyncPlayerResearch(Researcher.getFrom(event.getPlayer()), (ServerPlayer) event.getPlayer()));
	}
	
	public static class Provider implements ICapabilitySerializable<CompoundTag>{
		
		private final Researcher cap = new ResearcherImpl();
		private final LazyOptional<Researcher> optionalHandler = LazyOptional.of(() -> cap);

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
			// if Capability<T> == Capability<Researcher>, then T is Researcher, so this won't cause issues.
			// return capability == RESEARCHER_CAPABILITY ? LazyOptional.of(() -> (T)cap) : LazyOptional.empty();
			return RESEARCHER_CAPABILITY.orEmpty(capability, optionalHandler);
		}
	}
}