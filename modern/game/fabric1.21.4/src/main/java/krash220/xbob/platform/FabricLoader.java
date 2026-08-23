package krash220.xbob.platform;

import java.lang.reflect.InvocationTargetException;

import net.fabricmc.api.ClientModInitializer;

/**
 * Dev-environment entrypoint (runtime classpath), mirrors {@code :main} modbase entry.
 * Reflective so the shared {@code MainMod} (from the repository root) is loaded without
 * a compile-time dependency from the per-version core jar.
 */
public class FabricLoader implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        try {
            Class.forName("${MOD_ENTRYPOINT}").getConstructor().newInstance();
        } catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new RuntimeException("Failed to initialize ${MOD_NAME}", e);
        }
    }
}
