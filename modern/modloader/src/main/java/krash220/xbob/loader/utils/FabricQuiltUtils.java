package krash220.xbob.loader.utils;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.util.HashMap;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.impl.FabricLoaderImpl;
import net.fabricmc.loader.impl.gui.FabricGuiEntry;

/**
 * Fabric-only loader helpers for the modern (26.x) build.
 *
 * <p>The legacy build additionally supported Quilt via quilt-loader internals; the modern
 * modloader intentionally targets Fabric only until Quilt ships a compatible 26.x loader.
 */
public class FabricQuiltUtils {

    public static String getPlatform() {
        return "Fabric";
    }

    public static String getVersion() {
        return FabricLoaderImpl.INSTANCE.getGameProvider().getRawGameVersion();
    }

    public static void displayError(String main, String err, boolean exitAfter) {
        FabricGuiEntry.displayError(main, new RuntimeException(err), exitAfter);

        throw new RuntimeException(err);
    }

    public static Object getGameTransformer() {
        return FabricLoaderImpl.INSTANCE.getGameProvider().getEntrypointTransformer();
    }

    public static EnvType getEnvironmentType() {
        return FabricLoaderImpl.INSTANCE.getEnvironmentType();
    }

    public static FileSystem getFileSystem(URI uri) throws IOException {
        return FileSystems.newFileSystem(uri, new HashMap<>());
    }

    public static File getConfigDir() {
        return FabricLoader.getInstance().getConfigDir().toFile();
    }
}
