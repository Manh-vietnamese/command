package Sunflower.commands;

import Sunflower.config.Config;
import Sunflower.MainPlugin;
import Sunflower.messages.MessageManager;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class HealCommand implements CommandExecutor {

    private final MainPlugin plugin;
    private final MessageManager messageManager;
    private final Map<Player, Long> healCooldowns = new HashMap<>();

    private boolean checkCooldown(Player player, Map<Player, Long> cooldowns, int cooldownTime) {
        if (cooldowns.containsKey(player)) {
            long lastUsed = cooldowns.get(player);
            long currentTime = System.currentTimeMillis();
            long timeRemaining = (lastUsed + cooldownTime * 1000L) - currentTime;

            if (timeRemaining > 0) {
                Map<String, String> placeholders = new HashMap<>();
                placeholders.put("time", String.valueOf(timeRemaining / 1000));
                player.sendMessage(messageManager.get("Countdown", placeholders));
                return true; // 📌 Trả về true nếu vẫn đang trong cooldown
            }
        }
        return false; // 📌 Trả về false nếu có thể dùng lệnh
    }

    public HealCommand(MainPlugin plugin) {
        this.plugin = plugin;
        this.messageManager = plugin.getMessageManager();

        // Kiểm tra nếu `messageManager` bị null
        if (this.messageManager == null) {
            plugin.getLogger().severe("❌ MessageManager chưa được khởi tạo! Kiểm tra lại MainPlugin.java.");
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // 📌 Kiểm tra nếu lệnh được nhập từ Console
        if (!(sender instanceof Player)) {
            sender.sendMessage(messageManager.get("console_only"));
            return false;
        }

        Player player = (Player) sender;
        String rank = getPlayerRank(player);

        // 📌 Kiểm tra quyền: Nếu không có quyền, chặn lệnh
        if (rank == null) {
            player.sendMessage(messageManager.get("no_permission"));
            return false;
        }

        // 📌 Kiểm tra xem plugin đã load cấu hình hay chưa
        Config cfg = plugin.getConfigManager();
        if (cfg == null) {
            plugin.getLogger().severe("❌ ConfigManager chưa được khởi tạo! Kiểm tra lại MainPlugin.java.");
            player.sendMessage(messageManager.get("config_error"));
            return false;
        }

        int healCooldown = cfg.getHealCountdown(rank);
        if (healCooldown < 0) {
            plugin.getLogger().warning("⚠️ Giá trị cooldown cho heal của " + rank + " không hợp lệ! Dùng mặc định 30s.");
            healCooldown = 30;
        }

        // 📌 Gọi phương thức kiểm tra cooldown
        if (checkCooldown(player, healCooldowns, healCooldown)) {
            return false;
        }

        // 📌 Hồi máu đầy đủ cho người chơi
        player.setHealth(Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue());
        player.sendMessage(messageManager.get("heal.heal_success"));

        // 📌 Lưu cooldown
        healCooldowns.put(player, System.currentTimeMillis());

        return true;
    }

    private String getPlayerRank(Player player) {
        if (player.hasPermission("sun.countdown.heal.vip5")) return "vip5";
        if (player.hasPermission("sun.countdown.heal.vip4")) return "vip4";
        if (player.hasPermission("sun.countdown.heal.vip3")) return "vip3";
        if (player.hasPermission("sun.countdown.heal.vip2")) return "vip2";
        if (player.hasPermission("sun.countdown.heal.vip1")) return "vip1";
        if (player.hasPermission("sun.countdown.heal.vip0")) return "vip0";
        return null;  // 📌 Nếu không có quyền, trả về `null`
    }
}
