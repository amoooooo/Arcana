package net.arcanamod.containers.slots;

import net.arcanamod.aspects.UndecidedAspectStack;
import net.arcanamod.aspects.handlers.AspectHandler;
import net.arcanamod.aspects.handlers.AspectHolder;
import net.arcanamod.items.MagicDeviceItem;
import net.arcanamod.items.recipes.ArcanaRecipes;
import net.arcanamod.items.recipes.AspectCraftingInventory;
import net.arcanamod.items.recipes.IArcaneCraftingRecipe;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;

import javax.annotation.Nullable;
import java.util.Optional;

public class AspectCraftingResultSlot extends ResultSlot {
	private final AspectCraftingInventory craftMatrix;
	private final Player player;
	
	public AspectCraftingResultSlot(Player player, AspectCraftingInventory aspectCraftingInventory, Container inventoryIn, int slotIndex, int xPosition, int yPosition){
		super(player, aspectCraftingInventory, inventoryIn, slotIndex, xPosition, yPosition);
		this.craftMatrix = aspectCraftingInventory;
		this.player = player;
	}
	
	@Override
	public void onTake(Player thePlayer, ItemStack stack){
		this.checkTakeAchievements(stack);
		net.minecraftforge.common.ForgeHooks.setCraftingPlayer(thePlayer);
		NonNullList<ItemStack> nonnulllist;
		Optional<IArcaneCraftingRecipe> optionalRecipe = thePlayer.level.getRecipeManager().getRecipeFor(ArcanaRecipes.Types.ARCANE_CRAFTING_SHAPED, this.craftMatrix, thePlayer.level);
		net.minecraftforge.common.ForgeHooks.setCraftingPlayer(null);
		if(optionalRecipe.isPresent()){
			nonnulllist = thePlayer.level.getRecipeManager().getRemainingItemsFor(ArcanaRecipes.Types.ARCANE_CRAFTING_SHAPED, this.craftMatrix, thePlayer.level);
			IArcaneCraftingRecipe recipe = optionalRecipe.get();
			UndecidedAspectStack[] aspectStacks = recipe.getAspectStacks();
			if(aspectStacks.length != 0){
				if(craftMatrix.getWandSlot() != null){
					if(craftMatrix.getWandSlot().getItem() != ItemStack.EMPTY){
						AspectHandler handler = AspectHandler.getFrom(craftMatrix.getWandSlot().getItem());
						this.takeAspects(craftMatrix, handler, aspectStacks);
					}
				}
			}
		}else{
			nonnulllist = thePlayer.level.getRecipeManager().getRemainingItemsFor(RecipeType.CRAFTING, this.craftMatrix, thePlayer.level);
		}
		for(int i = 0; i < nonnulllist.size(); ++i){
			ItemStack itemstack = this.craftMatrix.getItem(i);
			ItemStack itemstack1 = nonnulllist.get(i);
			if(!itemstack.isEmpty() && !(itemstack.getItem() instanceof MagicDeviceItem)){
				this.craftMatrix.removeItem(i, 1);
				itemstack = this.craftMatrix.getItem(i);
			}
			
			if(!itemstack1.isEmpty()){
				if(itemstack.isEmpty()){
					this.craftMatrix.setInventorySlotContents(i, itemstack1);
				}else if(ItemStack.isSame(itemstack, itemstack1) && ItemStack.isSame(itemstack, itemstack1)){
					itemstack1.grow(itemstack.getCount());
					this.craftMatrix.setInventorySlotContents(i, itemstack1);
				}else if(!this.player.getInventory().add(itemstack1)){
					this.player.drop(itemstack1, false);
				}
			}
		}
	}
	
	private boolean takeAspects(AspectCraftingInventory craftMatrix, @Nullable AspectHandler handler, UndecidedAspectStack[] aspectStacks){
		if(handler == null)
			return false;
		if(handler.countHolders() >= 0)
			return false;
		
		boolean satisfied = true;
		boolean anySatisfied = false;
		boolean hasAny = false;
		for(AspectHolder holder : handler.getHolders()){
			for(UndecidedAspectStack stack : aspectStacks){
				if(stack.any){
					hasAny = true;
					if(holder.getStack().getAmount() >= stack.stack.getAmount()){
						if(!anySatisfied)
							holder.drain(stack.stack.getAmount(), false);
						anySatisfied = true;
					}
				}else if(holder.getStack().getAspect() == stack.stack.getAspect()){
					if(holder.getStack().getAmount() >= stack.stack.getAmount()){
						holder.drain(stack.stack.getAmount(), false);
					}else
						satisfied = false;
				}
			}
		}
		return satisfied && (!hasAny || anySatisfied);
	}
}
