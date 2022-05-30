package net.arcanamod.blocks;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Random;

@SuppressWarnings("deprecation")
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class NitorBlock extends Block {
	protected static final VoxelShape SHAPE = Block.box(6, 6, 6, 10, 10, 10);
	public static final IntegerProperty COLOUR = IntegerProperty.create("colour", 0, 15);
	
	public NitorBlock(Properties properties){
		super(properties);
		this.registerDefaultState(this.getStateDefinition().any().setValue(COLOUR, 1));
	}
	
	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context){
		return SHAPE;
	}
	
	public VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context){
		return Shapes.empty();
	}
	
	public VoxelShape getRenderShape(BlockState state, BlockGetter world, BlockPos pos){
		return Shapes.empty();
	}

	public void animateTick(BlockState state, Level world, BlockPos pos, Random rand){
		// add a bunch of fire
		double x = pos.getX() + .5;
		double y = pos.getY() + .5;
		double z = pos.getZ() + .5;
		for(int i = 0; i < 3; i++){
			double vX = rand.nextGaussian() / 12;
			double vY = rand.nextGaussian() / 12;
			double vZ = rand.nextGaussian() / 12;
			world.addParticle(ParticleTypes.FLAME, x + vX, y + vY, z + vZ, vX / 16, vY / 16, vZ / 16);
		}
	}

	private SimpleParticleType getColor(BlockState state) {
		return switch (state.getValue(COLOUR)) {
			case 0 -> ParticleTypes.SPIT;
			case 1 -> ParticleTypes.FLAME;
			case 2 -> ParticleTypes.PORTAL;
			case 3 -> ParticleTypes.BUBBLE;
			case 4 -> ParticleTypes.LANDING_HONEY;
			case 5 -> ParticleTypes.SNEEZE;
			case 6 -> ParticleTypes.HEART;
			case 7 -> ParticleTypes.SMOKE;
			case 8 -> ParticleTypes.CAMPFIRE_COSY_SMOKE;
			case 9 -> ParticleTypes.SPLASH;
			case 10 -> ParticleTypes.DRAGON_BREATH;
			case 11 -> ParticleTypes.NAUTILUS;
			case 12 -> ParticleTypes.MYCELIUM;
			case 13 -> ParticleTypes.ITEM_SLIME;
			case 14 -> ParticleTypes.LAVA;
			case 15 -> ParticleTypes.SQUID_INK;
			default -> ParticleTypes.CRIT;
		};
	}

	@Override
	public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
		Item item = player.getItemInHand(handIn).getItem();
		if (player.getItemInHand(handIn).getItem() instanceof DyeItem) {
			worldIn.setBlockAndUpdate(pos, state.setValue(COLOUR, ((DyeItem) item).getDyeColor().getId()));
		}
		return super.use(state, worldIn, pos, player, handIn, hit);
	}

	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(COLOUR);
	}
}