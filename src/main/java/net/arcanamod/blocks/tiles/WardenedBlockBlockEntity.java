package net.arcanamod.blocks.tiles;

import net.arcanamod.blocks.ArcanaBlocks;
import net.arcanamod.items.MagicDeviceItem;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNullableByDefault;
import java.util.Objects;
import java.util.Optional;

@ParametersAreNullableByDefault
@MethodsReturnNonnullByDefault
@SuppressWarnings({"OptionalUsedAsFieldOrParameterType", "RedundantTypeArguments", "unused"})
public class WardenedBlockBlockEntity extends BlockEntity {
	private Optional<BlockState> copyState = null;
	private Boolean holdingSpell = false;

	public WardenedBlockBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
		super(ArcanaTiles.WARDENED_BLOCK_TE.get(), pWorldPosition, pBlockState);
	}

	public void setState(Optional<BlockState> state) {
		copyState = state;
	}

	@Override
	public void saveAdditional(CompoundTag tag) {
		if (copyState.orElse(Blocks.BEDROCK.defaultBlockState()).getBlock() != ArcanaBlocks.WARDENED_BLOCK.get())
			tag.putString("type", Objects.requireNonNull(copyState.orElse(Blocks.BEDROCK.defaultBlockState()).getBlock().getRegistryName()).toString());

		super.saveAdditional(tag);
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		holdingSpell = level.getNearestPlayer(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), 64, true).getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof MagicDeviceItem;
		copyState = Optional.<BlockState>of(Objects.requireNonNull(ForgeRegistries.BLOCKS.getValue(new ResourceLocation(tag.getString("type")))).defaultBlockState());
	}

	public Optional<BlockState> getState() {
		return copyState;
	}

	//  When the world loads from disk, the server needs to send the TileEntity information to the client
	//  it uses getUpdatePacket(), getUpdateTag(), onDataPacket(), and handleUpdateTag() to do this:
	//  getUpdatePacket() and onDataPacket() are used for one-at-a-time TileEntity updates
	//  getUpdateTag() and handleUpdateTag() are used by vanilla to collate together into a single chunk update packet
	//  Not really required for this example since we only use the timer on the client, but included anyway for illustration
	@Override
	@Nullable
	public ClientboundBlockEntityDataPacket getUpdatePacket(){
		CompoundTag nbtTagCompound = new CompoundTag();
		saveAdditional(nbtTagCompound);
		int tileEntityType = ArcanaTiles.WARDENED_BLOCK_TE.hashCode();
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
		load(pkt.getTag());
	}

	/* Creates a tag containing all of the TileEntity information, used by vanilla to transmit from server to client */
	@Override
	public CompoundTag getUpdateTag(){
		CompoundTag nbtTagCompound = new CompoundTag();
		saveAdditional(nbtTagCompound);
		return nbtTagCompound;
	}

	/* Populates this TileEntity with information from the tag, used by vanilla to transmit from server to client */
	@Override
	public void handleUpdateTag(CompoundTag tag)
	{
		this.saveAdditional(tag);
	}

	public void tick() {
		holdingSpell = level.getNearestPlayer(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), 64, true).getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof MagicDeviceItem;
	}

	public Boolean isHoldingWand() {
		return holdingSpell;
	}
}
