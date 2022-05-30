package net.arcanamod.blocks.multiblocks.taint_scrubber;

import net.arcanamod.aspects.VisShareable;
import net.arcanamod.aspects.handlers.AspectBattery;
import net.arcanamod.aspects.handlers.AspectHandler;
import net.arcanamod.blocks.tiles.TaintScrubberBlockEntity;
import net.arcanamod.systems.taint.Taint;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static net.arcanamod.blocks.DelegatingBlock.switchBlock;

public class TaintScrubberBlock extends Block implements ITaintScrubberExtension, EntityBlock {
	
	public static final BooleanProperty SUPPORTED = BooleanProperty.create("supported"); // false by default
	
	public TaintScrubberBlock(Properties properties){
		super(properties);
	}
	
	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state){
		return new TaintScrubberBlockEntity(pos, state);
	}

//	@Override
//	public boolean hasTileEntity(BlockState state){
//		return true;
//	}
	
	public BlockState getStateForPlacement(@Nonnull BlockPlaceContext context){
		BlockState state = super.getStateForPlacement(context);
		return state != null ? state.setValue(SUPPORTED, false) : null;
	}
	
	protected void createBlockStateDefinition(@Nonnull StateDefinition.Builder<Block, BlockState> builder){
		super.createBlockStateDefinition(builder);
		builder.add(SUPPORTED);
	}
	
	@Override
	public boolean isValidConnection(Level world, BlockPos pos){
		return true;
	}
	
	@Override
	public void sendUpdate(Level world, BlockPos pos){}
	
	@Override
	public void run(Level world, BlockPos pos, CompoundTag compound){
		// TODO: don't use NBT for this, just have methods for getting range and speed from extensions directly
		// pick the highest range, and add speeds.
		int rh = compound.getInt("h_range");
		int rv = compound.getInt("v_range");
		for(int i = 0; i < compound.getInt("speed") + (rv / 32) + 1; i++){
			// Pick a block within a rh x rh x rv area.
			// If this block is air, stop. If this block doesn't have a tainted form, re-roll.
			// Do this up to 8 times.
			Block dead = null;
			BlockPos taintingPos = pos;
			int iter = 0;
			while(dead == null && iter < 8){
				// TODO: don't try to pick blocks below or above the height limit, for the Bore's sake.
				// TODO: separate up/down ranges would also be useful, also for the bore, so its not terrible on the surface.
				taintingPos = pos.north(RANDOM.nextInt(rh + 1) - (rh / 2)).west(RANDOM.nextInt(rh + 1) - (rh / 2)).above(RANDOM.nextInt(rv + 1) - (rv / 2));
				dead = world.getBlockState(taintingPos).getBlock();
				if(dead.defaultBlockState().isAir()){
					dead = null;
					break;
				}
				// Drain open/unsealed jars
				// TODO: replace with essentia input.
				if(dead instanceof EntityBlock){
					BlockEntity te = world.getBlockEntity(taintingPos);
					if(te instanceof VisShareable){
						VisShareable shareable = ((VisShareable)te);
						if(shareable.isVisShareable() && !shareable.isSecure()){
							AspectBattery vis = (AspectBattery)AspectHandler.getFrom(te);
							if(vis != null){
								if(vis.countHolders() != 0)
									vis.getHolder(RANDOM.nextInt(vis.countHolders())).drain(8, false);
							}
						}
						break;
					}
				}
				dead = Taint.getDeadOfBlock(Taint.getPureOfBlock(dead));
				// todo: what the heck?
				if(compound.getBoolean("silk_touch"))
					dead = Taint.getPureOfBlock(dead);
				iter++;
			}
			// Replace it with its dead form if found.
			if(dead != null && !world.isClientSide()){
				BlockState deadState = switchBlock(world.getBlockState(taintingPos), dead);
				world.setBlockAndUpdate(taintingPos, deadState);
				if(dead.defaultBlockState().isAir()){
					int rnd = RANDOM.nextInt(9) + 4;
					for(int j = 0; j < rnd; j++){
						world.addParticle(
								new BlockParticleOption(ParticleTypes.FALLING_DUST, Blocks.BLACK_CONCRETE_POWDER.defaultBlockState()),
								taintingPos.getX() + 0.5f + ((RANDOM.nextInt(9) - 4) / 10f), taintingPos.getY() + 0.5f + ((RANDOM.nextInt(9) - 4) / 10f), taintingPos.getZ() + 0.5f + ((RANDOM.nextInt(9) - 4) / 10f),
								0.1f, 0.1f, 0.1f
						); // Ash Particle if block is destroyed
					}
				}
			}
		}
	}
	
	@Override
	public CompoundTag getShareableData(CompoundTag compound){
		return compound;
	}
}