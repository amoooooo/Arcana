package net.arcanamod.items.armor;

import net.arcanamod.items.ArcanaItems;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

public enum ArcanaArmourMaterials implements ArmorMaterial {
	// TODO: Find Ingredient fuckery fix.
	GOGGLES("arcana:goggles_of_revealing", 5, new int[]{0, 0, 0, 2}, 15, SoundEvents.ARMOR_EQUIP_GOLD, 0, Items.GOLD_INGOT),
	ARCANIUM("arcana:arcanium_armor", 20, new int[]{2, 5, 7, 3}, 20, SoundEvents.ARMOR_EQUIP_IRON, 1, ArcanaItems.ARCANIUM_INGOT.get()),
	VOID_METAL("arcana:void_metal_armor", 17, new int[]{3, 6, 8, 4}, 10, SoundEvents.ARMOR_EQUIP_IRON, 2, ArcanaItems.VOID_METAL_INGOT.get());
	
	private static final int[] MAX_DAMAGE_ARRAY = new int[]{13, 15, 16, 11};
	private final String name;
	private final int maxDamageFactor;
	private final int[] damageReductionAmountArray;
	private final int enchantability;
	private final SoundEvent soundEvent;
	private final float toughness;
	private final Item repairMaterial;
	
	ArcanaArmourMaterials(String name, int maxDamageFactor, int[] damageReductionAmounts, int enchantability, SoundEvent equipSound, float toughness, Item repairMaterial){
		this.name = name;
		this.maxDamageFactor = maxDamageFactor;
		this.damageReductionAmountArray = damageReductionAmounts;
		this.enchantability = enchantability;
		this.soundEvent = equipSound;
		this.toughness = toughness;
		this.repairMaterial = repairMaterial;
	}
	
	public int getDurabilityForSlot(EquipmentSlot slotIn){
		return MAX_DAMAGE_ARRAY[slotIn.getIndex()] * this.maxDamageFactor;
	}
	
	public int getDefenseForSlot(EquipmentSlot slotIn){
		return damageReductionAmountArray[slotIn.getIndex()];
	}
	
	public int getEnchantmentValue(){
		return enchantability;
	}
	
	@Nonnull
	public SoundEvent getEquipSound(){
		return soundEvent;
	}
	
	public Ingredient getRepairIngredient(){
		return Ingredient.of(repairMaterial);
	}
	
	@Nonnull
	@OnlyIn(Dist.CLIENT)
	public String getName(){
		return name;
	}
	
	public float getToughness(){
		return toughness;
	}
	
	public float getKnockbackResistance(){
		return 0;
	}
}