package net.arcanamod.client.research.impls;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.arcanamod.client.research.RequirementRenderer;
import net.arcanamod.systems.research.impls.XpRequirement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.Collections;
import java.util.List;

public class XpRequirementRenderer implements RequirementRenderer<XpRequirement>{
	
	private static final ResourceLocation EXPERIENCE_ORB_TEXTURES = new ResourceLocation("textures/entity/experience_orb.png");
	
	public void render(PoseStack matrices, int x, int y, XpRequirement requirement, int ticks, float partialTicks, Player player){
		doXPRender(matrices, ticks, x, y, partialTicks);
	}
	
	public List<Component> tooltip(XpRequirement requirement, Player player){
		return Collections.singletonList(new TranslatableComponent("requirement.experience", requirement.getAmount()));
	}
	
	public static void doXPRender(PoseStack stack, int ticks, double x, double y, float partialTicks){
		final int u = 0, v = 16;
		float f8 = (ticks + partialTicks) / 2f;
		stack.pushPose();
		Minecraft.getInstance().getTextureManager().bindForSetup(EXPERIENCE_ORB_TEXTURES);
		GuiComponent.blit(stack, (int)x, (int)y, 16, 16, u, v, 16, 16, 64, 64);
		RenderSystem.disableBlend();
		stack.popPose();;
	}
}