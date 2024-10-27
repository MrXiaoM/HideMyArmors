package top.mrxiaom.hidemyarmors;

import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.wrappers.EnumWrappers;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permissible;

public class ModifierOldPacket {
    public static int convertSlot(EnumWrappers.ItemSlot slot) {
        switch (slot) {
            case MAINHAND: return ModifierVeryOldPacket.SLOT_HAND;
            case HEAD: return ModifierVeryOldPacket.SLOT_HEAD;
            case CHEST: return ModifierVeryOldPacket.SLOT_CHEST;
            case LEGS: return ModifierVeryOldPacket.SLOT_LEGS;
            case FEET: return ModifierVeryOldPacket.SLOT_FEET;
            case OFFHAND: return ModifierVeryOldPacket.SLOT_OFF_HAND;
            default: return -1;
        }
    }
    /**
     * 1.9 - 1.15.2 等旧版本
     */
    public static void modify(PacketContainer packet, Permissible perm, EntityPacketAdapter.TriFunction<Permissible, Integer, ItemStack, ItemStack> modifyItem) {
        StructureModifier<EnumWrappers.ItemSlot> itemSlots = packet.getItemSlots();
        StructureModifier<ItemStack> itemModifier = packet.getItemModifier();

        EnumWrappers.ItemSlot slot = itemSlots.readSafely(0);
        int slotNum = convertSlot(slot);
        if (slotNum == -1) return;
        ItemStack item = itemModifier.readSafely(0);
        ItemStack newItem = modifyItem.apply(perm, slotNum, item);
        if (newItem != null) {
            itemModifier.writeSafely(0, newItem);
        }
    }
}
