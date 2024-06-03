package me.desngr.util.builder;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;

public class ItemBuilder {

    private final ItemStack itemStack;
    private final ItemMeta meta;

    public ItemBuilder(Material material) {
        this.itemStack = new ItemStack(material);
        this.meta = itemStack.getItemMeta();
    }

    public ItemBuilder displayName(String name) {
        meta.setDisplayName(name);

        return this;
    }

    public ItemBuilder lore(List<String> lore) {
        meta.setLore(lore);

        return this;
    }

    public ItemBuilder setOwner(String owner) {
        ((SkullMeta) meta).setOwner(owner);

        return this;
    }

    public ItemStack build() {
        itemStack.setItemMeta(meta);

        return itemStack;
    }
}
