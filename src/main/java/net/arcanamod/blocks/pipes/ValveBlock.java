package net.arcanamod.blocks.pipes;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Random;

@SuppressWarnings("deprecation")
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ValveBlock extends TubeBlock{
	
	public ValveBlock(BlockBehaviour.Properties properties){
		super(properties);
	}
	
	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state){
		return new ValveBlockEntity(pos, state);
	}
	
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult raytrace){
		BlockEntity te = world.getBlockEntity(pos);
		if(te instanceof ValveBlockEntity){
			ValveBlockEntity valve = (ValveBlockEntity) te;
			valve.setEnabledAndNotify(!valve.enabledByHand());
			return InteractionResult.SUCCESS;
		}
		return super.use(state, world, pos, player, hand, raytrace);
	}
	
	public void neighborChanged(BlockState state, Level world, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving){
		super.neighborChanged(state, world, pos, block, fromPos, isMoving);
		if(!world.isClientSide()){
			BlockEntity te = world.getBlockEntity(pos);
			if(te instanceof ValveBlockEntity){
				ValveBlockEntity valve = (ValveBlockEntity) te;
				valve.setSuppressedByRedstone(world.hasNeighborSignal(pos));
				valve.setChanged();
				world.sendBlockUpdated(pos, state, state, 4);
			}
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState stateIn, Level world, BlockPos pos, Random rand){
		BlockEntity te = world.getBlockEntity(pos);
		if(te instanceof ValveBlockEntity && ((ValveBlockEntity)te).isSuppressedByRedstone() && rand.nextFloat() < 0.25F)
			addParticles(world, pos);
	}
	
	private static void addParticles(LevelAccessor world, BlockPos pos){
		double x = (double)pos.getX() + .5;
		double y = (double)pos.getY() + 1;
		double z = (double)pos.getZ() + .5;
		world.addParticle(DustParticleOptions.REDSTONE, x, y, z, 0.0D, 0.0D, 0.0D);
	}
	
	public boolean canConnectRedstone(BlockState state, BlockGetter world, BlockPos pos, @Nullable Direction side){
		return true;
	}
}