/*
 *
 *  *
 *  *  * MobCoins - Earn coins for killing mobs.
 *  *  * Copyright (C) 2018 Max Berkelmans AKA LemmoTresto
 *  *  *
 *  *  * This program is free software: you can redistribute it and/or modify
 *  *  * it under the terms of the GNU General Public License as published by
 *  *  * the Free Software Foundation, either version 3 of the License, or
 *  *  * (at your option) any later version.
 *  *  *
 *  *  * This program is distributed in the hope that it will be useful,
 *  *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  *  * GNU General Public License for more details.
 *  *  *
 *  *  * You should have received a copy of the GNU General Public License
 *  *  * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *  *
 *
 */

package me.max.lemonmobcoins.common.files.gui;

import com.google.common.reflect.TypeToken;
import me.max.lemonmobcoins.bukkit.gui.GuiHolder;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GuiManager {

    private int rows;
    private String command;
    private String title;
    private List<GuiMobCoinItem> items;

    public GuiManager(ConfigurationNode config, Logger logger){
        config = config.getNode("gui");
        rows = config.getNode("rows").getInt();
        command = config.getNode("command").getString();
        title = ChatColor.translateAlternateColorCodes('&', config.getNode("name").getString());
        items = new ArrayList<>();

        for (ConfigurationNode key : config.getNode("items").getChildrenList()){
            ConfigurationNode itemNode = config.getNode("items", key.getString());
            try {
                items.add(new GuiMobCoinItem.Builder(key.getString())
                        .setAmount(itemNode.getNode("amount").getInt())
                        .setSlot(itemNode.getNode("slot").getInt())
                        .setMaterial(itemNode.getNode("material").getString())
                        .setDisplayname(ChatColor.translateAlternateColorCodes('&', itemNode.getNode("displayname").getString()))
                        .setGlowing(itemNode.getNode("glowing").getBoolean())
                        .setLore(itemNode.getNode("lore").getList(TypeToken.of(String.class))
                                .stream()
                                .map(s -> ChatColor.translateAlternateColorCodes('&', s))
                                .collect(Collectors.toList()))
                        .setPermission(itemNode.getNode("permission").getBoolean())
                        .setPrice(itemNode.getNode("price").getDouble())
                        .setCommands(itemNode.getNode("commands").getList(TypeToken.of(String.class))).build());
            } catch (ObjectMappingException e) {
                logger.error("Error mapping config shop items!");
                e.printStackTrace();
            }
        }
    }

    public int getRows() {
        return rows;
    }

    @NotNull
    public List<GuiMobCoinItem> getItems() {
        return items;
    }

    @NotNull
    public String getTitle() {
        return title;
    }

    public Inventory getInventory(){
        Inventory inv = Bukkit.createInventory(new GuiHolder(), rows * 9, title);
        items.forEach(item -> inv.setItem(item.getSlot(), item.toBukkitItemStack()));
        return inv;
    }

    public GuiMobCoinItem getGuiMobCoinItemFromItemStack(@NotNull ItemStack item) {
        return items.stream().filter(guiMobCoinItem -> guiMobCoinItem.toBukkitItemStack().equals(item)).findFirst().orElse(null);
    }

    @NotNull
    public String getCommand() {
        return command;
    }
}