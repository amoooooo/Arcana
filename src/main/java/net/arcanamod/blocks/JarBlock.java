package net.arcanamod.blocks;

import net.arcanamod.ArcanaConfig;
import net.arcanamod.aspects.AspectLabel;
import net.arcanamod.aspects.AspectUtils;
import net.arcanamod.aspects.Aspects;
import net.arcanamod.blocks.bases.WaterloggableBlock;
import net.arcanamod.blocks.pipes.TubeBlock;
import net.arcanamod.blocks.tiles.JarBlockEntity;
import net.arcanamod.items.ArcanaItems;
import net.arcanamod.items.MagicDeviceItem;
import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("deprecation")
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class JarBlock extends WaterloggableBlock implements EntityBlock {
	public static final BooleanProperty UP = BooleanProperty.create("up");
	private Type type;

	public JarBlock(Properties properties, Type type){
		super(properties);
		this.type = type;
		this.registerDefaultState(this.getStateDefinition().any()
				.setValue(UP, Boolean.FALSE)
				.setValue(WATERLOGGED, Boolean.FALSE));
	}
	
	public VoxelShape SHAPE = Block.box(3, 0, 3, 13, 14, 13);
	
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context){
		return SHAPE;
	}
	
	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context){
		return SHAPE;
	}
	
