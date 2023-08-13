package ru.freen.bio.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import ru.freen.bio.utils.MySQL;

public class PlayerJoin implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        MySQL.playerExists(event.getPlayer().getName()).thenAccept(hasPlayerInDb -> {
            if(hasPlayerInDb)
                MySQL.addPlayer(event.getPlayer().getName(), "null");
        });
    }

}
