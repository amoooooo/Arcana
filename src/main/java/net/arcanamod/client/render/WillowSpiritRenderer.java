package net.arcanamod.client.render;

import net.arcanamod.Arcana;
import net.arcanamod.client.model.WillowEntityModel;
import net.arcanamod.entities.SpiritEntity;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class WillowSpiritRenderer extends MobRenderer<SpiritEntity, WillowEntityModel<SpiritEntity>> {
    protected static final ResourceLocation TEXTURE = new ResourceLocation(Arcana.MODID,
            "textures/entity/willow_spirit.png");

    public WillowSpiritRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new WillowEntityModel<>(), 0.5f);
    }

    /**
     * Returns the location of an entity's texture.
     *
     * @param pEntity
     */
    @Override
    public ResourceLocation getTextureLocation(SpiritEntity pEntity) {
        return TEXTURE;
    }
}

