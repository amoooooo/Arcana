package net.arcanamod.items.attachment.mixin;

import net.minecraft.client.gui.Font;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(FontRendererAccessor.class)
public interface FontRendererAccessor{
	@Invoker
	Font callGetFont(ResourceLocation fontLocation);
}