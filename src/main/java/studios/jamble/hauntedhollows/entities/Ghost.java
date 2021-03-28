package studios.jamble.hauntedhollows.entities;

import net.minecraft.client.renderer.entity.SlimeRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.monster.AbstractRaiderEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.monster.SlimeEntity;
import net.minecraft.entity.monster.VexEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import java.util.EnumSet;

public class Ghost extends MonsterEntity {

    protected static final DataParameter<Byte> VEX_FLAGS = EntityDataManager.createKey(VexEntity.class, DataSerializers.BYTE);

    public Ghost(EntityType<? extends MonsterEntity> type, World worldIn) {
        super(type, worldIn);
        this.moveController = new Ghost.MoveHelperController(this);
    }

    public static AttributeModifierMap.MutableAttribute registerAttributes(double health, double attack_damage, double movement_speed, double follow_range, double attack_knockback, double attack_speed) {
        return MobEntity.registerAttributes()
                .createMutableAttribute(Attributes.MAX_HEALTH, health)
                .createMutableAttribute(Attributes.ATTACK_DAMAGE, attack_damage)
                .createMutableAttribute(Attributes.MOVEMENT_SPEED, movement_speed)
                .createMutableAttribute(Attributes.FOLLOW_RANGE, follow_range)
                .createMutableAttribute(Attributes.ATTACK_KNOCKBACK, attack_knockback)
                .createMutableAttribute(Attributes.ATTACK_SPEED, attack_speed);
    }

