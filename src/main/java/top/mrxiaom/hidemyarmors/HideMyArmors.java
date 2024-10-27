package top.mrxiaom.hidemyarmors;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.utility.MinecraftVersion;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class HideMyArmors extends JavaPlugin {
    ProtocolManager protocolManager;
    public boolean eraseEquipmentsInfo = false;
    protected boolean newVersion;
    protected boolean supportCMD;
    protected boolean twoHands;
    @Override
    public void onEnable() {
        protocolManager = ProtocolLibrary.getProtocolManager();
        protocolManager.addPacketListener(new EntityPacketAdapter(this));
        MinecraftVersion ver = protocolManager.getMinecraftVersion();
        try {
            newVersion = ver.isAtLeast(MinecraftVersion.NETHER_UPDATE);
        } catch (Throwable t) {
            newVersion = false;
        }
        try {
            supportCMD = ver.isAtLeast(MinecraftVersion.VILLAGE_UPDATE);
        } catch (Throwable t) {
            supportCMD = false;
        }
        try {
            twoHands = ver.isAtLeast(MinecraftVersion.COMBAT_UPDATE);
        } catch (Throwable t) {
            twoHands = false;
        }
        reloadConfig();
        getLogger().info("HideMyArmors 插件已启用");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if (sender.isOp()) {
            reloadConfig();
            sender.sendMessage("§a配置文件已重载");
        }
        return true;
    }

    @Override
    public void onDisable() {
        if (protocolManager != null) protocolManager.removePacketListeners(this);
    }

    @Override
    public void reloadConfig() {
        this.saveDefaultConfig();
        super.reloadConfig();

        FileConfiguration config = getConfig();
        eraseEquipmentsInfo = config.getBoolean("erase-entity-equipments-information", false);
    }
}
