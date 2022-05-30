package net.arcanamod.client.research.impls;

import com.google.common.collect.Streams;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import net.arcanamod.client.research.RequirementRenderer;
import net.arcanamod.systems.research.impls.ItemTagRequirement;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import static net.minecraft.world.item.TooltipFlag.Default.ADVANCED;
import static net.minecraft.world.item.TooltipFlag.Default.NORMAL;

public class ItemTagRequirementRenderer implements RequirementRenderer<ItemTagRequirement>{
	
	public void render(PoseStack matrices, int x, int y, ItemTagRequirement requirement, int ticks, float partialTicks, Player player){
		// pick an item
		List<Item> items = Streams.stream(Registry.ITEM.getTagOrEmpty(requirement.getTag())).map(itemHolder -> itemHolder.value()).toList();
		ItemStack stack = new ItemStack(items.get((ticks / 30) % items.size()));

		Lighting.setupForFlatItems();
		ItemRenderer renderer = Minecraft.getInstance().getItemRenderer();
		renderer.renderAndDecorateItem(stack, x, y);
	}
	
	public List<Component> tooltip(ItemTagRequirement requirement, Player player){
		// pick an item
		List<Item> items = Streams.stream(Registry.ITEM.getTagOrEmpty(requirement.getTag())).map(itemHolder -> itemHolder.value()).toList();
		ItemStack stack = new ItemStack(items.get((player.tickCount / 30) % items.size()));
		
		List<net.minecraft.network.chat.Component> tooltip = stack.getTooltipLines(Minecraft.getInstance().player, Minecraft.getInstance().options.advancedItemTooltips ? ADVANCED : NORMAL);
		if(requirement.getAmount() != 0)
			tooltip.set(0, new TranslatableComponent("requirement.item.num", requirement.getAmount(), tooltip.get(0)));
		else
			tooltip.set(0, new TranslatableComponent("requirement.item.have", tooltip.get(0)));
		tooltip.add(new TranslatableComponent("requirement.tag.accepts_any", requirement.getTagName().toString()).withStyle(ChatFormatting.DARK_GRAY));
		return tooltip;
	}
	
	public boolean shouldDrawTickOrCross(ItemTagRequirement requirement, int amount){
		return amount == 0;
	}
}