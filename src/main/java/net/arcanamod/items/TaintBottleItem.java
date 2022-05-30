package net.arcanamod.items;

import net.arcanamod.entities.TaintBottleEntity;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class TaintBottleItem extends Item {
	
	public TaintBottleItem(Properties properties){
		super(properties);
	}
	
	public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand){
		world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.SPLASH_POTION_THROW, SoundSource.PLAYERS, 0.5F, 0.4F / (world.random.nextFloat() * 0.4F + 0.8F));
		
		if(!world.isClientSide()){
			TaintBottleEntity entity = new TaintBottleEntity(player, world);
			entity.shoot(player.getEyePosition(1.0F).x(), player.getEyePosition(1.0F).y(), player.getEyePosition(1.0F).z(), .5f, 1);
			world.addFreshEntity(entity);
		}
		
		ItemStack itemstack = player.getItemInHand(hand);
		player.awardStat(Stats.ITEM_USED.get(this));
		if(!player.getAbilities().instabuild)
			itemstack.shrink(1);
		
		return InteractionResultHolder.success(itemstack);
	}
}