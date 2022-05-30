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
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

import java.util.HashMap;

public class MiningCast extends Cast {

	@Override
	public ResourceLocation getId() {
		return ArcanaVariables.arcLoc("mining");
	}
	
	@Override
	public Aspect getSpellAspect() {
		return Aspects.MINING;
	}

	@Override
	public int getSpellDuration() {
		return 1;
	}

	public int getMiningLevel() {
		return SpellValues.getOrDefault(AspectUtils.getAspect(data,"firstModifier"), 2);
	}

	public int getExplosivePower() {
		return SpellValues.getOrDefault(AspectUtils.getAspect(data,"secondModifier"), 0);
	}

	public int getFortune() {
		return SpellValues.getOrDefault(AspectUtils.getAspect(data,"sinModifier"), 0);
	}

	@Override
	public InteractionResult useOnPlayer(Player playerTarget) {
		playerTarget.displayClientMessage(new TranslatableComponent("status.arcana.invalid_spell"), true);
		return InteractionResult.FAIL;
	}

	@Override
	public InteractionResult useOnEntity(Player caster, Entity entityTarget) {
		caster.displayClientMessage(new TranslatableComponent("status.arcana.invalid_spell"), true);
		return InteractionResult.FAIL;
	}

	@SuppressWarnings("deprecation")
	public InteractionResult useOnBlock(Player caster, Level world, BlockPos blockTarget) {
		if(caster.level.isClientSide) return InteractionResult.SUCCESS;
		BlockState blockToDestroy = caster.level.getBlockState(blockTarget);
		if (blockToDestroy.canHarvestBlock(world, blockTarget, caster) && blockToDestroy.getDestroySpeed(world, blockTarget) != -1 && blockTarget.getY() != 0) {
			// Spawn block_break particles
			world.levelEvent(2001, blockTarget, Block.getId(blockToDestroy));

			// Check of it has tile entity
			BlockEntity tileentity = blockToDestroy.hasBlockEntity() ? world.getBlockEntity(blockTarget) : null;

			// Create dummy Pickaxe with enchantments and mining level
			HashMap<Enchantment, Integer> map = new HashMap<>();
			map.put(Enchantments.BLOCK_FORTUNE,getFortune());
			ItemStack pickaxe = createDummyPickaxe(getMiningLevel());
			EnchantmentHelper.setEnchantments(map,pickaxe);

			// Spawn drops and destroy block.
			Block.getDrops(blockToDestroy, (ServerLevel) world, blockTarget, tileentity, caster, pickaxe); // Cast should not be there...
			FluidState ifluidstate = blockToDestroy.getBlock().getFluidState(blockToDestroy);
			world.setBlock(blockTarget, ifluidstate.createLegacyBlock(), 3);
			blockToDestroy.updateNeighbourShapes(caster.level, blockTarget,3);
		}
		return InteractionResult.SUCCESS;
	}

	private ItemStack createDummyPickaxe(int miningLevel) { // TODO: Check if it works
		return new ItemStack(new PickaxeItem(new Tier() {
			@Override
			public int getUses() {
				return 1;
			}

			@Override
			public float getSpeed() {
				return 1;
			}

			@Override
			public float getAttackDamageBonus() {
				return 1;
			}

			@Override
			public int getLevel() {
				return miningLevel;
			}

			@Override
			public int getEnchantmentValue() {
				return 0;
			}

			@Override
			public Ingredient getRepairIngredient() {
				return null;
			}
		},0,0,new Item.Properties()),1);
	}
}
