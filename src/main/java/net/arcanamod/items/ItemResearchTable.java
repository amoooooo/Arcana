package net.arcanamod.items;

import mcp.MethodsReturnNonnullByDefault;
import net.arcanamod.aspects.VisBattery;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class ItemResearchTable extends Item{
	
	public ItemResearchTable(Properties properties){
		super(properties);
		//setMaxDamage(0);
		//setMaxStackSize(1);
	}
	
	@Nullable
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt){
		VisBattery battery = new VisBattery();
		CompoundNBT compound = stack.getTag();
		if(compound != null && compound.hasUniqueId("BlockEntityTag"))
			battery.deserializeNBT(compound.getCompound("BlockEntityTag").getCompound("Aspects"));
		return battery;
	}
	
	/*@Nullable
	public CompoundNBT getNBTShareTag(ItemStack stack){
		CompoundNBT tag = super.getNBTShareTag(stack);
		if(tag != null && tag.hasKey("BlockEntityTag")){
			CompoundNBT aspectNBT = stack.getCapability(VisHandlerCapability.ASPECT_HANDLER, null).serializeNBT();
			tag.getCompoundTag("BlockEntityTag").setTag("Aspects", aspectNBT);
		}
		return tag;
	}
	
	public void readNBTShareTag(ItemStack stack, @Nullable CompoundNBT nbt){
		super.readNBTShareTag(stack, nbt);
		if(nbt != null)
			stack.getCapability(VisHandlerCapability.ASPECT_HANDLER, null).deserializeNBT(nbt.getCompoundTag("BlockEntityTag").getCompoundTag("Aspects"));
	}*/
	
	/*public ActionResultType onItemUse(PlayerEntity player, World world, BlockPos pos, Hand hand, Direction facing, float hitX, float hitY, float hitZ){
		if(world.isRemote)
			return ActionResultType.SUCCESS;
		
		if(!world.getBlockState(pos).getBlock().isReplaceable(world, pos))
			pos = pos.up();
		
		Direction direction = player.getHorizontalFacing();
		BlockPos blockpos = pos.offset(direction.rotateY());
		ItemStack itemStack = player.getHeldItem(hand);
		if(player.canPlayerEdit(pos, facing, itemStack) && player.canPlayerEdit(blockpos, facing, itemStack)){
			boolean canReplaceMain = world.getBlockState(pos).getBlock().isReplaceable(world, pos) || world.getBlockState(pos).getBlock().isAir(world.getBlockState(pos), world, pos);
			boolean canReplaceSecond = world.getBlockState(blockpos).getBlock().isReplaceable(world, blockpos) || world.getBlockState(blockpos).getBlock().isAir(world.getBlockState(blockpos), world, blockpos);
			if(canReplaceMain && canReplaceSecond){
				BlockState state = ArcanaBlocks.RESEARCH_TABLE.getStateForPlacement(world, pos, direction, hitX, hitY, hitZ, 0, player, hand);
				world.setBlockState(pos, state, 10);
				world.setBlockState(blockpos, state.withProperty(PART, RIGHT), 10);
				
				// play sound
				SoundType soundtype = state.getBlock().getSoundType(state, world, pos, player);
				world.playSound(null, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
				
				// update world
				// this should be updated to follow ItemBed, I don't think this will cause problems but still
				world.notifyNeighborsRespectDebug(pos, state.getBlock(), false);
				world.notifyNeighborsRespectDebug(blockpos, state.getBlock(), false);
				
				if(player instanceof ServerPlayerEntity)
					CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayerEntity)player, pos, itemStack);
				
				BlockItem.setTileEntityNBT(world, player, pos, itemStack);
				
				itemStack.shrink(1);
				return ActionResultType.SUCCESS;
			}
		}
		return ActionResultType.FAIL;
	}
	
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag){
		CompoundNBT compound = stack.getTagCompound();
		if(compound != null && compound.hasKey("BlockEntityTag")){
			CompoundNBT data = compound.getCompoundTag("BlockEntityTag");
			ItemStackHandler items = new ItemStackHandler(3);
			items.deserializeNBT(data.getCompoundTag("Items"));
			if(!items.getStackInSlot(0).isEmpty())
				tooltip.add("Wand: " + items.getStackInSlot(0).getDisplayName());
			if(!items.getStackInSlot(1).isEmpty())
				tooltip.add("Ink: " + items.getStackInSlot(1).getDisplayName());
			if(!items.getStackInSlot(2).isEmpty())
				tooltip.add("Note: " + items.getStackInSlot(2).getDisplayName());
			if(!data.getCompoundTag("Aspects").hasNoTags())
				tooltip.add("Contains vis");
		}
	}*/
}