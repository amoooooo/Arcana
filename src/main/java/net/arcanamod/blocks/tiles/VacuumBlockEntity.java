package net.arcanamod.blocks.tiles;

import net.arcanamod.blocks.ArcanaBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;

public class VacuumBlockEntity extends BlockEntity {

    int existTime = 0;
    int duration = Short.MAX_VALUE;
    private BlockState originBlock = null;

    public VacuumBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(ArcanaTiles.VACUUM_TE.get(), pWorldPosition, pBlockState);
    }

    public boolean shouldRenderFace(Direction p_228884_15_) {
        BlockState block = level.getBlockState(worldPosition.relative(p_228884_15_,-1));
        return block.getBlock() != Blocks.AIR
                && block.getBlock() != Blocks.CAVE_AIR
                && block.getBlock() != Blocks.VOID_AIR
                && block.getBlock() != ArcanaBlocks.VACUUM_BLOCK.get();
    }

    @Override
    public void load(CompoundTag compoundNBT) {
        super.load(compoundNBT);
        duration = compoundNBT.getInt("Duration");
        existTime = compoundNBT.getInt("ExistTime");
        originBlock = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(compoundNBT.getString("Block"))).defaultBlockState();
    }

    @Override
    public void saveAdditional(CompoundTag compoundNBT) {
        compoundNBT.putInt("Duration",duration);
        compoundNBT.putInt("ExistTime",existTime);
        compoundNBT.putString("Block",originBlock.getBlock().getRegistryName().toString());
        super.saveAdditional(compoundNBT);
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setOriginBlock(BlockState originBlock) {
        this.originBlock = originBlock;
    }

    public void tick() {
        existTime++;
        if (existTime >= duration) {
            if (originBlock != null)
                level.setBlockAndUpdate(worldPosition,originBlock);
            else level.setBlockAndUpdate(worldPosition,Blocks.AIR.defaultBlockState());
        }
    }
}
