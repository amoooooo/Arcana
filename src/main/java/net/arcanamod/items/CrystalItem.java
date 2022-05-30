package net.arcanamod.items;

import net.arcanamod.aspects.Aspect;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class CrystalItem extends Item {
    public final Aspect aspect;
    
    public CrystalItem(Item.Properties properties, Aspect aspect) {
        super(properties);
        this.aspect = aspect;
    }

    @Override
    public Component getName(ItemStack stack) {
        return new TranslatableComponent("item.arcana.crystal", new TranslatableComponent("aspect." + aspect.name()));
    }
}