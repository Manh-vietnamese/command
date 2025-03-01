package com.sunflowerplugin.flyfood.commands;

import com.sunflowerplugin.flyfood.Config;
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

    // Lưu thời gian bay của người chơi
    private final Map<Player, Long> flyStartTimes = new HashMap<>();
    private final Map<Player, Boolean> countdownActive = new HashMap<>();
    private final Map<Player, Boolean> flyEnabled = new HashMap<>();  // Lưu trạng thái bật của /fly

    public FlyCommand(MainPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("❌ Only players can use this command!");
            return false;
        }

        Player player = (Player) sender;

        // Kiểm tra quyền của người chơi
        String permission = "sunflower.fly";  // Quyền mặc định
        if (player.hasPermission("sunflower.fly.vip1")) {
            permission = "sunflower.fly.vip1";  // Nếu người chơi có quyền VIP
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
            player.sendMessage("❌ Your permissions or configuration are incorrect!");
            return false;
        }

        // Chặn người chơi sử dụng lại /fly khi đang trong countdown
        if (countdownActive.getOrDefault(player, false)) {
            player.sendMessage("⏳ You must wait for the countdown to finish before using /fly again!");
            return false;
        }

        // Nếu người chơi đang bay, tắt chế độ bay và bắt đầu countdown
        if (player.isFlying()) {
            player.setFlying(false);
            player.setAllowFlight(false);  // Vô hiệu hóa quyền bay
            player.sendMessage("✈️ Fly mode disabled. You must wait " + flyCountdown + "s before flying again.");
            flyStartTimes.remove(player);  // Xóa thời gian bay trước đó
            flyEnabled.put(player, false);  // Đánh dấu /fly đã bị vô hiệu hóa
            countdownActive.put(player, true);  // Đánh dấu countdown đang chạy

            // Bắt đầu đếm ngược thời gian countdown (Không hiển thị trên Action Bar)
            new BukkitRunnable() {
                int countdownTime = flyCountdown;

                @Override
                public void run() {
                    if (countdownTime > 0) {
                        countdownTime--;
                    } else {
                        // Khi countdown kết thúc, cho phép bay lại
                        countdownActive.put(player, false);  // Bỏ chặn sử dụng lệnh
                        player.sendMessage("✅ You can now use /fly again.");
                        cancel();
                    }
                }
            }.runTaskTimer(plugin, 0L, 20L); // Đếm ngược mỗi giây

            return false;
        }

        // Nếu /fly đã bật nhưng người chơi không đang bay, không reset thời gian Action Bar
        if (flyEnabled.getOrDefault(player, false)) {
            player.sendMessage("🚀 Fly mode is already enabled! Start flying to use your remaining time.");
            return false;
        }

        // Bật chế độ bay
        player.setAllowFlight(true);
        player.setFlying(true);
        flyStartTimes.put(player, System.currentTimeMillis());  // Lưu thời gian bắt đầu bay
        flyEnabled.put(player, true);  // Đánh dấu rằng chế độ bay đã được bật

        player.sendMessage("✈️ Fly mode enabled! You have " + flyUsageTime + " seconds.");

        // Bắt đầu đếm ngược thời gian bay và hiển thị lên Action Bar
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!flyStartTimes.containsKey(player)) {
                    cancel();
                    return;
                }

                long elapsedTime = (System.currentTimeMillis() - flyStartTimes.get(player)) / 1000;  // Thời gian đã bay (giây)
                int remainingTime = flyUsageTime - (int) elapsedTime;  // Thời gian còn lại

                if (remainingTime > 0) {
                    // Hiển thị thời gian còn lại trên Action Bar
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("⏳ Fly time left: " + remainingTime + "s"));
                } else {
                    // Khi hết thời gian, tắt chế độ bay
                    player.setFlying(false);
                    player.setAllowFlight(false);
                    player.sendMessage("❌ Your fly time has expired!");
                    flyStartTimes.remove(player);
                    flyEnabled.put(player, false);  // Đánh dấu chế độ bay đã bị vô hiệu hóa
                    cancel();  // Dừng đếm ngược
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);  // Cập nhật mỗi giây (20 ticks)

        return true;
    }
}
