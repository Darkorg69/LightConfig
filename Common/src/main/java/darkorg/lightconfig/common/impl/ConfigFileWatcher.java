package darkorg.lightconfig.common.impl;

import darkorg.lightconfig.common.LightConfig;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public final class ConfigFileWatcher {
    private static final long DEBOUNCE_MILLIS = 300;
    private static final Map<WatchKey, WatchedDirectory> WATCHED_DIRECTORIES = new LinkedHashMap<>();
    private static WatchService watchService;
    private static ScheduledExecutorService scheduler;

    public static synchronized void watchDirectory(Path pDirectory, Map<String, Runnable> pFileHandlers) throws IOException {
        ensureStarted();
        unwatchDirectoryLocked(pDirectory);
        Files.createDirectories(pDirectory);
        WatchKey key = pDirectory.register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_DELETE);
        WatchedDirectory watchedDirectory = new WatchedDirectory();
        watchedDirectory.fileHandlers.putAll(pFileHandlers);
        WATCHED_DIRECTORIES.put(key, watchedDirectory);
    }

    public static synchronized void unwatchDirectory(Path pDirectory) {
        unwatchDirectoryLocked(pDirectory);
    }

    private static void ensureStarted() throws IOException {
        if (watchService != null) {
            return;
        }
        watchService = FileSystems.getDefault().newWatchService();
        scheduler = Executors.newSingleThreadScheduledExecutor(pRunnable -> {
            Thread thread = new Thread(pRunnable, "LightConfig-FileWatcher");
            thread.setDaemon(true);
            return thread;
        });
        Thread watchThread = new Thread(ConfigFileWatcher::watchLoop, "LightConfig-FileWatcher");
        watchThread.setDaemon(true);
        watchThread.start();
    }

    private static void unwatchDirectoryLocked(Path pDirectory) {
        for (Map.Entry<WatchKey, WatchedDirectory> entry : new ArrayList<>(WATCHED_DIRECTORIES.entrySet())) {
            if (entry.getKey().watchable().equals(pDirectory)) {
                cancelDirectory(entry.getKey(), entry.getValue());
                WATCHED_DIRECTORIES.remove(entry.getKey());
            }
        }
    }

    private static void cancelDirectory(WatchKey pKey, WatchedDirectory pDirectory) {
        for (ScheduledFuture<?> pendingReload : pDirectory.pendingReloads.values()) {
            pendingReload.cancel(false);
        }
        pDirectory.pendingReloads.clear();
        pKey.cancel();
    }

    private static void watchLoop() {
        while (true) {
            WatchKey key;
            try {
                key = watchService.take();
            } catch (InterruptedException | ClosedWatchServiceException pException) {
                return;
            }
            List<WatchEvent<?>> events = key.pollEvents();
            synchronized (ConfigFileWatcher.class) {
                WatchedDirectory watchedDirectory = WATCHED_DIRECTORIES.get(key);
                if (watchedDirectory == null) {
                    key.reset();
                    continue;
                }
                boolean reloadAll = false;
                List<String> changedFiles = new ArrayList<>();
                for (WatchEvent<?> event : events) {
                    if (event.kind() == StandardWatchEventKinds.OVERFLOW) {
                        reloadAll = true;
                        break;
                    }
                    if (event.context() instanceof Path fileName) {
                        changedFiles.add(fileName.toString());
                    }
                }
                if (reloadAll) {
                    for (String fileName : watchedDirectory.fileHandlers.keySet()) {
                        scheduleReload(watchedDirectory, fileName);
                    }
                } else {
                    for (String fileName : changedFiles) {
                        if (watchedDirectory.fileHandlers.containsKey(fileName)) {
                            scheduleReload(watchedDirectory, fileName);
                        }
                    }
                }
                if (!key.reset()) {
                    cancelDirectory(key, watchedDirectory);
                    WATCHED_DIRECTORIES.remove(key);
                }
            }
        }
    }

    private static void scheduleReload(WatchedDirectory pDirectory, String pFileName) {
        Runnable handler = pDirectory.fileHandlers.get(pFileName);
        if (handler == null) {
            return;
        }
        ScheduledFuture<?> previousReload = pDirectory.pendingReloads.remove(pFileName);
        if (previousReload != null) {
            previousReload.cancel(false);
        }
        pDirectory.pendingReloads.put(pFileName, scheduler.schedule(() -> {
            synchronized (ConfigFileWatcher.class) {
                pDirectory.pendingReloads.remove(pFileName);
            }
            try {
                handler.run();
            } catch (RuntimeException pException) {
                LightConfig.LOGGER.warn("Watched config reload failed for {}", pFileName, pException);
            }
        }, DEBOUNCE_MILLIS, TimeUnit.MILLISECONDS));
    }

    private static final class WatchedDirectory {
        private final Map<String, Runnable> fileHandlers = new LinkedHashMap<>();
        private final Map<String, ScheduledFuture<?>> pendingReloads = new LinkedHashMap<>();
    }
}
