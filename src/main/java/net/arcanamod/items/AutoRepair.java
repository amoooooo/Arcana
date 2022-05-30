package net.arcanamod.items;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Helper class that implements auto-repair functionality, used by void metal tools.
 *
 * @author Luna
 * @see net.arcanamod.items.armor.AutoRepairArmorItem
 * @see net.arcanamod.items.tools.AutoRepairSwordItem
 * @see net.arcanamod.items.tools.AutoRepairShovelItem
 * @see net.arcanamod.items.tools.AutoRepairPickaxeItem
 * @see net.arcanamod.items.tools.AutoRepairHoeItem
 * @see net.arcanamod.items.tools.AutoRepairAxeItem
 */
@ParametersAreNonnullByDefault
public class AutoRepair {
	private static final String TAG_TIMER = "arcana:repair_timer";
	private static final int FULL_TIMER = 70;
	
	public static boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return slotChanged;
	}
	
	public static boolean shouldCauseBlockBreakReset(ItemStack oldStack, ItemStack newStack) {
		return newStack.getItem() != oldStack.getItem();
	}
	
	public static void inventoryTick(ItemStack stack, Level world, Entity entity, int itemSlot, boolean isSelected) {
		CompoundTag tag = stack.getOrCreateTag();
		if(!tag.contains(TAG_TIMER))
			tag.putInt(TAG_TIMER, FULL_TIMER);
		if(tag.getInt(TAG_TIMER) > 0)
			tag.putInt(TAG_TIMER, tag.getInt(TAG_TIMER) - 1);
		else{
			tag.putInt(TAG_TIMER, FULL_TIMER);
			stack.setDamageValue(stack.getDamageValue() - 1);
		}
	}
}