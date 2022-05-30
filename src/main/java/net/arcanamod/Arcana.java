package net.arcanamod;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;
import net.arcanamod.aspects.Aspect;
import net.arcanamod.aspects.Aspects;
import net.arcanamod.aspects.ItemAspectRegistry;
import net.arcanamod.aspects.handlers.AspectHandler;
import net.arcanamod.aspects.handlers.AspectHandlerCapability;
import net.arcanamod.aspects.handlers.AspectHolder;
import net.arcanamod.blocks.ArcanaBlocks;
import net.arcanamod.blocks.tiles.ArcanaTiles;
import net.arcanamod.capabilities.AuraChunkCapability;
import net.arcanamod.capabilities.ResearcherCapability;
import net.arcanamod.capabilities.TaintTrackableCapability;
import net.arcanamod.commands.NodeArgument;
import net.arcanamod.containers.ArcanaContainers;
import net.arcanamod.effects.ArcanaEffects;
import net.arcanamod.entities.ArcanaEntities;
import net.arcanamod.event.WorldLoadEvent;
import net.arcanamod.fluids.ArcanaFluids;
import net.arcanamod.items.ArcanaItems;
import net.arcanamod.items.WandItem;
import net.arcanamod.items.recipes.ArcanaRecipes;
import net.arcanamod.network.Connection;
import net.arcanamod.systems.research.*;
import net.arcanamod.systems.taint.Taint;
import net.arcanamod.util.AuthorisationManager;
import net.arcanamod.world.NodeType;
import net.arcanamod.world.WorldInteractionsRegistry;
import net.arcanamod.worldgen.ArcanaBiomes;
import net.arcanamod.worldgen.ArcanaFeatures;
import net.minecraft.commands.synchronization.ArgumentSerializer;
import net.minecraft.commands.synchronization.ArgumentTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.StartupMessageManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.util.Map;

/**
 * Base Arcana Class
 */
@Mod(Arcana.MODID)
public class Arcana{
	
	// Main
	public static final String MODID = "arcana";
	public static final Logger LOGGER = LogManager.getLogger("Arcana");
	public static Arcana instance;
	public static AuthorisationManager authManager;
	
	// Json Registry
	public static ResearchLoader researchManager;
	public static ItemAspectRegistry itemAspectRegistry;
	public static WorldInteractionsRegistry worldInteractionsRegistry;
	
	// Creative Mode Tabs
	public static CreativeModeTab ITEMS = new SupplierItemGroup(MODID,
			() -> new ItemStack(ArcanaBlocks.ARCANE_STONE.get()))
			.setHasSearchBar(true)
			.setBackgroundImage(new ResourceLocation("textures/gui/container/creative_inventory/tab_item_search.png"));
	public static CreativeModeTab TAINT = new SupplierItemGroup("taint",
			() -> new ItemStack(ArcanaBlocks.TAINTED_GRASS_BLOCK.get()));
	
	// Proxy
	public static CommonProxy proxy = DistExecutor.safeRunForDist(
			() -> ClientProxy::new,
			() -> CommonProxy::new
	);
	
	// Debug Mode
	public static final boolean debug = true;
	
	public Arcana(){
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		bus.addListener(this::setup);
		bus.addListener(this::enqueueIMC);
		bus.addListener(this::processIMC);
		bus.addListener(this::setupClient);
		
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ArcanaConfig.COMMON_SPEC);
		
		// deferred registry registration
		NodeType.init();
		Aspects.init();
		
		ArcanaBlocks.BLOCKS.register(bus);
		ArcanaEntities.ENTITY_TYPES.register(bus);
		ArcanaEntities.T_ENTITY_TYPES.register(bus);
		ArcanaItems.ITEMS.register(bus);
		ArcanaEffects.EFFECTS.register(bus);
		ArcanaRecipes.Serializers.SERIALIZERS.register(bus);
		ArcanaTiles.TES.register(bus);
		ArcanaContainers.CON.register(bus);
		ArcanaFeatures.FEATURES.register(bus);
		ArcanaFeatures.FOLAIGE_PLACERS.register(bus);
		ArcanaBiomes.BIOMES.register(bus);
		ArcanaFluids.FLUIDS.register(bus);
		
