package tk.shanebee.nbt;

import ch.njol.skript.Skript;
import ch.njol.skript.SkriptAddon;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.IOException;

public class NBeeT extends JavaPlugin {

    NBeeT instance;
    SkriptAddon addon;

    @Override
    public void onEnable() {
        if ((Bukkit.getPluginManager().getPlugin("Skript") != null) && (Skript.isAcceptRegistrations())) {
            String nms = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
            if (nms.equals("v1_12_R1")) {
                getLogger().info(ChatColor.AQUA + "Compatible NMS version: " + nms);
                instance = this;
                addon = Skript.registerAddon(this);
                try {
                    addon.loadClasses("tk.shanebee.nbt", "elements");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                getLogger().info(ChatColor.GREEN + "Successfully enabled");
            }
            else {
                getLogger().info(ChatColor.RED + "Incompatible NMS version: " + nms);
                getLogger().info(ChatColor.GOLD + "Please use Spigot 1.12.2");
                Bukkit.getPluginManager().disablePlugin(this);
            }
        }
        else {
            getLogger().info(ChatColor.RED + "Dependency Skript was not found");
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {}

}