package krash220.xbob.game.api;

import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.loading.FMLLoader;

public class Loader {

  public static String getPlatform() {
    return "NeoForge";
  }

  public static String getVersion() {
    // FancyModLoader 7.x（≤1.21.8）：静态 versionInfo()/dist，1.21.10+ 才换成 getCurrent()/getDist()。
    return FMLLoader.versionInfo().mcVersion();
  }

  public static boolean isClient() {
    return FMLEnvironment.dist.isClient();
  }
}
