package darkorg.lightconfig.common.api;

import darkorg.lightconfig.common.impl.ClientConfig;
import darkorg.lightconfig.common.impl.ServerConfig;
import org.apache.commons.lang3.tuple.Pair;

import java.util.function.Consumer;
import java.util.function.Function;

public final class ConfigRegistry {
    public static ModConfig createClientConfig(String pModId, String pFileName, Consumer<ConfigBuilder> pBuilder) {
        ConfigBuilder builder = new ConfigBuilder();
        pBuilder.accept(builder);
        String finalFileName = ensureSuffix(pFileName, "client");
        return new ClientConfig(pModId, finalFileName, builder.getValues());
    }

    public static <T> Pair<T, ModConfig> configureClient(String pModId, String pFileName, Function<ConfigBuilder, T> pFactory) {
        ConfigBuilder builder = new ConfigBuilder();
        T holder = pFactory.apply(builder);
        String finalFileName = ensureSuffix(pFileName, "client");
        return Pair.of(holder, new ClientConfig(pModId, finalFileName, builder.getValues()));
    }

    public static ModConfig createServerConfig(String pModId, String pFileName, Consumer<ConfigBuilder> pBuilder) {
        ConfigBuilder builder = new ConfigBuilder();
        pBuilder.accept(builder);
        String finalFileName = ensureSuffix(pFileName, "server");
        return new ServerConfig(pModId, finalFileName, builder.getValues());
    }

    public static <T> Pair<T, ModConfig> configureServer(String pModId, String pFileName, Function<ConfigBuilder, T> pFactory) {
        ConfigBuilder builder = new ConfigBuilder();
        T holder = pFactory.apply(builder);
        String finalFileName = ensureSuffix(pFileName, "server");
        return Pair.of(holder, new ServerConfig(pModId, finalFileName, builder.getValues()));
    }

    private static String ensureSuffix(String pFileName, String pSuffix) {
        if (pFileName.endsWith("-" + pSuffix) || pFileName.endsWith("_" + pSuffix)) {
            return pFileName;
        }
        return pFileName + "-" + pSuffix;
    }
}
