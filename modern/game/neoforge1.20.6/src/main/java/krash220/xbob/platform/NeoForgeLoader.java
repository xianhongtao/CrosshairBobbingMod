package krash220.xbob.platform;

import java.lang.reflect.InvocationTargetException;
import java.util.function.Supplier;
import krash220.xbob.game.api.Config;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(value = "${MOD_ID}", dist = net.neoforged.api.distmarker.Dist.CLIENT)
public class NeoForgeLoader {

  private static final Logger LOGGER = LoggerFactory.getLogger("${MOD_ID}");

  public NeoForgeLoader(IEventBus modBus, ModContainer container) {
    Supplier<IConfigScreenFactory> configFactory =
        () ->
            new IConfigScreenFactory() {
              @Override
              public net.minecraft.client.gui.screens.Screen createScreen(
                  net.minecraft.client.Minecraft mc,
                  net.minecraft.client.gui.screens.Screen modListScreen) {
                return new Config.ConfigScreen(null, modListScreen);
              }
            };

    container.registerExtensionPoint(IConfigScreenFactory.class, configFactory);

    modBus.addListener(this::onClientSetup);
  }

  private void onClientSetup(FMLClientSetupEvent event) {
    try {
      Class.forName("${MOD_ENTRYPOINT}").getConstructor().newInstance();
    } catch (ClassNotFoundException
        | NoSuchMethodException
        | SecurityException
        | InstantiationException
        | IllegalAccessException
        | IllegalArgumentException
        | InvocationTargetException e) {
      LOGGER.error("Failed to initialize {}", "${MOD_NAME}", e);
    }
  }
}
