package net.arcanamod.worldgen;

import com.mojang.serialization.Codec;
import net.arcanamod.ArcanaConfig;
import net.arcanamod.aspects.Aspect;
import net.arcanamod.aspects.Aspects;
import net.arcanamod.aspects.handlers.AspectHandler;
import net.arcanamod.blocks.ArcanaBlocks;
import net.arcanamod.blocks.CrystalClusterBlock;
import net.arcanamod.capabilities.AuraChunk;
import net.arcanamod.event.WorldTickHandler;
import net.arcanamod.world.Node;
import net.arcanamod.world.NodeType;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;
import static net.arcanamod.world.NodeType.DEFAULT;
import static net.arcanamod.world.NodeType.SPECIAL_TYPES;

@SuppressWarnings("deprecation")
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class NodeFeature extends Feature<NoneFeatureConfiguration>{
	
	private static final Map<Aspect, Supplier<Block>> CRYSTAL_CLUSTERS_FROM_ASPECTS = new HashMap<>();
	static{
		CRYSTAL_CLUSTERS_FROM_ASPECTS.put(Aspects.AIR, ArcanaBlocks.AIR_CLUSTER);
		CRYSTAL_CLUSTERS_FROM_ASPECTS.put(Aspects.EARTH, ArcanaBlocks.EARTH_CLUSTER);
		CRYSTAL_CLUSTERS_FROM_ASPECTS.put(Aspects.FIRE, ArcanaBlocks.FIRE_CLUSTER);
		CRYSTAL_CLUSTERS_FROM_ASPECTS.put(Aspects.WATER, ArcanaBlocks.WATER_CLUSTER);
		CRYSTAL_CLUSTERS_FROM_ASPECTS.put(Aspects.ORDER, ArcanaBlocks.ORDER_CLUSTER);
		CRYSTAL_CLUSTERS_FROM_ASPECTS.put(Aspects.CHAOS, ArcanaBlocks.CHAOS_CLUSTER);
		
	}

	public NodeFeature(Codec<NoneFeatureConfiguration> codec){
		super(codec);
	}

	@Override
	public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> pContext){
//	public boolean place(WorldGenLevel reader, ChunkGenerator generator, pContext.random()om pContext.random(), BlockPos pos, NoneFeatureConfiguration config){
		//requireNonNull(NodeChunk.getFrom((Chunk)world.getChunk(pos))).addNode(new Node(NORMAL.genNodeAspects(pos, world, pContext.random()), NORMAL, pos.getX(), pos.getY(), pos.getZ()));
		// its a chunkprimer, not a chunk, with no capability data attached
		// add it on the next tick.
		BlockPos newPos = pContext.origin().above(5 + pContext.random().nextInt(2));
		NodeType type = pContext.random().nextInt(100) < ArcanaConfig.SPECIAL_NODE_CHANCE.get() ? new ArrayList<>(SPECIAL_TYPES).get(pContext.random().nextInt(SPECIAL_TYPES.size())) : DEFAULT;
		if(pContext.random().nextInt(100) < ArcanaConfig.NODE_CHANCE.get()){
			WorldTickHandler.onTick.add(newWorld -> {
				AspectHandler aspects = type.genBattery(newPos, newWorld, pContext.random());
				requireNonNull(AuraChunk.getFrom((LevelChunk)newWorld.getChunk(newPos))).addNode(new Node(aspects, type, newPos.getX(), newPos.getY(), newPos.getZ(), 0));
				// Add some crystal clusters around here too
				int successes = 0;
				BlockPos.MutableBlockPos pointer = pContext.origin().mutable();
				for(int i = 0; i < 40 && successes < (pContext.random().nextInt(5) + 6); i++){
					// Pick a pContext.random()om block from the ground
					pointer.set(pContext.origin()).move(pContext.random().nextInt(7) - pContext.random().nextInt(7), pContext.random().nextInt(5) - pContext.random().nextInt(5), pContext.random().nextInt(7) - pContext.random().nextInt(7));
					if(newWorld.getBlockState(pointer).isAir() || newWorld.getBlockState(pointer).getMaterial().isReplaceable()){
						// If it has at least one open side,
						for(Direction value : Direction.values()){
							BlockState state = newWorld.getBlockState(pointer.relative(value));
							boolean replace = false;
							if(state.isSuffocating(newWorld, pointer.relative(value)) || (replace = state.getBlock() == Blocks.SNOW)){
								if(replace)
									pointer.move(value);
								// Place a crystal,
								Aspect aspect = aspects.getHolder(pContext.random().nextInt(aspects.countHolders())).getStack().getAspect();
								System.out.println(aspect);
								newWorld.setBlockAndUpdate(pointer, CRYSTAL_CLUSTERS_FROM_ASPECTS.get(aspect).get().defaultBlockState().setValue(CrystalClusterBlock.FACING, value.getOpposite()).setValue(CrystalClusterBlock.AGE, 3).setValue(CrystalClusterBlock.WATERLOGGED, newWorld.getBlockState(pointer).getFluidState().is(FluidTags.WATER)));
								// Increment successes
								successes++;
							}
							break;
						}
					}
				}
			});
		}
		return true;
	}
}