		MinecraftForge.EVENT_BUS.addListener(WorldLoadEvent::serverAboutToStart);
		MinecraftForge.EVENT_BUS.addListener(this::toolInteractionEvent);
		
		proxy.construct();
	}

	// Arcana Strippables
	protected static final Map<Block, Block> ARCANA_STRIPPABLES = (new ImmutableMap.Builder<Block, Block>())
			.put(ArcanaBlocks.SILVERWOOD_WOOD.get(), ArcanaBlocks.STRIPPED_SILVERWOOD_WOOD.get())
			.put(ArcanaBlocks.SILVERWOOD_LOG.get(), ArcanaBlocks.STRIPPED_SILVERWOOD_LOG.get())
			.put(ArcanaBlocks.DAIR_WOOD.get(), ArcanaBlocks.STRIPPED_DAIR_WOOD.get())
			.put(ArcanaBlocks.DAIR_LOG.get(), ArcanaBlocks.STRIPPED_DAIR_LOG.get())
			.put(ArcanaBlocks.GREATWOOD_WOOD.get(), ArcanaBlocks.STRIPPED_GREATWOOD_WOOD.get())
			.put(ArcanaBlocks.GREATWOOD_LOG.get(), ArcanaBlocks.STRIPPED_GREATWOOD_LOG.get())
			.put(ArcanaBlocks.EUCALYPTUS_WOOD.get(), ArcanaBlocks.STRIPPED_EUCALYPTUS_WOOD.get())
			.put(ArcanaBlocks.EUCALYPTUS_LOG.get(), ArcanaBlocks.STRIPPED_EUCALYPTUS_LOG.get())
			.put(ArcanaBlocks.HAWTHORN_WOOD.get(), ArcanaBlocks.STRIPPED_HAWTHORN_WOOD.get())
			.put(ArcanaBlocks.HAWTHORN_LOG.get(), ArcanaBlocks.STRIPPED_HAWTHORN_LOG.get())
			.put(ArcanaBlocks.WILLOW_WOOD.get(), ArcanaBlocks.STRIPPED_WILLOW_WOOD.get())
			.put(ArcanaBlocks.WILLOW_LOG.get(), ArcanaBlocks.STRIPPED_WILLOW_LOG.get())

