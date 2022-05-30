package net.arcanamod.systems.spell.casts.impl;

import net.arcanamod.ArcanaVariables;
import net.arcanamod.aspects.Aspect;
import net.arcanamod.aspects.Aspects;
import net.arcanamod.systems.spell.casts.Cast;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class FabricCast extends Cast {

	@Override
	public ResourceLocation getId() {
		return ArcanaVariables.arcLoc("fabric");
	}

	@Override
	public Aspect getSpellAspect() {
		return Aspects.FABRIC;
	}

	@Override
	public int getSpellDuration() {
		return 0;
	}

	@Override
	public InteractionResult useOnEntity(Player caster, Entity target) {
		target.sendMessage(new TextComponent("MCP is broken everyone shold use Yarn"), Util.NIL_UUID);
		target.sendMessage(new TextComponent(target.getName().getString()+" gets gold award on r/minecraft"), Util.NIL_UUID);
		target.sendMessage(new TextComponent(target.getName().getString()+" gets gold award on r/minecraft"), Util.NIL_UUID);
		return InteractionResult.SUCCESS;
	}

	@Override
	public InteractionResult useOnBlock(Player caster, Level world, BlockPos blockTarget) {
		caster.sendMessage(new TextComponent("hehe Ticking block entity"), Util.NIL_UUID);
		return InteractionResult.SUCCESS;
	}

	@Override
	public InteractionResult useOnPlayer(Player playerTarget) {
		playerTarget.sendMessage(new TextComponent("MCP is broken everyone shold use Yarn"), Util.NIL_UUID);
		playerTarget.sendMessage(new TextComponent(playerTarget.getName().getString()+" gets gold award on r/minecraft"), Util.NIL_UUID);
		playerTarget.sendMessage(new TextComponent(playerTarget.getName().getString()+" gets gold award on r/minecraft"), Util.NIL_UUID);
		return InteractionResult.SUCCESS;
	}
}
