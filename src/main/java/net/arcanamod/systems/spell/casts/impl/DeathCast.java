package net.arcanamod.systems.spell.casts.impl;

import net.arcanamod.ArcanaVariables;
import net.arcanamod.aspects.Aspect;
import net.arcanamod.systems.spell.casts.Cast;
import net.arcanamod.util.NotImplementedException;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import static net.arcanamod.aspects.Aspects.DEATH;

public class DeathCast extends Cast {
	@Override
	public ResourceLocation getId() {
		return ArcanaVariables.arcLoc("death");
	}

	@Override
	public InteractionResult useOnBlock(Player caster, Level world, BlockPos blockTarget){
		throw new NotImplementedException();
	}

	@Override
	public InteractionResult useOnPlayer(Player playerTarget){
		throw new NotImplementedException();
	}

	@Override
	public InteractionResult useOnEntity(Player caster, Entity entityTarget){
		throw new NotImplementedException();
	}

	@Override
	public Aspect getSpellAspect() {
		return DEATH;
	}

	@Override
	public int getSpellDuration() {
		return 1;
	}
}