    public void tick() {
        this.noClip = true;
        super.tick();
        this.noClip = false;
        this.setNoGravity(true);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new SwimGoal(this));
        this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.0D, false));
        this.goalSelector.addGoal(4, new Ghost.ChargeAttackGoal());
        this.goalSelector.addGoal(8, new Ghost.MoveRandomGoal());
        this.goalSelector.addGoal(9, new LookAtGoal(this, PlayerEntity.class, 3.0F, 1.0F));
        this.goalSelector.addGoal(10, new LookAtGoal(this, MobEntity.class, 8.0F));
        this.targetSelector.addGoal(1, (new HurtByTargetGoal(this, AbstractRaiderEntity.class)).setCallsForHelp());
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true));
    }

    private boolean getVexFlag(int mask) {
        int i = this.dataManager.get(VEX_FLAGS);
        return (i & mask) != 0;
    }

    protected void registerData() {
        super.registerData();
        this.dataManager.register(VEX_FLAGS, (byte)0);
    }

    private void setVexFlag(int mask, boolean value) {
        int i = this.dataManager.get(VEX_FLAGS);
        if (value) {
            i = i | mask;
        } else {
            i = i & ~mask;
        }

        this.dataManager.set(VEX_FLAGS, (byte)(i & 255));
    }

    public boolean isCharging() {
        return this.getVexFlag(1);
    }

    public void setCharging(boolean charging) {
        this.setVexFlag(1, charging);
    }

    class ChargeAttackGoal extends Goal {
        public ChargeAttackGoal() {
            this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        /**
         * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
         * method as well.
         */
        public boolean shouldExecute() {
            if (Ghost.this.getAttackTarget() != null && !Ghost.this.getMoveHelper().isUpdating() && Ghost.this.rand.nextInt(7) == 0) {
                return Ghost.this.getDistanceSq(Ghost.this.getAttackTarget()) > 4.0D && Ghost.this.world.getLight(Ghost.this.getAttackTarget().getPosition()) < 8;
            } else {
                return false;
            }
        }

        /**
         * Returns whether an in-progress EntityAIBase should continue executing
         */
        public boolean shouldContinueExecuting() {
            return Ghost.this.getMoveHelper().isUpdating() && Ghost.this.isCharging() && Ghost.this.getAttackTarget() != null && Ghost.this.getAttackTarget().isAlive();
        }

        /**
         * Execute a one shot task or start executing a continuous task
         */
        public void startExecuting() {
            LivingEntity livingentity = Ghost.this.getAttackTarget();
            Vector3d vector3d = livingentity.getEyePosition(1.0F);
            Ghost.this.moveController.setMoveTo(vector3d.x, vector3d.y - 0.5D, vector3d.z, 1.0D);
            Ghost.this.setCharging(true);
            Ghost.this.playSound(SoundEvents.ENTITY_VEX_CHARGE, 1.0F, 1.0F);
        }

        /**
         * Reset the task's internal state. Called when this task is interrupted by another one
         */
        public void resetTask() {
            Ghost.this.setCharging(false);
        }

        /**
         * Keep ticking a continuous task that has already been started
         */
        public void tick() {
            LivingEntity livingentity = Ghost.this.getAttackTarget();
            if (Ghost.this.getBoundingBox().intersects(livingentity.getBoundingBox())) {
                Ghost.this.attackEntityAsMob(livingentity);
                Ghost.this.setCharging(false);
            } else {
                double d0 = Ghost.this.getDistanceSq(livingentity);
                if (d0 < 9.0D) {
                    Vector3d vector3d = livingentity.getEyePosition(1.0F);
                    Ghost.this.moveController.setMoveTo(vector3d.x, vector3d.y - 0.5D, vector3d.z, 1.0D);
                }
            }

        }
    }

    class MoveHelperController extends MovementController {
        public MoveHelperController(Ghost vex) {
            super(vex);
        }

        public void tick() {
            if (this.action == MovementController.Action.MOVE_TO) {
                Vector3d vector3d = new Vector3d(this.posX - Ghost.this.getPosX(), this.posY - Ghost.this.getPosY(), this.posZ - Ghost.this.getPosZ());
                double d0 = vector3d.length();
                if (d0 < Ghost.this.getBoundingBox().getAverageEdgeLength()) {
                    this.action = MovementController.Action.WAIT;
                    Ghost.this.setMotion(Ghost.this.getMotion().scale(0.5D));
                } else {
                    Ghost.this.setMotion(Ghost.this.getMotion().add(vector3d.scale(this.speed * 0.05D / d0)));
                    if (Ghost.this.getAttackTarget() == null) {
                        Vector3d vector3d1 = Ghost.this.getMotion();
                        Ghost.this.rotationYaw = -((float) MathHelper.atan2(vector3d1.x, vector3d1.z)) * (180F / (float)Math.PI);
                        Ghost.this.renderYawOffset = Ghost.this.rotationYaw;
                    } else {
                        double d2 = Ghost.this.getAttackTarget().getPosX() - Ghost.this.getPosX();
                        double d1 = Ghost.this.getAttackTarget().getPosZ() - Ghost.this.getPosZ();
                        Ghost.this.rotationYaw = -((float)MathHelper.atan2(d2, d1)) * (180F / (float)Math.PI);
                        Ghost.this.renderYawOffset = Ghost.this.rotationYaw;
                    }
                }

            }
        }
    }

    class MoveRandomGoal extends Goal {
        public MoveRandomGoal() {
            this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        /**
         * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
         * method as well.
         */
        public boolean shouldExecute() {
            return !Ghost.this.getMoveHelper().isUpdating() && Ghost.this.rand.nextInt(7) == 0;
        }

        /**
         * Returns whether an in-progress EntityAIBase should continue executing
         */
        public boolean shouldContinueExecuting() {
            return false;
        }

        public int moveY(BlockPos pos) {
            if(pos.getY() > 135) {
                return -5;
            } else if(pos.getY() < 128) {
                return 5;
            }
            return Ghost.this.rand.nextInt(7) - 3;
        }

        /**
         * Keep ticking a continuous task that has already been started
         */
        public void tick() {
            BlockPos blockpos = Ghost.this.getPosition();

            for(int i = 0; i < 3; ++i) {
                BlockPos blockpos1 = blockpos.add(Ghost.this.rand.nextInt(15) - 7, moveY(blockpos), Ghost.this.rand.nextInt(15) - 7);
                if (Ghost.this.world.isAirBlock(blockpos1) && Ghost.this.world.getLight(blockpos1) < 8) {
                    Ghost.this.moveController.setMoveTo((double)blockpos1.getX() + 0.5D, (double)blockpos1.getY() + 0.5D, (double)blockpos1.getZ() + 0.5D, 0.25D);
                    if (Ghost.this.getAttackTarget() == null) {
                        Ghost.this.getLookController().setLookPosition((double)blockpos1.getX() + 0.5D, (double)blockpos1.getY() + 0.5D, (double)blockpos1.getZ() + 0.5D, 180.0F, 20.0F);
                    }
                    break;
                }
            }

        }
    }

}
