package net.arcanamod.util;

import com.mojang.math.Vector3d;
import net.minecraft.util.Mth;
import net.minecraft.util.math.Mth;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;

public final class LocalAxis{
	
	private LocalAxis(){}
	
	// Adapted from LookingPosArgument::toAbsolutePos (Yarn names)
	public static Vector3d toAbsolutePos(Vector3d localPos, Vector2f rotation, Vector3d worldPos){
		float yCos = Mth.cos((rotation.y + 90) * .017453292F);
		float ySin = Mth.sin((rotation.y + 90) * .017453292F);
		float xCos = Mth.cos(-rotation.x * .017453292F);
		float xSin = Mth.sin(-rotation.x * .017453292F);
		float a = Mth.cos((-rotation.x + 90) * .017453292F);
		float b = Mth.sin((-rotation.x + 90) * .017453292F);
		Vector3d vec3d2 = new Vector3d(yCos * xCos, xSin, ySin * xCos);
		Vector3d vec3d3 = new Vector3d(yCos * a, b, ySin * a);
		Vector3d vec3d4 = vec3d2.crossProduct(vec3d3).scale(-1);
		double xOff = vec3d2.x * localPos.z + vec3d3.x * localPos.y + vec3d4.x * localPos.x;
		double yOff = vec3d2.y * localPos.z + vec3d3.y * localPos.y + vec3d4.y * localPos.x;
		double zOff = vec3d2.z * localPos.z + vec3d3.z * localPos.y + vec3d4.z * localPos.x;
		return new Vector3d(worldPos.x + xOff, worldPos.y + yOff, worldPos.z + zOff);
	}
	
	// Converts from direction to pitch/yaw immediately.
	// TODO: better impl
	public static Vector3d toAbsolutePos(Vector3d localPos, Vector3d facing, Vector3d worldPos){
		float pitch = (float)Math.asin(-facing.y);
		float yaw = (float)Math.atan2(facing.x, facing.z);
		return toAbsolutePos(localPos, new Vector2f(pitch, yaw), worldPos);
	}
}