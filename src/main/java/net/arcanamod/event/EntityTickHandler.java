package net.arcanamod.event;

import com.google.common.collect.Sets;
import net.arcanamod.ArcanaConfig;
import net.arcanamod.capabilities.TaintTrackable;
import net.arcanamod.effects.ArcanaEffects;
import net.arcanamod.items.ArcanaItems;
import net.arcanamod.systems.spell.casts.DelayedCast;
import net.arcanamod.systems.spell.casts.ToggleableCast;
import net.arcanamod.systems.taint.Taint;
import net.arcanamod.world.AuraView;
import net.arcanamod.world.Node;
import net.arcanamod.world.ServerAuraView;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.Advancement;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static net.arcanamod.ArcanaVariables.arcLoc;

@Mod.EventBusSubscriber
public class EntityTickHandler{

	@SubscribeEvent
	public static void playerSleep(PlayerSleepInBedEvent event){
		if (!event.getPlayer().level.isClientSide) {
			ServerPlayer playerEntity = (ServerPlayer) event.getPlayer();
			Advancement interacted = playerEntity.level.getServer().getAdvancements().getAdvancement(arcLoc("interacted_with_magic"));
			Advancement gotArcanum = playerEntity.level.getServer().getAdvancements().getAdvancement(arcLoc("arcanum_accepted"));
			if (playerEntity.getInventory().hasAnyOf(Sets.newHashSet(Items.PAPER, ArcanaItems.SCRIBBLED_NOTES.get())) && playerEntity.getAdvancements().getOrStartProgress(interacted).isDone() && !playerEntity.getAdvancements().getOrStartProgress(gotArcanum).isDone()){
				playerEntity.getAdvancements().getOrStartProgress(gotArcanum).grantProgress("impossible");
				playerEntity.getInventory().add(new ItemStack(ArcanaItems.SCRIBBLED_NOTES_COMPLETE.get()));
			}
		}
	}

	@SubscribeEvent
	public static void tickPlayer(TickEvent.PlayerTickEvent event){
		Player player = event.player;
		
		// Give completed scribbled note when player is near node
		if(player instanceof ServerPlayer && event.side == LogicalSide.SERVER && event.phase == TickEvent.Phase.END){
			ServerPlayer serverPlayerEntity = (ServerPlayer)player;
			// If the player is near a node,
			AuraView view = new ServerAuraView(serverPlayerEntity.getLevel());
			Collection<Node> ranged = new ArrayList<>(view.getNodesWithinAABB(player.getBoundingBox().inflate(2)));

			if(!ranged.isEmpty()){
				// and is holding the scribbled notes item,
				if (player.getInventory().contains(new ItemStack(ArcanaItems.SCRIBBLED_NOTES.get()))) {
					// it switches it for a complete version,
					player.getInventory().setItem(getSlotFor(new ItemStack(ArcanaItems.SCRIBBLED_NOTES.get()), player.getInventory()), new ItemStack(ArcanaItems.SCRIBBLED_NOTES_COMPLETE.get()));
					// and gives them a status message.
					MutableComponent status = new TranslatableComponent("status.get_complete_note").withStyle(ChatFormatting.ITALIC, ChatFormatting.LIGHT_PURPLE);
					serverPlayerEntity.displayClientMessage(status, false);
					// and grant the advancement.
					MinecraftServer server = player.level.getServer();
					Advancement interacted = server.getAdvancements().getAdvancement(arcLoc("interacted_with_magic"));
					((ServerPlayer) player).getAdvancements().getOrStartProgress(interacted).grantProgress("impossible");
					Advancement gotArcanum = server.getAdvancements().getAdvancement(arcLoc("arcanum_accepted"));
					((ServerPlayer) player).getAdvancements().getOrStartProgress(gotArcanum).grantProgress("impossible");
				} else {
					MinecraftServer server = player.level.getServer();
					Advancement interacted = server.getAdvancements().getAdvancement(arcLoc("interacted_with_magic"));
					if (interacted != null)
						if(!serverPlayerEntity.getAdvancements().getOrStartProgress(interacted).isDone()){
							// grant the advancement.
							serverPlayerEntity.getAdvancements().getOrStartProgress(interacted).grantProgress("impossible");

							MutableComponent status = new TranslatableComponent("status.sleep_paper_get_arcanum").withStyle(ChatFormatting.ITALIC, ChatFormatting.LIGHT_PURPLE);
							serverPlayerEntity.displayClientMessage(status, false);
						}
				}
			}
			
			List<DelayedCast.Impl> spellsScheduledToDeletion = new ArrayList<>();
			DelayedCast.delayedCasts.forEach(delayedCast -> {
				if(delayedCast.ticks >= delayedCast.ticksPassed){
					delayedCast.spellEvent.accept(0);
					spellsScheduledToDeletion.add(delayedCast);
				}else
					delayedCast.ticksPassed++;
			});
			DelayedCast.delayedCasts.removeAll(spellsScheduledToDeletion);

			ToggleableCast.toggleableCasts.forEach(toggleableCast -> {
				if(toggleableCast.getSecond().ticks >= toggleableCast.getSecond().ticksPassed){
					toggleableCast.getSecond().spellEvent.accept(0);
					toggleableCast.getSecond().ticksPassed = 0;
				}else
					toggleableCast.getSecond().ticksPassed++;
			});
		}

	}
	
	@SubscribeEvent
	public static void tickEntities(LivingEvent.LivingUpdateEvent event){
		LivingEntity living = event.getEntityLiving();
		TaintTrackable trackable = TaintTrackable.getFrom(living);
		if(trackable != null && trackable.isTracking()){
			if(Taint.isAreaInTaintBiome(living.getOnPos(), living.level)){
				trackable.setInTaintBiome(true);
				trackable.addTimeInTaintBiome(1);
				if(!Taint.isTainted(living.getType()) && trackable.getTimeInTaintBiome() > ArcanaConfig.TAINT_EFFECT_TIME.get())
					living.addEffect(new MobEffectInstance(ArcanaEffects.TAINTED.get(), 5 * 20, 0, true, true));
			}else{
				trackable.setInTaintBiome(false);
				trackable.setTimeInTaintBiome(0);
				trackable.setTracking(false);
			}
		}
	}
	
	private static int getSlotFor(ItemStack stack, Inventory self){
		for(int i = 0; i < self.getContainerSize(); ++i)
			if(!self.getItem(i).isEmpty() && stackEqualExact(stack, self.getItem(i)))
				return i;
		
		return -1;
	}
	
	private static boolean stackEqualExact(ItemStack stack1, ItemStack stack2){
		return stack1.getItem() == stack2.getItem() && ItemStack.isSameItemSameTags(stack1, stack2);
	}
}