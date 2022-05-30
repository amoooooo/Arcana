package net.arcanamod.worldgen.trees;

import net.arcanamod.worldgen.ArcanaFeatures;
import net.minecraft.core.Holder;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Random;

@ParametersAreNonnullByDefault
public class GreatwoodTree extends AbstractTreeGrower {
	
	/*@Nullable
	protected ConfiguredFeature<BaseTreeFeatureConfig, ?> getHugeTreeFeature(Random rand){
		return ArcanaFeatures.GREATWOOD_TREE;
	}
	
	@Nullable
	protected ConfiguredFeature<BaseTreeFeatureConfig, ?> getTreeFeature(Random random, boolean largeHive){
		return null;
	}*/

	@Nullable
	@Override
	protected Holder<? extends ConfiguredFeature<?, ?>> getConfiguredFeature(Random pRandom, boolean pLargeHive) {
		return ArcanaFeatures.GREATWOOD_TREE;
	}
}
