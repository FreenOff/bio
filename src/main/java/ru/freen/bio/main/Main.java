package ru.freen.bio.main;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import ru.freen.bio.commands.CommandBio;
import ru.freen.bio.events.*;
import ru.freen.bio.utils.MySQL;
import java.util.HashMap;

public final class Main extends JavaPlugin {

    public static HashMap<Player, String> bid = new HashMap<Player, String>();
    private static Main plugin;

    @Override
    public void onEnable() {
        plugin=this;
        saveDefaultConfig();
        MySQL.connect(getConfig().getString("MySQL.jdbc"),getConfig().getString("MySQL.username"), getConfig().getString("MySQL.password"));
        MySQL.createPlayersTable();
        getCommand("bio").setExecutor(new CommandBio());
        Bukkit.getPluginManager().registerEvents(new ListenerClick(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerJoin(), this);
    }

    @Override
    public void onDisable() {
        MySQL.executorService.shutdown();
        MySQL.closeConnectionPool();
    }

    public static Main getInstance(){
        return plugin;
    }
}
