package net.arcanamod.client.research.impls;

import com.mojang.blaze3d.vertex.PoseStack;
import net.arcanamod.client.research.RequirementRenderer;
import net.arcanamod.systems.research.Puzzle;
import net.arcanamod.systems.research.ResearchBooks;
import net.arcanamod.systems.research.impls.Fieldwork;
import net.arcanamod.systems.research.impls.PuzzleRequirement;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static net.arcanamod.client.gui.ClientUiUtil.drawModalRectWithCustomSizedTexture;

public class PuzzleRequirementRenderer implements RequirementRenderer<PuzzleRequirement>{
	
	public void render(PoseStack matrices, int x, int y, PuzzleRequirement requirement, int ticks, float partialTicks, Player player){
		ResourceLocation icon = getFrom(requirement).getIcon();
		Minecraft.getInstance().getTextureManager().bindForSetup(icon != null ? icon : getFrom(requirement).getDefaultIcon());
		drawModalRectWithCustomSizedTexture(matrices, x, y, 0, 0, 16, 16, 16, 16);
	}
	
	public List<Component> tooltip(PuzzleRequirement requirement, Player player){
		if(!(getFrom(requirement) instanceof Fieldwork)){
			String desc = getFrom(requirement).getDesc();
			String puzzleDesc = desc != null ? desc : getFrom(requirement).getDefaultDesc();
			if(requirement.satisfied(player))
				return Arrays.asList(new TranslatableComponent(puzzleDesc), new TranslatableComponent("requirement.puzzle.complete"));
			return Arrays.asList(new TranslatableComponent(puzzleDesc), new TranslatableComponent("requirement.puzzle.get_note.1"), new TranslatableComponent("requirement.puzzle.get_note.2"));
		}else
			return Collections.singletonList(new TranslatableComponent(getFrom(requirement).getDesc()));
	}
	
	private Puzzle getFrom(PuzzleRequirement pr){
		return ResearchBooks.puzzles.get(pr.getPuzzleId());
	}
}