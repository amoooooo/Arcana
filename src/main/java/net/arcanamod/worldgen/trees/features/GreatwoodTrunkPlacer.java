package net.arcanamod.worldgen.trees.features;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
public class GreatwoodTrunkPlacer extends TrunkPlacer {

	public static final Codec<GreatwoodTrunkPlacer> CODEC = RecordCodecBuilder.create((builderInstance) -> trunkPlacerParts(builderInstance).apply(builderInstance, GreatwoodTrunkPlacer::new));
//	public static final TrunkPlacerType<GreatwoodTrunkPlacer> GREATWOOD_PLACER = Registry.register(Registry.TRUNK_PLACER_TYPES, Arcana.arcLoc("greatwood_placer"), TrunkPlacerTypeAccessor.createTrunkPlacerType(CODEC));
//	public static final TrunkPlacerType<GreatwoodTrunkPlacer> GREATWOOD_PLACER = Registry.register(Registry.TRUNK_PLACER_TYPES, Arcana.arcLoc("greatwood_trunk_placer"), new TrunkPlacerType<>(GreatwoodTrunkPlacer.CODEC));

	public GreatwoodTrunkPlacer(int baseHeight, int heightRandA, int heightRandB){
		super(baseHeight, heightRandA, heightRandB);
	}
	
	protected TrunkPlacerType<?> type(){
		return ArcanaTrunkPlacerTypes.GREATWOOD_TRUNK_PLACER != null ? ArcanaTrunkPlacerTypes.GREATWOOD_TRUNK_PLACER : TrunkPlacerType.GIANT_TRUNK_PLACER;
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
		BlockPos blockpos = pPos.below(); // Dirt pos

		// Place Dirt under trunk blocks
		setDirtAt(pLevel, pBlockSetter, pRandom, blockpos, pConfig);
		setDirtAt(pLevel, pBlockSetter, pRandom, blockpos.east(), pConfig);
		setDirtAt(pLevel, pBlockSetter, pRandom, blockpos.south(), pConfig);
		setDirtAt(pLevel, pBlockSetter, pRandom, blockpos.south().east(), pConfig);

		BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

		// Place roots?

		// Place main trunk
		for(int layer = 0; layer < pFreeTreeHeight; ++layer) {
			placeLogWithOffset(pLevel, pBlockSetter, pRandom, blockpos$mutableblockpos, pConfig, pPos, 0, layer, 0);
			if (layer < pFreeTreeHeight - 1) { // One top block
				placeLogWithOffset(pLevel, pBlockSetter, pRandom, blockpos$mutableblockpos, pConfig, pPos, 1, layer, 0);
				placeLogWithOffset(pLevel, pBlockSetter, pRandom, blockpos$mutableblockpos, pConfig, pPos, 1, layer, 1);
				placeLogWithOffset(pLevel, pBlockSetter, pRandom, blockpos$mutableblockpos, pConfig, pPos, 0, layer, 1);
			} else if(layer == pFreeTreeHeight -1) list.add(new FoliagePlacer.FoliageAttachment(pPos.above(layer), 0, true)); // Leaves top cap

			// Place branches
			if(layer > 6 && layer < pFreeTreeHeight - 3) { // Branch height range
				int branchCount = 1 + (layer / 8); // Branches at this level. Fewer branches at the bottom, but more at the top
				double offset = Math.PI * 2 * pRandom.nextDouble(); // Random rotation offset
				for (int i = 0; i < branchCount; i++) { // For each branch
					double angle = (((double) i / branchCount) * (Math.PI * 2)) + offset + (pRandom.nextDouble() * 0.2); // Branch direction
					int length = pRandom.nextInt(2) + (6 - branchCount) + layer / 10; // Branch length
					BlockPos start = pPos.relative(Direction.fromYRot(angle)); // Choose a starting location on the trunk

					// Traverse through the branch
					for (int j = 0; j <= length; j++) {
						int offsetX = (int) Math.cos(angle) * j;
						int offsetY = j/2; // The further form trunk, the more it curves upwards (not sure if I like it...)
						int offsetZ = (int) Math.sin(angle) * j;
						// TODO: Account for log rotation
						placeLogWithOffset(pLevel, pBlockSetter, pRandom, blockpos$mutableblockpos, pConfig, start, offsetX, offsetY, offsetZ);

						// If we're at the end, mark this position for generating leaves
						if (j == length) {
							list.add(new FoliagePlacer.FoliageAttachment(start.offset(offsetX, offsetY, offsetZ), 0, false));
						}
					}
				}
			}
		}

		return list;
	}

	private static void placeLogWithOffset(
			LevelSimulatedReader pLevel,
			BiConsumer<BlockPos, BlockState> pBlockSetter,
			Random pRandom,
			BlockPos.MutableBlockPos pPos,
			TreeConfiguration pConfig,
			BlockPos pOffsetPos,
			int pOffsetX,
			int pOffsetY,
			int pOffsetZ
	) {
		pPos.setWithOffset(pOffsetPos, pOffsetX, pOffsetY, pOffsetZ);
		placeLogIfFree(pLevel, pBlockSetter, pRandom, pPos, pConfig);
	}

