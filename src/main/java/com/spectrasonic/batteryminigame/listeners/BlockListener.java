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
        MessageUtils.sendConsoleMessage("Block placed - World: " + event.getBlock().getWorld().getName());
        
        if (!event.getBlock().getWorld().getName().equals("world")) {
            MessageUtils.sendConsoleMessage("Block rejected - Wrong world");
            return;
        }

        Player player = event.getPlayer();
        ItemStack itemInHand = event.getItemInHand();
        String batteryId = getBatteryId(itemInHand);
        
        if (batteryId == null) {
            MessageUtils.sendConsoleMessage("Block rejected - Not a valid battery item");
            return;
        }

        Location loc = event.getBlock().getLocation();
        MessageUtils.sendConsoleMessage("Checking coordinates - X: " + loc.getBlockX() + 
                                     ", Y: " + loc.getBlockY() + 
                                     ", Z: " + loc.getBlockZ());
        
        BlockCoord coord = blockManager.getBlockCoord(loc);
        
        if (coord != null) {
            event.setCancelled(true);
            
            try {
                NexoFurniture.place(batteryId, loc, Rotation.CLOCKWISE, BlockFace.UP);
                
                blockManager.setBlockState(coord, true);
                String message = "<green><bold>Se ha puesto el bloque " + coord.getId();
                MessageUtils.sendBroadcastMessage(message);
                MessageUtils.sendConsoleMessage(message);
                
                MessageUtils.sendConsoleMessage("Block states after placement: " + blockManager.getBlockStates());

                if (blockManager.areAllPlaced()) {
                    String opMessage = "<green><bold>Se han puesto todos los bloques";
                    Bukkit.getOnlinePlayers().stream()
                        .filter(Player::isOp)
                        .forEach(op -> op.sendMessage(miniMessage.deserialize(opMessage)));
                    MessageUtils.sendConsoleMessage(opMessage);
                }
            } catch (Exception e) {
                MessageUtils.sendConsoleMessage("<red>Error placing battery: " + e.getMessage());
            }
        } else {
            MessageUtils.sendConsoleMessage("<red>No matching coordinates found for placed block");
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        MessageUtils.sendConsoleMessage("Block broken - World: " + event.getBlock().getWorld().getName());
        
        if (!event.getBlock().getWorld().getName().equals("world")) {
            MessageUtils.sendConsoleMessage("Block break rejected - Wrong world");
            return;
        }

        Location loc = event.getBlock().getLocation();
        MessageUtils.sendConsoleMessage("Checking break coordinates - X: " + loc.getBlockX() + 
                                     ", Y: " + loc.getBlockY() + 
                                     ", Z: " + loc.getBlockZ());
        
        BlockCoord coord = blockManager.getBlockCoord(loc);
        
        if (coord != null) {
            // The NexoFurniture API will handle the block removal automatically
            blockManager.setBlockState(coord, false);
            String message = "<red><bold>Se ha quitado el bloque " + coord.getId();
            MessageUtils.sendBroadcastMessage(message);
            MessageUtils.sendConsoleMessage(message);
            MessageUtils.sendConsoleMessage("Block states after removal: " + blockManager.getBlockStates());
        } else {
            MessageUtils.sendConsoleMessage("<red>No matching coordinates found for broken block");
        }
    }
}