package net.arcanamod.effects;

import net.arcanamod.entities.TaintedGooWrapper;
import net.arcanamod.systems.taint.Taint;
import net.arcanamod.systems.taint.TaintDamageSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.player.Player;

public class TaintedEffect extends MobEffect {
	public TaintedEffect() {
		super(MobEffectCategory.HARMFUL, 0xa200ff);
	}
	
	@Override
	public void applyEffectTick(LivingEntity entity, int amplifier) {
		if (!Taint.isTainted(entity.getType())) {
			entity.hurt(TaintDamageSource.TAINT, 1.0F + amplifier);
			if ((entity.getHealth() <= (entity.getMaxHealth() / 4f) || entity.getHealth() == 1)
					&& entity.level.getDifficulty() != Difficulty.PEACEFUL) {
				changeEntityToTainted(entity);
			}
		}
	}
	
	private void changeEntityToTainted(LivingEntity entityLiving) {
		if (!(entityLiving instanceof Player) && Taint.getTaintedOfEntity(entityLiving.getType()) != null) {
			LivingEntity l = (LivingEntity)Taint.getTaintedOfEntity(entityLiving.getType()).create(entityLiving.level);
			if (l != null) {
				l.setPose(entityLiving.getPose());
				if (!l.getLevel().isClientSide()) {
					l.getLevel().addFreshEntity(l);
				}
				entityLiving.remove(Entity.RemovalReason.DISCARDED);
			}
		}
	}
	
	@Override
	public boolean isDurationEffectTick(int duration, int amplifier) {
		// Same rate as Regeneration
		int k = 30 >> amplifier;
		if(k > 0)
			return duration % k == 0;
		return true;
	}

	@Override
	public void removeAttributeModifiers(LivingEntity entityLivingBaseIn, AttributeMap attributeMapIn, int amplifier) {
		super.removeAttributeModifiers(entityLivingBaseIn, attributeMapIn, amplifier);
		((TaintedGooWrapper) entityLivingBaseIn).setGooTicks(0);
	}
}