	// OG
	/*
	public List<FoliagePlacer.Foliage> getFoliages(IWorldGenerationReader world, Random rand, int treeHeight, BlockPos pos, Set<BlockPos> logs, MutableBoundingBox box, BaseTreeFeatureConfig config){
		// todo: customisation options?
		int height = rand.nextInt(3) + rand.nextInt(3) + /*treeHeight/ 18;
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();
		if(y >= 1 && y + height + 1 < 255){
			BlockPos ground = pos.down();
			/*if(!isSoil(world, ground, config.getSapling()))
				return false;
			else if(!isSpaceClearForHeight(world, pos, height))
				return false;
			else{/
			func_236909_a_(world, ground);
			func_236909_a_(world, ground.east());
			func_236909_a_(world, ground.south());
			func_236909_a_(world, ground.south().east());
			int top = y + height - 1;
			Set<BlockPos> leafNodes = new HashSet<>();

			// roots
			for(int x1 = -1; x1 <= 2; x1++){
				for(int z1 = -1; z1 <= 2; z1++){
					// Skip root placement if we're in the trunk
					if((x1 == 0 || x1 == 1) && (z1 == 0 || z1 == 1)){
						continue;
					}
					
					// Get the root height by nesting random calls to make it biased towards 0
					int rootHeight = rand.nextInt(rand.nextInt(4) + 1);
					
					if(isRootOnEdge(x1) && isRootOnEdge(z1)){
						rootHeight--; // Reduce on corners
					}
					
					if(rootHeight > 0){
						BlockPos groundPos = new BlockPos(x + x1, y - 1, z + z1);
						
						if(TreeFeature.isAirAt(world, groundPos)){
							world.setBlockState(groundPos, Blocks.DIRT.getDefaultState(), 3);
						}
						
						// Place roots
						for(int curHeight = 0; curHeight < rootHeight; curHeight++){
							int curY = y + curHeight;
							BlockPos curPos = new BlockPos(x + x1, curY, z + z1);
							
							if(TreeFeature.isReplaceableAt(world, curPos)){
								func_236911_a_(world, rand, curPos, logs, box, config);
							}
						}
					}
				}
			}
			
			// main trunk
			for(int curHeight = 0; curHeight < height; ++curHeight){
				int curY = y + curHeight;
				BlockPos curPos = new BlockPos(x, curY, z);
				if(TreeFeature.isReplaceableAt(world, curPos)){
					func_236911_a_(world, rand, curPos, logs, box, config);
					func_236911_a_(world, rand, curPos.east(), logs, box, config);
					func_236911_a_(world, rand, curPos.south(), logs, box, config);
					func_236911_a_(world, rand, curPos.east().south(), logs, box, config);
				}
				
				// Branches
				if(curHeight > 6 && curHeight < height - 3){
					int branchCount = 1 + (curHeight / 8);
					double offset = Math.PI * 2 * rand.nextDouble();
					
					// Make fewer branches at the bottom, but more at the top
					for(int i = 0; i < branchCount; i++){
						double angle = (((double)i / branchCount) * (Math.PI * 2)) + offset + (rand.nextDouble() * 0.2);
						int length = rand.nextInt(2) + (6 - branchCount) + curHeight / 10;
						// Choose a starting location on the trunk
						BlockPos start = chooseStart(curPos, rand);
						
						for(int j = 0; j <= length; j++){
							// Traverse through the branch
							BlockPos local = start.add(Math.cos(angle) * j, j / 2.0, Math.sin(angle) * j);
							
							// Place logs if it's air
							if(TreeFeature.isAirAt(world, local)){
								func_236911_a_(world, rand, local, logs, box, config);
							}
							
							// If we're at the end, mark this position for generating leaves
							if(j == length){
								leafNodes.add(local);
							}
						}
					}
					
				}
				
				// Add leaves to the top of the trunk
				if(curHeight == height - 1){
					leafNodes.add(curPos);
					leafNodes.add(curPos.east());
					leafNodes.add(curPos.south());
					leafNodes.add(curPos.east().south());
				}
			}
			List<FoliagePlacer.Foliage> list = Lists.newArrayList();
			for(BlockPos node : leafNodes)
				// position, ???, ???
				list.add(new FoliagePlacer.Foliage(node, 1, false));
			return list;
		}
		return Collections.emptyList();
	}
	
	private boolean isRootOnEdge(int axis){
		return axis == -1 || axis == 2;
	}
	
	private static BlockPos chooseStart(BlockPos start, Random random){
		switch(random.nextInt(4)){
			default:
			case 0:
				return start;
			case 1:
				return start.east();
			case 2:
				return start.south();
			case 3:
				return start.east().south();
		}
	}
	
	private boolean isSpaceClearForHeight(IWorldGenerationBaseReader world, BlockPos pos, int height){
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();
		BlockPos.Mutable cursor = new BlockPos.Mutable();
		
		for(int i = 0; i <= height + 1; ++i){
			int r = 1;
			if(i == 0)
				r = 0;
			
			if(i >= height - 1)
				r = 2;
			
			for(int xr = -r; xr <= r; ++xr)
				for(int zr = -r; zr <= r; ++zr)
					if(!TreeFeature.isReplaceableAt(world, cursor.setPos(x + xr, y + i, z + zr)))
						return false;
		}
		
		return true;
	}
	*/
}