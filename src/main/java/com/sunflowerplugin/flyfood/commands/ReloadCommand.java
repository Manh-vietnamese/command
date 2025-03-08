package com.sunflowerplugin.flyfood.commands;

import com.sunflowerplugin.flyfood.MainPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ReloadCommand implements CommandExecutor {

    private final MainPlugin plugin;
    private final FoodCommand foodCmd;

    public ReloadCommand(MainPlugin plugin, FoodCommand foodCmd) {
        this.plugin = plugin;
        this.foodCmd = foodCmd;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("❌ Please specify the plugin to reload.");
            return false;
        }

        if (args[0].equalsIgnoreCase("flyfood")) {
            plugin.reloadConfigs();  // 📌 Khôi phục file nếu bị mất
            foodCmd.clearCooldowns();  // Xóa cooldown khi reload
            sender.sendMessage("✔️ FlyFood plugin config reloaded and checked for missing files!");
        }

        return true;
    }
}
