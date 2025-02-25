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

        Location loc = event.getBlock().getLocation();
        BlockCoord coord = blockManager.getBlockCoord(loc);
        
        if (coord != null) {
            event.setCancelled(true);
            
            if (placeFurniture(batteryId, loc)) {
                blockManager.setBlockState(coord, true);
                String message = "<green><bold>[✔︎]Se ha puesto la Bateria " + coord.getId();
                MessageUtils.sendBroadcastMessage(message);

                if (blockManager.areAllPlaced()) {
                    String opMessage = "<yellow><bold>[!] Se han puesto todas las Baterias";
                    Bukkit.getOnlinePlayers().stream()
                        .filter(Player::isOp)
                        .forEach(op -> op.sendMessage(miniMessage.deserialize(opMessage)));
                }

                // Remove item from inventory if in Adventure or Survival mode
                if (player.getGameMode() == GameMode.ADVENTURE || player.getGameMode() == GameMode.SURVIVAL) {
                    player.getInventory().removeItem(itemInHand);
                }
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (!event.getBlock().getWorld().getName().equals("world")) {
            return;
        }

        Player player = event.getPlayer();
        Location loc = event.getBlock().getLocation();
        BlockCoord coord = blockManager.getBlockCoord(loc);
        
        if (coord != null) {
            blockManager.setBlockState(coord, false);
            String message = "<red><bold>[X] Se ha quitado la Bateria " + coord.getId();
            MessageUtils.sendBroadcastMessage(message);

            // Return item to inventory if in Adventure or Survival mode
            if (player.getGameMode() == GameMode.ADVENTURE || player.getGameMode() == GameMode.SURVIVAL) {
                String batteryId = "bateria" + coord.getId(); // Assuming the ID corresponds to the battery
                ItemStack batteryItem = NexoItems.itemFromId(batteryId).build();
                player.getInventory().addItem(batteryItem);
            }
        }
    }

    private boolean placeFurniture(String batteryId, Location loc) {
        try {
            NexoFurniture.place(batteryId, loc, Rotation.CLOCKWISE, BlockFace.UP);
            return true;
        } catch (Exception e) {
            // Log the exception or handle it accordingly
            return false;
        }
    }
}