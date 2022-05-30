package net.arcanamod.blocks;

import net.arcanamod.aspects.Aspect;
import net.arcanamod.aspects.handlers.AspectHolder;
import net.arcanamod.blocks.bases.WaterloggableBlock;
import net.arcanamod.items.ArcanaItems;
import net.arcanamod.world.AuraView;
import net.arcanamod.world.Node;
import net.arcanamod.world.ServerAuraView;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collection;
import java.util.Random;

@SuppressWarnings("deprecation")
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class CrystalClusterBlock extends WaterloggableBlock{
	
	public static final IntegerProperty AGE = BlockStateProperties.AGE_3;
	public static final DirectionProperty FACING = BlockStateProperties.FACING;
	private Aspect aspect;
	
	public CrystalClusterBlock(Block.Properties properties, Aspect aspect){
		super(properties);
		this.aspect = aspect;
		registerDefaultState(this.stateDefinition.any().setValue(WATERLOGGED, Boolean.FALSE).setValue(AGE, 3).setValue(FACING, Direction.UP));
	}

	/*
	@Nonnull
	public BlockState getStateForPlacement(BlockPlaceContext context){
		// Placement = the block item, which you get with silk touch, so fully grown
		return super.getStateForPlacement(context).setValue(AGE, 3).setValue(FACING, context.getFace());
	}
	 */

	@Override
	public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
		ItemStack stack = player.getItemInHand(handIn);
		if (stack.getItem() == Items.WRITABLE_BOOK){
			stack.setCount(stack.getCount()-1);
			player.addItem(new ItemStack(ArcanaItems.ARCANUM.get()));
		}
		return super.use(state, worldIn, pos, player, handIn, hit);
	}

	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder){
		builder.add(FACING, WATERLOGGED, AGE);
	}
	
	public boolean hasAnalogOutputSignal(BlockState state){
		return true;
	}
	
	public int getAnalogOutputSignal(BlockState state, Level world, BlockPos pos){
		// output comparator signal when fully grown
		return state.getValue(AGE) == 3 ? 15 : 0;
	}
	
//	public BlockState updatePostPlacement(BlockState state, Direction facing, BlockState facingState, IWorld world, BlockPos currentPos, BlockPos facingPos){
//		return facing == state.getValue(FACING).getOpposite() && !this.isValidPosition(state, world, currentPos) ? Blocks.AIR.getDefaultState() : super.updatePostPlacement(state, facing, facingState, world, currentPos, facingPos);
//	}
	
	public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos){
		Direction dir = state.getValue(FACING);
		return state.isFaceSturdy(world, pos.relative(dir.getOpposite()), dir);
	}
	
	public ItemStack getPickBlock(BlockState state, BlockHitResult target, BlockGetter world, BlockPos pos, Player player){
		// They're not ItemBlocks
		return new ItemStack(ForgeRegistries.ITEMS.getValue(getRegistryName()));
	}
	
	public void randomTick(BlockState state, ServerLevel world, BlockPos pos, Random random){
		super.randomTick(state, world, pos, random);
		// If we're not fully grown,
		if(state.getValue(AGE) != 3){
			// Check for any nodes in a 9x9x9 area
			ServerAuraView view = (ServerAuraView)AuraView.SIDED_FACTORY.apply(world);
			Collection<Node> nodes = view.getNodesWithinAABB(new AABB(pos.below(4).south(4).west(4), pos.above(4).north(4).east(4)));
			// For each node in range,
			for(Node node : nodes){
				// If it has more than 4 of our aspect,
				AspectHolder holder = node.getAspects().findFirstHolderContaining(aspect);
				if(holder != null && holder.getStack().getAmount() > 4){
					// Take 2-4 of the aspect,
					holder.drain(world.random.nextInt(3) + 2, false);
					// Sync the node,
					view.sendChunkToClients(node);
					// Increment out growth stage,
					world.setBlockAndUpdate(pos, state.setValue(AGE, state.getValue(AGE) + 1));
					// And stop
					break;
				}
			}
		}
	}
}