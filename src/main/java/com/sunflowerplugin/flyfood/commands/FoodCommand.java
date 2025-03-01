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
    // Tạo map để lưu cooldown cho từng player
    private final Map<Player, Long> foodCooldown = new HashMap<>();

    public FoodCommand(MainPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        Player player = (Player) sender;

        // Xác định quyền "sunflower.food.default" hay "sunflower.food.vip1"
        String permission = "sunflower.food.default";
        if (player.hasPermission("sunflower.food.vip1")) {
            permission = "sunflower.food.vip1";
        }

        // Nếu player không có quyền thì báo
        if (!player.hasPermission(permission)) {
            player.sendMessage("❌ You do not have permission to use this command.");
            return false;
        }

        // Lấy cooldown từ file config
        Config cfg = plugin.getPluginConfig();
        int foodCooldownTime = cfg.getFoodCooldown(permission);

        // Nếu không thể lấy giá trị, plugin đã bị vô hiệu hóa trong Config.java
        if (foodCooldownTime == -1) {
            return false;
        }

        // Debugging log to check the cooldown time
        plugin.getLogger().info("Cooldown time for permission " + permission + ": " + foodCooldownTime);

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
