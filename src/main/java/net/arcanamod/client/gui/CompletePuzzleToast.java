package net.arcanamod.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.arcanamod.items.ArcanaItems;
import net.arcanamod.systems.research.ResearchEntry;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.advancements.FrameType;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class CompletePuzzleToast implements Toast {
	// good thing we don't have any other research books that work }:D
	private static final ItemStack ICON = new ItemStack(ArcanaItems.ARCANUM.get());
	
	private ResearchEntry entry;
	
	public CompletePuzzleToast(@Nullable ResearchEntry entry){
		this.entry = entry;
	}
	
	// draw
	public Visibility render(PoseStack matrixStack, ToastComponent toastGui, long delta){
		/*
		toastGui.getMinecraft().getTextureManager().bindForSetup(Toast.TEXTURE);
		RenderSystem.color3f(1, 1, 1);
		toastGui.blit(matrixStack, 0, 0, 0, 32, 160, 32);
		// Puzzle Complete!
		// <Research Name>
		boolean present = entry != null;
		toastGui.getMinecraft().fontRenderer.drawString(matrixStack, I18n.format("puzzle.toast.title"), 30, present ? 7 : 12, 0xff500050);
		if(present)
			toastGui.getMinecraft().fontRenderer.drawString(matrixStack, I18n.format(entry.name()), 30, 18, 0xff000000);
		toastGui.getMinecraft().getItemRenderer().renderItemAndEffectIntoGUI(null, ICON, 8, 8);
		return delta >= 5000 ? IToast.Visibility.HIDE : IToast.Visibility.SHOW;
		*/
		// I hope this works ...for now.
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderTexture(0, TEXTURE);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		DisplayInfo displayinfo = new DisplayInfo(ICON, new TranslatableComponent(this.entry.name()),new TranslatableComponent(this.entry.description()), null, FrameType.TASK, true, true, false);
		if (displayinfo != null) {
			List<FormattedCharSequence> list = toastGui.getMinecraft().font.split(displayinfo.getTitle(), 125);
			int i = 16776960;
			if (list.size() == 1) {
				toastGui.getMinecraft().font.draw(matrixStack, displayinfo.getFrame().getDisplayName(), 30.0F, 7.0F, i | -16777216);
				toastGui.getMinecraft().font.draw(matrixStack, list.get(0), 30.0F, 18.0F, -1);
			} else {
				int j = 1500;
				float f = 300.0F;
				if (delta < 1500L) {
					int k = Mth.floor(Mth.clamp((float)(1500L - delta) / 300.0F, 0.0F, 1.0F) * 255.0F) << 24 | 67108864;
					toastGui.getMinecraft().font.draw(matrixStack, displayinfo.getFrame().getDisplayName(), 30.0F, 11.0F, i | k);
				} else {
					int i1 = Mth.floor(Mth.clamp((float)(delta - 1500L) / 300.0F, 0.0F, 1.0F) * 252.0F) << 24 | 67108864;
					int l = this.height() / 2 - list.size() * 9 / 2;

					for(FormattedCharSequence formattedcharsequence : list) {
						toastGui.getMinecraft().font.draw(matrixStack, formattedcharsequence, 30.0F, (float)l, 16777215 | i1);
						l += 9;
					}
				}
			}
			toastGui.getMinecraft().getItemRenderer().renderAndDecorateFakeItem(displayinfo.getIcon(), 8, 8);
			return delta >= 5000L ? Toast.Visibility.HIDE : Toast.Visibility.SHOW;
		} else return Toast.Visibility.HIDE;
	}
}