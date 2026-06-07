package com.yourname.werewolf.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.Level;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.synced.SynchedEntityData;
import net.minecraft.network.synced.EntityDataAccessor;
import net.minecraft.network.synced.EntityDataSerializers;
import net.minecraft.world.phys.Vec3;
import com.yourname.werewolf.animation.WerewolfAnimationController;
import java.util.UUID;

public class WerewolfEntity extends Mob {
    
    // Animation state accessors
    private static final EntityDataAccessor<Integer> ANIMATION_STATE = SynchedEntityData.defineId(WerewolfEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> HEAD_ROTATION = SynchedEntityData.defineId(WerewolfEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> NECK_ROTATION = SynchedEntityData.defineId(WerewolfEntity.class, EntityDataSerializers.FLOAT);
    
    private UUID ownerUUID;
    private WerewolfAnimationController animationController;
    private float movementBlend = 0.0F;

    // Animation state constants (customize these with your own animations)
    public static final int ANIM_IDLE = 0;
    public static final int ANIM_WALK_FORWARD = 1;
    public static final int ANIM_WALK_BACKWARD = 2;
    public static final int ANIM_STRAFE_LEFT = 3;
    public static final int ANIM_STRAFE_RIGHT = 4;
    public static final int ANIM_SPRINT = 5;
    public static final int ANIM_CUSTOM_1 = 6;  // Placeholder for custom animation
    public static final int ANIM_CUSTOM_2 = 7;  // Placeholder for custom animation

    public WerewolfEntity(Level level) {
        super(net.minecraftforge.registries.ForgeRegistries.ENTITY_TYPES.getValue(
            new net.minecraft.resources.ResourceLocation("yourmodid", "werewolf")), level);
        this.animationController = new WerewolfAnimationController(this);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ANIMATION_STATE, ANIM_IDLE);
        this.entityData.define(HEAD_ROTATION, 0.0F);
        this.entityData.define(NECK_ROTATION, 0.0F);
    }

    @Override
    public void tick() {
        super.tick();
        
        if (!this.level().isClientSide) {
            updateAnimationState();
            updateHeadTracking();
        }
    }

    /**
     * Update animation state based on movement
     */
    private void updateAnimationState() {
        Vec3 motion = this.getDeltaMovement();
        float horizontalSpeed = (float) Math.sqrt(motion.x * motion.x + motion.z * motion.z);
        
        int newAnimState = ANIM_IDLE;
        
        if (horizontalSpeed > 0.05F) {
            if (this.isSprinting()) {
                newAnimState = ANIM_SPRINT;
            } else {
                // Determine direction based on yaw and motion
                float yaw = this.getYRot();
                double motionAngle = Math.atan2(motion.z, motion.x);
                
                if (motion.z > 0.05F) {
                    newAnimState = ANIM_WALK_FORWARD;
                } else if (motion.z < -0.05F) {
                    newAnimState = ANIM_WALK_BACKWARD;
                } else if (motion.x > 0.05F) {
                    newAnimState = ANIM_STRAFE_RIGHT;
                } else if (motion.x < -0.05F) {
                    newAnimState = ANIM_STRAFE_LEFT;
                }
            }
        }
        
        this.entityData.set(ANIMATION_STATE, newAnimState);
        this.animationController.setCurrentAnimation(newAnimState);
    }

    /**
     * Update head and neck tracking based on rotation
     */
    private void updateHeadTracking() {
        float yRot = this.getYRot() % 360;
        float xRot = this.getXRot();
        
        this.entityData.set(HEAD_ROTATION, yRot);
        this.entityData.set(NECK_ROTATION, xRot);
        
        // Update neck yaw (for looking left/right)
        this.yHeadRot = yRot;
        this.setXRot(xRot);
    }

    /**
     * Set the owner (player) of this werewolf
     */
    public void setOwnerUUID(UUID uuid) {
        this.ownerUUID = uuid;
    }

    public UUID getOwnerUUID() {
        return this.ownerUUID;
    }

    /**
     * Get current animation state
     */
    public int getCurrentAnimation() {
        return this.entityData.get(ANIMATION_STATE);
    }

    /**
     * Set custom animation (for user-defined animations)
     */
    public void playCustomAnimation(int animationSlot) {
        if (animationSlot >= ANIM_CUSTOM_1 && animationSlot <= ANIM_CUSTOM_2) {
            this.entityData.set(ANIMATION_STATE, animationSlot);
            this.animationController.setCurrentAnimation(animationSlot);
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        if (this.ownerUUID != null) {
            compound.putUUID("OwnerUUID", this.ownerUUID);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.hasUUID("OwnerUUID")) {
            this.ownerUUID = compound.getUUID("OwnerUUID");
        }
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
            .add(Attributes.MAX_HEALTH, 30.0D)
            .add(Attributes.MOVEMENT_SPEED, 0.3D)
            .add(Attributes.ATTACK_DAMAGE, 8.0D)
            .add(Attributes.FOLLOW_RANGE, 32.0D);
    }
}
