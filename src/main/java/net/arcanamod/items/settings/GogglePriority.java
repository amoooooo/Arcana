package net.arcanamod.items.settings;

import net.arcanamod.items.armor.GogglesItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.EquipmentSlot;

public enum GogglePriority{
	SHOW_NONE,
	SHOW_NODE,
	SHOW_ASPECTS;
	
	public static GogglePriority getClientGogglePriority(){
		LocalPlayer player = Minecraft.getInstance().player;
		return !(player == null) && !player.getItemBySlot(EquipmentSlot.HEAD).isEmpty() && player.getItemBySlot(EquipmentSlot.HEAD).getItem() instanceof GogglesItem ? ((GogglesItem)player.getItemBySlot(EquipmentSlot.HEAD).getItem()).priority : SHOW_NONE;
	}
}
