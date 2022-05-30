package net.arcanamod.blocks.multiblocks.research_table;

import net.arcanamod.blocks.ArcanaBlocks;
import net.arcanamod.blocks.bases.GroupedBlock;
import net.arcanamod.blocks.bases.WaterloggableBlock;
import net.arcanamod.blocks.multiblocks.StaticComponent;
import net.arcanamod.blocks.tiles.ResearchTableBlockEntity;
import net.arcanamod.items.ArcanaItems;
import net.arcanamod.util.ShapeUtils;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class ResearchTableComponentBlock extends WaterloggableBlock implements StaticComponent, GroupedBlock {
	public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
	public static final BooleanProperty PAPER = BooleanProperty.create("paper");
	public static final Vec3i COM_OFFSET = new Vec3i(1, 0, 0);
	public static final Vec3i COM_INVERT = new Vec3i(-1, 0, 0);

	public ResearchTableComponentBlock(Properties properties){
		super(properties);
		this.registerDefaultState(this.defaultBlockState()
				.setValue(FACING, Direction.NORTH)
				.setValue(PAPER, false));
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirror) {
		return state.rotate(mirror.getRotation(state.getValue(FACING)));
	}

	@Override
	public BlockState rotate(BlockState state, Rotation direction) {
		return state.setValue(FACING, direction.rotate(state.getValue(FACING)));
	}

	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.MODEL;
	}

	public boolean isCore(BlockPos pos, BlockState state) {
		return false;
	}

	public BlockPos getCorePos(BlockPos pos, BlockState state) {
		return pos.offset(ShapeUtils.fromNorth(COM_INVERT, state.getValue(FACING)));
	}

	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(FACING).add(PAPER);
	}

	public void playerWillDestroy(Level world, BlockPos pos, BlockState state, Player player) {
		BlockPos corePos = getCorePos(pos, state);
		if (world.getBlockState(corePos).getBlock() == ArcanaBlocks.RESEARCH_TABLE.get()) {
			world.destroyBlock(corePos, false);
		}
		// TODO: loot table that detects harvested by player
		if (!player.isCreative()) {
			dropResources(state, world, pos);
		}
		super.playerWillDestroy(world, pos, state, player);
	}

	public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
		if (state.getBlock() != newState.getBlock()) {
			BlockEntity tileentity = worldIn.getBlockEntity(getCorePos(pos, state));
			if (tileentity instanceof ResearchTableBlockEntity) {
				Containers.dropContents(worldIn, pos, (ResearchTableBlockEntity)tileentity);
			}
			super.onRemove(state, worldIn, pos, newState, isMoving);
		}
	}

	public void neighborChanged(BlockState state, Level world, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
		BlockPos corePos = getCorePos(pos, state);
		if (world.getBlockState(corePos).getBlock() != ArcanaBlocks.RESEARCH_TABLE.get())
			world.destroyBlock(pos, false);
		super.neighborChanged(state, world, pos, block, fromPos, isMoving);
	}

	public CreativeModeTab getGroup() {
		return null;
	}

	@Override
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult rayTraceResult) {
		if(world.isClientSide)
			return InteractionResult.SUCCESS;
		BlockPos corePos = getCorePos(pos, state);
		BlockEntity te = world.getBlockEntity(corePos);
		if (te instanceof ResearchTableBlockEntity) {
			NetworkHooks.openGui((ServerPlayer) player, (MenuProvider) te, buf -> buf.writeBlockPos(corePos));
			return InteractionResult.SUCCESS;
		}
		return super.use(state, world, pos, player, handIn, rayTraceResult);
	}

	@Override
	public Item asItem() {
		return ArcanaItems.RESEARCH_TABLE_ITEM.get();
	}
}