package net.arcanamod.effects;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class ChargedEffect extends MobEffect {
	public ChargedEffect(){
		super(MobEffectCategory.HARMFUL, 0x1212FF);
	}

	@Override
	public void applyEffectTick(LivingEntity entityLivingBaseIn, int amplifier) {
		super.applyEffectTick(entityLivingBaseIn, amplifier);
		if (entityLivingBaseIn.level.isRainingAt(entityLivingBaseIn.getOnPos()))
			addAttributeModifier(Attributes.ARMOR,"0963515b-6188-4415-9bb6-edb7a45914cd",0.5f, AttributeModifier.Operation.MULTIPLY_TOTAL);
	}

	@Override
	public boolean isDurationEffectTick(int duration, int amplifier){
		return true;
	}
}