//	@Override
//	public boolean hasTileEntity(BlockState state){
//		return true;
//	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state){
		return new JarBlockEntity(this.type, pos, state);
	}
	
	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder){
		super.createBlockStateDefinition(builder);
		builder.add(UP);
	}
	
	public BlockState getStateForPlacement(BlockPlaceContext context){
		return super.getStateForPlacement(context)
				.setValue(UP, false);
	}

	@Override
	public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving){
		if(worldIn.getBlockState(pos.above()).getBlock() instanceof TubeBlock)
			worldIn.setBlockAndUpdate(pos, state.setValue(UP, true));
		else
			worldIn.setBlockAndUpdate(pos, state.setValue(UP, false));
	}
	
	@Override
	public void onPlace(BlockState state, Level worldIn, BlockPos pos, BlockState oldState, boolean isMoving){
		if(worldIn.getBlockState(pos.above()).getBlock() instanceof TubeBlock)
			worldIn.setBlockAndUpdate(pos, state.setValue(UP, true));
		else
			worldIn.setBlockAndUpdate(pos, state.setValue(UP, false));
	}

	@Override
	public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		if (placer != null && ((JarBlockEntity) Objects.requireNonNull(worldIn.getBlockEntity(pos))).label != null) {
			((JarBlockEntity) Objects.requireNonNull(worldIn.getBlockEntity(pos))).label.direction = getYaw(placer);
		}
		super.setPlacedBy(worldIn, pos, state, placer, stack);
	}

	@Override
	public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
		JarBlockEntity jar = ((JarBlockEntity) Objects.requireNonNull(worldIn.getBlockEntity(pos)));
		if (jar.label == null && player.getItemInHand(handIn).getItem() == ArcanaItems.LABEL.get()) {
			if (!player.isCreative()) {
				player.getItemInHand(handIn).setCount(player.getItemInHand(handIn).getCount() - 1);
			}
			if (hit.getDirection() != Direction.UP && hit.getDirection() != Direction.DOWN) {
				jar.label = new AspectLabel(hit.getDirection());
			} else {
				jar.label = new AspectLabel(getYaw(player));
			}
		} else if (player.getItemInHand(handIn).getItem() instanceof MagicDeviceItem && player.isCrouching()) {
			playerWillDestroy(worldIn, pos, state, player);
			worldIn.removeBlock(pos, false);
		} else if (jar.label != null && player.getItemInHand(handIn).getItem() == Blocks.AIR.asItem() && player.isCrouching()) {
			if (!player.isCreative()) {
				if (!player.addItem(new ItemStack(ArcanaItems.LABEL.get()))) {
					ItemEntity itementity = new ItemEntity(worldIn,
							player.getX(),
							player.getY(),
							player.getZ(), new ItemStack(ArcanaItems.LABEL.get()));
					itementity.setNoPickUpDelay();
					worldIn.addFreshEntity(itementity);
				}
			}
			jar.label = null;
		} else if (jar.label != null && player.getItemInHand(handIn).getItem() instanceof MagicDeviceItem) {
			if (hit.getDirection() != Direction.UP && hit.getDirection() != Direction.DOWN) {
				jar.label.direction = hit.getDirection();
			} else {
				jar.label.direction = getYaw(player);
			}
			jar.label.seal = Aspects.EMPTY;
		}
		return super.use(state, worldIn, pos, player, handIn, hit);
	}

	@Override
	public void playerDestroy(Level worldIn, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity te, ItemStack stack) {
		if (te instanceof JarBlockEntity) {
			JarBlockEntity jte = (JarBlockEntity) te;
			if (!worldIn.isClientSide && jte.vis.getHolder(0).getStack().getAmount() == 0 && jte.label == null) {
				// te.setPos(new BlockPos(0, 0, 0));
				if (((JarBlockEntity) te).label != null) {
					((JarBlockEntity) te).label.direction = Direction.NORTH;
				}
				dropResources(state, worldIn, pos, te, player, stack);
			}
		}
	}

	@Override
	public void playerWillDestroy(Level worldIn, BlockPos pos, BlockState state, Player player) {
		if (!player.isCreative()) {
			BlockEntity te = worldIn.getBlockEntity(pos);
			if (te instanceof JarBlockEntity) {
				JarBlockEntity jte = (JarBlockEntity) te;
				if (!worldIn.isClientSide && jte.vis.getHolder(0).getStack().getAmount() != 0 || jte.label != null){
//					te.setPos(new BlockPos(0, 0, 0));
					if (((JarBlockEntity) te).label != null) {
						((JarBlockEntity) te).label.direction = Direction.NORTH;
					}
					ItemEntity itementity = new ItemEntity(worldIn, pos.getX(), pos.getY(), pos.getZ(), getItem(worldIn, pos, state));
					itementity.setDefaultPickUpDelay();
					worldIn.addFreshEntity(itementity);
				}
			}
		}
		super.playerWillDestroy(worldIn, pos, state, player);
	}

	public static Direction getYaw(LivingEntity player) {
		int yaw = (int)player.getYHeadRot();
		if (yaw<0)              //due to the yaw running a -360 to positive 360
			yaw+=360;    //not sure why it's that way
		yaw+=22;    //centers coordinates you may want to drop this line
		yaw%=360;  //and this one if you want a strict interpretation of the zones
		int facing = yaw/45;  //  360degrees divided by 45 == 8 zones
		switch (facing){
			case 0: case 1:
				return Direction.NORTH;
			case 2: case 3:
				return Direction.EAST;
			case 4: case 5:
				return Direction.SOUTH;
			default:
				return Direction.WEST;
		}
	}

	public ItemStack getItem(BlockGetter worldIn, BlockPos pos, BlockState state){
		ItemStack itemstack = super.getCloneItemStack(worldIn, pos, state);
		JarBlockEntity jarTe = (JarBlockEntity) worldIn.getBlockEntity(pos);
		CompoundTag compoundnbt = new CompoundTag();
		jarTe.saveAdditional(compoundnbt);
		if(!compoundnbt.isEmpty())
			itemstack.setTag(compoundnbt);
		
		return itemstack;
	}
	
	public boolean hasAnalogOutputSignal(BlockState state){
		return true;
	}
	
	public int getAnalogOutputSignal(BlockState block, Level world, BlockPos pos){
		BlockEntity te = world.getBlockEntity(pos);
		if(te instanceof JarBlockEntity){
			JarBlockEntity jar = (JarBlockEntity) te;
			return (int)Math.ceil((jar.vis.getHolder(0).getStack().getAmount() / 100f) * 15);
		}
		return 0;
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable BlockGetter worldIn, List<Component> tooltip, TooltipFlag flagIn) {
		super.appendHoverText(stack, worldIn, tooltip, flagIn);
		if (stack.getTag() != null)
			if(!stack.getTag().isEmpty()) {
				CompoundTag cell = stack.getTag().getCompound("BlockEntityTag").getCompound("aspects").getCompound("cells").getCompound("cell_0");
				if (stack.getTag().getCompound("BlockEntityTag").contains("label")) {
					tooltip.add(new TextComponent("Labelled").setStyle(Style.EMPTY.withColor(ChatFormatting.DARK_GRAY)));
				}
				if (cell.getInt("amount") > 0) {
					tooltip.add(new TextComponent(AspectUtils.getLocalizedAspectDisplayName(Objects.requireNonNull(
							AspectUtils.getAspectByName(cell.getString("aspect")))) + ": " +
							cell.getInt("amount")).setStyle(Style.EMPTY.withColor(ChatFormatting.AQUA)));
				}
				if (ArcanaConfig.JAR_ANIMATION_SPEED.get()>=299792458D){ // Small easter egg ;)
					tooltip.add(new TextComponent("\"being faster than light leaves you in the darkness\" -jar").setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)));
				}
			}
	}

	public enum Type{
		BASIC,
		SECURED,
		VOID,
		VACUUM,
		PRESSURE
	}
}