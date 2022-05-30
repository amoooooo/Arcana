package net.arcanamod.items.armor;

import net.arcanamod.items.settings.GogglePriority;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;

public class GogglesItem extends ArmorItem {
	
	public GogglePriority priority;
	
	public GogglesItem(ArmorMaterial material, Properties properties, GogglePriority priority){
		super(material, EquipmentSlot.HEAD, properties);
		this.priority = priority;
	}
}