package darkorg.lightconfig.forge.event;

import darkorg.lightconfig.common.LightConfig;
import darkorg.lightconfig.common.event.ModEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = LightConfig.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class ForgeEvents {
    @SubscribeEvent
    public static void onServerAboutToStart(ServerAboutToStartEvent pEvent) {
        ModEvents.onServerStarting(pEvent.getServer());
    }

    @SubscribeEvent
    public static void onServerStopping(ServerStoppingEvent pEvent) {
        ModEvents.onServerStopping(pEvent.getServer());
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent pEvent) {
        if (pEvent.getEntity() instanceof ServerPlayer serverPlayer) {
            serverPlayer.server.execute(() -> ModEvents.sendServerConfigsTo(serverPlayer));
        }
    }
}
