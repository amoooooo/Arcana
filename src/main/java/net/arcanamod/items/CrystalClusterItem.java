package net.arcanamod.items;

import mcp.MethodsReturnNonnullByDefault;
import net.arcanamod.blocks.CrystalClusterBlock;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.core.BlockPos;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.World;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

/**
 * Copy of BlockItem, but allows specifying growth stage & uses different render layer.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class CrystalClusterItem extends Item {
	
	private final Block block;
	private int stage;
	
	public CrystalClusterItem(Block block, Item.Properties builder, int stage){
		super(builder);
		this.block = block;
		this.stage = stage;
	}
	
	/**
	 * Called when this item is used when targetting a Block
	 */
	public InteractionResult useOn(UseOnContext context){
		InteractionResult actionresulttype = this.tryPlace(new BlockPlaceContext(context));
		return actionresulttype != InteractionResult.SUCCESS && this.getFoodProperties() != null ? this.use(context.getLevel(), context.getPlayer(), context.getHand()).getResult() : actionresulttype;
	}
	
	public InteractionResult tryPlace(BlockPlaceContext context){
		if(!context.canPlace())
			return InteractionResult.FAIL;
		else{
			BlockPlaceContext ctx = getBlockItemUseContext(context);
			if(ctx == null)
				return InteractionResult.FAIL;
			else{
				BlockState blockstate = getStateForPlacement(ctx);
				if(blockstate == null)
					return InteractionResult.FAIL;
				else if(!placeBlock(ctx, blockstate.setValue(CrystalClusterBlock.AGE, stage)))
					return InteractionResult.FAIL;
				else{
					BlockPos blockpos = ctx.getClickedPos();
					Level world = ctx.getLevel();
					Player playerentity = ctx.getPlayer();
					ItemStack itemstack = ctx.getItemInHand();
					BlockState blockstate1 = world.getBlockState(blockpos);
					Block block = blockstate1.getBlock();
					if(block == blockstate.getBlock()){
						blockstate1 = func_219985_a(blockpos, world, itemstack, blockstate1);
						onBlockPlaced(blockpos, world, playerentity, itemstack, blockstate1);
						block.setPlacedBy(world, blockpos, blockstate1, playerentity, itemstack);
						if(playerentity instanceof ServerPlayer)
							CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayer)playerentity, blockpos, itemstack);
					}
					
					SoundType soundtype = blockstate1.getSoundType(world, blockpos, context.getPlayer());
					world.playSound(playerentity, blockpos, this.getPlaceSound(blockstate1, world, blockpos, context.getPlayer()), SoundSource.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
					itemstack.shrink(1);
					return InteractionResult.SUCCESS;
				}
			}
		}
	}
	
	protected SoundEvent getPlaceSound(BlockState state, Level world, BlockPos pos, Player entity){
		return state.getSoundType(world, pos, entity).getPlaceSound();
	}
	
	@Nullable
	public BlockPlaceContext getBlockItemUseContext(BlockPlaceContext context){
		return context;
	}
	
	protected boolean onBlockPlaced(BlockPos pos, Level worldIn, @Nullable Player player, ItemStack stack, BlockState state){
		return setTileEntityNBT(worldIn, player, pos, stack);
	}
	
	@Nullable
	protected BlockState getStateForPlacement(BlockPlaceContext context){
		BlockState blockstate = this.getBlock().getStateForPlacement(context);
		return blockstate != null && this.canPlace(context, blockstate) ? blockstate : null;
	}
	
	private BlockState func_219985_a(BlockPos pos, Level world, ItemStack stack, BlockState state){
		BlockState blockstate = state;
		CompoundTag compoundnbt = stack.getTag();
		if(compoundnbt != null){
			CompoundTag tag = compoundnbt.getCompound("BlockStateTag");
			StateDefinition<Block, BlockState> statecontainer = state.getBlock().getStateDefinition();
			
			for(String s : tag.getAllKeys()){
				Property<?> prop = statecontainer.getProperty(s);
				if(prop != null){
					String s1 = tag.get(s).getAsString();
					blockstate = func_219988_a(blockstate, prop, s1);
				}
			}
		}
		
		if(blockstate != state)
			world.setBlock(pos, blockstate, 2);
		
		return blockstate;
	}
	
	private static <T extends Comparable<T>> BlockState func_219988_a(BlockState state, Property<T> prop, String str){
		return prop.parseValue(str).map((val) -> state.setValue(prop, val)).orElse(state);
	}
	
	protected boolean canPlace(BlockPlaceContext p_195944_1_, BlockState p_195944_2_){
		Player playerentity = p_195944_1_.getPlayer();
		CollisionContext iselectioncontext = playerentity == null ? CollisionContext.empty() : CollisionContext.of(playerentity);
		return (!this.checkPosition() || p_195944_2_.canSurvive(p_195944_1_.getLevel(), p_195944_1_.getClickedPos())) && p_195944_1_.getLevel().isUnobstructed(p_195944_2_, p_195944_1_.getClickedPos(), iselectioncontext);
	}
	
	protected boolean checkPosition(){
		return true;
	}
	
	protected boolean placeBlock(BlockPlaceContext context, BlockState state){
		return context.getLevel().setBlock(context.getClickedPos(), state, 11);
	}
	
	public static boolean setTileEntityNBT(Level worldIn, @Nullable Player player, BlockPos pos, ItemStack stackIn){
		MinecraftServer minecraftserver = worldIn.getServer();
		if(minecraftserver != null){
			CompoundTag compoundnbt = stackIn.getTagElement("BlockEntityTag");
			if(compoundnbt != null){
				BlockEntity tileentity = worldIn.getBlockEntity(pos);
				if(tileentity != null){
					if(!worldIn.isClientSide() && tileentity.onlyOpCanSetNbt() && (player == null || !player.canUseGameMasterBlocks()))
						return false;
					
					CompoundTag compoundnbt1 = tileentity.getTileData();
					CompoundTag compoundnbt2 = compoundnbt1.copy();
					compoundnbt1.merge(compoundnbt);
					compoundnbt1.putInt("x", pos.getX());
					compoundnbt1.putInt("y", pos.getY());
					compoundnbt1.putInt("z", pos.getZ());
					if(!compoundnbt1.equals(compoundnbt2)){
						tileentity.read(tileentity.getBlockState(), compoundnbt1);
						tileentity.setChanged();
						return true;
					}
				}
			}
			
		}
		return false;
	}
	
	public String toString(){
		return stage == 3 ? getBlock().toString() : super.toString();
	}
	
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn){
		super.appendHoverText(stack, worldIn, tooltip, flagIn);
		getBlock().appendHoverText(stack, worldIn, tooltip, flagIn);
	}
	
	public Block getBlock(){
		return getBlockRaw().delegate.get();
	}
	
	private Block getBlockRaw(){
		return block;
	}
}