package com.yourname.werewolf.item;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.nbt.CompoundTag;
import com.yourname.werewolf.transformation.TransformationManager;

public class SyringeItem extends Item {
    
    public SyringeItem(Item.Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        
        if (!level.isClientSide && player.isShiftKeyDown()) {
            // Trigger transformation
            TransformationManager.toggleWerewolfTransformation(player);
            return InteractionResultHolder.success(itemStack);
        }
        
        return InteractionResultHolder.pass(itemStack);
    }
}
