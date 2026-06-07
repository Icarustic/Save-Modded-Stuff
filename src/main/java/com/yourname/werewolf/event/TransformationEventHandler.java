package com.yourname.werewolf.event;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import com.yourname.werewolf.transformation.TransformationManager;
import com.yourname.werewolf.item.SyringeItem;

@Mod.EventBusSubscriber(modid = "yourmodid")
public class TransformationEventHandler {
    
    /**
     * Handle player right-click with Syringe item
     */
    @SubscribeEvent
    public static void onPlayerInteract(PlayerInteractEvent.RightClickItem event) {
        Player player = event.getEntity();
        ItemStack heldItem = event.getItemStack();
        
        if (heldItem.getItem() instanceof SyringeItem) {
            if (!player.level().isClientSide && player.isShiftKeyDown()) {
                event.setCanceled(true);
                TransformationManager.toggleWerewolfTransformation(player);
            }
        }
    }

    /**
     * Handle player death - revert transformation if player dies
     */
    @SubscribeEvent
    public static void onPlayerDeath(PlayerEvent.Clone event) {
        Player player = event.getEntity();
        
        if (TransformationManager.isTransformed(player)) {
            TransformationManager.revertTransformation(player);
        }
    }

    /**
     * Handle player logout - clean up werewolf entity
     */
    @SubscribeEvent
    public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        Player player = event.getEntity();
        
        if (TransformationManager.isTransformed(player)) {
            TransformationManager.revertTransformation(player);
        }
    }
}
