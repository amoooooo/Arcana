package net.arcanamod.blocks;

import net.arcanamod.blocks.tiles.CrucibleBlockEntity;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Random;

@SuppressWarnings("deprecation")
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class CrucibleBlock extends Block implements EntityBlock {
	
	public static final VoxelShape INSIDE = box(2.0D, 4.0D, 2.0D, 14.0D, 15.0D, 14.0D);
	protected static final VoxelShape SHAPE = Shapes.join(
			box(0, 0, 0, 16, 15, 16),
			Shapes.or(
					box(0.0D, 0.0D, 3.0D, 16.0D, 3.0D, 13.0D),
					box(3.0D, 0.0D, 0.0D, 13.0D, 3.0D, 16.0D),
					box(2.0D, 0.0D, 2.0D, 14.0D, 3.0D, 14.0D),
					INSIDE),
			BooleanOp.ONLY_FIRST);
	
	public static final BooleanProperty FULL = BooleanProperty.create("full");
	
	public CrucibleBlock(Properties properties){
		super(properties);
		this.registerDefaultState(this.defaultBlockState().setValue(FULL, false));
	}
	
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder){
		builder.add(FULL);
	}
	
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}
	
	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context){
		return INSIDE;
	}
	
	public boolean isPathfindable(BlockState state, BlockGetter worldIn, BlockPos pos, PathComputationType type){
		return false;
	}
	
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult rayTrace){
		ItemStack itemstack = player.getItemInHand(handIn);
		if(itemstack.isEmpty()){
			if(player.isCrouching()){
				if(state.getValue(FULL)){
					if(!world.isClientSide){
						world.setBlock(pos, state.setValue(FULL, false), 2);
						world.playSound(null, pos, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
					}
					((CrucibleBlockEntity)world.getBlockEntity(pos)).empty();
				}
				return InteractionResult.SUCCESS;
			}
			return InteractionResult.PASS;
		}else{
			Item item = itemstack.getItem();
			if(item == Items.WATER_BUCKET){
				if(!state.getValue(FULL) && !world.isClientSide){
					if(!player.isCreative())
						player.setItemInHand(handIn, new ItemStack(Items.BUCKET));
					player.awardStat(Stats.FILL_CAULDRON);
					world.setBlock(pos, state.setValue(FULL, true), 2);
					world.playSound(null, pos, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
				}
				return InteractionResult.SUCCESS;
			}else if(item == Items.BUCKET){
				if(state.getValue(FULL) && !world.isClientSide && ((CrucibleBlockEntity)world.getBlockEntity(pos)).getAspectStackMap().isEmpty()){
					if(!player.isCreative()){
						itemstack.shrink(1);
						if(itemstack.isEmpty())
							player.setItemInHand(handIn, new ItemStack(Items.WATER_BUCKET));
						else if(!player.addItem(new ItemStack(Items.WATER_BUCKET)))
							player.drop(new ItemStack(Items.WATER_BUCKET), false);
					}
					player.awardStat(Stats.USE_CAULDRON);
					world.setBlock(pos, state.setValue(FULL, false), 2);
					world.playSound(null, pos, SoundEvents.BUCKET_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
				}
				return InteractionResult.SUCCESS;
			}
		}
		return super.use(state, world, pos, player, handIn, rayTrace);
	}
	
	public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving){
		BlockEntity entity = world.getBlockEntity(pos);
		if(entity instanceof CrucibleBlockEntity)
			((CrucibleBlockEntity)entity).empty();
		super.onRemove(state, world, pos, newState, isMoving);
	}
	
	public void animateTick(BlockState state, Level world, BlockPos pos, Random rand){
		// if boiling, show bubbles
		if(((CrucibleBlockEntity)world.getBlockEntity(pos)).isBoiling()){
			// we boiling
			double x = pos.getX();
			double y = pos.getY();
			double z = pos.getZ();
			// bubble column particles remove themselves quickly, we might want our own thing
			world.addParticle(ParticleTypes.BUBBLE_COLUMN_UP, x + .125 + rand.nextFloat() * .75f, y + .8125f, z + .125 + rand.nextFloat() * .75f, 0.0D, 0.04D, 0.0D);
			world.addParticle(ParticleTypes.BUBBLE_COLUMN_UP, x + .125 + rand.nextFloat() * .75f, y + .8125f, z + .125 + rand.nextFloat() * .75f, 0.0D, 0.04D, 0.0D);
		}
	}
	
	public void handlePrecipitation(Level world, BlockPos pos) {
		if(world.random.nextInt(20) == 1){
			float f = world.getBiome(pos).value().getBaseTemperature();
			if(!(f < 0.15F)){
				BlockState blockstate = world.getBlockState(pos);
				if(!blockstate.getValue(FULL))
					world.setBlock(pos, blockstate.setValue(FULL, true), 2);
			}
		}
	}
	
//	@Override
//	public boolean hasTileEntity(BlockState state){
//		return true;
//	}
	
	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state){
		return new CrucibleBlockEntity(pos, state);
	}
}