package net.arcanamod.blocks;

import net.arcanamod.blocks.bases.WaterloggableBlock;
import net.arcanamod.blocks.tiles.ArcaneCraftingTableBlockEntity;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@SuppressWarnings("deprecation")
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ArcaneCraftingTableBlock extends WaterloggableBlock implements EntityBlock {
	public ArcaneCraftingTableBlock(Properties properties) {
		super(properties);
	}

//	@Override
//	public boolean hasTileEntity(BlockState state) {
//		return true;
//	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state){
		return new ArcaneCraftingTableBlockEntity(pos, state);
	}

	@Override
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult rayTraceResult) {
		if(world.isClientSide)
			return InteractionResult.SUCCESS;
		BlockEntity te = world.getBlockEntity(pos);
		if(te instanceof ArcaneCraftingTableBlockEntity){
			NetworkHooks.openGui((ServerPlayer) player, (MenuProvider) te, buf -> buf.writeBlockPos(pos));
			return InteractionResult.SUCCESS;
		}
		return super.use(state, world, pos, player, handIn, rayTraceResult);
	}
	
	public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean moving){
		if(state.getBlock() != newState.getBlock()){
			BlockEntity tileentity = world.getBlockEntity(pos);
			if(tileentity instanceof Container){
				Containers.dropContents(world, pos, (Container) tileentity);
				world.updateNeighbourForOutputSignal(pos, this);
			}
			
			super.onRemove(state, world, pos, newState, moving);
		}
	}
}