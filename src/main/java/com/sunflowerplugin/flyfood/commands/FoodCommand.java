package com.sunflowerplugin.flyfood.commands;

import com.sunflowerplugin.flyfood.Config;
import com.sunflowerplugin.flyfood.MainPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class FoodCommand implements CommandExecutor {

    private final MainPlugin plugin;
    private final Map<Player, Long> foodCooldown = new HashMap<>();

    public FoodCommand(MainPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        Player player = (Player) sender;

        // Kiểm tra quyền của người chơi
        String permission = "sunflower.food.default"; // Quyền mặc định
        if (player.hasPermission("sunflower.food.vip1")) {
            permission = "sunflower.food.vip1";  // Nếu người chơi có quyền vip
        }

        // Kiểm tra quyền của người chơi trước khi thực hiện lệnh
        if (!player.hasPermission(permission)) {
            player.sendMessage("❌ You do not have the required permission to use this command.");
            return false;
        }

        // Kiểm tra quyền và lấy thời gian cooldown từ cấu hình
        Config cfg = plugin.getPluginConfig();
        int foodCooldownTime = cfg.getFoodCooldown(permission);

        // Nếu không có quyền trong config, gửi thông báo lỗi và không thực hiện lệnh
        if (foodCooldownTime == -1) {
            player.sendMessage("❌ You do not have the required permission or configuration for this command.");
            return false;
        }

        long currentTime = System.currentTimeMillis();
        long lastUsed = foodCooldown.getOrDefault(player, 0L);

        // Kiểm tra xem đã qua đủ thời gian cooldown chưa
        if (currentTime - lastUsed < foodCooldownTime * 1000L) {
            long remainingTime = (foodCooldownTime - (currentTime - lastUsed) / 1000);
            player.sendMessage("⏳ Please wait " + remainingTime + " more seconds before using this command again.");
            return false;
        }

        // Hồi đầy thanh đói
        player.setFoodLevel(20);
        player.sendMessage("✅ Your hunger has been restored!");

        // Lưu thời điểm dùng lệnh
        foodCooldown.put(player, currentTime);
        return true;
    }

    public void clearCooldowns() {
        foodCooldown.clear();
    }
}
