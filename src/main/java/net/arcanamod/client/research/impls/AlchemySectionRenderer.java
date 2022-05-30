package net.arcanamod.client.research.impls;

import com.mojang.blaze3d.vertex.PoseStack;
import net.arcanamod.aspects.AspectStack;
import net.arcanamod.client.gui.ClientUiUtil;
import net.arcanamod.client.gui.ResearchEntryScreen;
import net.arcanamod.items.recipes.AlchemyRecipe;
import net.arcanamod.systems.research.impls.AlchemySection;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;

import java.util.List;

import static net.arcanamod.client.gui.ClientUiUtil.drawTexturedModalRect;
import static net.arcanamod.client.gui.ResearchEntryScreen.HEIGHT_OFFSET;

public class AlchemySectionRenderer extends AbstractCraftingSectionRenderer<AlchemySection>{
	
	void renderRecipe(PoseStack matrices, Recipe<?> recipe, AlchemySection section, int pageIndex, int screenWidth, int screenHeight, int mouseX, int mouseY, boolean right, Player player){
		if(recipe instanceof AlchemyRecipe){
			AlchemyRecipe alchemyRecipe = (AlchemyRecipe)recipe;
			int x = right ? ResearchEntryScreen.PAGE_X + ResearchEntryScreen.RIGHT_X_OFFSET : ResearchEntryScreen.PAGE_X, y = ResearchEntryScreen.PAGE_Y;
			
			int ulX = x + (screenWidth - 256 + ResearchEntryScreen.PAGE_WIDTH) / 2 - 35, ulY = y + (screenHeight - 181 + ResearchEntryScreen.PAGE_HEIGHT) / 2 - 10 + HEIGHT_OFFSET;
			mc().getTextureManager().bindForSetup(textures);
			drawTexturedModalRect(matrices, ulX, ulY, 73, 1, 70, 70);
			drawTexturedModalRect(matrices, ulX + 19, ulY - 4, 23, 145, 17, 17);
			
			int inputX = ulX + 1, inputY = ulY - 5;
			ItemStack[] stacks = alchemyRecipe.getIngredients().get(0).getItems();
			item(stacks[displayIndex(stacks.length, player)], inputX, inputY);
			
			// Display aspects
			List<AspectStack> aspects = alchemyRecipe.getAspects();
			int aspectsWidth = Math.min(3, aspects.size());
			int aspectStartX = ulX + 9 - (10 * (aspectsWidth - 3)), aspectStartY = ulY + 30;
			for(int i = 0, size = aspects.size(); i < size; i++){
				AspectStack aspect = aspects.get(i);
				int xx = aspectStartX + (i % aspectsWidth) * 20;
				int yy = aspectStartY + (i / aspectsWidth) * 20;
				ClientUiUtil.renderAspectStack(matrices, aspect, xx, yy);
			}
		}else
			error();
	}

	void renderRecipeTooltips(PoseStack matrices, Recipe<?> recipe, AlchemySection section, int pageIndex, int screenWidth, int screenHeight, int mouseX, int mouseY, boolean right, Player player){
		if(recipe instanceof AlchemyRecipe){
			AlchemyRecipe alchemyRecipe = (AlchemyRecipe)recipe;
			int x = right ? ResearchEntryScreen.PAGE_X + ResearchEntryScreen.RIGHT_X_OFFSET : ResearchEntryScreen.PAGE_X, y = ResearchEntryScreen.PAGE_Y;
			int ulX = x + (screenWidth - 256 + ResearchEntryScreen.PAGE_WIDTH) / 2 - 35, ulY = y + (screenHeight - 181 + ResearchEntryScreen.PAGE_HEIGHT) / 2 - 10 + HEIGHT_OFFSET;
			int inputX = ulX + 1, inputY = ulY - 5;
			ItemStack[] stacks = alchemyRecipe.getIngredients().get(0).getItems();
			tooltipArea(matrices, stacks[displayIndex(stacks.length, player)], mouseX, mouseY, screenWidth, screenHeight, inputX, inputY);
			List<AspectStack> aspects = alchemyRecipe.getAspects();
			int aspectsWidth = Math.min(3, aspects.size());
			int aspectStartX = ulX + 12 - (8 * (aspectsWidth - 3)), aspectStartY = ulY + 29;
			for(int i = 0, size = aspects.size(); i < size; i++){
				AspectStack stack = aspects.get(i);
				int xx = aspectStartX + (i % aspectsWidth) * 19;
				int yy = aspectStartY + (i / aspectsWidth) * 19;
				aspectTooltipArea(matrices, stack.getAspect(), mouseX, mouseY, screenWidth, screenHeight, xx, yy);
			}
		}
	}
}