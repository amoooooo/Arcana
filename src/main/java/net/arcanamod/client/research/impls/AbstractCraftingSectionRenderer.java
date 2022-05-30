package net.arcanamod.client.research.impls;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.arcanamod.aspects.Aspect;
import net.arcanamod.client.gui.ClientUiUtil;
import net.arcanamod.client.gui.ResearchEntryScreen;
import net.arcanamod.client.research.EntrySectionRenderer;
import net.arcanamod.systems.research.ResearchBook;
import net.arcanamod.systems.research.ResearchBooks;
import net.arcanamod.systems.research.impls.AbstractCraftingSection;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraftforge.client.gui.GuiUtils;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Optional;

import static net.arcanamod.client.gui.ClientUiUtil.drawTexturedModalRect;
import static net.arcanamod.client.gui.ResearchEntryScreen.HEIGHT_OFFSET;

public abstract class AbstractCraftingSectionRenderer<T extends AbstractCraftingSection> implements EntrySectionRenderer<T>{
	
	protected ResourceLocation textures = null;
	
	public void render(PoseStack stack, T section, int pageIndex, int screenWidth, int screenHeight, int mouseX, int mouseY, boolean right, Player player){
		// if recipe exists: render result at specified position, defer drawing recipe
		// otherwise: render error message
		ResearchBook book = ResearchBooks.getEntry(section.getEntry()).category().book();
		// don't make a new RLoc every frame
		if(textures == null || !textures.getNamespace().equals(book.getKey().getNamespace()))
			textures = new ResourceLocation(book.getKey().getNamespace(), "textures/gui/research/" + book.getPrefix() + ResearchEntryScreen.OVERLAY_SUFFIX);
		Optional<? extends Recipe<?>> optRecipe = player.level.getRecipeManager().byKey(section.getRecipe());
		optRecipe.ifPresent(recipe -> {
			// draw result
			ItemStack result = recipe.getResultItem();
			renderResult(stack, right ? ResearchEntryScreen.PAGE_X + ResearchEntryScreen.RIGHT_X_OFFSET : ResearchEntryScreen.PAGE_X, resultOffset(recipe, section, pageIndex, screenWidth, screenHeight, mouseX, mouseY, right, player), result, screenWidth, screenHeight);
			renderRecipe(stack, recipe, section, pageIndex, screenWidth, screenHeight, mouseX, mouseY, right, player);
		});
		// else display error
		if(!optRecipe.isPresent())
			error();
	}
	
	public void renderAfter(PoseStack stack, T section, int pageIndex, int screenWidth, int screenHeight, int mouseX, int mouseY, boolean right, Player player){
		Optional<? extends Recipe<?>> optRecipe = player.level.getRecipeManager().byKey(section.getRecipe());
		optRecipe.ifPresent(recipe -> {
			// draw result
			ItemStack result = recipe.getResultItem();
			renderResultTooltips(stack, right ? ResearchEntryScreen.PAGE_X + ResearchEntryScreen.RIGHT_X_OFFSET : ResearchEntryScreen.PAGE_X, resultOffset(recipe, section, pageIndex, screenWidth, screenHeight, mouseX, mouseY, right, player), result, screenWidth, screenHeight, mouseX, mouseY);
			renderRecipeTooltips(stack, recipe, section, pageIndex, screenWidth, screenHeight, mouseX, mouseY, right, player);
		});
		if(!optRecipe.isPresent())
			error();
	}
	
	abstract void renderRecipe(PoseStack matrices, Recipe<?> recipe, T section, int pageIndex, int screenWidth, int screenHeight, int mouseX, int mouseY, boolean right, Player player);
	
	abstract void renderRecipeTooltips(PoseStack matrices, Recipe<?> recipe, T section, int pageIndex, int screenWidth, int screenHeight, int mouseX, int mouseY, boolean right, Player player);
	
	int resultOffset(Recipe<?> recipe, T section, int pageIndex, int screenWidth, int screenHeight, int mouseX, int mouseY, boolean right, Player player){
		return ResearchEntryScreen.PAGE_Y;
	}
	
	private void renderResult(PoseStack stack, int x, int y, ItemStack result, int screenWidth, int screenHeight){
		mc().getTextureManager().bindForSetup(textures);
		int rX = x + (screenWidth - 256) / 2 + (ResearchEntryScreen.PAGE_WIDTH - 58) / 2;
		int rY = y + (screenHeight - 181) / 2 + 16 + HEIGHT_OFFSET;
		drawTexturedModalRect(stack, rX, rY, 1, 167, 58, 20);
		item(result, rX + 29 - 8, rY + 10 - 8);
		int stX = x + (screenWidth - 256) / 2 + (ResearchEntryScreen.PAGE_WIDTH - fr().width(result.getDisplayName().getString())) / 2 + 5;
		int stY = y + (screenHeight - 181) / 2 + 11 - fr().lineHeight + HEIGHT_OFFSET;
		fr().draw(stack, result.getDisplayName().getString(), stX, stY, 0);
		RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
		RenderSystem.enableBlend();
	}
	
	protected void renderResultTooltips(PoseStack matrix, int x, int y, ItemStack result, int screenWidth, int screenHeight, int mouseX, int mouseY){
		int rX = x + (screenWidth - 256) / 2 + (ResearchEntryScreen.PAGE_WIDTH - 58) / 2 + 21;
		int rY = y + (screenHeight - 181) / 2 + 18 + HEIGHT_OFFSET;
		tooltipArea(matrix, result, mouseX, mouseY, screenWidth, screenHeight, rX, rY);
	}
	
	protected void item(ItemStack stack, int x, int y){
		mc().getItemRenderer().renderAndDecorateFakeItem(stack, x, y);
		mc().getItemRenderer().renderGuiItemDecorations(mc().font, stack, x, y, null);
	}
	
	protected void tooltipArea(PoseStack matrices, ItemStack stack, int mouseX, int mouseY, int screenWidth, int screenHeight, int areaX, int areaY){
		if(mouseX >= areaX && mouseX < areaX + 16 && mouseY >= areaY && mouseY < areaY + 16)
			tooltip(matrices, stack, mouseX, mouseY, screenWidth, screenHeight);
	}
	
	protected void aspectTooltipArea(PoseStack stack, Aspect aspect, int mouseX, int mouseY, int screenWidth, int screenHeight, int areaX, int areaY){
		if(mouseX >= areaX && mouseX < areaX + 16 && mouseY >= areaY && mouseY < areaY + 16)
			ClientUiUtil.drawAspectTooltip(stack, aspect, "", mouseX, mouseY, screenWidth, screenHeight);
	}
	
	protected void tooltip(PoseStack matricies, ItemStack stack, int mouseX, int mouseY, int screenWidth, int screenHeight){
		mc().screen.renderTooltip(matricies, new ArrayList<>(stack.getTooltipLines(mc().player, mc().options.advancedItemTooltips ? TooltipFlag.Default.ADVANCED : TooltipFlag.Default.NORMAL)), Optional.empty(), mouseX, mouseY, fr());
	}
	
	public int span(T section, Player player){
		return 1;
	}
	
	protected void error(){
		// display error
	}
	
	protected int displayIndex(int max, @Nonnull Entity player){
		return (player.tickCount / 30) % max;
	}
}