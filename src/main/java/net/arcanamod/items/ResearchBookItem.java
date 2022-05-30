package net.arcanamod.items;

import net.arcanamod.client.ClientUtils;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class ResearchBookItem extends Item {
	public final ResourceLocation book;
	
	public ResearchBookItem(Properties properties, ResourceLocation book) {
		super(properties);
		this.book = book;
	}
	
	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
		player.getItemInHand(hand).getOrCreateTag().putBoolean("open", true);
		ClientUtils.openResearchBookUI(book, null, player.getItemInHand(hand));
		return new InteractionResultHolder<>(InteractionResult.SUCCESS, player.getItemInHand(hand));
	}
}