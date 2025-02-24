package com.spectrasonic.batteryminigame.model;

// Record que representa la coordenada del bloque y su identificador
public record BlockCoord(int x, int y, int z, int id) {
    // Comprueba si una ubicación coincide con estas coordenadas
    public boolean matches(org.bukkit.Location loc) {
        return loc.getBlockX() == x && loc.getBlockY() == y && loc.getBlockZ() == z;
    }
}
