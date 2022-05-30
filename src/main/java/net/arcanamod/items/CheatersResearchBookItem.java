package net.arcanamod.items;

import net.arcanamod.capabilities.Researcher;
import net.arcanamod.network.Connection;
import net.arcanamod.network.PkModifyResearch;
import net.arcanamod.systems.research.ResearchBooks;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class CheatersResearchBookItem extends ResearchBookItem {
	
	public CheatersResearchBookItem(Properties properties, ResourceLocation book) {
		super(properties, book);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand){
		// also grant all research
		if(!world.isClientSide() && player instanceof ServerPlayer)
			ResearchBooks.streamEntries().forEach(entry -> {
				Researcher from = Researcher.getFrom(player);
				if(from != null && !entry.meta().contains("locked")){
					from.completeEntry(entry);
					Connection.sendModifyResearch(PkModifyResearch.Diff.complete, entry.key(), (ServerPlayer)player);
				}
			});
		return super.use(world, player, hand);
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flag){
		super.appendHoverText(stack, world, tooltip, flag);
		tooltip.add(new TranslatableComponent("item.arcana.cheaters_arcanum.desc"));
	}
}