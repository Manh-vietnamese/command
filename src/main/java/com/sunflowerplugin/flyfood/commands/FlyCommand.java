package com.sunflowerplugin.flyfood.commands;

import com.sunflowerplugin.flyfood.Config;
import com.sunflowerplugin.flyfood.MainPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FlyCommand implements CommandExecutor {

    private final MainPlugin plugin;

    public FlyCommand(MainPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;
        // Kiểm tra quyền
        if (!player.hasPermission("sunflower.fly")) {
            player.sendMessage("You do not have permission to fly.");
            return false;
        }

        // Lấy config mới nhất
        Config cfg = plugin.getPluginConfig();
        String permission = "sunflower.fly";  // Quyền mặc định
        if (player.hasPermission("sunflower.fly.vip1")) {
            permission = "sunflower.fly.vip1";  // Nếu người chơi có quyền vip
        }

        // Lấy countdown và thời gian sử dụng bay từ cấu hình
        int flyCountdown = cfg.getFlyCountdown(permission);
        int flyUsageTime = cfg.getFlyUsageTime(permission);

        // Nếu không thể lấy giá trị, plugin đã bị vô hiệu hóa trong Config.java
        if (flyCountdown == -1 || flyUsageTime == -1) {
            return false;
        }

        // Bật/tắt chế độ bay
        if (player.isFlying()) {
            player.setFlying(false);
            player.sendMessage("Fly mode disabled. Countdown: " + flyCountdown + " seconds.");
        } else {
            player.setFlying(true);
            player.sendMessage("Fly mode enabled. Usage time: " + flyUsageTime + " seconds. Countdown: " + flyCountdown + " seconds.");
        }

        return true;
    }
}
