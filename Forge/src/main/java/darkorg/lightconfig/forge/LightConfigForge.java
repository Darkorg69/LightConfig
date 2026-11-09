package darkorg.lightconfig.forge;

import darkorg.lightconfig.common.LightConfig;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;

@Mod(LightConfig.MOD_ID)
public final class LightConfigForge {
    public LightConfigForge(FMLJavaModLoadingContext pContext) {
        LightConfig.init();
        if (FMLEnvironment.dist == Dist.CLIENT) {
            LightConfig.initClient();
        } else {
            LightConfig.initServer();
        }
    }
}
