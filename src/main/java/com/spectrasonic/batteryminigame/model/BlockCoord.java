package com.spectrasonic.batteryminigame.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BlockCoord {
    private final int x;
    private final int y;
    private final int z;
    private final int id;

    // Comprueba si una ubicación coincide con estas coordenadas
    public boolean matches(org.bukkit.Location loc) {
        return loc.getBlockX() == x && loc.getBlockY() == y && loc.getBlockZ() == z;
    }
}
