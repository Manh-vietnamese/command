package com.sunflowerplugin.flyfood.commands;

import com.sunflowerplugin.flyfood.config.Config;
import com.sunflowerplugin.flyfood.MainPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class FoodCommand implements CommandExecutor {

    private final MainPlugin plugin;
    private final Map<Player, Long> foodCooldowns = new HashMap<>();

    public FoodCommand(MainPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("❌ Only players can use this command!");
            return false;
        }

        Player player = (Player) sender;
        String rank = getPlayerRank(player);

        // 📌 Kiểm tra quyền
        if (rank == null) {
            player.sendMessage("❌ You do not have permission to use this command!");
            return false;
        }

        Config cfg = plugin.getConfigManager();
        int foodCooldown = cfg.getFoodCountdown(rank);

        if (foodCooldowns.containsKey(player)) {
            long lastUsed = foodCooldowns.get(player);
            long currentTime = System.currentTimeMillis();
            long timeRemaining = (lastUsed + foodCooldown * 1000L) - currentTime;

            if (timeRemaining > 0) {
                player.sendMessage("⏳ You must wait " + timeRemaining / 1000 + " seconds before using /food again.");
                return false;
            }
        }

        foodCooldowns.put(player, System.currentTimeMillis());
        player.sendMessage("🍽️ Food command executed!");

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
