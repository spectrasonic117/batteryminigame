package com.spectrasonic.batteryminigame.listeners;

import com.spectrasonic.batteryminigame.Main;
import com.spectrasonic.batteryminigame.manager.BlockManager;
import com.spectrasonic.batteryminigame.model.BlockCoord;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.Location;

public class BlockListener implements Listener {
    private final Main plugin;
    private final BlockManager blockManager;
    // Instancia de MiniMessage para formatear mensajes
    private final MiniMessage miniMessage = MiniMessage.miniMessage();

    public BlockListener(Main plugin, BlockManager blockManager) {
        this.plugin = plugin;
        this.blockManager = blockManager;
    }

    // Evento para manejar la colocación de bloques
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        // Procesar solo si es un bloque BARRIER en el mundo "world"
        if (!event.getBlock().getType().equals(Material.BARRIER) ||
            !event.getBlock().getWorld().getName().equals("world"))
            return;

        Location loc = event.getBlock().getLocation();
        BlockCoord coord = blockManager.getBlockCoord(loc);
        if (coord == null) return;

        // Actualiza el estado y notifica a todos los jugadores
        blockManager.setBlockState(coord, true);
        String message = "<green><bold>Se ha puesto el bloque " + coord.id();
        Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(miniMessage.deserialize(message)));

        // Si todos los bloques están colocados, notifica a los operadores
        if (blockManager.areAllPlaced()) {
            String opMessage = "<green><bold>Se han puesto todos los bloques";
            Bukkit.getOnlinePlayers().stream()
                .filter(player -> player.isOp())
                .forEach(player -> player.sendMessage(miniMessage.deserialize(opMessage)));
        }
    }

    // Evento para manejar la remoción de bloques
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        // Procesar solo si es un bloque BARRIER en el mundo "world"
        if (!event.getBlock().getType().equals(Material.BARRIER) ||
            !event.getBlock().getWorld().getName().equals("world"))
            return;

        Location loc = event.getBlock().getLocation();
        BlockCoord coord = blockManager.getBlockCoord(loc);
        if (coord == null) return;

        // Actualiza el estado y notifica a todos los jugadores
        blockManager.setBlockState(coord, false);
        String message = "<red><bold>Se ha quitado el bloque " + coord.id();
        Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(miniMessage.deserialize(message)));
    }
}
