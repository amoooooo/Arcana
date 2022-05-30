package net.arcanamod.worldgen.trees.features;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SilverwoodTrunkPlacer extends TrunkPlacer {
	
	public static final Codec<SilverwoodTrunkPlacer> CODEC = RecordCodecBuilder.create((builderInstance) -> trunkPlacerParts(builderInstance).apply(builderInstance, SilverwoodTrunkPlacer::new));
//	public static final TrunkPlacerType<SilverwoodTrunkPlacer> SILVERWOOD_PLACER = Registry.register(Registry.TRUNK_REPLACER, Arcana.arcLoc("greatwood_placer"), TrunkPlacerTypeAccessor.createTrunkPlacerType(CODEC));
	
	public SilverwoodTrunkPlacer(int baseHeight, int heightRandA, int heightRandB){
		super(baseHeight, heightRandA, heightRandB);
	}

	@Override
	protected TrunkPlacerType<?> type(){
		return ArcanaTrunkPlacerTypes.SILVERWOOD_TRUNK_PLACER != null ? ArcanaTrunkPlacerTypes.SILVERWOOD_TRUNK_PLACER : TrunkPlacerType.BENDING_TRUNK_PLACER;
	}

	@Override
	public List<FoliagePlacer.FoliageAttachment> placeTrunk(
			LevelSimulatedReader pLevel,
			BiConsumer<BlockPos, BlockState> pBlockSetter,
			Random pRandom,
			int pFreeTreeHeight,
			BlockPos pPos,
			TreeConfiguration pConfig
	) {
		List<FoliagePlacer.FoliageAttachment> list = Lists.newArrayList();
		setDirtAt(pLevel, pBlockSetter, pRandom, pPos.below(), pConfig);

		BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

		for(int x = -2; x <= 2; x++){
			for(int z = -2; z <= 2; z++){
				int dist = Math.abs(x) + Math.abs(z);
				if(dist <= 2) {
					int logHeight = pFreeTreeHeight;
					if(dist == 1){
						// plus shape: 70% of height
						logHeight = (int)(pFreeTreeHeight * 0.7) + pRandom.nextInt(2);
					}else if(dist == 2){
						// "roots"
						logHeight = 1 + pRandom.nextInt(2);
					}
					// Place the logs
					generateLogColumn(pLevel, pBlockSetter, pRandom, blockpos$mutableblockpos, pConfig, pPos, logHeight);
					list.add(new FoliagePlacer.FoliageAttachment(pPos.offset(0, logHeight, 0), 0, false));
				}
			}
		}


		return list;
	}

	/*
	// more like Generate
	public List<FoliagePlacer.Foliage> getFoliages(IWorldGenerationReader world, Random rand, int treeHeight, BlockPos pos, Set<BlockPos> logs, MutableBoundingBox box, BaseTreeFeatureConfig config){
		int height = rand.nextInt(2) + rand.nextInt(2) + 12;
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();
		if(y >= 1 && y + height + 1 < 255){
			BlockPos ground = pos.down();
			for(int x1 = -2; x1 <= 2; x1++){
				for(int z1 = -2; z1 <= 2; z1++){
					// Manhattan distance from center
					int dist = Math.abs(x1) + Math.abs(z1);
					
					// Place in a 2-wide plus formation
					if(dist <= 2){
						// 0 distance: normal height
						int logHeight = height;
						if(dist == 1){
							// plus shape: 70% of height
							logHeight = (int)(height * 0.7) + rand.nextInt(2);
						}else if(dist == 2){
							// 2-wide plus shape: 1 high plus random
							logHeight = 1 + rand.nextInt(2);
						}
						
						// Place the logs
						generateLogColumn(world, rand, pos.add(x1, 0, z1), logHeight, config);
					}
				}
			}
			
			// Iterate in a spheroid to place leaves
			for(int x1 = -4; x1 <= 4; x1++){
				for(int z1 = -4; z1 <= 4; z1++){
					for(int y1 = -5; y1 <= 5; y1++){
						double rX = x1 / 4.0;
						double rZ = z1 / 4.0;
						double rY = y1 / 5.0;
						double dist = rX * rX + rZ * rZ + rY * rY;
						
						// Apply randomness to the radius and place leaves
						if(dist <= 0.8 + (rand.nextDouble() * 0.4)){
							BlockPos local = pos.add(x1, height - 4 + y1, z1);
							if(isAirAt(world, local)){
								world.setBlockState(local, config.leavesProvider.getBlockState(rand, local), 3);
							}
						}
					}
				}
			}
			
			// add pure node at half height
			if(rand.nextInt(100) < ArcanaConfig.SILVERWOOD_NODE_CHANCE.get())
				WorldTickHandler.onTick.add(w -> {
					NodeType type = NodeType.PURE;
					AspectHandler aspects = type.genBattery(pos, w, rand);
					requireNonNull(AuraChunk.getFrom((Chunk)w.getChunk(pos))).addNode(new Node(aspects, type, x, y + height / 2f, z, 0));
				});
			
			return Lists.newArrayList(new FoliagePlacer.Foliage(pos.up(height - 4), 1, false));
		}
		return Collections.emptyList();
	}*/
	
	private void generateLogColumn(LevelSimulatedReader pLevel, BiConsumer<BlockPos, BlockState> pBlockSetter, Random pRandom, BlockPos.MutableBlockPos pPos, TreeConfiguration pConfig, BlockPos pOffsetPos, int height) {
		for (int y = 0; y < height; y++) {
			pPos.setWithOffset(pOffsetPos, 0, y, 0);
			placeLogIfFree(pLevel, pBlockSetter, pRandom, pPos, pConfig);
		}
	}
}