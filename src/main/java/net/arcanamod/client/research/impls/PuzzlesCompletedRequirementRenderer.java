package net.arcanamod.client.research.impls;

import com.mojang.blaze3d.vertex.PoseStack;
import net.arcanamod.Arcana;
import net.arcanamod.capabilities.Researcher;
import net.arcanamod.client.research.RequirementRenderer;
import net.arcanamod.systems.research.impls.PuzzlesCompletedRequirement;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.Arrays;
import java.util.List;

import static net.arcanamod.client.gui.ClientUiUtil.drawModalRectWithCustomSizedTexture;

public class PuzzlesCompletedRequirementRenderer implements RequirementRenderer<PuzzlesCompletedRequirement>{
	
	private static final ResourceLocation ICON = Arcana.arcLoc("textures/item/research_note_complete.png");
	
	public void render(PoseStack matrices, int x, int y, PuzzlesCompletedRequirement requirement, int ticks, float partialTicks, Player player){
		Minecraft.getInstance().getTextureManager().bindForSetup(ICON);
		drawModalRectWithCustomSizedTexture(matrices, x, y, 0, 0, 16, 16, 16, 16);
	}
	
	public List<Component> tooltip(PuzzlesCompletedRequirement requirement, Player player){
		return Arrays.asList(new TranslatableComponent("requirement.puzzles_completed", requirement.getAmount()), new TranslatableComponent("requirement.puzzles_completed.progress", Researcher.getFrom(player).getPuzzlesCompleted(), requirement.getAmount()));
	}
}