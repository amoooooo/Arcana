package net.arcanamod.blocks;

import net.arcanamod.ArcanaConfig;
import net.arcanamod.aspects.handlers.AspectHolder;
import net.arcanamod.blocks.tiles.AlembicBlockEntity;
import net.arcanamod.world.AuraView;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@SuppressWarnings("deprecation")
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class AlembicBlock extends Block implements EntityBlock {
	
	protected static final VoxelShape SHAPE = Shapes.or(
			box(1, 1, 1, 15, 15, 15),
			box(0, 2, 0, 16, 4, 16),
			box(0, 12, 0, 16, 14, 16),
			box(4, 0, 4, 12, 2, 12),
			box(4, 14, 4, 12, 16, 12)
	).optimize();
	
	public AlembicBlock(Properties properties){
		super(properties);
	}
	
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context){
		return SHAPE;
	}
	
//	@Override
//	public boolean hasBlockEntity(BlockState state){
//		return true;
//	}
	
	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state){
		return new AlembicBlockEntity(pos, state);
	}
	
	public void neighborChanged(BlockState state, Level world, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving){
		super.neighborChanged(state, world, pos, block, fromPos, isMoving);
		if(!world.isClientSide()){
			BlockEntity te = world.getBlockEntity(pos);
			if(te instanceof AlembicBlockEntity){
				AlembicBlockEntity alembic = (AlembicBlockEntity) te;
				alembic.suppressedByRedstone = world.hasNeighborSignal(pos);
				alembic.setChanged();
				world.sendBlockUpdated(pos, state, state, 4);
			}
		}
	}
	
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit){
		BlockEntity te = world.getBlockEntity(pos);
		if(te instanceof AlembicBlockEntity)
			if(player.getItemInHand(hand).isEmpty() && player.isCrouching()){
				AlembicBlockEntity alembic = (AlembicBlockEntity) te;
				// get rid of the content of the alembic
				for(AspectHolder holder : ((AlembicBlockEntity)te).aspects.getHolders()){
					AuraView.getSided(world).addFluxAt(pos, (float)(holder.getStack().getAmount() * ArcanaConfig.ASPECT_DUMPING_WASTE.get()));
					holder.drain(holder.getStack().getAmount(), false);
					// TODO: flux particles
				}
				alembic.setChanged();
				world.sendBlockUpdated(pos, state, state, 4);
			}else{
				if(world.isClientSide())
					return InteractionResult.SUCCESS;
				NetworkHooks.openGui((ServerPlayer)player, (MenuProvider) te, buffer -> buffer.writeBlockPos(pos));
				return InteractionResult.SUCCESS;
			}
		return super.use(state, world, pos, player, hand, hit);
	}
}