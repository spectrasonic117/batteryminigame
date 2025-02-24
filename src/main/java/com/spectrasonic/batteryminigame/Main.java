package com.spectrasonic.batteryminigame;

import com.spectrasonic.batteryminigame.Utils.MessageUtils;
import com.spectrasonic.batteryminigame.listeners.BlockListener;
import com.spectrasonic.batteryminigame.manager.BlockManager;
import org.bukkit.plugin.java.JavaPlugin;
import lombok.Getter;

public final class Main extends JavaPlugin {

    // Gestor de estados de bloques
    @Getter
    private BlockManager blockManager;

    @Override
    public void onEnable() {
        blockManager = new BlockManager();

        registerCommands();
        registerEvents();
        MessageUtils.sendStartupMessage(this);

    }

    @Override
    public void onDisable() {
        MessageUtils.sendShutdownMessage(this);
    }

    public void registerCommands() {
        // Set Commands Here
    }

    public void registerEvents() {
        getServer().getPluginManager().registerEvents(new BlockListener(this, blockManager), this);
    }

}
