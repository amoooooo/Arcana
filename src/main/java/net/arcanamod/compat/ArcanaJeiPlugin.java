package net.arcanamod.compat;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.arcanamod.blocks.ArcanaBlocks;
import net.arcanamod.compat.jei.AlchemyCategory;
import net.arcanamod.compat.jei.ArcaneCraftingCategory;
import net.arcanamod.compat.jei.CrystalStudyCategory;
import net.arcanamod.compat.jei.DummyRecipe;
import net.arcanamod.items.ArcanaItems;
import net.arcanamod.items.recipes.AlchemyRecipe;
import net.arcanamod.items.recipes.ArcaneCraftingShapedRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.Collections;
import java.util.List;

import static net.arcanamod.ArcanaVariables.arcLoc;

@JeiPlugin
public class ArcanaJeiPlugin implements IModPlugin {

    public static final ResourceLocation ARCANE_WORKBENCH_UUID = arcLoc("arcane_crafting_jei");
    public static final ResourceLocation ALCHEMY_UUID = arcLoc("alchemy_uuid");
    public static final ResourceLocation CRYSTAL_UUID = arcLoc("crystal_study");

    @Override
    public ResourceLocation getPluginUid() {
        return arcLoc("jei");
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        IModPlugin.super.registerRecipes(registration);
        registration.addRecipes(ArcaneCraftingShapedRecipe.RECIPES, ARCANE_WORKBENCH_UUID);
        registration.addRecipes(AlchemyRecipe.RECIPES, ALCHEMY_UUID);
        registration.addRecipes(Collections.singletonList(new DummyRecipe()), CRYSTAL_UUID);
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IModPlugin.super.registerCategories(registration);
        registration.addRecipeCategories(new ArcaneCraftingCategory(registration.getJeiHelpers()));
        registration.addRecipeCategories(new AlchemyCategory(registration.getJeiHelpers()));
        registration.addRecipeCategories(new CrystalStudyCategory(registration.getJeiHelpers()));
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        IModPlugin.super.registerRecipeCatalysts(registration);
        registration.addRecipeCatalyst(new ItemStack(ArcanaBlocks.ARCANE_CRAFTING_TABLE.get()),ARCANE_WORKBENCH_UUID);
        registration.addRecipeCatalyst(new ItemStack(ArcanaBlocks.CRUCIBLE.get()),ALCHEMY_UUID);
        registration.addRecipeCatalyst(new ItemStack(ArcanaItems.AIR_CRYSTAL_SEED.get()),CRYSTAL_UUID);
        registration.addRecipeCatalyst(new ItemStack(ArcanaItems.EARTH_CRYSTAL_SEED.get()),CRYSTAL_UUID);
        registration.addRecipeCatalyst(new ItemStack(ArcanaItems.FIRE_CRYSTAL_SEED.get()),CRYSTAL_UUID);
        registration.addRecipeCatalyst(new ItemStack(ArcanaItems.WATER_CRYSTAL_SEED.get()),CRYSTAL_UUID);
        registration.addRecipeCatalyst(new ItemStack(ArcanaItems.CHAOS_CRYSTAL_SEED.get()),CRYSTAL_UUID);
        registration.addRecipeCatalyst(new ItemStack(ArcanaItems.ORDER_CRYSTAL_SEED.get()),CRYSTAL_UUID);
    }
}