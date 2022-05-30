package net.arcanamod.entities;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.arcanamod.systems.spell.Homeable;
import net.arcanamod.systems.spell.casts.Cast;
import net.arcanamod.systems.spell.casts.Casts;
import net.arcanamod.systems.spell.casts.ICast;
import net.minecraft.commands.arguments.ParticleArgument;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.*;

public class SpellCloudEntity extends Entity implements Homeable{
	private static final Logger PRIVATE_LOGGER = LogManager.getLogger();
	private static final EntityDataAccessor<Float> RADIUS;
	private static final EntityDataAccessor<Integer> COLOR;
	private static final EntityDataAccessor<Boolean> IGNORE_RADIUS;
	private static final EntityDataAccessor<ParticleOptions> PARTICLE;
	private ICast spell;
	private final Map<Entity, Integer> reapplicationDelayMap = Maps.newHashMap();
	private int duration;
	private int waitTime;
	private int reapplicationDelay;
	private boolean colorSet;
	private int durationOnUse;
	private float radiusOnUse;
	private float radiusPerTick;
	private LivingEntity owner;
	private UUID ownerUniqueId;

	private List<Class<? extends Entity>> homeTargets = new ArrayList<>();

	public void enableHoming(Class<? extends Entity>... targets) {
		this.homeTargets = Lists.newArrayList(targets);
	}

	@Override
	public List<Class<? extends Entity>> getHomeables() {
		return homeTargets;
	}

	public static class CloudVariableGrid{
		public Player player;
		Level world;
		Vec3 area;
		int rMultP;

		public CloudVariableGrid(Player player, Level world, Vec3 positionVec, int i) {
		}
	}

	public SpellCloudEntity(EntityType<? extends SpellCloudEntity> entityType, Level world) {
		super(entityType, world);
		this.noPhysics = true;
		this.setRadius(3.0F);
	}

	public SpellCloudEntity(Level world, double x, double y, double z) {
		this(ArcanaEntities.SPELL_CLOUD.get(), world);
		this.setPos(x, y, z);
	}

	public SpellCloudEntity(Level world, Vec3 vec) {
		this(ArcanaEntities.SPELL_CLOUD.get(), world);
		this.setPos(vec.x,vec.y,vec.z);
	}

	protected void defineSynchedData() {
		this.getEntityData().define(COLOR, 0);
		this.getEntityData().define(RADIUS, 0.5F);
		this.getEntityData().define(IGNORE_RADIUS, false);
		this.getEntityData().define(PARTICLE, ParticleTypes.ENTITY_EFFECT);
	}

	public void setRadius(float p_184483_1_) {
		if (!this.level.isClientSide()) {
			this.getEntityData().set(RADIUS, p_184483_1_);
		}

	}

	public void recalculateSize() {
		double x = this.getX();
		double y = this.getY();
		double z = this.getZ();
		super.refreshDimensions();
		this.setPos(x, y, z);
	}

	public float getRadius() {
		return this.getEntityData().get(RADIUS);
	}

	public void setSpell(ICast spell) {
		this.spell = spell;
		if (!this.colorSet) {
			this.updateFixedColor();
		}
	}

	private void updateFixedColor() {
		if (this.spell == null) {
			this.getEntityData().set(COLOR, 0);
		} else {
			this.getEntityData().set(COLOR, this.spell.getSpellAspect().getColorRange().get(3));
		}
	}

	public int getColor() {
		return this.getEntityData().get(COLOR);
	}

	public void setColor(int color) {
		this.colorSet = true;
		this.getEntityData().set(COLOR, color);
	}

	public ParticleOptions getParticleData() {
		return this.getEntityData().get(PARTICLE);
	}

	public void setParticleData(ParticleOptions p_195059_1_) {
		this.getEntityData().set(PARTICLE, p_195059_1_);
	}

	protected void setIgnoreRadius(boolean p_184488_1_) {
		this.getEntityData().set(IGNORE_RADIUS, p_184488_1_);
	}

	public boolean shouldIgnoreRadius() {
		return (Boolean) this.getEntityData().get(IGNORE_RADIUS);
	}

	public int getDuration() {
		return this.duration;
	}

	public void setDuration(int p_184486_1_) {
		this.duration = p_184486_1_;
	}

