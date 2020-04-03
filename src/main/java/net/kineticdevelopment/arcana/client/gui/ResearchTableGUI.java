package net.kineticdevelopment.arcana.client.gui;

import net.kineticdevelopment.arcana.common.objects.containers.ResearchTableContainer;
import net.kineticdevelopment.arcana.common.objects.tile.ResearchTableTileEntity;
import net.kineticdevelopment.arcana.core.Main;
import net.kineticdevelopment.arcana.core.aspects.Aspect;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.util.ResourceLocation;

public class ResearchTableGUI extends GuiContainer{
	
	ResearchTableTileEntity te;
	public static final int WIDTH = 376;
	public static final int HEIGHT = 280;
	
	protected Aspect heldAspect;
	
	private static final ResourceLocation bg = new ResourceLocation(Main.MODID, "textures/gui/container/gui_researchbook.png");
	
	public ResearchTableGUI(ResearchTableTileEntity te, ResearchTableContainer container){
		super(container);
		this.te = te;
		xSize = WIDTH;
		ySize = HEIGHT;
	}
	
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY){
		mc.getTextureManager().bindTexture(bg);
		drawModalRectWithCustomSizedTexture(guiLeft, guiTop, 0, 0, WIDTH, HEIGHT, 378, 378);
	}
	
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY){
		// draw stored aspects
		// draw hovered aspect
	}
	
	public void drawScreen(int mouseX, int mouseY, float partialTicks){
		drawDefaultBackground();
		super.drawScreen(mouseX, mouseY, partialTicks);
		renderHoveredToolTip(mouseX, mouseY);
	}
}