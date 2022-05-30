package net.arcanamod.containers;

import net.arcanamod.containers.slots.AspectCraftingResultSlot;
import net.arcanamod.containers.slots.IWandSlotListener;
import net.arcanamod.containers.slots.WandSlot;
import net.arcanamod.items.recipes.ArcanaRecipes;
import net.arcanamod.items.recipes.AspectCraftingInventory;
import net.arcanamod.items.recipes.IArcaneCraftingRecipe;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ArcaneCraftingTableMenu extends RecipeBookMenu<AspectCraftingInventory> implements IWandSlotListener {

	public final Container inventory;
	public final Inventory playerInventory;
	public final AspectCraftingInventory craftMatrix;
	public final ResultContainer craftResult = new ResultContainer();
	public final int craftResultSlot;

	public ArcaneCraftingTableMenu(MenuType<?> type, int id, Inventory playerInventory, Container inventory){
		super(type, id);
		this.inventory = inventory;
		this.playerInventory = playerInventory;
		WandSlot wandSlot = new WandSlot(this, inventory, 10, 160, 18);
		this.craftMatrix = new AspectCraftingInventory(this, wandSlot, 3, 3, inventory, playerInventory.player);
		this.addSlot(new AspectCraftingResultSlot(playerInventory.player, this.craftMatrix, this.craftResult, 0, 160, 64));
		this.craftResultSlot = 1;
		this.addSlot(wandSlot);
		for(int i = 0; i < 3; ++i)
			for(int j = 0; j < 3; ++j)
				this.addSlot(new Slot(craftMatrix, j + i * 3, 42 + j * 23, 41 + i * 23));
		addPlayerSlots(playerInventory);
		// guarantee craft() called on serverside
		craft(this, this.playerInventory.player.level, this.playerInventory.player, this.craftMatrix, this.craftResult);
	}

	public ArcaneCraftingTableMenu(int id, Inventory playerInventory, Container inventory){
		this(ArcanaContainers.ARCANE_CRAFTING_TABLE.get(), id, playerInventory, inventory);
	}

	public ArcaneCraftingTableMenu(int i, Inventory playerInventory){
		this(ArcanaContainers.ARCANE_CRAFTING_TABLE.get(), i, playerInventory, new SimpleContainer(10));
	}

	protected static void craft(AbstractContainerMenu container, Level world, Player playerEntity, AspectCraftingInventory craftingInventory, ResultContainer resultInventory){
		if (!world.isClientSide) {
			ServerPlayer serverplayerentity = (ServerPlayer)playerEntity;
			ItemStack itemstack = ItemStack.EMPTY;
			// look for arcane crafting
			if(world.getServer() != null){
				Optional<IArcaneCraftingRecipe> optional = world.getServer().getRecipeManager().getRecipeFor(ArcanaRecipes.Types.ARCANE_CRAFTING_SHAPED, craftingInventory, world);
				if(optional.isPresent()){
					IArcaneCraftingRecipe iarcanecraftingrecipe = optional.get();
					if(resultInventory.setRecipeUsed(world, serverplayerentity, iarcanecraftingrecipe)){
						itemstack = iarcanecraftingrecipe.assemble(craftingInventory);
					}
				}
				// if arcane crafting is not possible, look for regular crafting
				else{
					Optional<CraftingRecipe> craftingOptional = world.getServer().getRecipeManager().getRecipeFor(RecipeType.CRAFTING, craftingInventory, world);
					if(craftingOptional.isPresent()){
						CraftingRecipe recipe = craftingOptional.get();
						if(resultInventory.setRecipeUsed(world, serverplayerentity, recipe))
							itemstack = recipe.assemble(craftingInventory);
					}
				}
				resultInventory.setItem(0, itemstack);
				serverplayerentity.connection.send(new ClientboundContainerSetSlotPacket(container.containerId, container.incrementStateId(), 0, itemstack));
			}
		}
	}

	/**
	 * Callback for when the crafting matrix is changed.
	 */
	public void slotsChanged(Container inventory){
		craft(this, this.playerInventory.player.level, this.playerInventory.player, this.craftMatrix, this.craftResult);
	}

	/**
	 * Determines whether supplied player can use this container
	 */
	@Override
	public boolean stillValid(Player player){
		return inventory == null || inventory.stillValid(player);
	}

	private void addPlayerSlots(Container playerInventory){
		for(int i = 0; i < 3; ++i) {
			for(int j = 0; j < 9; ++j) {
				this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 16 + j * 18, 151 + i * 18));
			}
		}

		for(int k = 0; k < 9; ++k) {
			this.addSlot(new Slot(playerInventory, k, 16 + k * 18, 209));
		}
	}

	@Override
	public void fillCraftSlotsStackedContents(StackedContents itemHelper){
		craftMatrix.fillStackedContents(itemHelper);
	}

	@Override
	public void clearCraftingContent(){
		this.inventory.clearContent();
	}

	@Override
	public boolean recipeMatches(Recipe<? super AspectCraftingInventory> recipe){
		return recipe.matches(craftMatrix, playerInventory.player.level);
	}

	@Override
	public int getResultSlotIndex(){
		return craftResultSlot;
	}

	@Override
	public int getGridWidth(){
		return 3;
	}

	@Override
	public int getGridHeight(){
		return 3;
	}

	@Override
	public int getSize(){
		return craftMatrix.getSizeInventory();
	}
	
	public RecipeBookType getRecipeBookType(){
		return RecipeBookType.CRAFTING;
	}

	@Override
	public boolean shouldMoveToInventory(int index) {
		return index != this.getResultSlotIndex();
	}

	/**
	 * Handle when the stack in slot {@code index} is shift-clicked. Normally this moves the stack between the player
	 * inventory and the other inventory(s).
	 */
	public ItemStack quickMoveStack(Player player, int index) {
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = slots.get(index);
		if (slot != null && slot.hasItem()) {
			ItemStack itemstack1 = slot.getItem();
			itemstack = itemstack1.copy();
			if (index == 0) {
				itemstack1.getItem().onCraftedBy(itemstack1, player.level, player);
				if (!moveItemStackTo(itemstack1, 11, 47, true)) {
					return ItemStack.EMPTY;
				}

				slot.onQuickCraft(itemstack1, itemstack);
			} else if (index >= 11 && index < 47) {
				if (!moveItemStackTo(itemstack1, 1, 11, false)) {
					if (index < 38) {  // prioritize hotbar
						if (!moveItemStackTo(itemstack1, 38, 47, false)) {
							return ItemStack.EMPTY;
						}
					} else if (!moveItemStackTo(itemstack1, 11, 38, false)) {
						return ItemStack.EMPTY;
					}
				}
			} else if (!moveItemStackTo(itemstack1, 11, 47, false)) {
				return ItemStack.EMPTY;
			}

			if (itemstack1.isEmpty()) {
				slot.set(ItemStack.EMPTY);
			} else {
				slot.setChanged();
			}

			if (itemstack1.getCount() == itemstack.getCount()) {
				return ItemStack.EMPTY;
			}

			slot.onTake(player, itemstack1);
			if (index == 0) {
				player.drop(itemstack1, false);
			}
		}

		return itemstack;
	}

	@Override
	public void onWandSlotUpdate() {
		craft(this, playerInventory.player.level, playerInventory.player, craftMatrix, craftResult);
	}
	
	public void removed(Player player){
		super.removed(player);
	}
}