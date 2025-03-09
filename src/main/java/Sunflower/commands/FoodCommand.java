package Sunflower.commands;

import Sunflower.config.Config;
import Sunflower.MainPlugin;
import Sunflower.messages.MessageManager;
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
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        Player player = (Player) sender;
        String rank = getPlayerRank(player);

        // 📌 Kiểm tra quyền
        if (rank == null) {
            player.sendMessage(messageManager.get("no_permission"));
            return false;
        }

        // 📌 Kiểm tra xem Config có bị null không
        Config cfg = plugin.getConfigManager();
        if (cfg == null) {
            //plugin.getLogger().severe("❌ ConfigManager chưa được khởi tạo! Kiểm tra lại MainPlugin.java");
            player.sendMessage("❌ Plugin gặp lỗi khi lấy config! Liên hệ admin.");
            return false;
        }

        int foodCooldown = cfg.getFoodCountdown(rank);

        // 📌 Kiểm tra cooldown
        if (foodCooldowns.containsKey(player)) {
            long lastUsed = foodCooldowns.get(player);
            long currentTime = System.currentTimeMillis();
            long timeRemaining = (lastUsed + foodCooldown * 1000L) - currentTime;

            if (timeRemaining > 0) {
                Map<String, String> placeholders = new HashMap<>();
                placeholders.put("time", String.valueOf(timeRemaining / 1000));

                player.sendMessage(messageManager.get("Countdown", placeholders));
                return false;
            }
        }

        // 📌 HỒI THANH ĐÓI - Đặt thanh đói về mức tối đa
        player.setFoodLevel(20);  // Hồi đầy thanh đói (hunger bar)
        player.setSaturation(10); // Đặt saturation để tránh giảm đói ngay lập tức

        // 📌 Lưu thời gian cooldown
        foodCooldowns.put(player, System.currentTimeMillis());
        player.sendMessage(messageManager.get("food.food_executed"));

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
        if (player.hasPermission("sun.countdown.food.vip1")) return "vip1";
        if (player.hasPermission("sun.countdown.food.vip0")) return "vip0";
        return null;  // Nếu không có quyền, trả về `null0
    }
}
