package net.arcanamod.blocks.tainted;

import net.arcanamod.Arcana;
import net.arcanamod.blocks.bases.GroupedBlock;
import net.arcanamod.capabilities.TaintTrackable;
import net.arcanamod.systems.taint.Taint;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Random;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class TaintedStairsBlock extends StairBlock implements GroupedBlock{
	public static final BooleanProperty UNTAINTED = Taint.UNTAINTED;
	
	public TaintedStairsBlock(Block parent){
		super(parent::defaultBlockState, Properties.copy(parent));
		Taint.addTaintMapping(parent, this);
	}
	
	public boolean isRandomlyTicking(BlockState state){
		return true;
	}
	
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder){
		super.createBlockStateDefinition(builder);
		builder.add(UNTAINTED);
	}
	
	public BlockState getStateForPlacement(BlockPlaceContext context){
		BlockState placement = super.getStateForPlacement(context);
		return placement != null ? placement.setValue(UNTAINTED, true) : null;
	}
	
	public void tick(BlockState state, ServerLevel world, BlockPos pos, Random random){
		super.tick(state, world, pos, random);
		Taint.tickTaintedBlock(state, world, pos, random);
	}
	
	@Nullable
	@Override
	public CreativeModeTab getGroup(){
		return Arcana.TAINT;
	}
	
	@SuppressWarnings("deprecation")
	public void entityInside(BlockState state, Level world, BlockPos pos, Entity entity){
		super.entityInside(state, world, pos, entity);
		startTracking(entity);
	}
	
	public void stepOn(Level world, BlockPos pos, BlockState state, Entity entity){
		super.stepOn(world, pos, state, entity);
		startTracking(entity);
	}
	
	private void startTracking(Entity entity){
		if(entity instanceof LivingEntity){
			// Start tracking taint biome for entity
			TaintTrackable trackable = TaintTrackable.getFrom((LivingEntity)entity);
			if(trackable != null)
				trackable.setTracking(true);
		}
	}
}
