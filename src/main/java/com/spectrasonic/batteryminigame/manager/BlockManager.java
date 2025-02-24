// File: src/main/java/com/spectrasonic/batteryminigame/manager/BlockManager.java
package com.spectrasonic.batteryminigame.manager;

import com.spectrasonic.batteryminigame.model.BlockCoord;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Gestor para manejar las posiciones y estado de los bloques
public class BlockManager {
    private final List<BlockCoord> blockCoords;
    private final Map<BlockCoord, Boolean> blockStates;

    public BlockManager() {
        blockCoords = new ArrayList<>();
        // Definir posiciones específicas con su id respectiva
        blockCoords.add(new BlockCoord(218, 83, -1449, 1));
        blockCoords.add(new BlockCoord(277, 73, -1399, 2));
        blockCoords.add(new BlockCoord(159, 73, -1399, 3));
        blockCoords.add(new BlockCoord(218, 76, -1340, 4));

        blockStates = new HashMap<>();
        // Inicializar estado de cada bloque como no colocado (false)
        for (BlockCoord coord : blockCoords) {
            blockStates.put(coord, false);
        }
    }

    // Devuelve la coordenada si la ubicación coincide con alguna predefinida
    public BlockCoord getBlockCoord(Location loc) {
        for (BlockCoord coord : blockCoords) {
            if (coord.matches(loc)) {
                return coord;
            }
        }
        return null;
    }

    // Actualiza el estado del bloque (true = colocado, false = retirado)
    public void setBlockState(BlockCoord coord, boolean state) {
        blockStates.put(coord, state);
    }

    // Comprueba si todos los bloques están colocados
    public boolean areAllPlaced() {
        return blockStates.values().stream().allMatch(state -> state);
    }
}
