package net.arcanamod.worldgen.trees.features;

import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.arcanamod.worldgen.ArcanaFeatures;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Random;
import java.util.function.BiConsumer;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class GreatwoodFoliagePlacer extends FoliagePlacer {

	public static final Codec<GreatwoodFoliagePlacer> CODEC = RecordCodecBuilder.create(a -> makeCodec(a).apply(a, GreatwoodFoliagePlacer::new));

	protected static <P extends GreatwoodFoliagePlacer> Products.P3<RecordCodecBuilder.Mu<P>, IntProvider, IntProvider, Integer> makeCodec(RecordCodecBuilder.Instance<P> builder) {
		return foliagePlacerParts(builder).and(Codec.intRange(0, 24).fieldOf("height").forGetter((placer) -> placer.height));
	}

	protected final int height;

	public GreatwoodFoliagePlacer(IntProvider radius, IntProvider offset, int height){
		super(radius, offset);
		this.height = height;
	}

	@Override
	protected FoliagePlacerType<?> type(){
		return ArcanaFeatures.GREATWOOD_FOLIAGE.get();
	}

	// generate
	protected void createFoliage(LevelSimulatedReader pLevel, BiConsumer<BlockPos, BlockState> pBlockSetter, Random pRandom, TreeConfiguration pConfiguration, int pMaxFreeTreeHeight, FoliagePlacer.FoliageAttachment pAttachment, int pFoliageHeight, int pFoliageRadius, int pOffset) {
		/* OG
		BlockPos node = foliage.func_236763_a_();
		// Iterate in a spheroid to place leaves
		for(int x1 = -3; x1 <= 3; x1++){
			for(int z1 = -3; z1 <= 3; z1++){
				for(int y1 = -2; y1 <= 2; y1++){
					double rX = x1 / 3.0;
					double rZ = z1 / 3.0;
					double rY = y1 / 2.0;
					// Scale the distance to customize the blob shape
					rX *= 1.1;
					rZ *= 1.1;
					rY *= 0.95;
					double dist = rX * rX + rZ * rZ + rY * rY;

					// Apply randomness to the radius and place leaves
					if(dist <= 1 + (pRandom.nextDouble() * 0.3)){
						BlockPos local = node.offset(x1, y1, z1);
						if(isAirAt(world, local)){
							world.setBlockState(local, config.leavesProvider.getBlockState(rand, local), 3);
						}
					}
				}
			}
		}*/

		for(int i = pOffset; i >= pOffset - pFoliageHeight; --i) {
			int j = pFoliageRadius + (i != pOffset && i != pOffset - pFoliageHeight ? 1 : 0);
			this.placeLeavesRow(pLevel, pBlockSetter, pRandom, pConfiguration, pAttachment.pos(), j, i, pAttachment.doubleTrunk());
		}
	}

	@Override
	public int foliageHeight(Random pRandom, int pHeight, TreeConfiguration pConfig) {
		return this.height;
	}

	@Override
	protected boolean shouldSkipLocation(Random pRandom, int pLocalX, int pLocalY, int pLocalZ, int pRange, boolean pLarge) {
		return Mth.square((float)pLocalX + 0.25F) + Mth.square((float)pLocalZ + 0.25F) > (float)(pRange * pRange);
	}
}
