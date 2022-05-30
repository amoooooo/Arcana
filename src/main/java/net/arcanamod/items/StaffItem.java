package net.arcanamod.items;

import net.arcanamod.aspects.AspectStack;
import net.arcanamod.items.attachment.Cap;
import net.arcanamod.items.attachment.Core;
import net.arcanamod.systems.spell.Spell;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class StaffItem extends MagicDeviceItem{
	public StaffItem(Item.Properties properties){
		super(properties);
	}

	@Override
	public boolean canCraft() {
		return false;
	}

	@Override
	public boolean canUseSpells() {
		return true;
	}

	@Override
	public String getDeviceName() {
		return "item.arcana.wand.variant.staff";
	}

	@Override
	protected float getVisModifier() {
		return 2.5f;
	}

	@Override
	protected float getDifficultyModifier() {
		return 1;
	}

	@Override
	protected float getComplexityModifier() {
		return 1;
	}

	public static ItemStack withCapAndCore(String cap, String core){
		CompoundTag nbt = new CompoundTag();
		nbt.putString("cap", cap);
		nbt.putString("core", core);
		ItemStack stack = new ItemStack(ArcanaItems.WAND.get(), 1);
		stack.setTag(nbt);
		return stack;
	}

	public static ItemStack withCapAndCore(ResourceLocation cap, ResourceLocation core){
		return withCapAndCore(cap.toString(), core.toString());
	}

	public static ItemStack withCapAndCore(Cap cap, Core core){
		return withCapAndCore(cap.getId(), core.getId());
	}

	public int getUseDuration(ItemStack stack){
		return 72000;
	}

	public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items){
		if(allowdedIn(group)){
			// iron/wooden, silver/dair, gold/greatwood, thaumium/silverwood, void/arcanium
			items.add(withCapAndCoreForCt("iron_cap", "wood_wand"));
			items.add(withCapAndCoreForCt("silver_cap", "dair_wand"));
			items.add(withCapAndCoreForCt("gold_cap", "greatwood_wand"));
			items.add(withCapAndCoreForCt("thaumium_cap", "silverwood_wand"));
			items.add(withCapAndCoreForCt("void_cap", "arcanium_wand"));
		}
	}

	public static ItemStack withCapAndCoreForCt(String cap, String core){
		CompoundTag nbt = new CompoundTag();
		nbt.putString("cap", "arcana:" + cap);
		nbt.putString("core", "arcana:" + core);
		ItemStack stack = new ItemStack(ArcanaItems.STAFF.get(), 1);
		stack.setTag(nbt);
		return stack;
	}

	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(@Nonnull ItemStack stack, @Nullable Level world, @Nonnull List<Component> tooltip, @Nonnull TooltipFlag flag){
		super.appendHoverText(stack, world, tooltip, flag);
		// Add focus info
		Spell spell = getFocus(stack).getSpell(stack);
		if(spell != null){
			Optional<Component> name = spell.getName(getFocusData(stack).getCompound("Spell"));
			name.ifPresent(e -> tooltip.add(new TranslatableComponent("tooltip.arcana.spell", e,
					spell.getSpellCosts().toList().stream()
							.map(AspectStack::getAspect)
							.map(aspect -> I18n.get("aspect." + aspect.name()))
							.collect(Collectors.joining(", ")))));
		}
	}
}
