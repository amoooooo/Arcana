package net.arcanamod.world;

import net.arcanamod.capabilities.AuraChunk;
import net.arcanamod.client.ClientAuraHandler;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.LevelChunk;

import java.util.ArrayList;
import java.util.Collection;

/**
 * A view of the nodes in the world for a particular tick.
 */
public class ClientAuraView implements AuraView{
	
	ClientLevel world;
	
	public ClientAuraView(ClientLevel world){
		this.world = world;
	}
	
	public Collection<Node> getAllNodes(){
		Collection<Node> allNodes = new ArrayList<>();
		for(ChunkPos chunkPos : ClientAuraHandler.CLIENT_LOADED_CHUNKS){
			ChunkAccess chunk = world.getChunk(chunkPos.x, chunkPos.z, ChunkStatus.FULL, false);
			if(chunk instanceof LevelChunk){ // also a nonnull check
				AuraChunk nc = AuraChunk.getFrom((LevelChunk)chunk);
				if(nc != null)
					allNodes.addAll(nc.getNodes());
			}
		}
		return allNodes;
	}
	
	public Level getWorld(){
		return world;
	}
}