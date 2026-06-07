package com.yourname.werewolf.animation;

import com.yourname.werewolf.entity.WerewolfEntity;

/**
 * Controller for managing werewolf animations
 * Links movement input to appropriate animations
 */
public class WerewolfAnimationController {
    
    private final WerewolfEntity werewolf;
    private int currentAnimation = WerewolfEntity.ANIM_IDLE;
    private int transitionTicks = 0;
    private static final int TRANSITION_DURATION = 5; // ticks

    public WerewolfAnimationController(WerewolfEntity werewolf) {
        this.werewolf = werewolf;
    }

    /**
     * Set the current animation state
     */
    public void setCurrentAnimation(int animationState) {
        if (currentAnimation != animationState) {
            currentAnimation = animationState;
            transitionTicks = 0;
        }
    }

    /**
     * Get the current animation being played
     */
    public int getCurrentAnimation() {
        return currentAnimation;
    }

    /**
     * Get animation blend factor (0.0 - 1.0) for smooth transitions
     */
    public float getAnimationBlend() {
        return Math.min(1.0F, transitionTicks / (float) TRANSITION_DURATION);
    }

    /**
     * Update animation controller each tick
     */
    public void tick() {
        if (transitionTicks < TRANSITION_DURATION) {
            transitionTicks++;
        }
    }

    // ===== ANIMATION SLOT MAPPINGS =====
    // These methods can be called from your animation JSON files or rendering code
    
    /**
     * Get idle animation - called when werewolf is stationary
     * CUSTOMIZE: Replace with your animation resource location
     */
    public String getIdleAnimation() {
        return "werewolf:animation.idle";
    }

    /**
     * Get walk forward animation
     * CUSTOMIZE: Add your walk forward animation
     */
    public String getWalkForwardAnimation() {
        return "werewolf:animation.walk_forward";
    }

    /**
     * Get walk backward animation
     * CUSTOMIZE: Add your walk backward animation
     */
    public String getWalkBackwardAnimation() {
        return "werewolf:animation.walk_backward";
    }

    /**
     * Get strafe left animation
     * CUSTOMIZE: Add your strafe left animation
     */
    public String getStrafeLeftAnimation() {
        return "werewolf:animation.strafe_left";
    }

    /**
     * Get strafe right animation
     * CUSTOMIZE: Add your strafe right animation
     */
    public String getStrafeRightAnimation() {
        return "werewolf:animation.strafe_right";
    }

    /**
     * Get sprint animation
     * CUSTOMIZE: Add your sprint animation
     */
    public String getSprintAnimation() {
        return "werewolf:animation.sprint";
    }

    /**
     * Get custom animation slot 1
     * CUSTOMIZE: Add your custom animation here
     */
    public String getCustomAnimation1() {
        return "werewolf:animation.custom_1";
    }

    /**
     * Get custom animation slot 2
     * CUSTOMIZE: Add your custom animation here
     */
    public String getCustomAnimation2() {
        return "werewolf:animation.custom_2";
    }

    /**
     * Get animation for current state
     */
    public String getAnimationForState(int state) {
        return switch (state) {
            case WerewolfEntity.ANIM_IDLE -> getIdleAnimation();
            case WerewolfEntity.ANIM_WALK_FORWARD -> getWalkForwardAnimation();
            case WerewolfEntity.ANIM_WALK_BACKWARD -> getWalkBackwardAnimation();
            case WerewolfEntity.ANIM_STRAFE_LEFT -> getStrafeLeftAnimation();
            case WerewolfEntity.ANIM_STRAFE_RIGHT -> getStrafeRightAnimation();
            case WerewolfEntity.ANIM_SPRINT -> getSprintAnimation();
            case WerewolfEntity.ANIM_CUSTOM_1 -> getCustomAnimation1();
            case WerewolfEntity.ANIM_CUSTOM_2 -> getCustomAnimation2();
            default -> getIdleAnimation();
        };
    }

    /**
     * Head tracking - interpolate head rotation based on look direction
     */
    public float getHeadRotationBlend(float partialTick) {
        return werewolf.getYRot() + (werewolf.yRotO - werewolf.getYRot()) * (1.0F - partialTick);
    }

    /**
     * Neck tracking - interpolate neck rotation based on vertical look
     */
    public float getNeckRotationBlend(float partialTick) {
        return werewolf.getXRot() + (werewolf.xRotO - werewolf.getXRot()) * (1.0F - partialTick);
    }
}
