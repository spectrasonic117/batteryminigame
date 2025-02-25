package com.spectrasonic.batteryminigame.listeners;

import com.spectrasonic.batteryminigame.Main;
import com.spectrasonic.batteryminigame.manager.BlockManager;
import com.spectrasonic.batteryminigame.model.BlockCoord;
import net.kyori.adventure.text.minimessage.MiniMessage;
import com.spectrasonic.batteryminigame.Utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.Location;
import org.bukkit.Rotation;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import com.nexomc.nexo.api.NexoFurniture;
import com.nexomc.nexo.api.NexoItems;
import lombok.RequiredArgsConstructor;
import org.bukkit.GameMode;

@RequiredArgsConstructor
public class BlockListener implements Listener {
    private final Main plugin;
    private final BlockManager blockManager;
    private final MiniMessage miniMessage = MiniMessage.miniMessage();
    private final String[] validBatteryIds = {"bateria", "bateria2", "bateria3", "bateria4"};

    private String getBatteryId(ItemStack item) {
        for (String batteryId : validBatteryIds) {
            ItemStack batteryItem = NexoItems.itemFromId(batteryId).build();
            if (batteryItem != null && batteryItem.equals(item)) {
                return batteryId;
            }
        }
        return null;
    }

   @EventHandler
public void onBlockPlace(BlockPlaceEvent event) {
    if (!event.getBlock().getWorld().getName().equals("world")) {
        return;
    }

    Player player = event.getPlayer();
    ItemStack itemInHand = event.getItemInHand();
    String batteryId = getBatteryId(itemInHand);
    
    if (batteryId == null) {
        return;
    }

    // Check if the player is in Survival or Adventure mode
    if (player.getGameMode() == GameMode.SURVIVAL || player.getGameMode() == GameMode.ADVENTURE) {
        // Remove one item from the player's hand
        itemInHand.setAmount(itemInHand.getAmount() - 1);
    }

    Location loc = event.getBlock().getLocation();
    BlockCoord coord = blockManager.getBlockCoord(loc);
    
    if (coord != null) {
        event.setCancelled(true);
        
        try {
            NexoFurniture.place(batteryId, loc, Rotation.CLOCKWISE, BlockFace.UP);
            blockManager.setBlockState(coord, true);
            MessageUtils.sendBroadcastMessage("<green><bold>[✔] Se ha puesto la Bateria <yellow><bold>" + coord.getId());

            if (blockManager.areAllPlaced()) {
                String opMessage = "<yellow><bold>[!] Se han puesto todas las Baterias";
                Bukkit.getOnlinePlayers().stream()
                    .filter(Player::isOp)
                    .forEach(op -> op.sendMessage(miniMessage.deserialize(opMessage)));
            }
        } catch (Exception e) {
            // Silent fail
        }
    }
}

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (!event.getBlock().getWorld().getName().equals("world")) {
            return;
        }

        Location loc = event.getBlock().getLocation();
        BlockCoord coord = blockManager.getBlockCoord(loc);
        
        if (coord != null) {
            blockManager.setBlockState(coord, false);
            MessageUtils.sendBroadcastMessage("<red><bold>[X] Quitada la Bateria <yellow><bold>" + coord.getId());
        }
    }
}