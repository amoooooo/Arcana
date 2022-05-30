package net.arcanamod.capabilities;

import net.arcanamod.world.Node;
import net.arcanamod.world.NodeType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;

public class AuraChunkImpl implements AuraChunk{
	
	Collection<Node> nodes = new ArrayList<>();
	float taint;
	
	public void addNode(Node node){
		nodes.add(node);
	}
	
	public void removeNode(Node node){
		nodes.remove(node);
	}
	
	public void setNodes(Collection<Node> nodes){
		this.nodes = nodes;
	}
	
	public Collection<Node> getNodes(){
		return new ArrayList<>(nodes);
	}
	
	public Collection<Node> getNodesWithinAABB(AABB bounds){
		Collection<Node> set = new ArrayList<>();
		for(Node node : getNodes())
			if(bounds.contains(node.x(), node.y(), node.z()))
				set.add(node);
		return set;
	}
	
	public Collection<Node> getNodesOfType(NodeType type){
		Collection<Node> set = new ArrayList<>();
		for(Node node : getNodes())
			if(node.type().equals(type))
				set.add(node);
		return set;
	}
	
	public Collection<Node> getNodesOfTypeWithinAABB(NodeType type, AABB bounds){
		Collection<Node> set = new ArrayList<>();
		for(Node node : getNodes())
			if(node.type().equals(type))
				if(bounds.contains(node.x(), node.y(), node.z()))
					set.add(node);
		return set;
	}
	
	public float getFluxLevel(){
		return taint;
	}
	
	public void addFlux(float amount){
		taint += amount;
		taint = Math.max(taint, 0);
	}
	
	public void setFlux(float newTaint){
		taint = Math.max(newTaint, 0);
	}
	
	public CompoundTag serializeNBT(){
		// Just make a list of CompoundNBTs from each node.
		CompoundTag compound = new CompoundTag();
		ListTag data = new ListTag();
		for(Node node : nodes)
			data.add(node.serializeNBT());
		compound.put("nodes", data);
		compound.putFloat("flux", taint);
		return compound;
	}
	
	public void deserializeNBT(@Nonnull CompoundTag data){
		// Go through the list and deserialize each entry
		ListTag list = data.getList("nodes", Tag.TAG_COMPOUND);
		Collection<Node> nodeSet = new ArrayList<>(list.size());
		for(Tag nodeNBT : list)
			if(nodeNBT instanceof CompoundTag)
				nodeSet.add(Node.fromTag((CompoundTag)nodeNBT));
		nodes = nodeSet;
		taint = data.getFloat("flux");
		// load old integer taint
		if(data.contains("taint"))
			taint += data.getInt("taint");
	}
}
