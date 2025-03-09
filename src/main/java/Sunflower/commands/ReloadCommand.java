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
        // 📌 Kiểm tra quyền `sun.admin`
        if (!sender.hasPermission("sun.admin")) {
            sender.sendMessage(messageManager.get("no_permission"));
            return false;
        }

        new ConfigValidator(plugin).validateFiles();
        plugin.reloadConfigs();  // 📌 Khôi phục file nếu bị mất
        foodCmd.clearCooldowns();  // Xóa cooldown khi reload
        sender.sendMessage(messageManager.get("plugin.reload_success"));

        return true;
    }
}
