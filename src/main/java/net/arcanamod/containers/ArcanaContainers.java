package net.arcanamod.containers;

import net.arcanamod.Arcana;
import net.arcanamod.blocks.pipes.PumpBlockEntity;
import net.arcanamod.blocks.tiles.AlembicBlockEntity;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ArcanaContainers{
	
	public static final DeferredRegister<MenuType<?>> CON = DeferredRegister.create(ForgeRegistries.CONTAINERS, Arcana.MODID);

	public static final RegistryObject<MenuType<FociForgeMenu>> FOCI_FORGE = CON.register("foci_forge", () -> IForgeMenuType.create(FociForgeMenu::new));
	public static final RegistryObject<MenuType<ResearchTableMenu>> RESEARCH_TABLE = CON.register("research_table", () -> IForgeMenuType.create(ResearchTableMenu::new));
	public static final RegistryObject<MenuType<ArcaneCraftingTableMenu>> ARCANE_CRAFTING_TABLE = CON.register("arcane_crafting_table", () -> IForgeMenuType.create((id, inventory, buffer) -> new ArcaneCraftingTableMenu(id, inventory, (Container) inventory.player.level.getBlockEntity(buffer.readBlockPos()))));
	public static final RegistryObject<MenuType<AspectCrystallizerMenu>> ASPECT_CRYSTALLIZER = CON.register("aspect_crystallizer", () -> IForgeMenuType.create((id, inventory, buffer) -> new AspectCrystallizerMenu(id, (Container) inventory.player.level.getBlockEntity(buffer.readBlockPos()), inventory)));
	public static final RegistryObject<MenuType<AlembicMenu>> ALEMBIC = CON.register("alembic", () -> IForgeMenuType.create((id, inventory, buffer) -> new AlembicMenu(id, (AlembicBlockEntity)inventory.player.level.getBlockEntity(buffer.readBlockPos()), inventory)));
	public static final RegistryObject<MenuType<PumpMenu>> PUMP = CON.register("essentia_pump", () -> IForgeMenuType.create((id, inventory, buffer) -> new PumpMenu(id, (PumpBlockEntity) inventory.player.level.getBlockEntity(buffer.readBlockPos()), inventory)));
}