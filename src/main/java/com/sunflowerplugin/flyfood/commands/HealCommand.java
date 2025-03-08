package com.sunflowerplugin.flyfood.commands;

import com.sunflowerplugin.flyfood.Config;
import com.sunflowerplugin.flyfood.MainPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class HealCommand implements CommandExecutor {

    private final MainPlugin plugin;
    private final Map<Player, Long> healCooldowns = new HashMap<>();

    public HealCommand(MainPlugin plugin) {
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

        // 📌 Kiểm tra quyền: Nếu không có quyền, chặn lệnh
        if (rank == null) {
            player.sendMessage("❌ You do not have permission to use this command!");
            return false;
        }

        Config cfg = plugin.getConfigManager();
        int healCooldown = cfg.getHealCountdown(rank);

        // 📌 Kiểm tra cooldown
        if (healCooldowns.containsKey(player)) {
            long lastUsed = healCooldowns.get(player);
            long currentTime = System.currentTimeMillis();
            long timeRemaining = (lastUsed + healCooldown * 1000) - currentTime;

            if (timeRemaining > 0) {
                player.sendMessage("⏳ You must wait " + timeRemaining / 1000 + " seconds before using /heal again.");
                return false;
            }
        }

        // 📌 Hồi máu đầy đủ cho người chơi
        player.setHealth(player.getMaxHealth());
        player.sendMessage("❤️ You have been fully healed!");

        // 📌 Lưu cooldown
        healCooldowns.put(player, System.currentTimeMillis());

        return true;
    }

    // 📌 Phương thức xóa cooldown khi reload plugin
    public void clearCooldowns() {
        healCooldowns.clear();
    }

    private String getPlayerRank(Player player) {
        if (player.hasPermission("sunflower.heal.vip5")) return "vip5";
        if (player.hasPermission("sunflower.heal.vip4")) return "vip4";
        if (player.hasPermission("sunflower.heal.vip3")) return "vip3";
        return null;  // 📌 Nếu không có quyền, trả về `null`
    }
}
