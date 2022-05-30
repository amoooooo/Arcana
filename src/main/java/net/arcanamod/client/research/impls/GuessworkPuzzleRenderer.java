package net.arcanamod.client.research.impls;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import net.arcanamod.Arcana;
import net.arcanamod.client.research.PuzzleRenderer;
import net.arcanamod.containers.slots.AspectSlot;
import net.arcanamod.systems.research.impls.Guesswork;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.crafting.Recipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static net.arcanamod.client.gui.ClientUiUtil.drawTexturedModalRect;

public class GuessworkPuzzleRenderer extends GuiComponent implements PuzzleRenderer<Guesswork>{
	
	private static final ResourceLocation texture = new ResourceLocation(Arcana.MODID, "textures/gui/research/arcanum_gui_overlay.png");
	
	private static final String[] hintSymbols = {"#", "A", "S", "D", "F", "&", "Q", "@", "~"};
	private static final int[] hintColours = {0x8a8a8a, 0x32a852, 0x58e8da, 0x7a58e8, 0xc458e8, 0xe858d5, 0xe85858, 0x136e13, 0x6e6613, 0x99431c, 0x43355e};
	
	public void render(PoseStack stack, Guesswork puzzle, List<AspectSlot> puzzleSlots, List<Slot> puzzleItemSlots, int screenWidth, int screenHeight, int mouseX, int mouseY, Player player){
		drawPaper(stack, screenWidth, screenHeight);
		mc().getTextureManager().bindForSetup(texture);
		// render result
		int rX = paperLeft(screenWidth) + 78;
		int rY = paperTop(screenHeight) + 13;
		drawTexturedModalRect(stack, rX, rY, 1, 167, 58, 20);
		int ulX = paperLeft(screenWidth) + 70;
		int ulY = paperTop(screenHeight) + 49;
		drawTexturedModalRect(stack, ulX, ulY, 145, 1, 72, 72);
		Lighting.setupForFlatItems();
		mc().getItemRenderer().renderAndDecorateItem(player.level.getRecipeManager().byKey(puzzle.getRecipe()).orElse(null).getResultItem(), rX + 29 - 8, rY + 10 - 8);
		
		List<Map.Entry<ResourceLocation, String>> indexHelper = new ArrayList<>(puzzle.getHints().entrySet());
		Recipe<?> recipe = player.level.getRecipeManager().byKey(puzzle.getRecipe()).orElse(null);
		
		if(recipe != null){
			for(int y = 0; y < 3; y++)
				for(int x = 0; x < 3; x++){
					int index = x + y * 3;
					if(recipe.getIngredients().size() > index && recipe.getIngredients().get(index).getItems().length > 0){
						ResourceLocation name = recipe.getIngredients().get(index).getItems()[0].getItem().getRegistryName();
						int hint = indexMatchingKey(indexHelper, name);
						if(hint != -1)
							fr().drawShadow(stack, hintSymbols[hint % hintSymbols.length], ulX + 20 + x * 23, ulY + y * 23, hintColours[hint % hintColours.length]);
					}
				}
			
			int hintY = ulY + 101;
			int hintBaseX = ulX - 70;
			String text = "Hints: ";
			fr().drawShadow(stack, text, hintBaseX, hintY, 0x8a8a8a);
			for(int i = 0; i < indexHelper.size(); i++){
				int hintX = hintBaseX + fr().width(text) + i * 12;
				fr().drawShadow(stack, hintSymbols[i % hintSymbols.length], hintX, hintY, hintColours[i % hintColours.length]);
			}
			for(int i = 0; i < indexHelper.size(); i++){
				Map.Entry<ResourceLocation, String> entry = indexHelper.get(i);
				int hintX = hintBaseX + fr().width(text) + i * 12;
				if(mouseX >= hintX - 1 && mouseX < hintX + 11 && mouseY >= hintY - 1 && mouseY < hintY + 11)
					Minecraft.getInstance().screen.renderTooltip(stack, Lists.newArrayList(new TextComponent(I18n.get(entry.getValue()))), Optional.empty(), mouseX, mouseY, fr());
			}
		}
	}
	
	private static int indexMatchingKey(List<Map.Entry<ResourceLocation, String>> indexHelper, ResourceLocation key){
		for(int i = 0; i < indexHelper.size(); i++)
			if(indexHelper.get(i).getKey().equals(key))
				return i;
		return -1;
	}
}