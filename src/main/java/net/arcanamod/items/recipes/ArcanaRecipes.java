package net.arcanamod.items.recipes;

import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SimpleRecipeSerializer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static net.arcanamod.Arcana.MODID;

public class ArcanaRecipes{
	public static class Serializers{
		public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, MODID);

		public static final RegistryObject<RecipeSerializer<WandsRecipe>> CRAFTING_WANDS = SERIALIZERS.register("crafting_special_wands", () -> new SimpleRecipeSerializer<>(WandsRecipe::new));
		public static final RegistryObject<RecipeSerializer<AlchemyRecipe>> ALCHEMY = SERIALIZERS.register("alchemy", AlchemyRecipe.Serializer::new);
		public static final RegistryObject<RecipeSerializer<ArcaneCraftingShapedRecipe>> ARCANE_CRAFTING_SHAPED = SERIALIZERS.register("arcane_crafting_shaped", ArcaneCraftingShapedRecipe.Serializer::new);
	}
	public static class Types{
		public static final RecipeType<IArcaneCraftingRecipe> ARCANE_CRAFTING_SHAPED = RecipeType.register("arcana:arcane_crafting_shaped");
	}
}