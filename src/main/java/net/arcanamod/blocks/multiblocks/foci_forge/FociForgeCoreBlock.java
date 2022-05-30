package net.arcanamod.blocks.multiblocks.foci_forge;

import net.arcanamod.blocks.ArcanaBlocks;
import net.arcanamod.blocks.bases.GroupedBlock;
import net.arcanamod.blocks.multiblocks.StaticComponent;
import net.arcanamod.blocks.tiles.FociForgeBlockEntity;
import net.arcanamod.items.ArcanaItems;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import static net.arcanamod.blocks.multiblocks.foci_forge.FociForgeComponentBlock.*;

@SuppressWarnings("deprecation")
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class FociForgeCoreBlock extends Block implements StaticComponent, GroupedBlock, EntityBlock {

	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		VoxelShape shape;
		Direction facing = state.getValue(FACING);
		switch(facing) {
			case EAST:
				shape = SHAPE_E;
				break;
			case SOUTH:
				shape = SHAPE_S;
				break;
			case WEST:
				shape = SHAPE_W;
				break;
			case NORTH:
			default:
				shape = SHAPE_N;
				break;
		}
		return shape;
	}

	public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

	public FociForgeCoreBlock(Properties properties) {
		super(properties);
	}

//	@Override
//	public boolean hasBlockEntity(BlockState state) {
//		return true;
//	}

	@Override
	public CreativeModeTab getGroup() {
		return null;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state){
		return new FociForgeBlockEntity(pos, state);
	}

	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.MODEL;
	}

	public boolean isCore(BlockPos pos, BlockState state) {
		return true;
	}

	public BlockPos getCorePos(BlockPos pos, BlockState state) {
		return pos;
	}

	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(FACING);
	}

	@Override
	public void playerWillDestroy(Level world, BlockPos pos, BlockState state, Player player){
		Direction facing = state.getValue(FACING);
		for (FociForgeComponentBlock.Component com : FociForgeComponentBlock.Component.values()) {
			BlockPos offset = pos.offset(com.getOffset(facing));
			if (world.getBlockState(offset).getBlock() == ArcanaBlocks.FOCI_FORGE_COMPONENT.get())
				world.destroyBlock(offset, false);
		}
		super.playerWillDestroy(world, pos, state, player);
	}

	@Override
	public void neighborChanged(BlockState state, Level world, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
		Direction facing = state.getValue(FACING);
		boolean broke = false;
		for (FociForgeComponentBlock.Component com : FociForgeComponentBlock.Component.values()) {
			if (world.getBlockState(pos.offset(com.getOffset(facing))).getBlock() != ArcanaBlocks.FOCI_FORGE_COMPONENT.get()) {
				broke = true;
				break;
			}
		}
		if (broke) {
			for (FociForgeComponentBlock.Component com : FociForgeComponentBlock.Component.values()) {
				world.destroyBlock(pos.offset(com.getOffset(facing)), false);
			}
		}
		super.neighborChanged(state, world, pos, block, fromPos, isMoving);
	}

	public BlockState getStateForPlacement(BlockPlaceContext context) {
		Direction facing = context.getHorizontalDirection().getOpposite();
		if (!context.getLevel().getBlockState(context.getClickedPos()).canBeReplaced(context))
			return null;
		for (FociForgeComponentBlock.Component com : FociForgeComponentBlock.Component.values())
			if (!context.getLevel().getBlockState(context.getClickedPos().offset(com.getOffset(facing))).canBeReplaced(context))
				return null;
		return this.defaultBlockState().setValue(FACING, facing);
	}

	public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		super.setPlacedBy(world, pos, state, placer, stack);
		if (!world.isClientSide) {
			Direction facing = state.getValue(FACING);
			for (FociForgeComponentBlock.Component com : FociForgeComponentBlock.Component.values()) {
				BlockPos comPos = pos.offset(com.getOffset(facing));
				world.setBlockAndUpdate(comPos,
						ArcanaBlocks.FOCI_FORGE_COMPONENT.get().defaultBlockState()
								.setValue(FociForgeComponentBlock.FACING, facing)
								.setValue(FociForgeComponentBlock.COMPONENT, com));
			}
			for (FociForgeComponentBlock.Component com : FociForgeComponentBlock.Component.values()) {
				BlockPos comPos = pos.offset(com.getOffset(facing));
				world.blockUpdated(comPos, Blocks.AIR);
				state.updateNeighbourShapes(world, comPos, 3);
			}
		}
	}

	@Override
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult rayTraceResult) {
		if(world.isClientSide)
			return InteractionResult.SUCCESS;
		BlockEntity te = world.getBlockEntity(pos);
		if (te instanceof FociForgeBlockEntity){
			NetworkHooks.openGui((ServerPlayer)player, (MenuProvider) te, buf -> buf.writeBlockPos(pos));
			return InteractionResult.SUCCESS;
		}
		return super.use(state, world, pos, player, hand, rayTraceResult);
	}

	@Override
	public Item asItem() {
		return ArcanaItems.FOCI_FORGE_ITEM.get();
	}
}
