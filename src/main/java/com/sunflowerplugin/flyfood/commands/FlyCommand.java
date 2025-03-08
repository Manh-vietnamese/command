package com.sunflowerplugin.flyfood.commands;

import com.sunflowerplugin.flyfood.config.Config;
import com.sunflowerplugin.flyfood.MainPlugin;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

public class FlyCommand implements CommandExecutor {

    private final MainPlugin plugin;
    private final Map<Player, Long> flyStartTimes = new HashMap<>();
    private final Map<Player, Boolean> countdownActive = new HashMap<>();
    private final Map<Player, Boolean> flyEnabled = new HashMap<>();
    private final Map<Player, Integer> flyCountdownRemaining = new HashMap<>();

    public FlyCommand(MainPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        Player player = (Player) sender;
        String rank = getPlayerRank(player);

        // 📌 Chặn lệnh nếu người chơi đã có chế độ bay
        if (player.getAllowFlight() && player.isFlying()) {
            player.sendMessage("⚠️ You are already flying!");
            return false;
        }

        // 📌 Kiểm tra quyền: Nếu không có quyền, chặn lệnh
        if (rank == null) {
            sender.sendMessage(plugin.getMessageManager().get("no_permission"));
            return false;
        }

        Config cfg = plugin.getConfigManager();
        int flyCountdown = cfg.getFlyCountdown(rank);
        int flyUsageTime = cfg.getFlyUsageTime(rank);

        // 📌 Kiểm tra nếu người chơi đang trong countdown
        if (countdownActive.getOrDefault(player, false)) {
            int remainingCountdown = flyCountdownRemaining.getOrDefault(player, 0);

            // 📌 Tạo placeholders
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("time", String.valueOf(remainingCountdown));

            // 📌 Gửi tin nhắn với thời gian countdown còn lại
            player.sendMessage(plugin.getMessageManager().get("Countdown", placeholders));
            return false;
        }

        // 📌 Nếu đang bay mà nhập lại lệnh, tắt bay và bắt đầu countdown
        if (flyEnabled.getOrDefault(player, false)) {
            disableFlight(player, flyCountdown);
            return true;
        }

        // 📌 Bắt đầu thời gian bay
        enableFlight(player, flyUsageTime);
        return true;
    }

    // 📌 Bật chế độ bay
    private void enableFlight(Player player, int flyUsageTime) {
        player.setAllowFlight(true);
        player.setFlying(true);
        flyStartTimes.put(player, System.currentTimeMillis());
        flyEnabled.put(player, true);

        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("time", String.valueOf(flyUsageTime));
        player.sendMessage(plugin.getMessageManager().get("fly_enabled", placeholders));

        // 📌 Bắt đầu bộ đếm thời gian bay
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!flyStartTimes.containsKey(player)) {
                    cancel();
                    return;
                }

                long elapsedTime = (System.currentTimeMillis() - flyStartTimes.get(player)) / 1000;
                int remainingTime = flyUsageTime - (int) elapsedTime;

                if (remainingTime > 0) {
                    Map<String, String> placeholders = new HashMap<>();
                    placeholders.put("time", String.valueOf(remainingTime));
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(plugin.getMessageManager().get("fly_time_left", placeholders)));
                } else {
                    disableFlight(player, plugin.getConfigManager().getFlyCountdown(getPlayerRank(player)));
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    // 📌 Tắt chế độ bay và bắt đầu countdown
    private void disableFlight(Player player, int countdownTime) {
        player.setFlying(false);
        player.setAllowFlight(false);
        flyStartTimes.remove(player);
        flyEnabled.put(player, false);
        countdownActive.put(player, true);
        flyCountdownRemaining.put(player, countdownTime);

        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("time", String.valueOf(countdownTime));
        player.sendMessage(plugin.getMessageManager().get("fly_disabled", placeholders));

        // 📌 Bắt đầu countdown
        new BukkitRunnable() {
            @Override
            public void run() {
                int remainingCountdown = flyCountdownRemaining.getOrDefault(player, 0);
                if (remainingCountdown > 0) {
                    flyCountdownRemaining.put(player, remainingCountdown - 1);
                } else {
                    countdownActive.put(player, false);
                    flyCountdownRemaining.remove(player);
                    player.sendMessage(plugin.getMessageManager().get("fly_ready"));
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    // 📌 Kiểm tra quyền của người chơi theo đúng `config.yml`
    private String getPlayerRank(Player player) {
        if (player.hasPermission("sun.usage.time.fly.vip3")) return "vip3";
        if (player.hasPermission("sun.usage.time.fly.vip2")) return "vip2";
        if (player.hasPermission("sun.usage.time.fly.vip1")) return "vip1";
        if (player.hasPermission("sun.usage.time.fly.vip0")) return "vip0";
        return null;  // 📌 Nếu không có quyền, trả về `null`
    }
}
