package net.arcanamod.fluids;

import mcp.MethodsReturnNonnullByDefault;
import net.arcanamod.entities.TaintedGooWrapper;
import net.arcanamod.systems.taint.Taint;
import net.arcanamod.effects.ArcanaEffects;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.block.BlockState;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.potion.EffectInstance;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fluids.ForgeFlowingFluid;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Random;
import java.util.function.Supplier;

@SuppressWarnings("deprecation")
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class TaintFluid extends FlowingFluid {
	public TaintFluid(Supplier<? extends FlowingFluid> supplier, ForgeFlowingFluid.Properties properties) {
		super(supplier, properties);
	}

	@Override
	public void randomTick(BlockState state, ServerLevel world, BlockPos pos, Random random) {
		super.randomTick(state, world, pos, random);
		Taint.tickTaintedBlock(state, world, pos, random);
	}

	@Override
	public boolean ticksRandomly(BlockState state) {
		return true;
	}

	@Override
	public void onEntityCollision(BlockState state, Level world, BlockPos pos, Entity entity) {
		if(entity instanceof LivingEntity) {
			((TaintedGooWrapper) entity).setGooTicks(((TaintedGooWrapper) entity).getGooTicks() + 1);
			if (((TaintedGooWrapper) entity).getGooTicks() > 6) {
				((LivingEntity) entity).addEffect(new MobEffectInstance(ArcanaEffects.TAINTED.get(), 5 * 20));
			}
		}
	}

	@Override
	public Fluid getFlowing() {
		return null;
	}

	@Override
	public Fluid getSource() {
		return null;
	}

	@Override
	protected boolean canConvertToSource() {
		return false;
	}

	@Override
	protected void beforeDestroyingBlock(LevelAccessor pLevel, BlockPos pPos, BlockState pState) {

	}

	@Override
	protected int getSlopeFindDistance(LevelReader pLevel) {
		return 0;
	}

	@Override
	protected int getDropOff(LevelReader pLevel) {
		return 0;
	}

	@Override
	public Item getBucket() {
		return null;
	}

	@Override
	protected boolean canBeReplacedWith(FluidState pFluidState, BlockGetter pBlockReader, BlockPos pPos, Fluid pFluid, Direction pDirection) {
		return false;
	}

	@Override
	public int getTickDelay(LevelReader p_76120_) {
		return 0;
	}

	@Override
	protected float getExplosionResistance() {
		return 0;
	}

	@Override
	protected BlockState createLegacyBlock(FluidState pState) {
		return null;
	}

	@Override
	public boolean isSource(FluidState pState) {
		return false;
	}

	@Override
	public int getAmount(FluidState pState) {
		return 0;
	}
}