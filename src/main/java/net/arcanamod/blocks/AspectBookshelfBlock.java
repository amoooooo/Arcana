package net.arcanamod.blocks;

import net.arcanamod.blocks.bases.WaterloggableBlock;
import net.arcanamod.blocks.tiles.AspectBookshelfBlockEntity;
import net.arcanamod.items.PhialItem;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import static net.arcanamod.ArcanaSounds.playPhialshelfSlideSound;

@SuppressWarnings("deprecation")
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class AspectBookshelfBlock extends WaterloggableBlock implements EntityBlock {
	public static final DirectionProperty HORIZONTAL_FACING = BlockStateProperties.FACING;
	public static final BooleanProperty FULL_SIZE = BlockStateProperties.EXTENDED;
	public VoxelShape SHAPE_NORTH = Block.box(0, 0, 8, 16, 16, 16);
	public VoxelShape SHAPE_SOUTH = Block.box(0, 0, 0, 16, 16, 8);
	public VoxelShape SHAPE_EAST = Block.box(0, 0, 0, 8, 16, 16);
	public VoxelShape SHAPE_WEST = Block.box(8, 0, 0, 16, 16, 16);
	public VoxelShape SHAPE_UP = Block.box(0, 8, 0, 16, 16, 16);
	public VoxelShape SHAPE_DOWN = Block.box(0, 0, 0, 16, 8, 16);

	public AspectBookshelfBlock(boolean fullBlock, Properties properties){
		super(properties);
		this.registerDefaultState(this.stateDefinition.any()
			.setValue(HORIZONTAL_FACING, Direction.NORTH)
			.setValue(WATERLOGGED, Boolean.FALSE)
			.setValue(FULL_SIZE, fullBlock));
	}

	public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
		if (state.getBlock() != newState.getBlock()) {
			BlockEntity tileentity = worldIn.getBlockEntity(pos);
			if (tileentity instanceof AspectBookshelfBlockEntity) {
				Containers.dropContents(worldIn, pos, (AspectBookshelfBlockEntity)tileentity);
			}
			super.onRemove(state, worldIn, pos, newState, isMoving);
		}
	}

	public boolean triggerEvent(BlockState state, Level worldIn, BlockPos pos, int id, int param) {
		super.triggerEvent(state, worldIn, pos, id, param);
		BlockEntity tileentity = worldIn.getBlockEntity(pos);
		return tileentity != null && tileentity.triggerEvent(id, param);
	}

	@Nullable
	public MenuProvider getMenuProvider(BlockState state, Level worldIn, BlockPos pos) {
		BlockEntity tileentity = worldIn.getBlockEntity(pos);
		return tileentity instanceof MenuProvider ? (MenuProvider) tileentity : null;
	}

	public BlockState getStateForPlacement(BlockPlaceContext context){
		if (context.getPlayer() != null) {
			if (context.getPlayer().isCrouching() && context.getClickedFace().getOpposite() != Direction.UP) {
				return super.getStateForPlacement(context).setValue(HORIZONTAL_FACING, context.getClickedFace().getOpposite());
			}
		}
		return super.getStateForPlacement(context).setValue(HORIZONTAL_FACING, context.getHorizontalDirection().getOpposite());
	}

	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(HORIZONTAL_FACING, WATERLOGGED, FULL_SIZE);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		if (state.getValue(FULL_SIZE)) {
			return super.getShape(state, worldIn, pos, context);
		} else {
			switch (state.getValue(HORIZONTAL_FACING)) {
				case SOUTH:
					return SHAPE_SOUTH;
				case EAST:
					return SHAPE_EAST;
				case WEST:
					return SHAPE_WEST;
				case UP:
					return SHAPE_UP;
				case DOWN:
					return SHAPE_DOWN;
				default:
					return SHAPE_NORTH;
			}
		}
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context){
		return getShape(state, world, pos, context);
	}

	public BlockState rotate(BlockState state, Rotation rot){
		return state.setValue(HORIZONTAL_FACING, rot.rotate(state.getValue(HORIZONTAL_FACING)));
	}

	public BlockState mirror(BlockState state, Mirror mirrorIn){
		return state.rotate(mirrorIn.getRotation(state.getValue(HORIZONTAL_FACING)));
	}

	@Nullable @Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state){
		return new AspectBookshelfBlockEntity(state.getValue(HORIZONTAL_FACING), pos, state);
	}

