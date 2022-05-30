package net.arcanamod.client.research.impls;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import net.arcanamod.client.research.RequirementRenderer;
import net.arcanamod.systems.research.impls.ItemRequirement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Player;

import java.util.List;

import static net.minecraft.world.item.TooltipFlag.Default.ADVANCED;
import static net.minecraft.world.item.TooltipFlag.Default.NORMAL;

public class ItemRequirementRenderer implements RequirementRenderer<ItemRequirement> {
	
	public void render(PoseStack matrices, int x, int y, ItemRequirement requirement, int ticks, float partialTicks, Player player){
		Lighting.setupForFlatItems();
		ItemRenderer renderer = Minecraft.getInstance().getItemRenderer();
		renderer.renderAndDecorateItem(requirement.getStack(), x, y);
	}
	
	public List<Component> tooltip(ItemRequirement requirement, Player player){
		List<Component> tooltip = requirement.getStack().getTooltipLines(Minecraft.getInstance().player, Minecraft.getInstance().options.advancedItemTooltips ? ADVANCED : NORMAL);
		if(requirement.getAmount() != 0)
			tooltip.set(0, new TranslatableComponent("requirement.item.num", requirement.getAmount(), tooltip.get(0)));
		else
			tooltip.set(0, new TranslatableComponent("requirement.item.have", tooltip.get(0)));
		return tooltip;
	}
	
	public boolean shouldDrawTickOrCross(ItemRequirement requirement, int amount){
		return amount == 0;
	}
}