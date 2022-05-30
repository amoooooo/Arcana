package net.arcanamod.systems.spell.casts.impl;

import net.arcanamod.ArcanaVariables;
import net.arcanamod.aspects.Aspect;
import net.arcanamod.aspects.AspectUtils;
import net.arcanamod.aspects.Aspects;
import net.arcanamod.blocks.ArcanaBlocks;
import net.arcanamod.blocks.tiles.VacuumBlockEntity;
import net.arcanamod.systems.spell.SpellValues;
import net.arcanamod.systems.spell.casts.Cast;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class VacuumCast extends Cast {

    @Override
    public ResourceLocation getId() {
        return ArcanaVariables.arcLoc("vacuum");
    }
    
    /**
     * Core aspect in spell.
     *
     * @return returns core aspect.
     */
    @Override
    public Aspect getSpellAspect() {
        return Aspects.VACUUM;
    }

    @Override
    public int getSpellDuration() {
        return 1;
    }

    @Override
    public InteractionResult useOnBlock(Player caster, Level world, BlockPos blockTarget) {
        if(caster.level.isClientSide) return InteractionResult.SUCCESS;
        BlockPos.betweenClosed(
                blockTarget.offset(-Math.floor(getWidth(caster)), -Math.floor(getWidth(caster)), -Math.floor(getWidth(caster))),
                blockTarget.relative(caster.getDirection(), getDistance(caster)).offset(
                        Math.floor(getWidth(caster)),
                        Math.floor(getWidth(caster)),
                        Math.floor(getWidth(caster)))).forEach(blockPos -> {
            Block blockToReplace = world.getBlockState(blockTarget).getBlock();
            if (blockToReplace != Blocks.AIR && blockToReplace != Blocks.CAVE_AIR) {
                BlockState vaccumBlock = ArcanaBlocks.VACUUM_BLOCK.get().defaultBlockState();
                world.setBlockAndUpdate(blockTarget, vaccumBlock);
                ((VacuumBlockEntity)world.getBlockEntity(blockTarget)).setDuration(getDuration(caster));
                ((VacuumBlockEntity)world.getBlockEntity(blockTarget)).setOriginBlock(blockToReplace.defaultBlockState());
            }
        });
        return InteractionResult.SUCCESS;
    }

    protected int getWidth(Player playerEntity) {
        return SpellValues.getOrDefault(AspectUtils.getAspect(data,"sinModifier"), 1);
    }

    protected int getDistance(Player playerEntity) {
        return SpellValues.getOrDefault(AspectUtils.getAspect(data,"secondModifier"), 16);
    }

    /**
     * Gets Vacuum blocks duration from modifiers
     * @return Vacuum blocks duration
     */
    protected int getDuration(Player playerEntity) {
        return (1+SpellValues.getOrDefault(AspectUtils.getAspect(data,"firstModifier"), 0))*100;
    }

    @Override
    public InteractionResult useOnPlayer(Player playerTarget) {
        //playerTarget.sendStatusMessage(new TranslationTextComponent("status.arcana.invalid_spell"), true);
        BlockPos pos = playerTarget.getOnPos().below();
        BlockPos.betweenClosed(pos.offset(
                -Math.floor(getWidth(playerTarget)),
                -Math.floor(getWidth(playerTarget)),
                -Math.floor(getWidth(playerTarget))),
                pos.relative(playerTarget.getDirection(),getDistance(playerTarget)).offset(
                        Math.floor(getWidth(playerTarget)),
                        Math.floor(getWidth(playerTarget)),
                        Math.floor(getWidth(playerTarget)))).forEach(blockPos -> {
            Block blockToReplace = playerTarget.level.getBlockState(pos).getBlock();
            if (blockToReplace != Blocks.AIR && blockToReplace != Blocks.CAVE_AIR) {
                BlockState vaccumBlock = ArcanaBlocks.VACUUM_BLOCK.get().defaultBlockState();
                playerTarget.level.setBlockAndUpdate(pos, vaccumBlock);
                ((VacuumBlockEntity)playerTarget.level.getBlockEntity(pos)).setDuration(getDuration(playerTarget));
                ((VacuumBlockEntity)playerTarget.level.getBlockEntity(pos)).setOriginBlock(blockToReplace.defaultBlockState());
            }
        });
        return InteractionResult.FAIL;
    }

    @Override
    public InteractionResult useOnEntity(Player caster, Entity entityTarget) {
        //caster.sendStatusMessage(new TranslationTextComponent("status.arcana.invalid_spell"), true);
        BlockPos pos = entityTarget.getOnPos().below();
        BlockPos.betweenClosed(pos.offset(
                -Math.floor(getWidth(caster)),
                -Math.floor(getWidth(caster)),
                -Math.floor(getWidth(caster))),
                pos.relative(caster.getDirection(),getDistance(caster)).offset(
                        Math.floor(getWidth(caster)),
                        Math.floor(getWidth(caster)),
                        Math.floor(getWidth(caster)))).forEach(blockPos -> {
            Block blockToReplace = entityTarget.level.getBlockState(pos).getBlock();
            if (blockToReplace != Blocks.AIR && blockToReplace != Blocks.CAVE_AIR) {
                BlockState vaccumBlock = ArcanaBlocks.VACUUM_BLOCK.get().defaultBlockState();
                entityTarget.level.setBlockAndUpdate(pos, vaccumBlock);
                ((VacuumBlockEntity)entityTarget.level.getBlockEntity(pos)).setDuration(getDuration(caster));
                ((VacuumBlockEntity)entityTarget.level.getBlockEntity(pos)).setOriginBlock(blockToReplace.defaultBlockState());
            }
        });
        return InteractionResult.FAIL;
    }
}