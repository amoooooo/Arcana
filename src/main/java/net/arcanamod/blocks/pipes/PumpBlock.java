package net.arcanamod.blocks.pipes;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class PumpBlock extends TubeBlock{
	
	public static final DirectionProperty FACING = BlockStateProperties.FACING;
	
	public PumpBlock(Properties properties){
		super(properties);
		this.registerDefaultState(this.stateDefinition.any()
				.setValue(NORTH, Boolean.FALSE)
				.setValue(EAST, Boolean.FALSE)
				.setValue(SOUTH, Boolean.FALSE)
				.setValue(WEST, Boolean.FALSE)
				.setValue(UP, Boolean.FALSE)
				.setValue(DOWN, Boolean.FALSE)
				.setValue(FACING, Direction.UP));
	}
	
	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state){
		return new PumpBlockEntity(state.getValue(FACING), pos, state);
	}
	
	public BlockState getStateForPlacement(BlockPlaceContext context){
		return super.getStateForPlacement(context).setValue(FACING, context.getClickedFace());
	}
	
	protected void fillStateContainer(StateDefinition.Builder<Block, BlockState> builder){
		builder.add(NORTH, EAST, SOUTH, WEST, UP, DOWN, FACING);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult rayTraceResult){
		if(world.isClientSide)
			return InteractionResult.SUCCESS;
		BlockEntity te = world.getBlockEntity(pos);
		if(te instanceof PumpBlockEntity){
			NetworkHooks.openGui((ServerPlayer) player, (MenuProvider) te, buf -> buf.writeBlockPos(pos));
			return InteractionResult.SUCCESS;
		}
		return super.use(state, world, pos, player, hand, rayTraceResult);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void neighborChanged(BlockState state, Level world, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving){
		super.neighborChanged(state, world, pos, block, fromPos, isMoving);
		if(!world.isClientSide()){
			BlockEntity te = world.getBlockEntity(pos);
			if(te instanceof PumpBlockEntity){
				PumpBlockEntity alembic = (PumpBlockEntity) te;
				alembic.suppressedByRedstone = world.hasNeighborSignal(pos);
				alembic.setChanged();
				world.sendBlockUpdated(pos, state, state, 4);
			}
		}
	}
}