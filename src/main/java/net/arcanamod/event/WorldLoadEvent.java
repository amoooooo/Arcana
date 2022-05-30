package net.arcanamod.event;

import com.mojang.brigadier.CommandDispatcher;
import net.arcanamod.Arcana;
import net.arcanamod.ArcanaConfig;
import net.arcanamod.aspects.ItemAspectRegistry;
import net.arcanamod.capabilities.Researcher;
import net.arcanamod.commands.FillAspectCommand;
import net.arcanamod.commands.NodeCommand;
import net.arcanamod.commands.ResearchCommand;
import net.arcanamod.commands.TaintCommand;
import net.arcanamod.items.ArcanaItems;
import net.arcanamod.network.Connection;
import net.arcanamod.network.PkSyncResearch;
import net.arcanamod.systems.research.ResearchBooks;
import net.arcanamod.systems.research.ResearchLoader;
import net.arcanamod.world.WorldInteractionsRegistry;
import net.minecraft.advancements.Advancement;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.network.PacketDistributor;

import static net.arcanamod.ArcanaVariables.arcLoc;

/**
 * Class for handling any events that occur upon world load
 *
 * @author Atlas
 */
@EventBusSubscriber
public class WorldLoadEvent{
	
	@SubscribeEvent
	public static void onWorldLoad(PlayerEvent.PlayerLoggedInEvent event){
		// It's definitely an ServerPlayerEntity.
		Connection.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer)event.getPlayer()), new PkSyncResearch(ResearchBooks.books, ResearchBooks.puzzles));
		Researcher researcher = Researcher.getFrom(event.getPlayer());
		Connection.sendSyncPlayerResearch(researcher, (ServerPlayer)event.getPlayer());
		
		// If the player should get a one-time scribbled notes,
		if(ArcanaConfig.SPAWN_WITH_NOTES.get()){
			ServerPlayer player = (ServerPlayer)event.getPlayer();
			MinecraftServer server = player.level.getServer();
			if(server != null){
				Advancement hasNote = server.getAdvancements().getAdvancement(arcLoc("obtained_note"));
				// and they haven't already got them this way,
				if(hasNote != null)
					if(!player.getAdvancements().getOrStartProgress(hasNote).isDone()){
						// give them the notes,
						player.addItem(new ItemStack(ArcanaItems.SCRIBBLED_NOTES.get()));
						// and grant the advancement, so they never get it again.
						player.getAdvancements().getOrStartProgress(hasNote).grantProgress("impossible");
					}
			}
		}
	}
	
	@SubscribeEvent
	public static void serverAboutToStart(AddReloadListenerEvent event){
		//IReloadableResourceManager manager = (IReloadableResourceManager)event.getServer().getDataPackRegistries().getResourceManager();
		event.addListener(Arcana.researchManager = new ResearchLoader());
		event.addListener(Arcana.itemAspectRegistry = new ItemAspectRegistry(event.getServerResources().getRecipeManager()));
		event.addListener(Arcana.worldInteractionsRegistry = new WorldInteractionsRegistry());
	}
	
	@SubscribeEvent
	public static void commandRegister(RegisterCommandsEvent event){
		CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
		ResearchCommand.register(dispatcher);
		FillAspectCommand.register(dispatcher);
		NodeCommand.register(dispatcher);
		TaintCommand.register(dispatcher);
	}
}