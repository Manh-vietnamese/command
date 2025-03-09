package Sunflower.commands;

import Sunflower.MainPlugin;
import Sunflower.config.ConfigValidator;
import Sunflower.messages.MessageManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ReloadCommand implements CommandExecutor {

    private final MainPlugin plugin;
    private final FoodCommand foodCmd;
    private final MessageManager messageManager;

    public ReloadCommand(MainPlugin plugin, FoodCommand foodCmd) {
        this.plugin = plugin;
        this.foodCmd = foodCmd;
        this.messageManager = plugin.getMessageManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(messageManager.get("reload_missing_args"));
            return false;
        }

        if (args[0].equalsIgnoreCase("flyfood")) {
            new ConfigValidator(plugin).validateFiles();
            plugin.reloadConfigs();  // 📌 Khôi phục file nếu bị mất
            foodCmd.clearCooldowns();  // Xóa cooldown khi reload
            sender.sendMessage(messageManager.get("reload_success"));
        }

        return true;
    }

}

