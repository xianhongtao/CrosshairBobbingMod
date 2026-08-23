package krash220.xbob.game.api;

import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.loading.FMLLoader;

public class Loader {

    public static String getPlatform() {
        return "NeoForge";
    }

    public static String getVersion() {
        // 1.21.11 FancyModLoader 10.0.36：与 26.x 相同，不是 1.21.1 的 versionInfo()/dist 字段。
        return FMLLoader.getCurrent().getVersionInfo().mcVersion();
    }

    public static boolean isClient() {
        return FMLEnvironment.getDist().isClient();
    }
}
