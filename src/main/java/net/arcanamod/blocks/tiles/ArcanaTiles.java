package net.arcanamod.blocks.tiles;

import com.google.common.collect.Sets;
import net.arcanamod.Arcana;
import net.arcanamod.blocks.ArcanaBlocks;
import net.arcanamod.blocks.pipes.PipeWindowBlockEntity;
import net.arcanamod.blocks.pipes.PumpBlockEntity;
import net.arcanamod.blocks.pipes.TubeBlockEntity;
import net.arcanamod.blocks.pipes.ValveBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@SuppressWarnings("ConstantConditions")
public class ArcanaTiles{
	
	public static final DeferredRegister<BlockEntityType<?>> TES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, Arcana.MODID);

	public static final RegistryObject<BlockEntityType<JarBlockEntity>> JAR_TE =
			TES.register("jar", () -> new BlockEntityType<>(JarBlockEntity::new, Sets.newHashSet(ArcanaBlocks.JAR.get(), ArcanaBlocks.SECURE_JAR.get(), ArcanaBlocks.VOID_JAR.get(), ArcanaBlocks.VACUUM_JAR.get(), ArcanaBlocks.PRESSURE_JAR.get()), null));
	public static final RegistryObject<BlockEntityType<AspectBookshelfBlockEntity>> ASPECT_SHELF_TE =
			TES.register("aspect_shelf", () -> new BlockEntityType<>(AspectBookshelfBlockEntity::new, Sets.newHashSet(ArcanaBlocks.ASPECT_BOOKSHELF.get(), ArcanaBlocks.ASPECT_BOOKSHELF_BLOCK.get()), null));
	public static final RegistryObject<BlockEntityType<ResearchTableBlockEntity>> RESEARCH_TABLE_TE =
			TES.register("research_table", () -> new BlockEntityType<>(ResearchTableBlockEntity::new, Sets.newHashSet(ArcanaBlocks.RESEARCH_TABLE.get()), null));
	public static final RegistryObject<BlockEntityType<FociForgeBlockEntity>> FOCI_FORGE_TE =
			TES.register("foci_forge", () -> new BlockEntityType<>(FociForgeBlockEntity::new, Sets.newHashSet(ArcanaBlocks.FOCI_FORGE.get()), null));
	public static final RegistryObject<BlockEntityType<AspectTesterBlockEntity>> ASPECT_TESTER =
			TES.register("aspect_tester", () -> new BlockEntityType<>(AspectTesterBlockEntity::new, Sets.newHashSet(ArcanaBlocks.ASPECT_TESTER.get()), null));
	public static final RegistryObject<BlockEntityType<TaintScrubberBlockEntity>> TAINT_SCRUBBER_TE =
			TES.register("taint_scrubber", () -> new BlockEntityType<>(TaintScrubberBlockEntity::new, Sets.newHashSet(ArcanaBlocks.TAINT_SCRUBBER_MK1.get()), null));
	
	public static final RegistryObject<BlockEntityType<TubeBlockEntity>> ASPECT_TUBE_TE =
			TES.register("essentia_tube", () -> new BlockEntityType<>(TubeBlockEntity::new, Sets.newHashSet(ArcanaBlocks.ASPECT_TUBE.get()), null));
//	public static final RegistryObject<BlockEntityType<TubeBlockEntity>> ASPECT_TUBE_TE =
//		TES.register("essentia_tube", () -> BlockEntityType.Builder.of(TubeBlockEntity::new, ArcanaBlocks.ASPECT_TUBE.get()).build(null));
	public static final RegistryObject<BlockEntityType<ValveBlockEntity>> ASPECT_VALVE_TE =
			TES.register("essentia_valve", () -> new BlockEntityType<>(ValveBlockEntity::new, Sets.newHashSet(ArcanaBlocks.ASPECT_VALVE.get()), null));
	public static final RegistryObject<BlockEntityType<PipeWindowBlockEntity>> ASPECT_WINDOW_TE =
			TES.register("essentia_window", () -> new BlockEntityType<>(PipeWindowBlockEntity::new, Sets.newHashSet(ArcanaBlocks.ASPECT_WINDOW.get()), null));
	public static final RegistryObject<BlockEntityType<PumpBlockEntity>> ASPECT_PUMP_TE =
			TES.register("essentia_pump", () -> new BlockEntityType<>(PumpBlockEntity::new, Sets.newHashSet(ArcanaBlocks.ASPECT_PUMP.get()), null));
	
	public static final RegistryObject<BlockEntityType<PedestalBlockEntity>> PEDESTAL_TE =
			TES.register("pedestal", () -> new BlockEntityType<>(PedestalBlockEntity::new, Sets.newHashSet(ArcanaBlocks.PEDESTAL.get()), null));
	public static final RegistryObject<BlockEntityType<AlembicBlockEntity>> ALEMBIC_TE =
			TES.register("alembic", () -> new BlockEntityType<>(AlembicBlockEntity::new, Sets.newHashSet(ArcanaBlocks.ALEMBIC.get()), null));
	public static final RegistryObject<BlockEntityType<CrucibleBlockEntity>> CRUCIBLE_TE =
			TES.register("crucible", () -> new BlockEntityType<>(CrucibleBlockEntity::new, Sets.newHashSet(ArcanaBlocks.CRUCIBLE.get()), null));
	public static final RegistryObject<BlockEntityType<ArcaneCraftingTableBlockEntity>> ARCANE_WORKBENCH_TE =
			TES.register("arcane_crafting_table", () -> new BlockEntityType<>(ArcaneCraftingTableBlockEntity::new, Sets.newHashSet(ArcanaBlocks.ARCANE_CRAFTING_TABLE.get()), null));
	public static final RegistryObject<BlockEntityType<AspectCrystallizerBlockEntity>> ASPECT_CRYSTALLIZER_TE =
			TES.register("aspect_crystallizer", () -> new BlockEntityType<>(AspectCrystallizerBlockEntity::new, Sets.newHashSet(ArcanaBlocks.ASPECT_CRYSTALLIZER.get()), null));
	public static final RegistryObject<BlockEntityType<VacuumBlockEntity>> VACUUM_TE =
			TES.register("vacuum", () -> new BlockEntityType<>(VacuumBlockEntity::new, Sets.newHashSet(ArcanaBlocks.VACUUM_BLOCK.get()), null));
	public static final RegistryObject<BlockEntityType<WardenedBlockBlockEntity>> WARDENED_BLOCK_TE =
			TES.register("wardened_block", () -> new BlockEntityType<>(WardenedBlockBlockEntity::new, Sets.newHashSet(ArcanaBlocks.WARDENED_BLOCK.get()), null));
}