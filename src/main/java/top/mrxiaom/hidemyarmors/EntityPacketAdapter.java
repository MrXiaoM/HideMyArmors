package top.mrxiaom.hidemyarmors;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.wrappers.EnumWrappers.ItemSlot;
import com.comphenix.protocol.wrappers.Pair;
import de.tr7zw.changeme.nbtapi.*;
import de.tr7zw.changeme.nbtapi.iface.ReadableNBT;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.permissions.Permissible;

import java.util.List;

public class EntityPacketAdapter extends PacketAdapter {
    HideMyArmors plugin;
    public EntityPacketAdapter(HideMyArmors plugin) {
        super(plugin, ListenerPriority.NORMAL, PacketType.Play.Server.ENTITY_EQUIPMENT);
        this.plugin = plugin;
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        PacketContainer packet = event.getPacket();
        Player player = event.getPlayer();
        if (packet.getType().equals(PacketType.Play.Server.ENTITY_EQUIPMENT)) {
            List<Player> players = player.getWorld().getPlayers();
            int entityId = packet.getIntegers().readSafely(0);
            if (entityId == player.getEntityId()) return;
            players.removeIf(it -> it.getEntityId() != entityId);
            Permissible perm = players.isEmpty() ? null : players.get(0);

            StructureModifier<List<Pair<ItemSlot, ItemStack>>> modifier = packet.getSlotStackPairLists();
            List<Pair<ItemSlot, ItemStack>> list = modifier.read(0);
            for (Pair<ItemSlot, ItemStack> pair : list) {
                if (checkHide(perm, pair.getFirst())) {
                    pair.setSecond(new ItemStack(Material.AIR));
                    continue;
                }
                if (plugin.eraseEquipmentsInfo) {
                    ItemStack newItem = eraseItemMeta(pair.getSecond());
                    if (newItem != null) {
                        pair.setSecond(newItem);
                    }
                }
            }
            modifier.write(0, list);
        }
    }

    private boolean checkHide(Permissible perm, ItemSlot slot) {
        if (perm == null) return false;
        if (perm.hasPermission("hidemyarmors.hide." + slot.name().toLowerCase())) return true;
        return hasArmorsPerm(perm, slot) || hasHandsPerm(perm, slot);
    }

    private boolean hasArmorsPerm(Permissible perm, ItemSlot slot) {
        return ((slot == ItemSlot.HEAD
                || slot == ItemSlot.CHEST
                || slot == ItemSlot.LEGS
                || slot == ItemSlot.FEET)
                && perm.hasPermission("hidemyarmors.hide.armors"));
    }

    private boolean hasHandsPerm(Permissible perm, ItemSlot slot) {
        return ((slot == ItemSlot.MAINHAND
                || slot == ItemSlot.OFFHAND)
                && perm.hasPermission("hidemyarmors.hide.hands"));
    }

    private ItemStack eraseItemMeta(ItemStack item) {
        if (item == null || item.getType().isAir()) return null;
        ItemMeta oldMeta = item.getItemMeta();
        if (oldMeta == null) return null;

        if (NBT.get(item, nbt -> {
            boolean has = nbt.hasTag("magic_cosmetic"); // 1.20.5+
            if (has) return true;
            ReadableNBT tag = nbt.hasTag("tag", NBTType.NBTTagCompound) ? nbt.getCompound("tag") : null;
            return tag != null && tag.hasTag("magic_cosmetic");
        })) return null;

        ItemStack newItem = new ItemStack(item.getType(), item.getAmount());
        ItemMeta meta = newItem.getItemMeta();
        boolean flag = false;
        if (oldMeta instanceof SkullMeta) {
            meta = oldMeta;
            flag = true;
        }
        if (!flag && meta != null) {
            if (oldMeta instanceof LeatherArmorMeta && meta instanceof LeatherArmorMeta) {
                Color color = ((LeatherArmorMeta) oldMeta).getColor();
                ((LeatherArmorMeta) meta).setColor(color);
            }
            if (oldMeta.hasEnchants()) {
                meta.addEnchant(Enchantment.WATER_WORKER, 1919, true);
            }
            if (oldMeta.hasCustomModelData()) {
                meta.setCustomModelData(oldMeta.getCustomModelData());
            }
        }
        newItem.setItemMeta(meta);
        return newItem;
    }
}