	public void tick() {
		super.tick();

		boolean ignores = this.shouldIgnoreRadius();
		float radius = this.getRadius();
		if (this.level.isClientSide()) {
			ParticleOptions lvt_3_1_ = this.getParticleData();
			float lvt_6_1_;
			float lvt_7_1_;
			float lvt_8_1_;
			int lvt_10_1_;
			int lvt_11_1_;
			int lvt_12_1_;
			if (ignores) {
				if (this.random.nextBoolean()) {
					for (int lvt_4_1_ = 0; lvt_4_1_ < 2; ++lvt_4_1_) {
						float lvt_5_1_ = this.random.nextFloat() * 6.2831855F;
						lvt_6_1_ = Mth.sqrt(this.random.nextFloat()) * 0.2F;
						lvt_7_1_ = Mth.cos(lvt_5_1_) * lvt_6_1_;
						lvt_8_1_ = Mth.sin(lvt_5_1_) * lvt_6_1_;
						if (lvt_3_1_.getType() == ParticleTypes.ENTITY_EFFECT) {
							int lvt_9_1_ = this.random.nextBoolean() ? 16777215 : this.getColor();
							lvt_10_1_ = lvt_9_1_ >> 16 & 255;
							lvt_11_1_ = lvt_9_1_ >> 8 & 255;
							lvt_12_1_ = lvt_9_1_ & 255;
							this.level.addParticle(lvt_3_1_, this.getX() + (double) lvt_7_1_, this.getY(), this.getZ() + (double) lvt_8_1_, ((float) lvt_10_1_ / 255.0F), ((float) lvt_11_1_ / 255.0F), ((float) lvt_12_1_ / 255.0F));
						} else {
							this.level.addParticle(lvt_3_1_, this.getX() + (double) lvt_7_1_, this.getY(), this.getZ() + (double) lvt_8_1_, 0.0D, 0.0D, 0.0D);
						}
					}
				}
			} else {
				float lvt_4_2_ = 3.1415927F * radius * radius;

				for (int lvt_5_2_ = 0; (float) lvt_5_2_ < lvt_4_2_; ++lvt_5_2_) {
					lvt_6_1_ = this.random.nextFloat() * 6.2831855F;
					lvt_7_1_ = Mth.sqrt(this.random.nextFloat()) * radius;
					lvt_8_1_ = Mth.cos(lvt_6_1_) * lvt_7_1_;
					float lvt_9_2_ = Mth.sin(lvt_6_1_) * lvt_7_1_;
					if (lvt_3_1_.getType() == ParticleTypes.ENTITY_EFFECT) {
						lvt_10_1_ = this.getColor();
						lvt_11_1_ = lvt_10_1_ >> 16 & 255;
						lvt_12_1_ = lvt_10_1_ >> 8 & 255;
						int lvt_13_1_ = lvt_10_1_ & 255;
						this.level.addParticle(lvt_3_1_, this.getX() + (double) lvt_8_1_, this.getY(), this.getZ() + (double) lvt_9_2_, ((float) lvt_11_1_ / 255.0F), ((float) lvt_12_1_ / 255.0F), ((float) lvt_13_1_ / 255.0F));
					} else {
						this.level.addParticle(lvt_3_1_, this.getX() + (double) lvt_8_1_, this.getY(), this.getZ() + (double) lvt_9_2_, (0.5D - this.random.nextDouble()) * 0.15D, 0.009999999776482582D, (0.5D - this.random.nextDouble()) * 0.15D);
					}
				}
			}
		} else {
			Homeable.startHoming(this);
			
			if (this.tickCount >= this.waitTime + this.duration) {
				this.remove(RemovalReason.DISCARDED);
				return;
			}

			boolean lvt_3_2_ = this.tickCount < this.waitTime;
			if (ignores != lvt_3_2_) {
				this.setIgnoreRadius(lvt_3_2_);
			}

			if (lvt_3_2_) {
				return;
			}

			if (this.radiusPerTick != 0.0F) {
				radius += this.radiusPerTick;
				if (radius < 0.5F) {
					this.remove(RemovalReason.DISCARDED);
					return;
				}

				this.setRadius(radius);
			}

			if (this.tickCount % 5 == 0) {
				Iterator lvt_4_3_ = this.reapplicationDelayMap.entrySet().iterator();

				while (lvt_4_3_.hasNext()) {
					Map.Entry<Entity, Integer> lvt_5_3_ = (Map.Entry) lvt_4_3_.next();
					if (this.tickCount >= (Integer) lvt_5_3_.getValue()) {
						lvt_4_3_.remove();
					}
				}

				List<LivingEntity> lvt_5_4_ = this.level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox());
				if (!lvt_5_4_.isEmpty()) {
					Iterator var25 = lvt_5_4_.iterator();

					while (true) {
						LivingEntity lvt_7_3_;
						double lvt_12_3_;
						do {
							do {
								do {
									if (!var25.hasNext()) {
										return;
									}

									lvt_7_3_ = (LivingEntity) var25.next();
								} while (this.reapplicationDelayMap.containsKey(lvt_7_3_));
							} while (!lvt_7_3_.isAffectedByPotions());

							double lvt_8_3_ = lvt_7_3_.getX() - this.getX();
							double lvt_10_3_ = lvt_7_3_.getZ() - this.getZ();
							lvt_12_3_ = lvt_8_3_ * lvt_8_3_ + lvt_10_3_ * lvt_10_3_;
						} while (lvt_12_3_ > (double) (radius * radius));

						this.reapplicationDelayMap.put(lvt_7_3_, this.tickCount + this.reapplicationDelay);

						if (spell != null)
							((Cast) spell).useOnEntity((Player) owner, lvt_7_3_);

						if (this.radiusOnUse != 0.0F) {
							radius += this.radiusOnUse;
							if (radius < 0.5F) {
								this.remove(RemovalReason.DISCARDED);
								return;
							}

							this.setRadius(radius);
						}

						if (this.durationOnUse != 0) {
							this.duration += this.durationOnUse;
							if (this.duration <= 0) {
								this.remove(RemovalReason.DISCARDED);
								return;
							}
						}
					}
				}
			}
		}

	}

	public void setRadiusOnUse(float radius) {
		this.radiusOnUse = radius;
	}

	public void setRadiusPerTick(float radius) {
		this.radiusPerTick = radius;
	}

	public void setWaitTime(int time) {
		this.waitTime = time;
	}

	public void setOwner(@Nullable LivingEntity owner) {
		this.owner = owner;
		this.ownerUniqueId = owner == null ? null : owner.getUUID();
	}

	@Nullable
	public LivingEntity getOwner() {
		if (this.owner == null && this.ownerUniqueId != null && this.level instanceof ServerLevel) {
			Entity entity = ((ServerLevel) this.level).getEntity(this.ownerUniqueId);
			if (entity instanceof LivingEntity) {
				this.owner = (LivingEntity) entity;
			}
		}

		return this.owner;
	}

	protected void readAdditionalSaveData(CompoundTag compoundNBT) {
		this.tickCount = compoundNBT.getInt("Age");
		this.duration = compoundNBT.getInt("Duration");
		this.waitTime = compoundNBT.getInt("WaitTime");
		this.reapplicationDelay = compoundNBT.getInt("ReapplicationDelay");
		this.durationOnUse = compoundNBT.getInt("DurationOnUse");
		this.radiusOnUse = compoundNBT.getFloat("RadiusOnUse");
		this.radiusPerTick = compoundNBT.getFloat("RadiusPerTick");
		this.setRadius(compoundNBT.getFloat("Radius"));
		this.ownerUniqueId = compoundNBT.getUUID("OwnerUUID");
		if (compoundNBT.contains("Particle", 8)) {
			try {
				this.setParticleData(ParticleArgument.readParticle(new StringReader(compoundNBT.getString("Particle"))));
			} catch (CommandSyntaxException var5) {
				PRIVATE_LOGGER.warn("Couldn't load custom particle {}", compoundNBT.getString("Particle"), var5);
			}
		}

		if (compoundNBT.contains("Color", 99)) {
			this.setColor(compoundNBT.getInt("Color"));
		}

		if (compoundNBT.contains("Spell", 8)) {
			this.setSpell(Casts.castMap.get(new ResourceLocation(compoundNBT.getString("Spell"))));
		}

	}

	protected void addAdditionalSaveData(CompoundTag compoundNBT) {
		compoundNBT.putInt("Age", this.tickCount);
		compoundNBT.putInt("Duration", this.duration);
		compoundNBT.putInt("WaitTime", this.waitTime);
		compoundNBT.putInt("ReapplicationDelay", this.reapplicationDelay);
		compoundNBT.putInt("DurationOnUse", this.durationOnUse);
		compoundNBT.putFloat("RadiusOnUse", this.radiusOnUse);
		compoundNBT.putFloat("RadiusPerTick", this.radiusPerTick);
		compoundNBT.putFloat("Radius", this.getRadius());
		compoundNBT.putString("Particle", this.getParticleData().writeToString());
		if (this.ownerUniqueId != null) {
			compoundNBT.putUUID("OwnerUUID", this.ownerUniqueId);
		}
		if (this.colorSet) {
			compoundNBT.putInt("Color", this.getColor());
		}
		if (this.spell != null) {
			compoundNBT.putString("spell", ((Cast) spell).getId().toString()); // TODO: REPLACE (SPELL) wit (ISPELL)
		}
	}

	public void onSyncedDataUpdated(EntityDataAccessor<?> dataParameter) {
		if (RADIUS.equals(dataParameter)) {
			this.recalculateSize();
		}

		super.onSyncedDataUpdated(dataParameter);
	}

	public PushReaction getPushReaction() {
		return PushReaction.IGNORE;
	}

	@Override
	public Packet<?> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	public EntityDimensions getSize(Pose p_213305_1_) {
		return EntityDimensions.scalable(this.getRadius() * 2.0F, 0.5F);
	}

	static {
		RADIUS = SynchedEntityData.defineId(AreaEffectCloud.class, EntityDataSerializers.FLOAT);
		COLOR = SynchedEntityData.defineId(AreaEffectCloud.class, EntityDataSerializers.INT);
		IGNORE_RADIUS = SynchedEntityData.defineId(AreaEffectCloud.class, EntityDataSerializers.BOOLEAN);
		PARTICLE = SynchedEntityData.defineId(AreaEffectCloud.class, EntityDataSerializers.PARTICLE);
	}
}