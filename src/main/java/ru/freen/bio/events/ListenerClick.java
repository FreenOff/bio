package ru.freen.bio.events;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import ru.freen.bio.main.Main;
import ru.freen.bio.utils.Color;
import ru.freen.bio.utils.MySQL;

public class ListenerClick implements Listener {

    public void onClick(InventoryClickEvent event){
        if(event.getView().getTitle().equalsIgnoreCase("BIO || Заявки")){
            if(event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("Ничего нет")){
                event.setCancelled(false);
                return;
            }
            String targetPlayer = event.getCurrentItem().getItemMeta().getDisplayName();
            try {
                if (event.getClick().isLeftClick()) {
                    MySQL.updatePlayerBiography(targetPlayer, Main.bid.get(targetPlayer));
                    Main.bid.remove(Bukkit.getPlayer(targetPlayer));
                    Bukkit.getPlayer(targetPlayer).sendMessage(Color.parser("&aВаша заявка на биографию принята ежже!!!"));
                    event.getWhoClicked().sendMessage(Color.parser("&2Принято: &aзаявка игрока " + targetPlayer + " принята!"));
                    return;
                }
                if (event.getClick().isRightClick()) {
                    Main.bid.remove(Bukkit.getPlayer(targetPlayer));
                    Bukkit.getPlayer(targetPlayer).sendMessage(Color.parser("&cВаша заявка на биографию отклонена!!!"));
                    event.getWhoClicked().sendMessage(Color.parser("&4Отказ: &cзаявка игрока " + targetPlayer + " отклонена!"));
                    return;
                }
            }catch(Exception ignored){
                event.getWhoClicked().sendMessage(Color.parser("&4Ошибка: &cигрок вышел!"));
                Main.bid.remove(Bukkit.getPlayer(targetPlayer));
            }
        }
    }

}
