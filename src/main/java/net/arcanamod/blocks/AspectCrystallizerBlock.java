package net.arcanamod.blocks;

import net.arcanamod.blocks.tiles.AspectCrystallizerBlockEntity;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@SuppressWarnings("deprecation")
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class AspectCrystallizerBlock extends Block {
	
	public AspectCrystallizerBlock(BlockBehaviour.Properties properties){
		super(properties);
	}
	
	public boolean hasTileEntity(BlockState state){
		return true;
	}
	
	@Nullable
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state){
		return new AspectCrystallizerBlockEntity(pos, state);
	}
	
	@Override
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult rayTraceResult){
		if(world.isClientSide)
			return InteractionResult.SUCCESS;
		BlockEntity te = world.getBlockEntity(pos);
		if(te instanceof AspectCrystallizerBlockEntity){
			NetworkHooks.openGui((ServerPlayer)player, (MenuProvider) te, buf -> buf.writeBlockPos(pos));
			return InteractionResult.SUCCESS;
		}
		return super.use(state, world, pos, player, hand, rayTraceResult);
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