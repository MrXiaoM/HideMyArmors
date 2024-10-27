package top.mrxiaom.hidemyarmors;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
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
import org.jetbrains.annotations.Nullable;

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

            if (plugin.newVersion) {
                ModifierNewPacket.modify(packet, perm, this::modifyItem);
            } else if (plugin.twoHands) {
                ModifierOldPacket.modify(packet, perm, this::modifyItem);
            } else {
                ModifierVeryOldPacket.modify(packet, perm, this::modifyItem);
            }
        }
    }

    public interface TriFunction<A, B, C, R> {
        R apply(A a, B b, C c);
    }

    @Nullable
    private ItemStack modifyItem(Permissible perm, int slot, ItemStack item) {
        if (checkHide(perm, slot)) {
             return new ItemStack(Material.AIR);
        }
        if (plugin.eraseEquipmentsInfo) {
            return eraseItemMeta(item);
        }
        return null;
    }

    private final String[] slotsName = new String[] { "mainhand", "head", "chest", "legs", "feet", "offhand" };
    private boolean checkHide(Permissible perm, int slot) {
        if (perm == null) return false;
        if (perm.hasPermission("hidemyarmors.hide." + slotsName[slot])) return true;
        return hasArmorsPerm(perm, slot) || hasHandsPerm(perm, slot);
    }

    private boolean hasArmorsPerm(Permissible perm, int slot) {
        return ((slot == ModifierVeryOldPacket.SLOT_HEAD
                || slot == ModifierVeryOldPacket.SLOT_CHEST
                || slot == ModifierVeryOldPacket.SLOT_LEGS
                || slot == ModifierVeryOldPacket.SLOT_FEET)
                && perm.hasPermission("hidemyarmors.hide.armors"));
    }

    private boolean hasHandsPerm(Permissible perm, int slot) {
        return ((slot == ModifierVeryOldPacket.SLOT_HAND
                || slot == ModifierVeryOldPacket.SLOT_OFF_HAND)
                && perm.hasPermission("hidemyarmors.hide.hands"));
    }

    private ItemStack eraseItemMeta(ItemStack item) {
        if (item == null || item.getType().equals(Material.AIR)) return null;
        ItemMeta oldMeta = item.getItemMeta();
        if (oldMeta == null) return null;

        if (NBT.get(item, nbt -> {
            boolean has = nbt.hasTag("magic_cosmetic");
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
            if (plugin.supportCMD) { // 1.14+
                CustomModelData.transfer(oldMeta, meta);
            }
        }
        newItem.setItemMeta(meta);
        return newItem;
    }
}
