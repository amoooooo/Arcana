package net.arcanamod.blocks.multiblocks.foci_forge;

import net.arcanamod.blocks.ArcanaBlocks;
import net.arcanamod.blocks.bases.GroupedBlock;
import net.arcanamod.blocks.multiblocks.IStaticEnum;
import net.arcanamod.blocks.multiblocks.StaticComponent;
import net.arcanamod.blocks.tiles.FociForgeBlockEntity;
import net.arcanamod.items.ArcanaItems;
import net.arcanamod.util.ShapeUtils;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.ParametersAreNonnullByDefault;

@SuppressWarnings("deprecation")
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class FociForgeComponentBlock extends Block implements StaticComponent, GroupedBlock {

	public static final VoxelShape SHAPE_N = Shapes.or(
			Shapes.create(0, 0, 0, 32 / 16f, 4 / 16f, 32 / 16f),
			Shapes.create(0, 4 / 16f, 17 / 16f, 32 / 16f, 16 / 16f, 31 / 16f),
			Shapes.create(26 / 16f, 4 / 16f, 1 / 16f, 32 / 16f, 27 / 16f, 31 / 16f),
			Shapes.create(18 / 16f, 16 / 16f, 19 / 16f, 26 / 16f, 30 / 16f, 28 / 16f)
	).optimize();
	public static final VoxelShape SHAPE_E = ShapeUtils.rotate(SHAPE_N, Direction.EAST);
	public static final VoxelShape SHAPE_S = ShapeUtils.rotate(SHAPE_N, Direction.SOUTH);
	public static final VoxelShape SHAPE_W = ShapeUtils.rotate(SHAPE_N, Direction.WEST);

	public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
	public static final EnumProperty<Component> COMPONENT = EnumProperty.create("ff_com", Component.class);
	
	public FociForgeComponentBlock(BlockBehaviour.Properties properties){
		super(properties);
		this.registerDefaultState(this.getStateDefinition().any()
				.setValue(FACING, Direction.NORTH)
				.setValue(COMPONENT, Component.F));
	}

	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		VoxelShape shape;
		Direction facing = state.getValue(FACING);
		Vec3i fromCore = state.getValue(COMPONENT).getInvert(facing);
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
		return shape.move(fromCore.getX(), fromCore.getY(), fromCore.getZ());
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
		return RenderShape.INVISIBLE;
	}

	public boolean isCore(BlockPos pos, BlockState state) {
		return false;
	}

	public BlockPos getCorePos(BlockPos pos, BlockState state) {
		return pos.offset(state.getValue(COMPONENT).getInvert(state.getValue(FACING)));
	}

	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder){
		super.createBlockStateDefinition(builder);
		builder.add(FACING, COMPONENT);
	}

	public void playerWillDestroy(Level world, BlockPos pos, BlockState state, Player player){
		BlockPos corePos = getCorePos(pos, state);
		if(world.getBlockState(corePos).getBlock() == ArcanaBlocks.FOCI_FORGE.get())
			world.destroyBlock(corePos, false);
		// Components don't naturally spawn drops, for some reason
		if (!player.isCreative())
			dropResources(state, world, pos);
		super.playerWillDestroy(world, pos, state, player);
	}

	public void neighborChanged(BlockState state, Level world, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
		BlockPos corePos = getCorePos(pos, state);
		if (world.getBlockState(corePos).getBlock() != ArcanaBlocks.FOCI_FORGE.get())
			world.destroyBlock(pos, false);
		super.neighborChanged(state, world, pos, block, fromPos, isMoving);
	}

	public boolean isNormalCube(BlockState state, BlockGetter world, BlockPos pos) {
		return false;
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
		if (te instanceof FociForgeBlockEntity) {
			NetworkHooks.openGui((ServerPlayer) player, (MenuProvider) te, buf -> buf.writeBlockPos(corePos));
			return InteractionResult.SUCCESS;
		}
		return super.use(state, world, pos, player, handIn, rayTraceResult);
	}

	public enum Component implements IStaticEnum {
		U("u", 0, 1, 0),
		UR("ur", 1, 1, 0),
		R("r", 1, 0, 0),
		F("f",0, 0, 1),
		FU("fu",0, 1, 1),
		FUR("fur", 1, 1, 1),
		FR("fr", 1, 0, 1);

		private final String name;
		private final Vec3i offset;
		private final Vec3i invert;

		Component(String name, int x, int y, int z) {
			this.name = name;
			this.offset = new Vec3i(x, y, z);
			this.invert = new Vec3i(-x, -y, -z);
		}

		public String getName() {
			return name;
		}

		public Vec3i getOffset(Direction direction) {
			return ShapeUtils.fromNorth(this.offset, direction);
		}

		public Vec3i getInvert(Direction direction) {
			return ShapeUtils.fromNorth(this.invert, direction);
		}
		
		public String getSerializedName(){
			return name;
		}
	}

	@Override
	public Item asItem() {
		return ArcanaItems.FOCI_FORGE_ITEM.get();
	}
}