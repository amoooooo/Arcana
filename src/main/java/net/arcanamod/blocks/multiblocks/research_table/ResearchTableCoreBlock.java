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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import static net.arcanamod.blocks.multiblocks.research_table.ResearchTableComponentBlock.COM_OFFSET;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class ResearchTableCoreBlock extends WaterloggableBlock implements StaticComponent, GroupedBlock, EntityBlock {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty INK = BooleanProperty.create("ink");

    public ResearchTableCoreBlock(Properties properties) {
        super(properties);
    }

//    @Override
//    public boolean hasTileEntity(BlockState state) {
//        return true;
//    }

    @Override
    public CreativeModeTab getGroup() {
        return null;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state){
        return new ResearchTableBlockEntity(pos, state);
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
        builder.add(FACING).add(INK);
    }

    @Override
    public void playerWillDestroy(Level world, BlockPos pos, BlockState state, Player player){
        BlockPos offset = pos.offset(ShapeUtils.fromNorth(COM_OFFSET, state.getValue(FACING)));
        if(world.getBlockState(offset).getBlock() == ArcanaBlocks.RESEARCH_TABLE_COMPONENT.get()) {
            world.destroyBlock(offset, false);
        }
        super.playerWillDestroy(world, pos, state, player);
    }

    public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity tileentity = worldIn.getBlockEntity(pos);
            if (tileentity instanceof ResearchTableBlockEntity) {
                Containers.dropContents(worldIn, pos, (ResearchTableBlockEntity)tileentity);
            }
            super.onRemove(state, worldIn, pos, newState, isMoving);
        }
    }

    @Override
    public void neighborChanged(BlockState state, Level world, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        Direction facing = state.getValue(FACING);
        Vec3i rotated = ShapeUtils.fromNorth(COM_OFFSET, facing);
        if(world.getBlockState(pos.offset(rotated)).getBlock() != ArcanaBlocks.RESEARCH_TABLE_COMPONENT.get())
            world.destroyBlock(pos.offset(rotated), false);
        super.neighborChanged(state, world, pos, block, fromPos, isMoving);
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction facing = context.getHorizontalDirection();
        if (!context.getLevel().getBlockState(context.getClickedPos()).canBeReplaced(context))
            return null;
        if (!context.getLevel().getBlockState(context.getClickedPos().offset(ShapeUtils.fromNorth(COM_OFFSET, facing))).canBeReplaced(context))
            return null;
        return this.defaultBlockState().setValue(FACING, facing).setValue(INK, false);
    }

    public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(world, pos, state, placer, stack);
        if (!world.isClientSide) {
            Direction facing = state.getValue(FACING);
            BlockPos comPos = pos.offset(ShapeUtils.fromNorth(COM_OFFSET, facing));
            world.setBlockAndUpdate(comPos,
                    ArcanaBlocks.RESEARCH_TABLE_COMPONENT.get().defaultBlockState()
                            .setValue(ResearchTableComponentBlock.FACING, facing));
            world.blockUpdated(comPos, Blocks.AIR);
            state.updateNeighbourShapes(world, comPos, 3);
        }
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult rayTraceResult) {
        if(world.isClientSide)
            return InteractionResult.SUCCESS;
        BlockEntity te = world.getBlockEntity(pos);
        if (te instanceof ResearchTableBlockEntity) {
            NetworkHooks.openGui((ServerPlayer) player, (MenuProvider) te, buf -> buf.writeBlockPos(pos));
            return InteractionResult.SUCCESS;
        }
        return super.use(state, world, pos, player, hand, rayTraceResult);
    }

    @Override
    public Item asItem() {
        return ArcanaItems.RESEARCH_TABLE_ITEM.get();
    }
}