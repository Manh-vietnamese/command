package com.sunflowerplugin.flyfood.commands;

import com.sunflowerplugin.flyfood.MainPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Objects;

public class ReloadCommand implements CommandExecutor {

    private final MainPlugin plugin;

    public ReloadCommand(MainPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Kiểm tra và tạo lại config nếu cần thiết
        plugin.checkAndCreateConfig();  // Gọi lại phương thức này để đảm bảo thư mục/config được tạo lại

        plugin.reloadConfigs();  // Tải lại cấu hình

        // Lấy executor của lệnh "food" (phải cùng trong plugin.yml)
        CommandExecutor foodExecutor = Objects.requireNonNull(plugin.getCommand("food")).getExecutor();
        if (foodExecutor instanceof FoodCommand) {
            FoodCommand foodCmd = (FoodCommand) foodExecutor;
            foodCmd.clearCooldowns();  // Gọi hàm xóa cooldown
            sender.sendMessage("Đã tải lại config và reset cooldown cho tất cả người chơi!");
        }

        return true;
    }
}
