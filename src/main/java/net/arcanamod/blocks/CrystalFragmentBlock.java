package net.arcanamod.blocks;

import net.arcanamod.aspects.Aspect;
import net.arcanamod.blocks.bases.WaterloggableBlock;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class CrystalFragmentBlock extends WaterloggableBlock {
    public static final BooleanProperty UP = PipeBlock.UP;
    public static final BooleanProperty EAST = PipeBlock.EAST;
    public static final BooleanProperty WEST = PipeBlock.WEST;
    public static final BooleanProperty NORTH = PipeBlock.NORTH;
    public static final BooleanProperty SOUTH = PipeBlock.SOUTH;
    public static final BooleanProperty DOWN = PipeBlock.DOWN;
    public static final Map<Direction, BooleanProperty> FACING_TO_PROPERTY_MAP = PipeBlock.PROPERTY_BY_DIRECTION;
    public static final VoxelShape DOWN_AABB = Block.box(0.0D, 15.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    public static final VoxelShape EAST_AABB = Block.box(0.0D, 0.0D, 0.0D, 1.0D, 16.0D, 16.0D);
    public static final VoxelShape WEST_AABB = Block.box(15.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    public static final VoxelShape SOUTH_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 1.0D);
    public static final VoxelShape NORTH_AABB = Block.box(0.0D, 0.0D, 15.0D, 16.0D, 16.0D, 16.0D);
    public static final VoxelShape UP_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D);

    private Aspect aspect;

    public CrystalFragmentBlock(Properties properties, Aspect aspect) {
        super(properties);
        this.aspect = aspect;
        registerDefaultState(this.stateDefinition.any()
                .setValue(WATERLOGGED, Boolean.FALSE)
                .setValue(UP, Boolean.FALSE)
                .setValue(NORTH, Boolean.FALSE)
                .setValue(EAST, Boolean.FALSE)
                .setValue(SOUTH, Boolean.FALSE)
                .setValue(WEST, Boolean.FALSE)
                .setValue(DOWN, Boolean.FALSE));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context){
        BlockState state = super.getStateForPlacement(context);
        for (Direction dir : Direction.values()) {
            state = state.setValue(FACING_TO_PROPERTY_MAP.get(dir), canAttachTo(context.getClickedPos(), context.getLevel(), dir));
        }
        return state;
    }

    private static boolean canAttachTo(BlockPos pos, LevelReader world, Direction dir) {
        BlockPos offset = pos.relative(dir.getOpposite());
        return Block.isFaceFull(world.getBlockState(offset).getCollisionShape(world, offset), dir);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder){
        builder.add(UP, NORTH, EAST, SOUTH, WEST, DOWN, WATERLOGGED);
    }

//    @Override
//    public BlockState updatePostPlacement(BlockState state, Direction facing, BlockState facingState, IWorld world, BlockPos currentPos, BlockPos facingPos){
//        for (Direction dir : Direction.values()) {
//            state = state.with(FACING_TO_PROPERTY_MAP.get(dir), canAttachTo(currentPos, world, dir));
//        }
//
//        return super.updatePostPlacement(state, facing, facingState, world, currentPos, facingPos);
//    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        for (Direction dir : Direction.values()) {
            if (canAttachTo(pos, world, dir)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        VoxelShape voxelshape = Shapes.empty();
        if (state.getValue(UP)) {
            voxelshape = Shapes.or(voxelshape, UP_AABB);
        }
        if (state.getValue(EAST)) {
            voxelshape = Shapes.or(voxelshape, EAST_AABB);
        }
        if (state.getValue(WEST)) {
            voxelshape = Shapes.or(voxelshape, WEST_AABB);
        }
        if (state.getValue(NORTH)) {
            voxelshape = Shapes.or(voxelshape, NORTH_AABB);
        }
        if (state.getValue(SOUTH)) {
            voxelshape = Shapes.or(voxelshape, SOUTH_AABB);
        }
        if (state.getValue(DOWN)) {
            voxelshape = Shapes.or(voxelshape, DOWN_AABB);
        }

        return voxelshape;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    public void neighborChanged(BlockState state, Level world, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        boolean someAttached = false;
        for (Direction dir : Direction.values()) {
            boolean attached = canAttachTo(pos, world, dir);
            state = state.setValue(FACING_TO_PROPERTY_MAP.get(dir), attached);
            if (attached) {
                someAttached = true;
            }
        }
        if (!someAttached) {
            dropResources(state, world, pos);
            world.removeBlock(pos, false);
        } else {
            world.setBlockAndUpdate(pos, state);
            super.neighborChanged(state, world, pos, block, fromPos, isMoving);
        }
    }
}
