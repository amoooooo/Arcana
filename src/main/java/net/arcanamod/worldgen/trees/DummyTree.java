package net.arcanamod.worldgen.trees;

import net.minecraft.core.Holder;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Random;

public class DummyTree extends AbstractTreeGrower {
	/**
	 * Get a {@link ConfiguredFeature} Holder of tree grower.
	 *
	 * @param pRandom
	 * @param pLargeHive
	 */
	@Nullable
	@Override
	protected Holder<? extends ConfiguredFeature<?, ?>> getConfiguredFeature(@NotNull Random pRandom, boolean pLargeHive) {
		return null;
	}
}