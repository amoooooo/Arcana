package net.arcanamod.systems.spell.casts.impl;

import net.arcanamod.ArcanaVariables;
import net.arcanamod.aspects.Aspect;
import net.arcanamod.aspects.AspectUtils;
import net.arcanamod.effects.ArcanaEffects;
import net.arcanamod.systems.spell.SpellValues;
import net.arcanamod.systems.spell.casts.Cast;
import net.arcanamod.world.WorldInteractions;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import static net.arcanamod.aspects.Aspects.ICE;

public class IceCast extends Cast {
	@Override
	public ResourceLocation getId() {
		return ArcanaVariables.arcLoc("ice");
	}

	@Override
	public InteractionResult useOnBlock(Player caster, Level world, BlockPos blockTarget) {
		WorldInteractions.fromWorld(world).freezeBlock(blockTarget);
		return InteractionResult.SUCCESS;
	}

	@Override
	public InteractionResult useOnPlayer(Player playerTarget){
		playerTarget.addEffect(new MobEffectInstance(ArcanaEffects.FROZEN.get(),getFrozenDuration(),getAmplifier(),false,false));
		playerTarget.setRemainingFireTicks(-80);
		return InteractionResult.SUCCESS;
	}

	private int getAmplifier() {
		return SpellValues.getOrDefault(AspectUtils.getAspect(data,"secondModifier"),0);
	}

	private int getFrozenDuration() {
		return SpellValues.getOrDefault(AspectUtils.getAspect(data,"firstModifier"),3) * 20;
	}

	@Override
	public InteractionResult useOnEntity(Player caster, Entity entityTarget){
		if (entityTarget instanceof LivingEntity) {
			((LivingEntity) entityTarget).addEffect(new MobEffectInstance(ArcanaEffects.FROZEN.get(), getFrozenDuration(), getAmplifier(), false, false));
			((LivingEntity) entityTarget).setRemainingFireTicks(-80);
		}
		return InteractionResult.SUCCESS;
	}

	@Override
	public Aspect getSpellAspect() {
		return ICE;
	}

	@Override
	public int getSpellDuration() {
		return 1;
	}
}