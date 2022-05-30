package net.arcanamod.containers;

import net.arcanamod.aspects.Aspect;
import net.arcanamod.aspects.AspectStack;
import net.arcanamod.aspects.Aspects;
import net.arcanamod.aspects.handlers.AspectHandler;
import net.arcanamod.blocks.tiles.ResearchTableBlockEntity;
import net.arcanamod.client.gui.ResearchTableScreen;
import net.arcanamod.containers.slots.AspectSlot;
import net.arcanamod.containers.slots.AspectStoreSlot;
import net.arcanamod.items.ArcanaItems;
import net.arcanamod.items.ArcanaTags;
import net.arcanamod.systems.research.Puzzle;
import net.arcanamod.systems.research.ResearchBooks;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ResearchTableMenu extends AspectMenu {
	
	protected ResearchTableMenu(@Nullable MenuType<?> type, int id){
		super(type, id);
	}
	
	public ResearchTableBlockEntity te;
	public List<AspectSlot> scrollableSlots = new ArrayList<>();
	
	private ItemStack note, ink;
	public final List<AspectSlot> puzzleSlots = new ArrayList<>();
	public final List<Slot> puzzleItemSlots = new ArrayList<>();
	public Container puzzleInventorySlots;
	Player lastClickPlayer;
	
	public ResearchTableMenu(MenuType type, int id, Inventory playerInventory, ResearchTableBlockEntity te){
		super(type, id);
		this.te = te;
		addOwnSlots(playerInventory);
		addPlayerSlots(playerInventory);
		addAspectSlots(playerInventory);
	}

	@SuppressWarnings("ConstantConditions")
	public ResearchTableMenu(int i, Inventory playerInventory, FriendlyByteBuf packetBuffer) {
		this(ArcanaContainers.RESEARCH_TABLE.get(),i,playerInventory,(ResearchTableBlockEntity) playerInventory.player.getLevel().getBlockEntity(packetBuffer.readBlockPos()));
	}

	private void addPlayerSlots(Container playerInventory){
		int hotX = 79, invX = 139, baseY = ResearchTableScreen.HEIGHT - 61;
		// Slots for the main inventory
		for(int row = 0; row < 3; row++)
			for(int col = 0; col < 9; col++){
				int x = invX + col * 18;
				int y = row * 18 + baseY;
				addSlot(new Slot(playerInventory, col + row * 9 + 9, x, y));
			}
		
		for(int row = 0; row < 3; ++row)
			for(int col = 0; col < 3; ++col){
				int x = hotX + col * 18;
				int y = row * 18 + baseY;
				addSlot(new Slot(playerInventory, col + row * 3, x, y));
			}
	}
	
	private void addOwnSlots(Inventory playerInventory){
		@SuppressWarnings("ConstantConditions")
		IItemHandler itemHandler = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).orElse(null);
		note = te.note();
		ink = te.ink();
		// 137, 11
		addSlot(new SlotItemHandler(itemHandler, 1, 137, 11){
			public boolean mayPlace(@Nonnull ItemStack stack){
				// only ink
				return super.mayPlace(stack) && stack.is(ArcanaTags.SCRIBING_TOOLS);
			}
			
			public void setChanged(){
				super.setChanged();
				if((ink.isEmpty() && !te.ink().isEmpty()) || (!ink.isEmpty() && te.ink().isEmpty())){
					ink = te.ink();
					refreshPuzzleSlots(playerInventory);
				}
			}
		});
		// 155, 11
		addSlot(new SlotItemHandler(itemHandler, 2, 155, 11){
			public boolean mayPlace(@Nonnull ItemStack stack){
				// only notes
				return super.mayPlace(stack) && stack.getItem() == ArcanaItems.RESEARCH_NOTE.get() || stack.getItem() == ArcanaItems.RESEARCH_NOTE_COMPLETE.get();
			}
			
			public void setChanged(){
				super.setChanged();
				note = te.note();
				// remove added slots & aspect slots
				refreshPuzzleSlots(playerInventory);
			}
		});
	}
	
	public void clicked(int slot, int dragType, ClickType clickType, Player player){
		lastClickPlayer = player;
		super.clicked(slot, dragType, clickType, player);
		if(!ink.isEmpty())
			validate();
	}
	
	private void refreshPuzzleSlots(Container playerInventory){
		Player playerEntity = lastClickPlayer;
		if(playerEntity == null)
			playerEntity = ((Inventory)playerInventory).player;
		aspectSlots.removeAll(puzzleSlots);
		puzzleSlots.clear();
		
		if(puzzleInventorySlots != null)
			if(!playerEntity.level.isClientSide)
				clearContainer(playerEntity, puzzleInventorySlots);
		
		for(int i = puzzleItemSlots.size() - 1; i >= 0; i--)
			slots.remove(puzzleItemSlots.get(i));
		puzzleItemSlots.clear();

		Player finalPlayerEntity = playerEntity;
		getFromNote().ifPresent(puzzle -> {
			if(!ink.isEmpty() && ink.getDamageValue() < ink.getMaxDamage() - 1)
				if(note.getItem() == ArcanaItems.RESEARCH_NOTE.get()){
					for(AspectSlot slot : puzzle.getAspectSlots(() -> AspectHandler.getFrom(te))){
						puzzleSlots.add(slot);
						aspectSlots.add(slot);
					}
					List<Puzzle.SlotInfo> locations = puzzle.getItemSlotLocations(finalPlayerEntity);
					int size = locations.size();
					puzzleInventorySlots = new SimpleContainer(size){
						public void setChanged(){
							super.setChanged();
							ResearchTableMenu.this.slotsChanged(this);
						}
					};
					for(int i = 0; i < locations.size(); i++){
						Puzzle.SlotInfo slotInfo = locations.get(i);
						Slot slot = new Slot(puzzleInventorySlots, i, slotInfo.x, slotInfo.y){
							public int getSlotStackLimit(){
								return slotInfo.max != -1 ? slotInfo.max : super.getMaxStackSize();
							}
						};
						//if(slotInfo.bg_name != null)
						//	slot.setBackgroundName(slotInfo.bg_name);
						addSlot(slot);
						puzzleItemSlots.add(slot);
					}
				}
		});
		
		// populate from save, if there are any saved
		if(!note.isEmpty() && note.getTag() != null && note.getTag().contains("progress")){
			ListTag nbt = note.getTag().getList("progress", Tag.TAG_COMPOUND);
			for(Tag tag : nbt)
				if(tag instanceof CompoundTag){
					CompoundTag compound = (CompoundTag)tag;
					if(compound.contains("slot") && compound.contains("aspect")){
						int index = compound.getInt("slot");
						Aspect aspect = Aspects.ASPECTS.get(new ResourceLocation(compound.getString("aspect")));
						if(puzzleSlots.size() > index){
							AspectSlot slot1 = puzzleSlots.get(index);
							if(slot1 instanceof AspectStoreSlot)
								((AspectStoreSlot)slot1).getHolder().insert(new AspectStack(aspect, 1));
						}
					}
				}
		}
	}
	
	public void removed(@Nonnull Player player){
		super.removed(player);
		if(puzzleInventorySlots != null)
			if(!player.level.isClientSide)
				clearContainer(player, this.puzzleInventorySlots);
	}
	
	public boolean shouldReturnAspectsOnClose(){
		return false;
	}
	
	protected void addAspectSlots(Container playerInventory){
		Aspect[] values = (Aspect[]) Aspects.getWithoutEmpty().toArray();
		Supplier<AspectHandler> table = () -> AspectHandler.getFrom(te);
		for(int i = 0; i < values.length; i++){
			Aspect aspect = values[i];
			int yy = i / 6;
			int xx = i % 6;
			boolean visible = true;
			if(yy >= 6){
				visible = false;
				// wrap
				yy %= 6;
			}
			int x = 11 + 20 * xx;
			int y = 32 + 21 * yy;
			if(xx % 2 == 0)
				y += 5;
			AspectSlot slot = new AspectSlot(aspect, table, x, y);
			slot.visible = visible;
			getAspectSlots().add(slot);
			scrollableSlots.add(slot);
		}
		refreshPuzzleSlots(playerInventory);
	}
	
	@Override
	public ItemStack quickMoveStack(Player playerIn, int index){
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = slots.get(index);
		
		if(slot != null && slot.hasItem()){
			if(note.isEmpty() || index != 2){
				ItemStack itemstack1 = slot.getItem();
				itemstack = itemstack1.copy();
				
				if(index < 3){
					if(!this.moveItemStackTo(itemstack1, 3, slots.size(), true))
						return ItemStack.EMPTY;
				}else if(!this.moveItemStackTo(itemstack1, 0, 3, false))
					return ItemStack.EMPTY;
				
				if(itemstack1.isEmpty())
					slot.set(ItemStack.EMPTY);
				else
					slot.setChanged();
			}
		}
		
		return itemstack;
	}
	
	public void onAspectSlotChange() {
		super.onAspectSlotChange();
		if(!ink.isEmpty())
			validate();
		
		// save aspect slots to research note
		ListTag aspectSave = new ListTag();
		for(int i = 0, size = puzzleSlots.size(); i < size; i++){
			AspectSlot slot = puzzleSlots.get(i);
			if(slot.getAspect() != null){
				CompoundTag slotNbt = new CompoundTag();
				slotNbt.putInt("slot", i);
				slotNbt.putString("aspect", Aspects.ASPECT_IDS.get(slot.getAspect()).toString());
				aspectSave.add(slotNbt);
			}
		}
		if(!note.isEmpty())
			note.getOrCreateTag().put("progress", aspectSave);
	}
	
	public void validate(){
		if(getFromNote().isPresent()){
			Puzzle puzzle = getFromNote().get();
			if(puzzle.validate(puzzleSlots, puzzleItemSlots, lastClickPlayer, this)){
				ItemStack complete = new ItemStack(ArcanaItems.RESEARCH_NOTE_COMPLETE.get());
				CompoundTag data = note.getTag();
				complete.setTag(data);
				
				// Don't close them, because that will move aspects over to the main table.
				// This will need changing if slots need to be able to clean up arbitrary resources.
				aspectSlots.removeAll(puzzleSlots);
				puzzleSlots.clear();
				
				for(int i = puzzleItemSlots.size() - 1; i >= 0; i--){
					Slot slot = puzzleItemSlots.get(i);
					slots.remove(slot);
				}
				puzzleItemSlots.clear();
				puzzleInventorySlots = null;
				
				// lastClickPlayer can be null
				// so we can't go through this normal means
				int amount = te.ink().getItem().damageItem(te.ink(), 1, null, __ -> {
				});
				te.ink().hurt(amount, new Random(), null);
				
				te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).ifPresent(handler -> {
					handler.extractItem(2, 64, false);
					handler.insertItem(2, complete, false);
				});
			}
		}
	}
	
	public Optional<Puzzle> getFromNote(){
		if(!note.isEmpty() && note.getTag() != null && note.getTag().contains("puzzle"))
			return Optional.ofNullable(ResearchBooks.puzzles.get(new ResourceLocation(note.getTag().getString("puzzle"))));
		else
			return Optional.empty();
	}
	
	public boolean stillValid(Player player){
		return true;
	}

	/**
	 * Gets a list of every aspect handler that's open; i.e. can be modified in this GUI and might need syncing.
	 * The contents of this list must be the same on the server and client side.
	 *
	 * @return A list containing all open AspectHandlers.
	 */
	public List<AspectHandler> getOpenHandlers(){
		AspectHandler item = AspectHandler.getFrom(te.visItem());
		if(item != null)
			return Arrays.asList(AspectHandler.getFrom(te), item);
		else
			return Collections.singletonList(AspectHandler.getFrom(te));
	}
}