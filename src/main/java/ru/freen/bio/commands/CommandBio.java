package ru.freen.bio.commands;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import ru.freen.bio.main.Main;
import ru.freen.bio.utils.Color;
import ru.freen.bio.utils.MySQL;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandBio implements CommandExecutor {


    //bio do, set, reset, help, заявка на био
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if(strings.length==0) {
            commandSender.sendMessage(Color.parser("&6➤ &fПомощь:"));
            commandSender.sendMessage(Color.parser("&a/bio - биография об персонаж"));
            commandSender.sendMessage(Color.parser("&f"));
            commandSender.sendMessage(Color.parser("&eПодкоманды:"));
            commandSender.sendMessage(Color.parser("&f"));
            commandSender.sendMessage(Color.parser("&e/bio <краткое описание персонажа (60 символов)> &7- подать заявку на подтверждение биографии"));
            commandSender.sendMessage(Color.parser("&e/bio do &7- вывести в чат свою биографию"));
            return true;
        }

        if(strings[0].equalsIgnoreCase("set")){
            if(commandSender.hasPermission("bio.set")) {
                if (strings.length == 1 || strings.length == 2 || String.join(" ", Arrays.copyOfRange(strings, 2, strings.length)).length() > 60) {
                    commandSender.sendMessage(Color.parser("&4Ошибка: &cЧто-то не так"));
                    return true;
                }
                String bio = String.join(" ", Arrays.copyOfRange(strings, 2, strings.length));
                MySQL.updatePlayerBiography(strings[1], bio);
                commandSender.sendMessage(Color.parser("&aУспешно!"));
                return true;
            }
        }else if(strings[0].equalsIgnoreCase("reset")) {
            if (commandSender.hasPermission("bio.reset")) {
                if (strings.length == 1) {
                    commandSender.sendMessage(Color.parser("4Ошибка: &cЧто-то не так"));
                    return true;
                }
                MySQL.updatePlayerBiography(strings[1], "null");
                commandSender.sendMessage(Color.parser("&aУспешно!"));
            }
        }else if(strings[0].equalsIgnoreCase("do")) {
            if(!MySQL.playerExists(commandSender.getName()).join() || MySQL.getPlayerBiography(commandSender.getName()).join().equalsIgnoreCase("null")){
                commandSender.sendMessage(Color.parser("&4Ошибка: &cУ вас отсутствует краткая биография. Подать заявку: /bio <краткое описание персонажа>"));
                return true;
            }
            ((Player)commandSender).performCommand("/do "+MySQL.getPlayerBiography(commandSender.getName()));
            return true;
        }else if(strings[0].equalsIgnoreCase("list")){
            Inventory inv = Bukkit.createInventory(null, 54, Color.parser("&6&lBIO &0&l|| Заявки"));
            ItemStack barrier = new ItemStack(Material.BARRIER);
            ItemMeta barrierMeta = barrier.getItemMeta();
            barrierMeta.setDisplayName(Color.parser("&cНичего нет"));
            List<String> loreBarrier = new ArrayList<>();
            loreBarrier.add(Color.parser("&f"));
            loreBarrier.add(Color.parser("&fНет заявок на одобрение!"));
            loreBarrier.add(Color.parser("&f"));
            barrierMeta.setLore(loreBarrier);
            barrier.setItemMeta(barrierMeta);
            if(Main.bid.isEmpty()){
                for(int slots=9; slots<=45; slots++) {
                    inv.setItem(slots, barrier);
                }
                ((Player)commandSender).openInventory(inv);
                return true;
            }
            int slot = 9;
            for (Player players : Main.bid.keySet()) {
                if (slot >= 45) break;
                ItemStack bids = new ItemStack(Material.NAME_TAG);
                ItemMeta bidsMeta = bids.getItemMeta();
                bidsMeta.setDisplayName(Color.parser("&e"+players.getName()));
                List<String> loreBids = new ArrayList<>();
                loreBids.add(Color.parser("&cБиография: "+Main.bid.get(players)));
                loreBids.add(Color.parser("&a[ЛКМ, чтобы принять]"));
                loreBids.add(Color.parser("&c[ПКМ, чтобы отклонить]"));
                bidsMeta.setLore(loreBids);
                bids.setItemMeta(bidsMeta);
                inv.setItem(slot++, bids);
            }
            ((Player)commandSender).openInventory(inv);
        }else{
            if(Main.bid.containsKey((Player)commandSender)){
                commandSender.sendMessage(Color.parser("&4Ошибка: &cВы уже подали заявку, ожидайте одобрения"));
                return true;
            }
            String bio = String.join(" ", Arrays.copyOfRange(strings, 0, strings.length));
            if(bio.length()>60){
                commandSender.sendMessage(Color.parser("&4Ошибка: &cКраткая биография может содержать до 60 символов включительно ("+bio.length()+")"));
                return true;
            }
            if(commandSender.isOp()){
                MySQL.updatePlayerBiography(commandSender.getName(), bio);
                commandSender.sendMessage(Color.parser("&aУспешно!"));
                return true;
            }
            Main.bid.put((Player)commandSender, bio);
            commandSender.sendMessage(Color.parser("&2Успешно! &aВы отправили заявку на одобрение краткой биографии"));
            Bukkit.broadcast(Color.parser("&8INFO: &7Новая заявка на смену краткой биографии от "+commandSender.getName()+"!\n&8INFO: &7Просмотр заявок на смену биографий: /bio list"), "bio.warning");
        }

        return true;
    }

}
