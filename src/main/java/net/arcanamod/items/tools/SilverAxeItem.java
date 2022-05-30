package net.arcanamod.items.tools;

import net.arcanamod.systems.taint.Taint;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SilverAxeItem extends AxeItem {
	
	public SilverAxeItem(Tier tier, float attackDamage, float attackSpeed, Properties builder){
		super(tier, attackDamage, attackSpeed, builder);
	}
	
	public float getDestroySpeed(ItemStack stack, BlockState state){
		float speed = super.getDestroySpeed(stack, state);
		return Taint.isTainted(state.getBlock()) ? speed * 2 : speed;
	}
}