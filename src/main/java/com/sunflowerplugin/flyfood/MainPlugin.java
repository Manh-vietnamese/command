package com.sunflowerplugin.flyfood;

import com.sunflowerplugin.flyfood.commands.FlyCommand;
import com.sunflowerplugin.flyfood.commands.FoodCommand;
import com.sunflowerplugin.flyfood.commands.ReloadCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Objects;

public class MainPlugin extends JavaPlugin {

    private Config configManager;

    @Override
    public void onEnable() {
        getLogger().info("Bật FlyFood...");

        // Kiểm tra và tạo lại config nếu tệp bị thiếu hoặc bị hỏng
        this.checkAndCreateConfig();  // Gọi checkAndCreateConfig() để tạo thư mục nếu cần

        // Khởi tạo configManager
        this.configManager = new Config(this);

        try {
            if (getCommand("fly") == null || getCommand("food") == null || getCommand("freload") == null) {
                getLogger().severe("❌ Lỗi: Một hoặc nhiều lệnh bị thiếu trong plugin.yml!");
                return;
            }

            Objects.requireNonNull(getCommand("fly")).setExecutor(new FlyCommand(this));
            Objects.requireNonNull(getCommand("food")).setExecutor(new FoodCommand(this));
            Objects.requireNonNull(getCommand("freload")).setExecutor(new ReloadCommand(this));

            reloadConfigs();
        } catch (Exception e) {
            getLogger().severe("❌ Lỗi khi đăng ký lệnh: " + e.getMessage());
            getLogger().warning("Exception StackTrace:");
            getLogger().warning(e.toString());
        }

        getLogger().info("✅ Plugin FlyFood đã được bật!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Plugin FlyFood đang vô hiệu hóa...");
    }

    // Kiểm tra và tạo lại file config nếu cần thiết
    public void checkAndCreateConfig() {
        File dataFolder = getDataFolder();

        // Kiểm tra và tạo thư mục dữ liệu nếu nó không tồn tại
        if (!dataFolder.exists()) {
            getLogger().info("Tạo thư mục dữ liệu cho plugin...");
            dataFolder.mkdir();  // Tạo thư mục nếu không tồn tại
        }

        // Tạo tệp config.yml nếu chưa tồn tại
        File configFile = new File(dataFolder, "config.yml");
        if (!configFile.exists()) {
            getLogger().info("Tạo mới tệp config.yml...");
            saveDefaultConfig();  // Sao chép tệp config mặc định từ resources
        }
    }

    public void reloadConfigs() {
        try {
            // Đọc lại file config.yml
            this.configManager.reloadConfig();
            getLogger().info("✅ Đã tải lại cấu hình thành công!");
        } catch (Exception e) {
            getLogger().severe("❌ Lỗi khi tải lại cấu hình: " + e.getMessage());
            getLogger().warning("Exception StackTrace:");
            getLogger().warning(e.toString());
        }
    }

    // Hàm cho phép lấy configManager ở bất kỳ đâu
    public Config getPluginConfig() {
        return configManager;
    }
}
