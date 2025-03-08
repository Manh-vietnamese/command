package com.sunflowerplugin.flyfood.commands;

import com.sunflowerplugin.flyfood.config.Config;
import com.sunflowerplugin.flyfood.MainPlugin;
import com.sunflowerplugin.flyfood.messages.MessageManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class FoodCommand implements CommandExecutor {

    private final MainPlugin plugin;
    private final MessageManager messageManager;
    private final Map<Player, Long> foodCooldowns = new HashMap<>();

    public FoodCommand(MainPlugin plugin) {
        this.plugin = plugin;
        this.messageManager = plugin.getMessageManager();

        // 📌 Kiểm tra nếu messageManager bị null
        if (this.messageManager == null) {
            plugin.getLogger().severe("❌ MessageManager chưa được khởi tạo! Kiểm tra lại MainPlugin.java");
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(messageManager != null ? messageManager.get("food_only_players") : "❌ Only players can use this command!");
            return false;
        }

        Player player = (Player) sender;
        String rank = getPlayerRank(player);

        // 📌 Kiểm tra quyền
        if (rank == null) {
            player.sendMessage(messageManager != null ? messageManager.get("food_no_permission") : "❌ You do not have permission to use this command!");
            return false;
        }

        // 📌 Kiểm tra xem Config có bị null không
        Config cfg = plugin.getConfigManager();
        if (cfg == null) {
            plugin.getLogger().severe("❌ ConfigManager chưa được khởi tạo! Kiểm tra lại MainPlugin.java");
            player.sendMessage("❌ Plugin gặp lỗi khi lấy config! Liên hệ admin.");
            return false;
        }

        int foodCooldown = cfg.getFoodCountdown(rank);
        if (foodCooldown < 0) {
            plugin.getLogger().warning("⚠️ Giá trị cooldown cho food của " + rank + " không hợp lệ! Dùng mặc định 30s.");
            foodCooldown = 30;
        }

        // 📌 Kiểm tra cooldown
        if (foodCooldowns.containsKey(player)) {
            long lastUsed = foodCooldowns.get(player);
            long currentTime = System.currentTimeMillis();
            long timeRemaining = (lastUsed + foodCooldown * 1000L) - currentTime;

            if (timeRemaining > 0) {
                Map<String, String> placeholders = new HashMap<>();
                placeholders.put("time", String.valueOf(timeRemaining / 1000));

                player.sendMessage(messageManager != null ? messageManager.get("food_cooldown", placeholders) : "⏳ You must wait " + timeRemaining / 1000 + " seconds before using /food again.");
                return false;
            }
        }

        // 📌 Lưu thời gian cooldown
        foodCooldowns.put(player, System.currentTimeMillis());
        player.sendMessage(messageManager != null ? messageManager.get("food_executed") : "🍽️ Food command executed!");

        return true;
    }

    // 📌 Phương thức này sẽ xóa cooldown khi reload plugin
    public void clearCooldowns() {
        foodCooldowns.clear();
    }

    private String getPlayerRank(Player player) {
        if (player.hasPermission("sun.countdown.food.vip5")) return "vip5";
        if (player.hasPermission("sun.countdown.food.vip4")) return "vip4";
        if (player.hasPermission("sun.countdown.food.vip3")) return "vip3";
        if (player.hasPermission("sun.countdown.food.vip2")) return "vip2";
        return null;  // Nếu không có quyền, trả về `null`
    }
}
