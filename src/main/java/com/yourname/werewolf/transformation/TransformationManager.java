package com.yourname.werewolf.transformation;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import com.yourname.werewolf.entity.WerewolfEntity;
import com.yourname.werewolf.capability.PlayerTransformationData;
import net.minecraft.world.phys.Vec3;

public class TransformationManager {
    
    private static final String TRANSFORMATION_NBT_KEY = "WerewolfTransformation";
    private static final String ARMOR_NBT_KEY = "StoredArmor";
    private static final String OFFHAND_NBT_KEY = "StoredOffhand";

    /**
     * Toggle werewolf transformation for a player
     */
    public static void toggleWerewolfTransformation(Player player) {
        if (isTransformed(player)) {
            revertTransformation(player);
        } else {
            transformToWerewolf(player);
        }
    }

    /**
     * Transform player into werewolf
     */
    public static void transformToWerewolf(Player player) {
        if (player.level().isClientSide) return;
        
        // Store armor and offhand items
        storePlayerArmor(player);
        
        // Create werewolf entity
        WerewolfEntity werewolf = new WerewolfEntity(player.level());
        werewolf.setPos(player.getX(), player.getY(), player.getZ());
        werewolf.setYRot(player.getYRot());
        werewolf.setXRot(player.getXRot());
        werewolf.setOwnerUUID(player.getUUID());
        
        // Add werewolf to world
        player.level().addFreshEntity(werewolf);
        
        // Mark player as transformed
        CompoundTag tag = getTransformationTag(player);
        tag.putBoolean("IsTransformed", true);
        tag.putLong("WerewolfEntityId", werewolf.getId());
        
        // Hide player or make invisible
        player.setInvisible(true);
        player.setNoGravity(true);
    }

    /**
     * Revert player from werewolf form
     */
    public static void revertTransformation(Player player) {
        if (player.level().isClientSide) return;
        
        CompoundTag tag = getTransformationTag(player);
        
        // Restore armor
        restorePlayerArmor(player);
        
        // Remove werewolf entity if it exists
        if (tag.contains("WerewolfEntityId")) {
            long entityId = tag.getLong("WerewolfEntityId");
            var entity = player.level().getEntity((int) entityId);
            if (entity instanceof WerewolfEntity werewolf) {
                werewolf.remove(net.minecraft.world.entity.Entity.RemovalReason.DISCARDED);
            }
        }
        
        // Mark as not transformed
        tag.putBoolean("IsTransformed", false);
        tag.remove("WerewolfEntityId");
        
        // Restore player visibility and gravity
        player.setInvisible(false);
        player.setNoGravity(false);
    }

    /**
     * Check if player is currently transformed
     */
    public static boolean isTransformed(Player player) {
        return getTransformationTag(player).getBoolean("IsTransformed");
    }

    /**
     * Store player's armor and offhand items
     */
    private static void storePlayerArmor(Player player) {
        CompoundTag armorTag = new CompoundTag();
        ListTag armorList = new ListTag();
        
        // Store armor (head, chest, legs, feet)
        for (ItemStack armor : player.getArmorSlots()) {
            CompoundTag itemTag = new CompoundTag();
            armor.save(itemTag);
            armorList.add(itemTag);
        }
        
        armorTag.put("ArmorItems", armorList);
        
        // Store offhand item
        CompoundTag offhandTag = new CompoundTag();
        player.getOffhandItem().save(offhandTag);
        armorTag.put("OffhandItem", offhandTag);
        
        getTransformationTag(player).put(ARMOR_NBT_KEY, armorTag);
        
        // Clear armor and offhand from player
        for (int i = 0; i < 4; i++) {
            player.getArmorSlots().set(i, ItemStack.EMPTY);
        }
        player.setItemInHand(net.minecraft.world.InteractionHand.OFF_HAND, ItemStack.EMPTY);
    }

    /**
     * Restore player's armor and offhand items
     */
    private static void restorePlayerArmor(Player player) {
        CompoundTag tag = getTransformationTag(player);
        
        if (!tag.contains(ARMOR_NBT_KEY)) return;
        
        CompoundTag armorTag = tag.getCompound(ARMOR_NBT_KEY);
        ListTag armorList = armorTag.getList("ArmorItems", Tag.TAG_COMPOUND);
        
        // Restore armor
        for (int i = 0; i < armorList.size() && i < 4; i++) {
            ItemStack armor = ItemStack.of(armorList.getCompound(i));
            player.getArmorSlots().set(i, armor);
        }
        
        // Restore offhand
        if (armorTag.contains("OffhandItem")) {
            ItemStack offhand = ItemStack.of(armorTag.getCompound("OffhandItem"));
            player.setItemInHand(net.minecraft.world.InteractionHand.OFF_HAND, offhand);
        }
    }

    /**
     * Get or create transformation NBT tag for player
     */
    private static CompoundTag getTransformationTag(Player player) {
        CompoundTag playerTag = player.getPersistentData();
        if (!playerTag.contains(TRANSFORMATION_NBT_KEY)) {
            playerTag.put(TRANSFORMATION_NBT_KEY, new CompoundTag());
        }
        return playerTag.getCompound(TRANSFORMATION_NBT_KEY);
    }

    /**
     * Sync werewolf position and rotation with player
     */
    public static void syncWerewolfWithPlayer(Player player, WerewolfEntity werewolf) {
        werewolf.setPos(player.getX(), player.getY(), player.getZ());
        werewolf.setYRot(player.getYRot());
        werewolf.setXRot(player.getXRot());
    }
}
