package net.arcanamod.systems.spell.casts.impl;

import net.arcanamod.ArcanaVariables;
import net.arcanamod.aspects.Aspect;
import net.arcanamod.blocks.ArcanaBlocks;
import net.arcanamod.systems.spell.casts.Cast;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

import static net.arcanamod.aspects.Aspects.LIGHT;

public class LightCast extends Cast {
	@Override
	public ResourceLocation getId() {
		return ArcanaVariables.arcLoc("light");
	}

	@Override
	public InteractionResult useOnBlock(Player caster, Level world, BlockPos blockTarget) {
		if (world.getBlockState(blockTarget.above()).getBlock() == ArcanaBlocks.LIGHT_BLOCK.get()){
			return InteractionResult.SUCCESS;
		}
		if (world.getBlockState(blockTarget.above()).getBlock() == Blocks.AIR){
			world.setBlockAndUpdate(blockTarget.above(), ArcanaBlocks.LIGHT_BLOCK.get().defaultBlockState());
			return InteractionResult.SUCCESS;
		}
		return InteractionResult.FAIL;
	}

	@Override
	public InteractionResult useOnPlayer(Player playerTarget) {
		return placeLight(playerTarget);
	}

	@Override
	public InteractionResult useOnEntity(Player caster, Entity entityTarget) {
		return placeLight(entityTarget);
	}

	public InteractionResult placeLight(Entity entityTarget){
		if (entityTarget.level.getBlockState(entityTarget.getOnPos().above()).getBlock() == ArcanaBlocks.LIGHT_BLOCK.get()){
			return InteractionResult.SUCCESS;
		}
		if (entityTarget.level.getBlockState(entityTarget.getOnPos().above()).getBlock() == Blocks.AIR){
			entityTarget.level.setBlockAndUpdate(entityTarget.getOnPos().above(), ArcanaBlocks.LIGHT_BLOCK.get().defaultBlockState());
			return InteractionResult.SUCCESS;
		}
		return InteractionResult.FAIL;
	}

	@Override
	public Aspect getSpellAspect() {
		return LIGHT;
	}

	@Override
	public int getSpellDuration() {
		return 1;
	}
}