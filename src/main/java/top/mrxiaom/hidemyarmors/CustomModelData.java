package top.mrxiaom.hidemyarmors;

import org.bukkit.inventory.meta.ItemMeta;

public class CustomModelData {
    public static void transfer(ItemMeta oldMeta, ItemMeta newMeta) {
        if (oldMeta.hasCustomModelData()) {
            newMeta.setCustomModelData(oldMeta.getCustomModelData());
        }
    }
}
