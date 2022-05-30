package net.arcanamod.items.armor;

import net.arcanamod.items.AutoRepair;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class AutoRepairArmorItem extends ArmorItem {
	
	public AutoRepairArmorItem(ArmorMaterial materialIn, EquipmentSlot slot, Properties builder){
		super(materialIn, slot, builder);
	}
	
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged){
		return AutoRepair.shouldCauseReequipAnimation(oldStack, newStack, slotChanged);
	}
	
	public boolean shouldCauseBlockBreakReset(ItemStack oldStack, ItemStack newStack){
		return AutoRepair.shouldCauseBlockBreakReset(oldStack, newStack);
	}
	
	public void inventoryTick(ItemStack stack, Level world, Entity entity, int itemSlot, boolean isSelected){
		super.inventoryTick(stack, world, entity, itemSlot, isSelected);
		AutoRepair.inventoryTick(stack, world, entity, itemSlot, isSelected);
	}
}