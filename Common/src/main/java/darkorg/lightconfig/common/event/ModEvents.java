package darkorg.lightconfig.common.event;

import darkorg.lightconfig.common.LightConfig;
import darkorg.lightconfig.common.api.ConfigType;
import darkorg.lightconfig.common.api.ModConfig;
import darkorg.lightconfig.common.impl.AbstractModConfig;
import darkorg.lightconfig.common.impl.ConfigFileWatcher;
import darkorg.lightconfig.common.impl.ConfigLoader;
import darkorg.lightconfig.common.impl.ConfigSyncData;
import darkorg.lightconfig.common.platform.Services;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.level.storage.LevelResource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public final class ModEvents {
    private static final List<Path> WATCHED_SERVER_DIRECTORIES = new ArrayList<>();
    private static final Map<Path, Map<String, Runnable>> CLIENT_HANDLERS = new LinkedHashMap<>();
    private static Consumer<Runnable> clientExecutor;

    public static void onServerStarting(MinecraftServer pServer) {
        Path worldDirectory = pServer.getWorldPath(LevelResource.ROOT).toAbsolutePath().normalize();
        Path worldConfigDirectory = worldDirectory.resolve("serverconfig");
        Path defaultConfigDirectory = Services.PLATFORM_HELPER.getDefaultConfigDirectory();

        if (!WATCHED_SERVER_DIRECTORIES.isEmpty()) {
            for (Path directory : WATCHED_SERVER_DIRECTORIES) {
                ConfigFileWatcher.unwatchDirectory(directory);
            }
            WATCHED_SERVER_DIRECTORIES.clear();
        }

        for (ModConfig spec : LightConfig.getConfigs()) {
            if (spec.getConfigType() != ConfigType.SERVER) {
                continue;
            }

            try {
                Path worldConfigPath = worldConfigDirectory.resolve(spec.getModId()).resolve(spec.getFileName() + ".toml");
                boolean localExisted = Files.exists(worldConfigPath);
                boolean seeded = seedFromDefault(spec, worldConfigPath, defaultConfigDirectory.resolve(spec.getModId()).resolve(spec.getFileName() + ".toml"));

                if (spec instanceof AbstractModConfig baseSpec) {
                    baseSpec.setConfigPath(worldConfigPath);
                }
                spec.load();

                if (!localExisted && !seeded) {
                    LightConfig.LOGGER.info("Created server config '{}' from built-in defaults", worldConfigPath);
                }
            } catch (RuntimeException pException) {
                LightConfig.LOGGER.error("Failed to load server config '{}'; continuing without it", spec.getFileName(), pException);
            }
        }

        LightConfig.LOGGER.info("Server configs loaded for world '{}'", worldDirectory);
        watchServerConfigs(pServer);
    }

    public static void onServerStopping(MinecraftServer pServer) {
        for (Path directory : WATCHED_SERVER_DIRECTORIES) {
            ConfigFileWatcher.unwatchDirectory(directory);
        }
        WATCHED_SERVER_DIRECTORIES.clear();
    }

    private static boolean seedFromDefault(ModConfig pSpec, Path pWorldConfigPath, Path pDefaultConfigPath) {
        if (Files.exists(pWorldConfigPath) || !Files.exists(pDefaultConfigPath)) {
            return false;
        }

        List<String> errors = ConfigLoader.validate(pSpec, pDefaultConfigPath);
        if (!errors.isEmpty()) {
            LightConfig.LOGGER.warn("Ignoring invalid default config '{}':", pDefaultConfigPath);
            for (String error : errors) {
                LightConfig.LOGGER.warn("  - {}", error);
            }
            return false;
        }

        try {
            Files.createDirectories(pWorldConfigPath.getParent());
            Files.copy(pDefaultConfigPath, pWorldConfigPath);
            LightConfig.LOGGER.info("Created server config '{}' from default config '{}'", pWorldConfigPath, pDefaultConfigPath);
            return true;
        } catch (IOException pException) {
            LightConfig.LOGGER.warn("Failed to copy default config '{}' to '{}'; using built-in defaults", pDefaultConfigPath, pWorldConfigPath, pException);
            return false;
        }
    }

    private static void watchServerConfigs(MinecraftServer pServer) {
        Map<Path, Map<String, Runnable>> directories = new LinkedHashMap<>();
        for (ModConfig spec : LightConfig.getConfigs()) {
            if (spec.getConfigType() != ConfigType.SERVER) {
                continue;
            }
            Path configPath = spec.getConfigPath();
            if (configPath == null || configPath.getParent() == null || configPath.getFileName() == null) {
                continue;
            }
            directories.computeIfAbsent(configPath.getParent(), ignored -> new LinkedHashMap<>()).put(configPath.getFileName().toString(), () -> pServer.execute(() -> reloadAndResync(pServer, spec)));
        }

        for (Map.Entry<Path, Map<String, Runnable>> entry : directories.entrySet()) {
            try {
                ConfigFileWatcher.watchDirectory(entry.getKey(), entry.getValue());
                WATCHED_SERVER_DIRECTORIES.add(entry.getKey());
            } catch (IOException | RuntimeException pException) {
                LightConfig.LOGGER.warn("Failed to watch server configs for hot reload: {}", entry.getKey(), pException);
            }
        }
    }

    private static void reloadAndResync(MinecraftServer pServer, ModConfig pSpec) {
        try {
            pSpec.reload();
        } catch (RuntimeException pException) {
            LightConfig.LOGGER.warn("Hot reload failed for '{}', keeping previous values", pSpec.getFileName(), pException);
            return;
        }

        List<ServerPlayer> players = pServer.getPlayerList().getPlayers();
        CompoundTag syncData = ConfigSyncData.fromSpec(pSpec).toNBT();
        int sent = 0;
        for (ServerPlayer serverPlayer : players) {
            if (Services.PACKET_HELPER.canSend(serverPlayer)) {
                try {
                    Services.PACKET_HELPER.sendSyncDataS2C(serverPlayer, syncData);
                    sent++;
                } catch (RuntimeException pException) {
                    LightConfig.LOGGER.warn("Failed to sync config '{}' to '{}'", pSpec.getFileName(), serverPlayer.getName().getString(), pException);
                }
            }
        }
        LightConfig.LOGGER.info("Reloaded '{}' and synced to {} player(s)", pSpec.getFileName(), sent);
    }

    public static void onPlayReady(ServerGamePacketListenerImpl pServerGamePacketListener, MinecraftServer pMinecraftServer) {
        ServerPlayer serverPlayer = pServerGamePacketListener.getPlayer();

        pMinecraftServer.execute(() -> sendServerConfigsTo(serverPlayer));
    }

    public static void sendServerConfigsTo(ServerPlayer pServerPlayer) {
        if (!Services.PACKET_HELPER.canSend(pServerPlayer)) {
            LightConfig.LOGGER.debug("Skipping config sync for '{}' (channel unavailable, vanilla client?)", pServerPlayer.getName().getString());
            return;
        }

        int count = 0;
        for (ModConfig spec : LightConfig.getConfigs()) {
            if (spec.getConfigType() == ConfigType.SERVER) {
                try {
                    Services.PACKET_HELPER.sendSyncDataS2C(pServerPlayer, ConfigSyncData.fromSpec(spec).toNBT());
                    count++;
                } catch (RuntimeException pException) {
                    LightConfig.LOGGER.warn("Failed to sync config '{}' to '{}'", spec.getFileName(), pServerPlayer.getName().getString(), pException);
                }
            }
        }
        LightConfig.LOGGER.debug("Sent {} server config(s) to '{}'", count, pServerPlayer.getName().getString());
    }

    public static void startClientWatchers(Consumer<Runnable> pExecutor) {
        clientExecutor = pExecutor;
        refreshClientWatchers();
    }

    public static void onConfigRegistered(ModConfig pConfig) {
        if (pConfig.getConfigType() != ConfigType.CLIENT) {
            return;
        }
        Path configPath = pConfig.getConfigPath();
        if (configPath == null || configPath.getParent() == null || configPath.getFileName() == null) {
            return;
        }

        CLIENT_HANDLERS.computeIfAbsent(configPath.getParent(), ignored -> new LinkedHashMap<>()).put(configPath.getFileName().toString(), () -> {
            Consumer<Runnable> executor = clientExecutor;
            if (executor != null) {
                executor.accept(pConfig::reload);
            }
        });

        if (clientExecutor != null) {
            refreshClientWatchers();
        }
    }

    private static void refreshClientWatchers() {
        for (Map.Entry<Path, Map<String, Runnable>> entry : CLIENT_HANDLERS.entrySet()) {
            try {
                ConfigFileWatcher.watchDirectory(entry.getKey(), entry.getValue());
            } catch (IOException | RuntimeException pException) {
                LightConfig.LOGGER.warn("Failed to watch client config directory '{}'", entry.getKey(), pException);
            }
        }
    }
}
