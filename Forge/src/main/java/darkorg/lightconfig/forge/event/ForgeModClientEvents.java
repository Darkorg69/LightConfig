package darkorg.lightconfig.forge.event;

import darkorg.lightconfig.common.LightConfig;
import darkorg.lightconfig.common.event.ModEvents;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = LightConfig.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ForgeModClientEvents {
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent pEvent) {
        ModEvents.startClientWatchers(pRunnable -> Minecraft.getInstance().execute(pRunnable));
    }
}
