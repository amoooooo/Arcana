package net.arcanamod.items;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public class EnchantedFilterItem extends Item {
	// Alembic efficiency, Pump speck size
	public int efficiencyBoost;
	// Alembic time per distill, Pump speck speed
	public int speedBoost;
	
	public EnchantedFilterItem(Properties properties, int efficiencyBoost, int speedBoost) {
		super(properties);
		this.efficiencyBoost = efficiencyBoost;
		this.speedBoost = speedBoost;
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flag) {
		super.appendHoverText(stack, world, tooltip, flag);
		if(efficiencyBoost != 0)
			tooltip.add(plusMinus("item.arcana.enchanted_filter.efficiency_desc", efficiencyBoost));
		if(speedBoost != 0)
			tooltip.add(plusMinus("item.arcana.enchanted_filter.speed_desc", speedBoost));
	}
	
	private static Component plusMinus(String key, int count) {
		if(count >= 0) return new TranslatableComponent(key, new TextComponent(repeat("+", count))).withStyle(ChatFormatting.GREEN);
		return new TranslatableComponent(key, new TextComponent(repeat("-", -count))).withStyle(ChatFormatting.RED);
	}
	
	@Nonnull
	private static String repeat(@Nullable String base, int count) {
		return String.valueOf(base).repeat(Math.max(0, count));
	}
}