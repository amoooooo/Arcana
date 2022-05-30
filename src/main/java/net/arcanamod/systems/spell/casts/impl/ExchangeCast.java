package net.arcanamod.systems.spell.casts.impl;

import net.arcanamod.ArcanaVariables;
import net.arcanamod.aspects.Aspect;
import net.arcanamod.aspects.AspectUtils;
import net.arcanamod.aspects.Aspects;
import net.arcanamod.systems.spell.SpellValues;
import net.arcanamod.systems.spell.casts.Cast;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

public class ExchangeCast extends Cast {

	@Override
	public ResourceLocation getId() {
		return ArcanaVariables.arcLoc("exchange");
	}
	
	@Override
	public Aspect getSpellAspect() {
		return Aspects.EXCHANGE;
	}

	@Override
	public int getSpellDuration() {
		return 1;
	}

	public int getMiningLevel(){
		return SpellValues.getOrDefault(AspectUtils.getAspect(data,"firstModifier"), 2);
	}

	public int getSize(){
		return SpellValues.getOrDefault(AspectUtils.getAspect(data,"secondModifier"), 1);
	}

	@Override
	public InteractionResult useOnEntity(Player caster, Entity targetEntity) {
		caster.displayClientMessage(new TranslatableComponent("status.arcana.invalid_spell"), true);
		return InteractionResult.FAIL;
	}

	@Override
	public InteractionResult useOnBlock(Player caster, Level world, BlockPos blockTarget) {
		if (caster.level.isClientSide) return InteractionResult.SUCCESS;
		BlockState blockToDestroy = caster.level.getBlockState(blockTarget);
		if (blockToDestroy.getBlock().canHarvestBlock(blockToDestroy, caster.level, blockTarget, caster) && blockToDestroy.canHarvestBlock(world, blockTarget, caster)) {
			ItemStack held = caster.getItemInHand(InteractionHand.OFF_HAND);
			if (!held.isEmpty() && Block.byItem(held.getItem()) != Blocks.AIR) {
				for (ItemStack stack : caster.level.getBlockState(blockTarget).getDrops(new LootContext.Builder((ServerLevel) caster.level)
						.withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf((blockTarget)))
						.withParameter(LootContextParams.TOOL, new ItemStack(getMiningLevel() >= 3 ? Items.DIAMOND_PICKAXE : Items.IRON_PICKAXE)))) {
					caster.addItem(stack);
				}
				caster.level.setBlockAndUpdate(blockTarget, Block.byItem(held.getItem()).defaultBlockState());
				held.shrink(1);
				blockToDestroy.updateNeighbourShapes(caster.level, blockTarget, 3);
			}
		}
		return InteractionResult.SUCCESS;
	}

	@Override
	public InteractionResult useOnPlayer(Player playerTarget) {
		playerTarget.displayClientMessage(new TranslatableComponent("status.arcana.invalid_spell"), true);
		return InteractionResult.FAIL;
	}
}
