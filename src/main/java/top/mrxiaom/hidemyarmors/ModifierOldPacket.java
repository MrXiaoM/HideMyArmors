package top.mrxiaom.hidemyarmors;

import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.wrappers.EnumWrappers;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permissible;

public class ModifierOldPacket {
    /**
     * 1.8 - 1.15.2 等旧版本
     */
    public static void modify(PacketContainer packet, Permissible perm, EntityPacketAdapter.TriFunction<Permissible, EnumWrappers.ItemSlot, ItemStack, ItemStack> modifyItem) {
        StructureModifier<EnumWrappers.ItemSlot> itemSlots = packet.getItemSlots();
        StructureModifier<ItemStack> itemModifier = packet.getItemModifier();

        EnumWrappers.ItemSlot slot = itemSlots.readSafely(0);
        ItemStack item = itemModifier.readSafely(0);
        ItemStack newItem = modifyItem.apply(perm, slot, item);
        if (newItem != null) {
            itemModifier.writeSafely(0, newItem);
        }
    }
}