//			.put(ArcanaBlocks.TAINTED_SILVERWOOD_WOOD.get(), ArcanaBlocks.TAINTED_STRIPPED_SILVERWOOD_WOOD.get())
//			.put(ArcanaBlocks.TAINTED_SILVERWOOD_LOG.get(), ArcanaBlocks.TAINTED_STRIPPED_SILVERWOOD_LOG.get())
			.put(ArcanaBlocks.TAINTED_DAIR_WOOD.get(), ArcanaBlocks.TAINTED_STRIPPED_DAIR_WOOD.get())
			.put(ArcanaBlocks.TAINTED_DAIR_LOG.get(), ArcanaBlocks.TAINTED_STRIPPED_DAIR_LOG.get())
			.put(ArcanaBlocks.TAINTED_GREATWOOD_WOOD.get(), ArcanaBlocks.TAINTED_STRIPPED_GREATWOOD_WOOD.get())
			.put(ArcanaBlocks.TAINTED_GREATWOOD_LOG.get(), ArcanaBlocks.TAINTED_STRIPPED_GREATWOOD_LOG.get())
			.put(ArcanaBlocks.TAINTED_EUCALYPTUS_WOOD.get(), ArcanaBlocks.TAINTED_STRIPPED_EUCALYPTUS_WOOD.get())
			.put(ArcanaBlocks.TAINTED_EUCALYPTUS_LOG.get(), ArcanaBlocks.TAINTED_STRIPPED_EUCALYPTUS_LOG.get())
			.put(ArcanaBlocks.TAINTED_HAWTHORN_WOOD.get(), ArcanaBlocks.TAINTED_STRIPPED_HAWTHORN_WOOD.get())
			.put(ArcanaBlocks.TAINTED_HAWTHORN_LOG.get(), ArcanaBlocks.TAINTED_STRIPPED_HAWTHORN_LOG.get())
			.put(ArcanaBlocks.TAINTED_WILLOW_WOOD.get(), ArcanaBlocks.TAINTED_STRIPPED_WILLOW_WOOD.get())
			.put(ArcanaBlocks.TAINTED_WILLOW_LOG.get(), ArcanaBlocks.TAINTED_STRIPPED_WILLOW_LOG.get())

			.build();

	public void toolInteractionEvent(BlockEvent.BlockToolInteractEvent event){
		if (event.getHeldItemStack().getItem() instanceof AxeItem){
			BlockState originalState = event.getState();
			Block block = ARCANA_STRIPPABLES.get(originalState.getBlock());
			event.setFinalState(block != null ? block.defaultBlockState().setValue(RotatedPillarBlock.AXIS, originalState.getValue(RotatedPillarBlock.AXIS)) : null);

		/*
		if (event.getToolType() == ToolType.AXE){
			if (event.getState().getBlock()==ArcanaBlocks.SILVERWOOD_LOG.get())
				event.setFinalState(ArcanaBlocks.STRIPPED_SILVERWOOD_LOG.get().getDefaultState().with(AXIS, event.getState().get(AXIS)));
			if (event.getState().getBlock()==ArcanaBlocks.DAIR_LOG.get())
				event.setFinalState(ArcanaBlocks.STRIPPED_DAIR_LOG.get().getDefaultState().with(AXIS, event.getState().get(AXIS)));
			if (event.getState().getBlock()==ArcanaBlocks.GREATWOOD_LOG.get())
				event.setFinalState(ArcanaBlocks.STRIPPED_GREATWOOD_LOG.get().getDefaultState().with(AXIS, event.getState().get(AXIS)));
			if (event.getState().getBlock()==ArcanaBlocks.EUCALYPTUS_LOG.get())
				event.setFinalState(ArcanaBlocks.STRIPPED_EUCALYPTUS_LOG.get().getDefaultState().with(AXIS, event.getState().get(AXIS)));
			if (event.getState().getBlock()==ArcanaBlocks.HAWTHORN_LOG.get())
				event.setFinalState(ArcanaBlocks.STRIPPED_HAWTHORN_LOG.get().getDefaultState().with(AXIS, event.getState().get(AXIS)));
			if (event.getState().getBlock()==ArcanaBlocks.WILLOW_LOG.get())
				event.setFinalState(ArcanaBlocks.STRIPPED_WILLOW_LOG.get().getDefaultState().with(AXIS, event.getState().get(AXIS)));

			if (event.getState().getBlock()==ArcanaBlocks.TAINTED_DAIR_LOG.get())
				event.setFinalState(ArcanaBlocks.TAINTED_STRIPPED_DAIR_LOG.get().getDefaultState().with(AXIS, event.getState().get(AXIS)));
			if (event.getState().getBlock()==ArcanaBlocks.TAINTED_GREATWOOD_LOG.get())
				event.setFinalState(ArcanaBlocks.TAINTED_STRIPPED_GREATWOOD_LOG.get().getDefaultState().with(AXIS, event.getState().get(AXIS)));
			if (event.getState().getBlock()==ArcanaBlocks.TAINTED_EUCALYPTUS_LOG.get())
				event.setFinalState(ArcanaBlocks.TAINTED_STRIPPED_EUCALYPTUS_LOG.get().getDefaultState().with(AXIS, event.getState().get(AXIS)));
			if (event.getState().getBlock()==ArcanaBlocks.TAINTED_HAWTHORN_LOG.get())
				event.setFinalState(ArcanaBlocks.TAINTED_STRIPPED_HAWTHORN_LOG.get().getDefaultState().with(AXIS, event.getState().get(AXIS)));
			if (event.getState().getBlock()==ArcanaBlocks.TAINTED_WILLOW_LOG.get())
				event.setFinalState(ArcanaBlocks.TAINTED_STRIPPED_WILLOW_LOG.get().getDefaultState().with(AXIS, event.getState().get(AXIS)));

			if (event.getState().getBlock()==ArcanaBlocks.SILVERWOOD_WOOD.get())
				event.setFinalState(ArcanaBlocks.STRIPPED_SILVERWOOD_WOOD.get().getDefaultState());
			if (event.getState().getBlock()==ArcanaBlocks.DAIR_WOOD.get())
				event.setFinalState(ArcanaBlocks.STRIPPED_DAIR_WOOD.get().getDefaultState());
			if (event.getState().getBlock()==ArcanaBlocks.GREATWOOD_WOOD.get())
				event.setFinalState(ArcanaBlocks.STRIPPED_GREATWOOD_WOOD.get().getDefaultState());
			if (event.getState().getBlock()==ArcanaBlocks.EUCALYPTUS_WOOD.get())
				event.setFinalState(ArcanaBlocks.STRIPPED_EUCALYPTUS_WOOD.get().getDefaultState());
			if (event.getState().getBlock()==ArcanaBlocks.HAWTHORN_WOOD.get())
				event.setFinalState(ArcanaBlocks.STRIPPED_HAWTHORN_WOOD.get().getDefaultState());
			if (event.getState().getBlock()==ArcanaBlocks.WILLOW_WOOD.get())
				event.setFinalState(ArcanaBlocks.STRIPPED_WILLOW_WOOD.get().getDefaultState());

			if (event.getState().getBlock()==ArcanaBlocks.TAINTED_DAIR_WOOD.get())
				event.setFinalState(ArcanaBlocks.TAINTED_STRIPPED_DAIR_WOOD.get().getDefaultState());
			if (event.getState().getBlock()==ArcanaBlocks.TAINTED_GREATWOOD_WOOD.get())
				event.setFinalState(ArcanaBlocks.TAINTED_STRIPPED_GREATWOOD_WOOD.get().getDefaultState());
			if (event.getState().getBlock()==ArcanaBlocks.TAINTED_EUCALYPTUS_WOOD.get())
				event.setFinalState(ArcanaBlocks.TAINTED_STRIPPED_EUCALYPTUS_WOOD.get().getDefaultState());
			if (event.getState().getBlock()==ArcanaBlocks.TAINTED_HAWTHORN_WOOD.get())
				event.setFinalState(ArcanaBlocks.TAINTED_STRIPPED_HAWTHORN_WOOD.get().getDefaultState());
			if (event.getState().getBlock()==ArcanaBlocks.TAINTED_WILLOW_WOOD.get())
				event.setFinalState(ArcanaBlocks.TAINTED_STRIPPED_WILLOW_WOOD.get().getDefaultState());
			 */
		}
	}
	
	public static ResourceLocation arcLoc(String path){
		return new ResourceLocation(MODID, path);
	}
	
	private void setup(FMLCommonSetupEvent event){
		authManager = new AuthorisationManager();
		
		// init, init, init, init, init, init, init, init
		EntrySection.init();
		Requirement.init();
		ResearcherCapability.init();
		AspectHandlerCapability.init();
		AuraChunkCapability.init();
		TaintTrackableCapability.init();
		Puzzle.init();
		Taint.init();
		BackgroundLayer.init();
		StartupMessageManager.addModMessage("Arcana: Research registration completed");
		
		// register nodes as an argument
		ArgumentTypes.register("node_argument", NodeArgument.class, new ArgumentSerializer<>() {
			@Override
			public void serializeToNetwork(NodeArgument pArgument, FriendlyByteBuf pBuffer) {

			}

			@Override
			public NodeArgument deserializeFromNetwork(FriendlyByteBuf pBuffer) {
				return new NodeArgument();
			}

			@Override
			public void serializeToJson(NodeArgument pArgument, JsonObject pJson) {

			}
		});
		
		proxy.preInit(event);
		
		Connection.init();
		
		// dispenser behaviour for wand conversion
		DispenserBlock.registerBehavior(ArcanaItems.WAND.get(), new DispenseItemBehavior(){
			private final DefaultDispenseItemBehavior defaultDispenseItemBehavior = new DefaultDispenseItemBehavior();
			@Nonnull
			public ItemStack dispense(@Nonnull BlockSource source, @Nonnull ItemStack stack){
				ServerLevel level = source.getLevel();
//				Position position = DispenserBlock.getDispensePosition(source);
				BlockPos pos = source.getPos().relative(source.getBlockState().getValue(DispenserBlock.FACING));
				InteractionResult convert = WandItem.convert(level, pos, null);
				if(convert.consumesAction()){
					//successful = true;
					return stack;
				}else
					return this.defaultDispenseItemBehavior.dispense(source, stack);
			}
		});
		// TODO: replace this all with standard vis transfer code.
		DispenserBlock.registerBehavior(ArcanaItems.PHIAL.get(), new DispenseItemBehavior(){
			private final DefaultDispenseItemBehavior defaultDispenseItemBehavior = new DefaultDispenseItemBehavior();
			@Nonnull
			public ItemStack dispense(@Nonnull BlockSource source, @Nonnull ItemStack stack){
				ServerLevel level = source.getLevel();
				BlockPos pos = source.getPos().relative(source.getBlockState().getValue(DispenserBlock.FACING));
				BlockState state = level.getBlockState(pos);
				BlockEntity tile = level.getBlockEntity(pos);
				DispenserBlockEntity dispenser = source.getEntity();
				if(tile != null){
					LazyOptional<AspectHandler> cap = tile.getCapability(AspectHandlerCapability.ASPECT_HANDLER);
					if(cap.isPresent()){
						AspectHandler tileHandle = cap.orElse(null);
						AspectHolder myHandle = AspectHandler.getFrom(stack).getHolder(0);
						if(myHandle.getStack().getAmount() <= 0){
							for(AspectHolder holder : tileHandle.getHolders())
								if(holder.getStack().getAmount() > 0){
									float min = Math.min(holder.getStack().getAmount(), 8);
									Aspect aspect = holder.getStack().getAspect();
									ItemStack phialItemStack = new ItemStack(ArcanaItems.PHIAL.get());
									AspectHandler.getFrom(phialItemStack).insert(aspect, min);//.insert(0, new AspectStack(aspect, min), false);
									if(phialItemStack.getTag() == null)
										phialItemStack.setTag(phialItemStack.getShareTag());
									stack.shrink(1);
									if(!stack.isEmpty())
										if(dispenser.addItem(stack) == -1){
											/*
											Direction direction = source.getBlockState().getValue(DispenserBlock.FACING);
											Position position = DispenserBlock.getDispensePosition(source);
											spawnItem(source.getLevel(), stack, 6, direction, position);
											*/
											this.defaultDispenseItemBehavior.dispense(source, stack);
										}
									holder.drain(min, false);
									//successful = true;
									level.sendBlockUpdated(pos, state, state, 2);
									return phialItemStack;
								}
						}else{
							for(AspectHolder holder : tileHandle.getHolders())
								if((holder.getCapacity() - holder.getStack().getAmount() > 0 || holder.voids()) && (holder.getStack().getAspect() == myHandle.getStack().getAspect() || holder.getStack().getAspect() == Aspects.EMPTY)){
									float inserted = holder.insert(myHandle.getStack().getAmount(), false);
									if(inserted != 0){
										ItemStack newPhial = new ItemStack(ArcanaItems.PHIAL.get(), 1);
										AspectHolder oldHolder = AspectHandler.getFrom(stack).getHolder(0);
										AspectHolder newHolder = AspectHandler.getFrom(newPhial).getHolder(0);
										newHolder.insert(inserted, false);
										newPhial.setTag(newPhial.getShareTag());
										level.sendBlockUpdated(pos, state, state, 2);
										return newPhial;
									}else{
										level.sendBlockUpdated(pos, state, state, 2);
										stack.shrink(1);
										if(!stack.isEmpty())
											if(dispenser.addItem(stack) == -1){
												/*
												Direction direction = source.getBlockState().get(DispenserBlock.FACING);
												IPosition iposition = DispenserBlock.getDispensePosition(source);
												doDispense(source.getWorld(), stack, 6, direction, iposition);
												*/
												this.defaultDispenseItemBehavior.dispense(source, stack);
											}
										return new ItemStack(ArcanaItems.PHIAL.get());
									}
								}
						}
					}
				}
				return this.defaultDispenseItemBehavior.dispense(source, stack);
			}
		});
		
		//FeatureGenerator.setupFeatureGeneration();
	}

	private void setupClient(FMLClientSetupEvent event){
		// Moved to Client Proxy
	}
	
	private void enqueueIMC(InterModEnqueueEvent event){
		// tell curios or whatever about our baubles
	}
	
	private void processIMC(InterModProcessEvent event){
		// handle aspect registration from addons?
	}
}