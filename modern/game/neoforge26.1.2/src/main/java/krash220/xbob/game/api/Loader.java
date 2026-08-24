package krash220.xbob.game.api;

import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.loading.FMLLoader;

public class Loader {

  public static String getPlatform() {
    return "NeoForge";
  }

  public static String getVersion() {
    return FMLLoader.getCurrent().getVersionInfo().mcVersion();
  }

  public static boolean isClient() {
    return FMLEnvironment.getDist().isClient();
  }
}
