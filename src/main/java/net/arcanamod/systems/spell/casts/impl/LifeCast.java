package net.arcanamod.systems.spell.casts.impl;

import net.arcanamod.ArcanaVariables;
import net.arcanamod.aspects.Aspect;
import net.arcanamod.aspects.AspectUtils;
import net.arcanamod.aspects.Aspects;
import net.arcanamod.effects.ArcanaEffects;
import net.arcanamod.systems.spell.SpellValues;
import net.arcanamod.systems.spell.casts.Cast;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class LifeCast extends Cast {

	@Override
	public ResourceLocation getId() {
		return ArcanaVariables.arcLoc("life");
	}
	
	@Override
	public Aspect getSpellAspect() {
		return Aspects.LIFE;
	}

	@Override
	public int getSpellDuration() {
		return 1;
	}

	public int getVictusDuration() {
		return SpellValues.getOrDefault(AspectUtils.getAspect(data,"firstModifier"), 10);
	}

	public int getAmplifier() {
		return SpellValues.getOrDefault(AspectUtils.getAspect(data,"secondModifier"), 1);
	}

	@Override
	public InteractionResult useOnEntity(Player caster, Entity targetEntity) {
		if (targetEntity instanceof LivingEntity)
			((LivingEntity)targetEntity).addEffect(new MobEffectInstance(ArcanaEffects.VICTUS.get(),getVictusDuration(),getAmplifier(),false,false));
		return InteractionResult.SUCCESS;
	}

	@Override
	public InteractionResult useOnBlock(Player caster, Level world, BlockPos blockTarget) {

		return InteractionResult.SUCCESS;
	}

	@Override
	public InteractionResult useOnPlayer(Player playerTarget) {
		playerTarget.addEffect(new MobEffectInstance(ArcanaEffects.VICTUS.get(),getVictusDuration(),getAmplifier(),false,false));
		return InteractionResult.SUCCESS;
	}
}