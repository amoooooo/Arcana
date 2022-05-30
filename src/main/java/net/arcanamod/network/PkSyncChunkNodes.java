package net.arcanamod.network;

import net.arcanamod.Arcana;
import net.arcanamod.capabilities.AuraChunk;
import net.arcanamod.world.Node;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraftforge.network.NetworkEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Supplier;

public class PkSyncChunkNodes{
	
	public static final Logger LOGGER = LogManager.getLogger();
	
	ChunkPos chunk;
	Collection<Node> nodes;
	
	public PkSyncChunkNodes(ChunkPos chunk, Collection<Node> nodes){
		this.chunk = chunk;
		this.nodes = nodes;
	}
	
	public static void encode(PkSyncChunkNodes msg, FriendlyByteBuf buffer){
		CompoundTag compound = new CompoundTag();
		ListTag data = new ListTag();
		for(Node node : msg.nodes)
			data.add(node.serializeNBT());
		compound.put("nodes", data);
		buffer.writeNbt(compound);
		buffer.writeInt(msg.chunk.x);
		buffer.writeInt(msg.chunk.z);
	}
	
	public static PkSyncChunkNodes decode(FriendlyByteBuf buffer){
		ListTag list = buffer.readNbt().getList("nodes", Tag.TAG_COMPOUND);
		Collection<Node> nodeSet = new ArrayList<>(list.size());
		for(Tag nodeTag : list)
			if(nodeTag instanceof CompoundTag)
				nodeSet.add(Node.fromTag((CompoundTag)nodeTag));
		int x = buffer.readInt();
		int z = buffer.readInt();
		return new PkSyncChunkNodes(new ChunkPos(x, z), nodeSet);
	}
	
	public static void handle(PkSyncChunkNodes msg, Supplier<NetworkEvent.Context> supplier){
		supplier.get().enqueueWork(() -> {
			// I'm on client
			ChunkAccess chunk = Arcana.proxy.getWorldOnClient().getChunk(msg.chunk.x, msg.chunk.z);
			AuraChunk nc = AuraChunk.getFrom(chunk);
			if(nc != null)
				nc.setNodes(new ArrayList<>(msg.nodes));
		});
		supplier.get().setPacketHandled(true);
	}
}