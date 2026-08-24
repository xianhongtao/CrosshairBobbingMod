package krash220.xbob.game.api;

import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.loading.FMLLoader;

public class Loader {

  public static String getPlatform() {
    return "NeoForge";
  }

  public static String getVersion() {
    // FancyModLoader ≤9.0.18 为静态 versionInfo()/dist，≥10.0.14 为 getCurrent()/getDist()。
    return FMLLoader.versionInfo().mcVersion();
  }

  public static boolean isClient() {
    return FMLEnvironment.dist.isClient();
  }
}
