package net.arcanamod.containers.slots;

import net.arcanamod.items.GauntletItem;
import net.arcanamod.items.MagicDeviceItem;
import net.arcanamod.items.StaffItem;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class WandSlot extends Slot {
	private final IWandSlotListener listener;
	public WandSlot(IWandSlotListener listener, Container inventoryIn, int index, int xPosition, int yPosition) {
		super(inventoryIn, index, xPosition, yPosition);
		this.listener = listener;
	}

	@Override
	public boolean mayPlace(ItemStack stack) {
		Item item = stack.getItem();
		return item instanceof MagicDeviceItem && !(item instanceof GauntletItem) && !(item instanceof StaffItem);
	}

	@Override
	public void setChanged() {
		super.setChanged();
		listener.onWandSlotUpdate();
	}
}
