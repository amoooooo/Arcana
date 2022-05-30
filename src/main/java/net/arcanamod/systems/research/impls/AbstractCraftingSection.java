package net.arcanamod.systems.research.impls;

import net.arcanamod.systems.research.EntrySection;
import net.arcanamod.systems.research.Icon;
import net.arcanamod.systems.research.Pin;
import net.arcanamod.systems.research.ResearchEntry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;

import java.util.Optional;
import java.util.stream.Stream;

public abstract class AbstractCraftingSection extends EntrySection{
	
	ResourceLocation recipe;
	
	public AbstractCraftingSection(ResourceLocation recipe){
		this.recipe = recipe;
	}
	
	public AbstractCraftingSection(String s){
		this(new ResourceLocation(s));
	}
	
	public CompoundTag getData(){
		CompoundTag compound = new CompoundTag();
		compound.putString("recipe", recipe.toString());
		return compound;
	}
	
	public ResourceLocation getRecipe(){
		return recipe;
	}
	
	public Stream<Pin> getPins(int index, ServerLevel world, ResearchEntry entry){
		// if the recipe exists,
		Optional<? extends Recipe<?>> recipe = world.getRecipeManager().byKey(this.recipe);
		if(recipe.isPresent()){
			// get the item as the icon
			ItemStack output = recipe.get().getResultItem();
			Icon icon = new Icon(output.getItem().getRegistryName(), output);
			// and return a pin that points to this
			return Stream.of(new Pin(output.getItem(), entry, index, icon));
		}
		return super.getPins(index, world, entry);
	}
}