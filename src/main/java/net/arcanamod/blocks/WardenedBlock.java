package net.arcanamod.blocks;

import net.arcanamod.blocks.tiles.WardenedBlockBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;

import javax.annotation.Nullable;
import java.util.List;

public class WardenedBlock extends Block implements EntityBlock {
	protected WardenedBlockBlockEntity t;

	public WardenedBlock(Block.Properties properties) {
		super(properties);
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state){
		t = new WardenedBlockBlockEntity(pos, state);
		return t;
	}

//	@Override
//	public boolean hasTileEntity(BlockState state) {
//		return true;
//	}

	@Override
	public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
		if (t != null) return t.getState().orElse(Blocks.AIR.defaultBlockState()).getBlock().getDrops(state, builder); else return super.getDrops(state, builder);
	}

	@Override
	public void destroy(LevelAccessor worldIn, BlockPos pos, BlockState state) {
		super.destroy(worldIn, pos, state);
		//worldIn.setBlockState(pos,t.getState().orElse(Blocks.AIR.getDefaultState()),3);
	}
}
