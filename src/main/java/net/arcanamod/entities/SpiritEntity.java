package net.arcanamod.entities;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.FlyingMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import java.util.Random;

public class SpiritEntity extends FlyingMob implements FlyingAnimal {
    public SpiritEntity(EntityType<? extends SpiritEntity> type, Level world){
        super(type, world);
        xpReward = 5;
        moveControl = new SpiritEntity(type, world).getMoveControl();
    }

    @Override
    protected void registerGoals(){
        super.registerGoals();
        goalSelector.addGoal(5, new SpiritEntity.RandomFlyGoal(this));
        goalSelector.addGoal(7, new SpiritEntity.LookAroundGoal(this));
    }

    //@Override
    protected void registerAttributesh(){
        createMobAttributes();
        // TODO: stats
        getAttribute(Attributes.MAX_HEALTH).setBaseValue(16);
        getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(.23);
    }

    @Override
    public boolean isFlying() {
        return true;
    }

    static class LookAroundGoal extends Goal {

        private final SpiritEntity parentEntity;

        public LookAroundGoal(SpiritEntity entity){
            parentEntity = entity;
            setFlags(EnumSet.of(Flag.LOOK));
        }

        /**
         * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
         * method as well.
         */
        @Override
        public boolean canUse() {
            return true;
        }

        public void tick(){
            if(parentEntity.getTarget() == null){
                Vec3 motion = parentEntity.getDeltaMovement();
                parentEntity.setYRot(-((float) Mth.atan2(motion.x, motion.z)) * 57.295776F);
                parentEntity.setYBodyRot(parentEntity.yRotO);
            }else{
                LivingEntity lvt_1_2_ = parentEntity.getTarget();
                if(lvt_1_2_.distanceToSqr(parentEntity) < 4096.0D){
                    double lvt_4_1_ = lvt_1_2_.position().x - parentEntity.position().x;
                    double lvt_6_1_ = lvt_1_2_.position().z - parentEntity.position().z;
                    parentEntity.setYRot(-((float)Mth.atan2(lvt_4_1_, lvt_6_1_)) * 57.295776F);
                    parentEntity.setYBodyRot(parentEntity.yRotO);
                }
            }

        }
    }

    static class RandomFlyGoal extends Goal{
        private final SpiritEntity parentEntity;

        public RandomFlyGoal(SpiritEntity entity){
            parentEntity = entity;
            setFlags(EnumSet.of(Flag.MOVE));
        }

        public boolean shouldContinueExecuting(){
            return false;
        }

        public void startExecuting(){
            Random rand = parentEntity.getRandom();
            double x = parentEntity.getX() + (rand.nextFloat() * 2f - 1) * 16;
            double y = parentEntity.getY() + (rand.nextFloat() * 2f - 1) * 16;
            double z = parentEntity.getZ() + (rand.nextFloat() * 2f - 1) * 16;
            parentEntity.getNavigation().moveTo(x, y, z, 1);
        }

        /**
         * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
         * method as well.
         */
        @Override
        public boolean canUse() {
            MoveControl movementController = parentEntity.getMoveControl();
            if(!movementController.hasWanted()){
                return true;
            }else{
                double x = movementController.getWantedX() - parentEntity.getX();
                double y = movementController.getWantedY() - parentEntity.getY();
                double z = movementController.getWantedZ() - parentEntity.getZ();
                double distSquared = x * x + y * y + z * z;
                return distSquared < 1 || distSquared > 3600;
            }
        }
    }

    static class MoveHelperController extends MoveControl{
        private final SpiritEntity parentEntity;
        private int courseChangeCooldown;

        public MoveHelperController(SpiritEntity entity){
            super(entity);
            this.parentEntity = entity;
        }

        public void tick(){
            if(operation == Operation.MOVE_TO)
                if(courseChangeCooldown-- <= 0){
                    courseChangeCooldown += parentEntity.getRandom().nextInt(5) + 2;
                    Vec3 posDiff = new Vec3(wantedX - parentEntity.getX(), wantedY - parentEntity.getY(), wantedZ - parentEntity.getZ());
                    double posLength = posDiff.length();
                    posDiff = posDiff.normalize();
                    if(canMove(posDiff, Mth.ceil(posLength)))
                        parentEntity.setDeltaMovement(parentEntity.getDeltaMovement().add(posDiff.scale(.1)));
                    else
                        operation = Operation.WAIT;
                }
        }

        private boolean canMove(Vec3 pos, int size){
            AABB boundingBox = this.parentEntity.getBoundingBox();

            for(int i = 1; i < size; ++i){
                boundingBox = boundingBox.move(pos);
                // Check collision
                if(!parentEntity.level.noCollision(this.parentEntity, boundingBox))
                    return false;
            }
            return true;
        }
    }
}
