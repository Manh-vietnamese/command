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

public class HealCommand implements CommandExecutor {

    private final MainPlugin plugin;
    private final MessageManager messageManager;
    private final Map<Player, Long> healCooldowns = new HashMap<>();

    public HealCommand(MainPlugin plugin) {
        this.plugin = plugin;
        this.messageManager = plugin.getMessageManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(messageManager.get("heal_only_players"));
            return false;
        }

        Player player = (Player) sender;
        String rank = getPlayerRank(player);

        // 📌 Kiểm tra quyền: Nếu không có quyền, chặn lệnh
        if (rank == null) {
            player.sendMessage(messageManager.get("heal_no_permission"));
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
                Map<String, String> placeholders = new HashMap<>();
                placeholders.put("time", String.valueOf(timeRemaining / 1000));
                player.sendMessage(messageManager.get("heal_cooldown", placeholders));
                return false;
            }
        }

        // 📌 Hồi máu đầy đủ cho người chơi
        player.setHealth(player.getMaxHealth());
        player.sendMessage(messageManager.get("heal_success"));

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
