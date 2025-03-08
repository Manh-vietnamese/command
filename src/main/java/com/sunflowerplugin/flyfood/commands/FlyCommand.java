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

    public FlyCommand(MainPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
//        if (!(sender instanceof Player)) {
//            sender.sendMessage("❌ Only players can use this command!");
//            return false;
//        }

        Player player = (Player) sender;
        String rank = getPlayerRank(player);

        // 📌 Kiểm tra quyền: Nếu không có quyền, chặn lệnh
        if (rank == null) {
            sender.sendMessage(plugin.getMessageManager().get("no_permission"));
            return false;
        }

        Config cfg = plugin.getConfigManager();
        int flyCountdown = cfg.getFlyCountdown(rank);
        int flyUsageTime = cfg.getFlyUsageTime(rank);

        if (countdownActive.getOrDefault(player, false)) {
            sender.sendMessage(plugin.getMessageManager().get("fly_countdown"));
            return false;
        }

        if (player.isFlying()) {
            player.setFlying(false);
            player.setAllowFlight(false);

            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("time", String.valueOf(flyCountdown));
            sender.sendMessage(plugin.getMessageManager().get("fly_disabled", placeholders));

            flyStartTimes.remove(player);
            flyEnabled.put(player, false);
            countdownActive.put(player, true);

            new BukkitRunnable() {
                int countdownTime = flyCountdown;

                @Override
                public void run() {
                    if (countdownTime > 0) {
                        countdownTime--;
                    } else {
                        countdownActive.put(player, false);
                        sender.sendMessage(plugin.getMessageManager().get("fly_ready"));
                        cancel();
                    }
                }
            }.runTaskTimer(plugin, 0L, 20L);

            return false;
        }

        player.setAllowFlight(true);
        player.setFlying(true);
        flyStartTimes.put(player, System.currentTimeMillis());
        flyEnabled.put(player, true);

        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("time", String.valueOf(flyUsageTime));
        player.sendMessage(plugin.getMessageManager().get("fly_enabled", placeholders));

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
                    player.setFlying(false);
                    player.setAllowFlight(false);
                    player.sendMessage(plugin.getMessageManager().get("fly_expired"));
                    flyStartTimes.remove(player);
                    flyEnabled.put(player, false);
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);

        return true;
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
