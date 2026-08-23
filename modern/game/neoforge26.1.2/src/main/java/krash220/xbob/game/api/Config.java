package krash220.xbob.game.api;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.fml.loading.FMLPaths;

public class Config {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Map<String, Boolean> DEFAULT = new LinkedHashMap<>();
    private static final Map<String, Boolean> CONFIG = new LinkedHashMap<>();

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static void load() {
        File config = configFile();

        if (!config.exists()) {
            save();
        }

        try (FileInputStream fis = new FileInputStream(config)) {
            Map map = GSON.fromJson(new InputStreamReader(fis, StandardCharsets.UTF_8), Map.class);

            CONFIG.putAll(map);
        } catch (Exception e) {}
    }

    public static void save() {
        String cfg = GSON.toJson(CONFIG);

        try (FileOutputStream fos = new FileOutputStream(configFile())) {
            fos.write(cfg.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {}
    }

    private static File configFile() {
        return new File(FMLPaths.CONFIGDIR.get().toFile(), "${MOD_ID}.json");
    }

    public static void define(String key, Boolean defaultValue) {
        DEFAULT.put(key, defaultValue);
        CONFIG.put(key, defaultValue);
    }

    public static void set(String key, Boolean value) {
        CONFIG.put(key, value);
    }

    public static boolean check(String key) {
        Boolean val = CONFIG.get(key);

        if (val != null) {
            return val.booleanValue();
        } else {
            return DEFAULT.get(key).booleanValue();
        }
    }

    public static void registerGui() {
        // 配置界面通过 NeoForgeLoader 中 ModContainer#registerExtensionPoint(IConfigScreenFactory.class) 注册
    }

    public static class ConfigScreen extends Screen {

        private static final Component VALUE_ENABLE = Component.translatable("${MOD_ID}.config.value.enable").withStyle(ChatFormatting.GREEN);
        private static final Component VALUE_DISABLE = Component.translatable("${MOD_ID}.config.value.disable").withStyle(ChatFormatting.DARK_RED);

        public static Component getValueText(String key) {
            boolean bool = CONFIG.get(key).booleanValue();

            return Component.translatable("%s: %s", Component.translatable("${MOD_ID}.config." + key), bool ? VALUE_ENABLE : VALUE_DISABLE);
        }

        private final Screen previous;

        public ConfigScreen(ModContainer container, Screen previous) {
            super(Minecraft.getInstance(), Minecraft.getInstance().font, Component.translatable("${MOD_ID}.config.title"));

            this.previous = previous;
        }

        @Override
        public void onClose() {
            save();

            Minecraft.getInstance().setScreenAndShow(this.previous);
        }

        @Override
        protected void init() {
            int i = this.height / 6 - 12;

            for (String key : DEFAULT.keySet()) {
                this.addRenderableWidget(Button.builder(getValueText(key), btn -> {
                    set(key, !check(key));
                    btn.setMessage(getValueText(key));
                }).bounds(this.width / 2 - 150, i, 300, 20).build());

                i += 24;
            }

            this.addRenderableWidget(Button.builder(Component.translatable("gui.done"), btn -> this.onClose()).bounds(this.width / 2 - 100, this.height - 27, 200, 20).build());
        }

        @Override
        public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
            super.extractRenderState(graphics, mouseX, mouseY, partialTick);

            graphics.centeredText(this.font, this.title, this.width / 2, 15, 0xFFFFFF);
        }
    }
}
