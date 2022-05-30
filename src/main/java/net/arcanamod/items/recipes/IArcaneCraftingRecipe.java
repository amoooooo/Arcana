package net.arcanamod.items.recipes;

import net.arcanamod.aspects.UndecidedAspectStack;
import net.arcanamod.blocks.ArcanaBlocks;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public interface IArcaneCraftingRecipe extends Recipe<AspectCraftingInventory> {
	
	default RecipeType<?> getType() {
		return ArcanaRecipes.Types.ARCANE_CRAFTING_SHAPED;
	}

	@Override
	default ItemStack getToastSymbol() {
		return new ItemStack(ArcanaBlocks.ARCANE_CRAFTING_TABLE.get());
	}
	
	boolean matchesIgnoringAspects(AspectCraftingInventory inv, Level worldIn);
	
	UndecidedAspectStack[] getAspectStacks();
}