//	@Override
//	public boolean hasTileEntity(BlockState state) {
//		return true;
//	}

	@Override
	public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult p_225533_6_) {
		BlockEntity te = worldIn.getBlockEntity(pos);
		boolean vert = p_225533_6_.getDirection() == Direction.UP || p_225533_6_.getDirection() == Direction.DOWN;
		if ((vert && p_225533_6_.getDirection() == state.getValue(HORIZONTAL_FACING).getOpposite()) || (!vert && p_225533_6_.getDirection() == state.getValue(HORIZONTAL_FACING))) {
			int widthSlot = -1;
			int heightSlot = -1;
			switch (state.getValue(HORIZONTAL_FACING)) {
				case NORTH:
					widthSlot = 3 - (int) ((p_225533_6_.getLocation().x - pos.getX()) / .33);
					heightSlot = 3 - (int) ((p_225533_6_.getLocation().y - pos.getY()) / .33);
					break;
				case SOUTH:
					widthSlot = 1 + (int) ((p_225533_6_.getLocation().x - pos.getX()) / .33);
					heightSlot = 3 - (int) ((p_225533_6_.getLocation().y - pos.getY()) / .33);
					break;
				case EAST:
					widthSlot = 3 - (int) ((p_225533_6_.getLocation().z - pos.getZ()) / .33);
					heightSlot = 3 - (int) ((p_225533_6_.getLocation().y - pos.getY()) / .33);
					break;
				case WEST:
					widthSlot = 1 + (int) ((p_225533_6_.getLocation().z - pos.getZ()) / .33);
					heightSlot = 3 - (int) ((p_225533_6_.getLocation().y - pos.getY()) / .33);
					break;
				case UP:
					widthSlot = 3 - (int) ((p_225533_6_.getLocation().x - pos.getX()) / .33);
					heightSlot = 1 + (int) ((p_225533_6_.getLocation().z - pos.getZ()) / .33);
					break;
				case DOWN:
					widthSlot = 3 - (int) ((p_225533_6_.getLocation().x - pos.getX()) / .33);
					heightSlot = 3 - (int) ((p_225533_6_.getLocation().z - pos.getZ()) / .33);
					break;
			}
			if (heightSlot <= 0) {
				heightSlot = 1;
			} else if (heightSlot >= 4) {
				heightSlot = 3;
			}
			if (widthSlot <= 0) {
				widthSlot = 1;
			} else if (widthSlot >= 4) {
				widthSlot = 3;
			}
			int slot = (widthSlot + ((heightSlot - 1) * 3)) - 1;

			if (te instanceof AspectBookshelfBlockEntity) {
				AspectBookshelfBlockEntity abe = (AspectBookshelfBlockEntity) te;
				if (player.isCrouching()) {
					player.openMenu(abe);
				} else if (player.getItemInHand(handIn).getItem() instanceof PhialItem && abe.addPhial(player.getItemInHand(handIn), slot)) {
					player.getItemInHand(handIn).shrink(1);
					playPhialshelfSlideSound(player);
				} else {
					ItemStack returned = abe.removePhial(slot);
					if (returned != ItemStack.EMPTY) {
						if (!player.addItem(returned)) {
							ItemEntity itementity = new ItemEntity(worldIn,
									player.getX(),
									player.getY(),
									player.getZ(), returned);
							itementity.setNoPickUpDelay();
							worldIn.addFreshEntity(itementity);
							playPhialshelfSlideSound(player);
						}
					} else {
						return InteractionResult.PASS;
					}
				}
				return InteractionResult.SUCCESS;
			}
		}
		return InteractionResult.PASS;
	}

	public boolean hasAnalogOutputSignal(BlockState state){
		return true;
	}

	public int getAnalogOutputSignal(BlockState block, Level world, BlockPos pos){
		BlockEntity te = world.getBlockEntity(pos);
		assert te != null;
		return ((AspectBookshelfBlockEntity)te).getRedstoneOut();
	}
}