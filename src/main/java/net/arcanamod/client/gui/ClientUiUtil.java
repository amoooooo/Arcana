package net.arcanamod.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import net.arcanamod.Arcana;
import net.arcanamod.aspects.Aspect;
import net.arcanamod.aspects.AspectStack;
import net.arcanamod.aspects.AspectUtils;
import net.arcanamod.aspects.Aspects;
import net.arcanamod.aspects.handlers.AspectHandler;
import net.arcanamod.aspects.handlers.AspectHolder;
import net.arcanamod.capabilities.Researcher;
import net.arcanamod.items.attachment.Core;
import net.arcanamod.systems.research.Icon;
import net.arcanamod.systems.research.ResearchBooks;
import net.arcanamod.systems.research.ResearchEntry;
import net.arcanamod.util.Pair;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.gui.GuiUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ClientUiUtil{
	
	private static ResourceLocation RESEARCH_EXPERTISE = Arcana.arcLoc("research_expertise");
	
	public static void renderAspectStack(PoseStack matricies, AspectStack stack, int x, int y){
		renderAspectStack(matricies, stack, x, y, UiUtil.tooltipColour(stack.getAspect()));
	}
	
	public static void renderAspectStack(PoseStack matricies, AspectStack stack, int x, int y, int colour){
		renderAspectStack(matricies, stack.getAspect(), stack.getAmount(), x, y, colour);
	}
	
	public static void renderAspectStack(PoseStack stack, Aspect aspect, float amount, int x, int y){
		renderAspectStack(stack, aspect, amount, x, y, UiUtil.tooltipColour(aspect));
	}
	
	public static void renderAspectStack(PoseStack stack, Aspect aspect, float amount, int x, int y, int colour){
		Minecraft mc = Minecraft.getInstance();
		// render aspect
		renderAspect(stack, aspect, x, y);
		// render amount
		PoseStack PoseStack = new PoseStack();
		// if there is a fractional part, round it
		String s = (amount % 1 > 0.1) ? String.format("%.1f", amount) : String.format("%.0f", amount);
		PoseStack.translate(0, 0, mc.getItemRenderer().blitOffset + 200.0F);
		MultiBufferSource bufferSource = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
		mc.font.drawInBatch(s, x + 19 - mc.font.width(s), y + 10, colour, true, PoseStack.last().pose(), bufferSource, false, 0, 0xf000f0);
		Tesselator.getInstance().end();
	}
	
	public static void renderAspect(PoseStack stack, Aspect aspect, int x, int y){
		Minecraft mc = Minecraft.getInstance();
		mc.getTextureManager().bindForSetup(AspectUtils.getAspectTextureLocation(aspect));
		drawModalRectWithCustomSizedTexture(stack, x, y, 0, 0, 16, 16, 16, 16);
	}
	
	public static void drawModalRectWithCustomSizedTexture(PoseStack stack, int x, int y, float texX, float texY, int width, int height, int textureWidth, int textureHeight){
		int z = Minecraft.getInstance().screen != null ? Minecraft.getInstance().screen.getBlitOffset() : 1;
		GuiComponent.blit(stack, x, y, z, texX, texY, width, height, textureWidth, textureHeight);
	}
	
	public static void drawTexturedModalRect(PoseStack stack, int x, int y, float texX, float texY, int width, int height){
		drawModalRectWithCustomSizedTexture(stack, x, y, texX, texY, width, height, 256, 256);
	}
	
	public static boolean shouldShowAspectIngredients(){
		// true if research expertise has been completed
		Researcher from = Researcher.getFrom(Minecraft.getInstance().player);
		ResearchEntry entry = ResearchBooks.getEntry(RESEARCH_EXPERTISE);
		// If the player is null, their researcher is null, or research expertise no longer exists, display anyways
		return entry == null || (from != null && from.entryStage(entry) >= entry.sections().size());
	}
	
	public static void drawAspectTooltip(PoseStack stack, Aspect aspect, String descriptions, int mouseX, int mouseY, int screenWidth, int screenHeight){
		String name = AspectUtils.getLocalizedAspectDisplayName(aspect);
		
		List<Component> text = new ArrayList<>();
		text.add(new TextComponent(name));
		if(!descriptions.equals(""))
			for(String description : descriptions.split("\n"))
				text.add(new TextComponent(description).setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)));
		
		drawAspectStyleTooltip(stack, text, mouseX, mouseY, screenWidth, screenHeight);
		
		if(shouldShowAspectIngredients() && Screen.hasShiftDown()){
			stack.popPose();
			stack.translate(0, 0, 500);
			Minecraft mc = Minecraft.getInstance();
			stack.translate(0, 0, mc.getItemRenderer().blitOffset);
			
			// copied from GuiUtils#drawHoveringText but without text wrapping
			Font fontRenderer = Minecraft.getInstance().font;
			int tooltipTextWidth = fontRenderer.width(name);
			int tooltipX = mouseX + 12;
			if(tooltipX + tooltipTextWidth + 4 > screenWidth)
				tooltipX = mouseX - 16 - tooltipTextWidth;
			int tooltipY = mouseY - 12;
			if(tooltipY < 4)
				tooltipY = 4;
			else if(tooltipY + 12 > screenHeight)
				tooltipY = screenHeight - 12;
			
			int x = tooltipX - 4;
			int y = 10 + tooltipY + 5;
			Pair<Aspect, Aspect> combinationPairs = Aspects.COMBINATIONS.inverse().get(aspect);
			if(combinationPairs != null){
				int color = 0xa0222222;
				// 2px padding horizontally, 2px padding vertically
				GuiUtils.drawGradientRect(stack.last().pose(), 0, x, y - 2, x + 40, y + 18, color, color);
				x += 2;
				renderAspect(stack, combinationPairs.getFirst(), x, y);
				x += 20;
				renderAspect(stack, combinationPairs.getSecond(), x, y);
			}
			stack.pushPose();
		}
	}
	
	public static void drawAspectStyleTooltip(PoseStack stack, List<Component> text, int mouseX, int mouseY, int screenWidth, int screenHeight){
		Minecraft.getInstance().screen.renderTooltip(stack, text, Optional.empty(), mouseX, mouseY, Minecraft.getInstance().font);
	}
	
	public static void renderIcon(PoseStack stack, Icon icon, int x, int y, int itemZLevel){
		// first, check if its an item
		if(icon.getStack() != null && !icon.getStack().isEmpty()){
			// this, uhh, doesn't work
			// ItemRenderer adds 50 automatically, so we adjust for it
			Minecraft.getInstance().getItemRenderer().blitOffset = itemZLevel - 50;
			Minecraft.getInstance().getItemRenderer().renderAndDecorateItem(icon.getStack(), x, y);
		}else{
			// otherwise, check for a texture
			Minecraft.getInstance().getTextureManager().bindForSetup(icon.getResourceLocation());
			RenderSystem.enableDepthTest();
			drawModalRectWithCustomSizedTexture(stack, x, y, 0, 0, 16, 16, 16, 16);
		}
	}
	
	public static void renderVisCore(PoseStack stack, Core core, int x, int y){
		Minecraft.getInstance().getTextureManager().bindForSetup(core.getGuiTexture());
		drawModalRectWithCustomSizedTexture(stack, x, y, 0, 0, 49, 49, 49, 49);
	}
	
	public static void renderVisMeter(PoseStack stack, AspectHandler aspects, int x, int y){
		int poolOffset = 2;
		int poolSpacing = 6;
		int poolFromEdge = 24;
		// "2": distance to first vis pool
		// "+= 6": distance between vis pools
		// "24": constant distance to vis pool
		Aspect[] vertical = {Aspects.AIR, Aspects.CHAOS, Aspects.EARTH};
		Aspect[] horizontal = {Aspects.FIRE, Aspects.ORDER, Aspects.WATER};
		int offset = poolOffset;
		for(Aspect aspect : vertical){
			AspectHolder holder = aspects.findFirstHolderContaining(aspect);
			renderVisFill(stack, holder.getStack(), holder.getCapacity(), true, x + offset, y + poolFromEdge);
			offset += poolSpacing;
		}
		offset = poolOffset;
		for(Aspect aspect : horizontal){
			AspectHolder holder = aspects.findFirstHolderContaining(aspect);
			renderVisFill(stack, holder.getStack(), holder.getCapacity(), false, x + poolFromEdge, y + offset);
			offset += poolSpacing;
		}
	}
	
	public static void renderVisFill(PoseStack stack, AspectStack aspStack, float visMax, boolean vertical, int x, int y){
		int meterShort = 3;
		int meterLen = 16;
		int renderLen = (int)((aspStack.getAmount() * meterLen) / visMax);
		if(renderLen > 0){
			Minecraft.getInstance().getTextureManager().bindForSetup(aspStack.getAspect().getVisMeterTexture());
			if(vertical)
				drawModalRectWithCustomSizedTexture(stack, x, y, 0, 0, meterShort, renderLen, meterLen, meterLen);
			else
				drawModalRectWithCustomSizedTexture(stack, x, y, 0, 0, renderLen, meterShort, meterLen, meterLen);
		}
	}
	
	public static void renderVisDetailInfo(PoseStack matrices, AspectHandler aspects){
		int topMargin = 0;
		for(AspectHolder holder : aspects.getHolders()){
			Minecraft.getInstance().font.draw(matrices,
					I18n.get("aspect." + holder.getStack().getAspect().name().toLowerCase()) + ": " + holder.getStack().getAmount(),
					60, topMargin, java.awt.Color.WHITE.getRGB());
			topMargin += 10;
		}
	}
}
