package net.arcanamod.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import net.arcanamod.Arcana;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

public class ScribbledNoteScreen extends Screen {
    
    public static final ResourceLocation SCRIBBLED_NOTE_TEXTURE = new ResourceLocation(Arcana.MODID, "textures/gui/research/scribbled_notes.png");

    public ScribbledNoteScreen(MutableComponent component){
        super(component);
    }
    
    @Override
    public void render(PoseStack stack, int p_render_1_, int p_render_2_, float p_render_3_){
        String text = I18n.get("scribbledNote.text");
        getMinecraft().font.draw(stack, text, (width - getMinecraft().font.width(text)) / 2f, height / 2f, 1);
        getMinecraft().getTextureManager().bindForSetup(SCRIBBLED_NOTE_TEXTURE);
    }
    
    public boolean isPauseScreen(){
        return false;
    }
}