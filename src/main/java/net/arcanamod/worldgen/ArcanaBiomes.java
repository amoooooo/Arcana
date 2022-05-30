package net.arcanamod.worldgen;

import net.minecraft.core.Registry;
import net.minecraft.data.worldgen.BiomeDefaultFeatures;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static net.arcanamod.Arcana.MODID;

public class ArcanaBiomes{
	
	public static final DeferredRegister<Biome> BIOMES = DeferredRegister.create(ForgeRegistries.BIOMES, MODID);
	
	public static final RegistryObject<Biome> MAGICAL_FOREST = BIOMES.register("magical_forest", ArcanaBiomes::makeMagicalForestBiome);
	
	private static Biome makeMagicalForestBiome(){
		BiomeGenerationSettings.Builder biomegenerationsettings$builder = new BiomeGenerationSettings.Builder();
		BiomeDefaultFeatures.addDefaultCarversAndLakes(biomegenerationsettings$builder);
		BiomeDefaultFeatures.addDefaultCrystalFormations(biomegenerationsettings$builder);
		BiomeDefaultFeatures.addDefaultMonsterRoom(biomegenerationsettings$builder);
		BiomeDefaultFeatures.addDefaultUndergroundVariety(biomegenerationsettings$builder);
		BiomeDefaultFeatures.addDefaultSprings(biomegenerationsettings$builder);
		BiomeDefaultFeatures.addSurfaceFreezing(biomegenerationsettings$builder);
		BiomeDefaultFeatures.addForestFlowers(biomegenerationsettings$builder);
		BiomeDefaultFeatures.addDefaultOres(biomegenerationsettings$builder);
		BiomeDefaultFeatures.addDefaultSoftDisks(biomegenerationsettings$builder);
		BiomeDefaultFeatures.addDefaultFlowers(biomegenerationsettings$builder);
		BiomeDefaultFeatures.addForestGrass(biomegenerationsettings$builder);
		BiomeDefaultFeatures.addDefaultMushrooms(biomegenerationsettings$builder);
		BiomeDefaultFeatures.addDefaultExtraVegetation(biomegenerationsettings$builder);
		BiomeDefaultFeatures.addOtherBirchTrees(biomegenerationsettings$builder);

		biomegenerationsettings$builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, ArcanaFeatures.GREATWOOD_PLACED);

		// withAllForestFlowerGeneration(settings);
		// withFrozenTopLayer(settings);

		MobSpawnSettings.Builder mobspawnsettings$builder = new MobSpawnSettings.Builder();
		BiomeDefaultFeatures.farmAnimals(mobspawnsettings$builder);
		BiomeDefaultFeatures.commonSpawns(mobspawnsettings$builder);
		mobspawnsettings$builder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.WOLF, 5, 4, 4));
		//.isValidSpawnBiomeForPlayer();

		Biome biome = (new Biome.BiomeBuilder())
				.precipitation(Biome.Precipitation.RAIN)
				.biomeCategory(Biome.BiomeCategory.FOREST)
				.temperature(.6f)
				.downfall(.9f)
				.specialEffects((new BiomeSpecialEffects.Builder())
						.grassColorOverride(0x7ff3ac)
						.waterColor(0x3f76e4)
						.waterFogColor(0x50533)
						.fogColor(0xc0d8ff)
						.skyColor(getSkyColorWithTemperatureModifier(.7f))
						.ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS)
						.build())
				.mobSpawnSettings(mobspawnsettings$builder.build())
				.generationSettings(biomegenerationsettings$builder.build())
				.build();
		
		ResourceKey<Biome> key = ResourceKey.create(Registry.BIOME_REGISTRY, MAGICAL_FOREST.getId());
		BiomeManager.addBiome(BiomeManager.BiomeType.COOL, new BiomeManager.BiomeEntry(key, 5));
		BiomeDictionary.addTypes(key, BiomeDictionary.Type.FOREST, BiomeDictionary.Type.OVERWORLD, BiomeDictionary.Type.MAGICAL);
		
		return biome;
	}

	private static int getSkyColorWithTemperatureModifier(float temperature) {
		float temp = temperature / 3;
		temp = Mth.clamp(temp, -1, 1);
		return Mth.hsvToRgb(0.62222224F - temp * .05f, .5f + temp * .1f, 1);
	}
}