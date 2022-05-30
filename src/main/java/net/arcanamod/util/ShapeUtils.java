package net.arcanamod.util;

import com.mojang.math.Vector3d;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ShapeUtils{
	public static Vec3i fromNorth(Vec3i vec, Direction direction){
		switch(direction){
			case WEST:
				return new Vec3i(vec.getZ(), vec.getY(), -vec.getX());
			case SOUTH:
				return new Vec3i(-vec.getX(), vec.getY(), -vec.getZ());
			case EAST:
				return new Vec3i(-vec.getZ(), vec.getY(), vec.getX());
			case NORTH:
			default:
				return new Vec3i(vec.getX(), vec.getY(), vec.getZ());
		}
	}
	
	// Basic logic: rotating a shape around its center point (not 0,0,0)
	public static VoxelShape rotate(VoxelShape shape, Direction fromNorth){
		// IntelliJ says this needed to be an array because of the lambda function
		VoxelShape[] tmp = {Shapes.empty()};
		switch(fromNorth){
			case EAST:
				shape.forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ)
						-> tmp[0] = Shapes.or(tmp[0], Shapes.create(1 - maxZ, minY, minX, 1 - minZ, maxY, maxX)));
				break;
			case SOUTH:
				shape.forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ)
						-> tmp[0] = Shapes.or(tmp[0], Shapes.create(1 - maxX, minY, 1 - maxZ, 1 - minX, maxY, 1 - minZ)));
				break;
			case WEST:
				shape.forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ)
						-> tmp[0] = Shapes.or(tmp[0], Shapes.create(minZ, minY, 1 - maxX, maxZ, maxY, 1 - minX)));
				break;
			case NORTH:
			default:
				tmp[0] = shape;
				break;
		}
		return tmp[0];
	}
	
	public static double dist(Vector3d pos, Vec3i mask){
		if(mask.getX() != 0)
			return pos.x * mask.getX();
		if(mask.getY() != 0)
			return pos.y * mask.getY();
		if(mask.getZ() != 0)
			return pos.z * mask.getZ();
		return 0;
	}
}