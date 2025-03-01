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

        // Kiểm tra quyền của người chơi
        String permission = "sunflower.fly";  // Quyền mặc định
        if (player.hasPermission("sunflower.fly.vip1")) {
            permission = "sunflower.fly.vip1";  // Nếu người chơi có quyền vip
        }

        // Kiểm tra quyền của người chơi trước khi thực hiện lệnh
        if (!player.hasPermission(permission)) {
            player.sendMessage("❌ You do not have the required permission to use this command.");
            return false;
        }

        // Lấy countdown và thời gian sử dụng bay từ cấu hình
        Config cfg = plugin.getPluginConfig();
        int flyCountdown = cfg.getFlyCountdown(permission);
        int flyUsageTime = cfg.getFlyUsageTime(permission);

        // Nếu không có quyền trong config, gửi thông báo lỗi và không thực hiện lệnh
        if (flyCountdown == -1 || flyUsageTime == -1) {
            player.sendMessage("❌ You do not have the required permission or configuration for this command.");
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
