package net.arcanamod.client.research.impls;

import com.mojang.blaze3d.vertex.PoseStack;
import net.arcanamod.Arcana;
import net.arcanamod.client.research.RequirementRenderer;
import net.arcanamod.systems.research.impls.ResearchCompletedRequirement;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.Collections;
import java.util.List;

import static net.arcanamod.client.gui.ClientUiUtil.drawModalRectWithCustomSizedTexture;

public class ResearchCompletedRequirementRenderer implements RequirementRenderer<ResearchCompletedRequirement>{
	
	private static final ResourceLocation ICON = Arcana.arcLoc("textures/item/arcanum_open.png");
	
	public void render(PoseStack matrices, int x, int y, ResearchCompletedRequirement requirement, int ticks, float partialTicks, Player player){
		Minecraft.getInstance().getTextureManager().bindForSetup(ICON);
		drawModalRectWithCustomSizedTexture(matrices, x, y, 0, 0, 16, 16, 16, 16);
	}
	
	public List<Component> tooltip(ResearchCompletedRequirement requirement, Player player){
		return Collections.singletonList(new TranslatableComponent("requirement.research_completed"));
	}
}