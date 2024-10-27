package top.mrxiaom.hidemyarmors;

import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.Pair;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permissible;

import java.util.List;

public class ModifierNewPacket {
    /**
     * 1.16+ 新版本
     */
    public static void modify(PacketContainer packet, Permissible perm, EntityPacketAdapter.TriFunction<Permissible, EnumWrappers.ItemSlot, ItemStack, ItemStack> modifyItem) {
        StructureModifier<List<Pair<EnumWrappers.ItemSlot, ItemStack>>> modifier = packet.getSlotStackPairLists();

        List<Pair<EnumWrappers.ItemSlot, ItemStack>> list = modifier.readSafely(0);
        for (Pair<EnumWrappers.ItemSlot, ItemStack> pair : list) {
            ItemStack newItem = modifyItem.apply(perm, pair.getFirst(), pair.getSecond());
            if (newItem != null) {
                pair.setSecond(newItem);
            }
        }
        modifier.write(0, list);
    }
}
