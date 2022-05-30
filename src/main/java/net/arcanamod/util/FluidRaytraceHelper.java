package net.arcanamod.util;

import com.google.common.collect.ImmutableSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.Set;
import java.util.function.Predicate;

public class FluidRaytraceHelper {
	public static HitResult rayTrace(Entity projectile, boolean checkEntityCollision, boolean includeShooter, @Nullable Entity shooter, ClipContext.Block blockModeIn) {
		return rayTrace(projectile, checkEntityCollision, includeShooter, shooter, blockModeIn, true,
				(p_221270_2_) -> !p_221270_2_.isSpectator() && p_221270_2_.canBeCollidedWith() && (includeShooter || !p_221270_2_.equals(shooter)) && !p_221270_2_.noCulling,
				projectile.getBoundingBox().expandTowards(projectile.getDeltaMovement()).inflate(1.0D));
	}

	public static HitResult rayTrace(Entity projectile, AABB boundingBox, Predicate<Entity> filter, ClipContext.Block blockModeIn, boolean checkEntityCollision) {
		return rayTrace(projectile, checkEntityCollision, false, (Entity)null, blockModeIn, false, filter, boundingBox);
	}

	private static HitResult rayTrace(Entity projectile, boolean checkEntityCollision, boolean includeShooter, @Nullable Entity shooter, ClipContext.Block blockModeIn, boolean p_221268_5_, Predicate<Entity> filter, AABB boundingBox) {
		Vec3 vec3d = projectile.getDeltaMovement();
		Level world = projectile.level;
		Vec3 vec3d1 = projectile.getEyePosition();
		if (p_221268_5_ && !world.noCollision(
				projectile,
				projectile.getBoundingBox()/*,
				((Set<Entity>) (!includeShooter && shooter != null ? getEntityAndMount(shooter) : ImmutableSet.of()))::contains*/)) {
			return new BlockHitResult(vec3d1, Direction.getNearest(vec3d.x, vec3d.y, vec3d.z), new BlockPos(projectile.getEyePosition()), false);
		} else {
			Vec3 vec3d2 = vec3d1.add(vec3d);
			HitResult raytraceresult = world.clip(new ClipContext(vec3d1, vec3d2, blockModeIn, ClipContext.Fluid.SOURCE_ONLY, projectile));
			if (checkEntityCollision) {
				if (raytraceresult.getType() != HitResult.Type.MISS) {
					vec3d2 = raytraceresult.getLocation();
				}

				HitResult raytraceresult1 = ProjectileUtil.getEntityHitResult(world, projectile, vec3d1, vec3d2, boundingBox, filter);
				if (raytraceresult1 != null) {
					raytraceresult = raytraceresult1;
				}
			}

			return raytraceresult;
		}
	}

	private static Set<Entity> getEntityAndMount(Entity rider) {
		Entity entity = rider.getRootVehicle();
		return entity != null ? ImmutableSet.of(rider, entity) : ImmutableSet.of(rider);
	}
}
