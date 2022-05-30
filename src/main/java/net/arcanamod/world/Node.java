package net.arcanamod.world;

import com.mojang.math.Vector3d;
import net.arcanamod.aspects.handlers.AspectBattery;
import net.arcanamod.aspects.handlers.AspectHandler;
import net.minecraft.core.Position;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import java.util.UUID;

// implements position for BlockPos constructor convenience
public class Node implements Position {
	
	/** The aspects contained in the node. */
	AspectHandler aspects;
	/** The type of node this is. */
	NodeType type;
	/** The position of this node. */
	double x, y, z;
	/** The unique ID of this node. */
	UUID nodeUniqueId = Mth.createInsecureUUID();
	/** The time, in ticks, until the node gains some aspects. */
	int timeUntilRecharge;
	/** Any extra data, used by the node type (e.g. hungry nodes). */
	CompoundTag data = new CompoundTag();
	
	public Node(AspectHandler aspects, NodeType type, double x, double y, double z, int timeUntilRecharge){
		this.aspects = aspects;
		this.type = type;
		this.x = x;
		this.y = y;
		this.z = z;
		this.timeUntilRecharge = timeUntilRecharge;
	}
	
	public Node(AspectHandler aspects, NodeType type, double x, double y, double z, int timeUntilRecharge, CompoundTag data){
		this.aspects = aspects;
		this.type = type;
		this.x = x;
		this.y = y;
		this.z = z;
		this.timeUntilRecharge = timeUntilRecharge;
		this.data = data;
	}
	
	public Node(AspectHandler aspects, NodeType type, double x, double y, double z, int timeUntilRecharge, UUID nodeUniqueId, CompoundTag data){
		this.aspects = aspects;
		this.type = type;
		this.x = x;
		this.y = y;
		this.z = z;
		this.timeUntilRecharge = timeUntilRecharge;
		this.nodeUniqueId = nodeUniqueId;
		this.data = data;
	}
	
	public NodeType type(){
		return type;
	}
	
	public CompoundTag serializeNBT(){
		CompoundTag Tag = new CompoundTag();
		Tag.putString("type", NodeType.TYPES.inverse().get(type()).toString());
		Tag.put("aspects", aspects.serializeNBT());
		Tag.putDouble("x", x());
		Tag.putDouble("y", y());
		Tag.putDouble("z", z());
		Tag.putUUID("nodeUniqueId", nodeUniqueId);
		Tag.putInt("timeUntilRecharge", timeUntilRecharge);
		Tag.put("data", data);
		return Tag;
	}
	
	public static Node fromTag(CompoundTag Tag){
		AspectHandler aspects = new AspectBattery();
		aspects.deserializeNBT(Tag.getCompound("aspects"));
		NodeType type = NodeType.TYPES.get(new ResourceLocation(Tag.getString("type")));
		double x = Tag.getDouble("x"), y = Tag.getDouble("y"), z = Tag.getDouble("z");
		int timeUntilRecharge = Tag.getInt("timeUntilRecharge");
		CompoundTag data = Tag.getCompound("data");
		return Tag.hasUUID("nodeUniqueId") ? new Node(aspects, type, x, y, z, timeUntilRecharge, Tag.getUUID("nodeUniqueId"), data) : new Node(aspects, type, x, y, z, timeUntilRecharge, data);
	}
	
	public Vector3d getPosition(){
		return new Vector3d(x, y, z);
	}
	
	public AspectHandler getAspects(){
		return aspects;
	}
	
	public double x(){
		return x;
	}
	
	public double y(){
		return y;
	}
	
	public double z(){
		return z;
	}
	
	public int getTimeUntilRecharge(){
		return timeUntilRecharge;
	}
	
	public void setType(NodeType type){
		this.type = type;
	}
	
	public void setX(double x){
		this.x = x;
	}
	
	public void setY(double y){
		this.y = y;
	}
	
	public void setZ(double z){
		this.z = z;
	}
	
	public void setTimeUntilRecharge(int timeUntilRecharge){
		this.timeUntilRecharge = timeUntilRecharge;
	}
	
	public UUID nodeUniqueId(){
		return nodeUniqueId;
	}
	
	public CompoundTag getData(){
		return data;
	}
	
	public String toString(){
		return "Node{" +
				"aspects=" + aspects +
				", type=" + type +
				", x=" + x +
				", y=" + y +
				", z=" + z +
				", nodeUniqueId=" + nodeUniqueId +
				", timeUntilRecharge=" + timeUntilRecharge +
				", data=" + data +
				'}';
	}
}