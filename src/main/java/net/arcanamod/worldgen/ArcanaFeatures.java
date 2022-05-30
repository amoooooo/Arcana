package net.arcanamod.worldgen;

import net.arcanamod.Arcana;
import net.arcanamod.blocks.ArcanaBlocks;
import net.arcanamod.worldgen.trees.features.GreatwoodFoliagePlacer;
import net.arcanamod.worldgen.trees.features.GreatwoodTrunkPlacer;
import net.arcanamod.worldgen.trees.features.SilverwoodFoliagePlacer;
import net.arcanamod.worldgen.trees.features.SilverwoodTrunkPlacer;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.WeightedPlacedFeature;
import net.minecraft.world.level.levelgen.feature.configurations.RandomFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.BlobFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.trunkplacers.StraightTrunkPlacer;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;
import java.util.Set;

import static net.arcanamod.Arcana.MODID;

@Mod.EventBusSubscriber(modid = Arcana.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ArcanaFeatures{
	
	public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(ForgeRegistries.FEATURES, MODID);
	public static final DeferredRegister<FoliagePlacerType<?>> FOLAIGE_PLACERS = DeferredRegister.create(ForgeRegistries.FOLIAGE_PLACER_TYPES, MODID);
	/*
//	public static TreeConfiguration GREATWOOD_TREE_CONFIG;
	public static TreeConfiguration SILVERWOOD_TREE_CONFIG;
	
	public static TreeConfiguration TAINTED_GREATWOOD_TREE_CONFIG;
	
	public static TreeConfiguration TAINTED_OAK_TREE_CONFIG;
	public static TreeConfiguration TAINTED_DARK_OAK_TREE_CONFIG;
	public static TreeConfiguration TAINTED_BIRCH_TREE_CONFIG;
	public static TreeConfiguration TAINTED_ACACIA_TREE_CONFIG;
	public static TreeConfiguration TAINTED_SPRUCE_TREE_CONFIG;
	public static TreeConfiguration TAINTED_PINE_TREE_CONFIG;
	public static TreeConfiguration TAINTED_FANCY_OAK_TREE_CONFIG;
	public static TreeConfiguration TAINTED_JUNGLE_TREE_CONFIG; // Only the No Vines variant, since the vines variant only spawns during worldgen
	public static TreeConfiguration TAINTED_MEGA_JUNGLE_TREE_CONFIG;
	public static TreeConfiguration TAINTED_MEGA_SPRUCE_TREE_CONFIG;
	public static TreeConfiguration TAINTED_MEGA_PINE_TREE_CONFIG;
	// couldn't bother to make the bee trees too. sue me
	
	// features have to exist first because forge is stupid and insists on registering biomes first
	public static Feature<NoneFeatureConfiguration> NODE = new NodeFeature(NoneFeatureConfiguration.CODEC);
//	public static Holder<ConfiguredFeature<TreeConfiguration, ?>> GREATWOOD_TREE;
	public static Holder<ConfiguredFeature<TreeConfiguration, ?>> SILVERWOOD_TREE;
	
	public static Holder<ConfiguredFeature<TreeConfiguration, ?>> TAINTED_GREATWOOD_TREE;
	public static Holder<ConfiguredFeature<TreeConfiguration, ?>> TAINTED_OAK_TREE;
	public static Holder<ConfiguredFeature<TreeConfiguration, ?>> TAINTED_DARK_OAK_TREE;
	public static Holder<ConfiguredFeature<TreeConfiguration, ?>> TAINTED_BIRCH_TREE;
	public static Holder<ConfiguredFeature<TreeConfiguration, ?>> TAINTED_ACACIA_TREE;
	public static Holder<ConfiguredFeature<TreeConfiguration, ?>> TAINTED_SPRUCE_TREE;
	public static Holder<ConfiguredFeature<TreeConfiguration, ?>> TAINTED_PINE_TREE;
	public static Holder<ConfiguredFeature<TreeConfiguration, ?>> TAINTED_FANCY_OAK_TREE;
	public static Holder<ConfiguredFeature<TreeConfiguration, ?>> TAINTED_JUNGLE_TREE;
	public static Holder<ConfiguredFeature<TreeConfiguration, ?>> TAINTED_MEGA_JUNGLE_TREE;
	public static Holder<ConfiguredFeature<TreeConfiguration, ?>> TAINTED_MEGA_SPRUCE_TREE;
	public static Holder<ConfiguredFeature<TreeConfiguration, ?>> TAINTED_MEGA_PINE_TREE;
	
	public static ConfiguredFeature<?, ?> MAGICAL_FOREST_BONUS_TREES;
	public static ConfiguredFeature<?, ?> MAGICAL_FOREST_GIANT_MUSHROOMS;
	public static ConfiguredFeature<?, ?> MAGIC_MUSHROOM_PATCH;*/
	
	public static RegistryObject<FoliagePlacerType<GreatwoodFoliagePlacer>> GREATWOOD_FOLIAGE = FOLAIGE_PLACERS.register("greatwood_foliage_placer", () -> new FoliagePlacerType<>(GreatwoodFoliagePlacer.CODEC));
	public static RegistryObject<FoliagePlacerType<SilverwoodFoliagePlacer>> SILVERWOOD_FOLIAGE = FOLAIGE_PLACERS.register("silverwood_foliage_placer", () -> new FoliagePlacerType<>(SilverwoodFoliagePlacer.CODEC));

	// Kaupenjoe Tutorial
	// As in TreeFeatures class
	public static final Holder<ConfiguredFeature<TreeConfiguration, ?>> GREATWOOD_TREE =
			FeatureUtils.register("greatwood_tree", Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
					BlockStateProvider.simple(ArcanaBlocks.GREATWOOD_LOG.get()),
					new GreatwoodTrunkPlacer(20, 6, 3),
					BlockStateProvider.simple(ArcanaBlocks.GREATWOOD_LEAVES.get()),
					new GreatwoodFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0), 4),
					new TwoLayersFeatureSize(1, 0, 2)).build());

	public static final Holder<ConfiguredFeature<TreeConfiguration, ?>> TAINTED_GREATWOOD_TREE =
			FeatureUtils.register("tainted_greatwood_tree", Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
					BlockStateProvider.simple(ArcanaBlocks.TAINTED_GREATWOOD_LOG.get()),
					new GreatwoodTrunkPlacer(20, 6, 3),
					BlockStateProvider.simple(ArcanaBlocks.TAINTED_GREATWOOD_LEAVES.get()),
					new GreatwoodFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0), 4),
					new TwoLayersFeatureSize(1, 0, 2)).build());

	public static final Holder<ConfiguredFeature<TreeConfiguration, ?>> SILVERWOOD_TREE =
			FeatureUtils.register("silverwood_tree", Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
					BlockStateProvider.simple(ArcanaBlocks.SILVERWOOD_LOG.get()),
					new SilverwoodTrunkPlacer(14, 6, 3),
					BlockStateProvider.simple(ArcanaBlocks.GREATWOOD_LEAVES.get()),
					new SilverwoodFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0), 8),
					new TwoLayersFeatureSize(1, 0, 2)).build());

	public static final Holder<ConfiguredFeature<TreeConfiguration, ?>> TAINTED_OAK_TREE =
			FeatureUtils.register("tainted_oak_tree", Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
					BlockStateProvider.simple(ArcanaBlocks.TAINTED_OAK_LOG.get()),
					new StraightTrunkPlacer(4, 2, 0),
					BlockStateProvider.simple(ArcanaBlocks.TAINTED_OAK_LEAVES.get()),
					new BlobFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0), 3),
					new TwoLayersFeatureSize(1, 0, 1)).build());

	// As in TreePlacements class
	public static final Holder<PlacedFeature> GREATWOOD_CHECKED = PlacementUtils.register("greatwood_checked", GREATWOOD_TREE,
			PlacementUtils.filteredByBlockSurvival(ArcanaBlocks.GREATWOOD_SAPLING.get()));

	public static final Holder<PlacedFeature> SILVERWOOD_CHECKED = PlacementUtils.register("silverwood_checked", GREATWOOD_TREE,
			PlacementUtils.filteredByBlockSurvival(ArcanaBlocks.SILVERWOOD_SAPLING.get()));

	// As in VegetationFeatures class
	public static final Holder<ConfiguredFeature<RandomFeatureConfiguration, ?>> GREATWOOD_SPAWN =
			FeatureUtils.register("greatwood_spawn", Feature.RANDOM_SELECTOR,
					new RandomFeatureConfiguration(List.of(new WeightedPlacedFeature(GREATWOOD_CHECKED,
							0.5F)), GREATWOOD_CHECKED));

	public static final Holder<ConfiguredFeature<RandomFeatureConfiguration, ?>> SILVERWOOD_SPAWN =
			FeatureUtils.register("silverwood_spawn", Feature.RANDOM_SELECTOR,
					new RandomFeatureConfiguration(List.of(new WeightedPlacedFeature(SILVERWOOD_CHECKED,
							0.5F)), SILVERWOOD_CHECKED));

	// As in VegetationPlacements class
	public static final Holder<PlacedFeature> GREATWOOD_PLACED = PlacementUtils.register("greatwood_placed",
			GREATWOOD_SPAWN, VegetationPlacements.treePlacement(
					PlacementUtils.countExtra(3, 0.1f, 2)));

	public static final Holder<PlacedFeature> SILVERWOOD_PLACED = PlacementUtils.register("silverwood_placed",
			SILVERWOOD_SPAWN, VegetationPlacements.treePlacement(
					PlacementUtils.countExtra(3, 0.1f, 2)));

	/*
	@SubscribeEvent(priority = EventPriority.HIGH)
	public static void onItemRegister(BiomeLoadingEvent event){
		event.getGeneration().getFeatures(
				GenerationStep.Decoration.TOP_LAYER_MODIFICATION,
				ArcanaFeatures.NODE
					.withPlacement(new NoneFeatureConfiguration())
					.withPlacement(Placement.TOP_SOLID_HEIGHTMAP.configure(IPlacementConfig.NO_PLACEMENT_CONFIG))
			);
		
		if(event.getName().equals(arcLoc("magical_forest"))){
			event.getGeneration().getFeatures(GenerationStep.Decoration.VEGETAL_DECORATION).add(ArcanaFeatures.MAGICAL_FOREST_BONUS_TREES);
			event.getGeneration().withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, ArcanaFeatures.MAGICAL_FOREST_GIANT_MUSHROOMS);
			event.getGeneration().withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, ArcanaFeatures.MAGIC_MUSHROOM_PATCH);
		}
	}*/

	@SubscribeEvent(priority = EventPriority.HIGH)
	public static void generateTrees(final BiomeLoadingEvent event) {
		ResourceKey<Biome> key = ResourceKey.create(Registry.BIOME_REGISTRY, event.getName());
		Set<BiomeDictionary.Type> types = BiomeDictionary.getTypes(key);

		if (types.contains(BiomeDictionary.Type.FOREST)) {
			List<Holder<PlacedFeature>> base =
					event.getGeneration().getFeatures(GenerationStep.Decoration.VEGETAL_DECORATION);

			base.add(GREATWOOD_PLACED);
		}
	}
}