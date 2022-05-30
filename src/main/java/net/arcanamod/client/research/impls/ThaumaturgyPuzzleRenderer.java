package net.arcanamod.client.research.impls;

import com.mojang.blaze3d.vertex.PoseStack;
import net.arcanamod.client.research.PuzzleRenderer;
import net.arcanamod.containers.slots.AspectSlot;
import net.arcanamod.systems.research.impls.Thaumaturgy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;

import java.util.List;

public class ThaumaturgyPuzzleRenderer implements PuzzleRenderer<Thaumaturgy> {
	@Override
	public void render(PoseStack stack, Thaumaturgy puzzle, List<AspectSlot> puzzleSlots, List<Slot> puzzleItemSlots, int screenWidth, int screenHeight, int mouseX, int mouseY, Player player) {

	}
}
