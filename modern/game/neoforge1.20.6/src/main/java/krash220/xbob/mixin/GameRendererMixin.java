package krash220.xbob.mixin;

import krash220.xbob.game.api.bus.GuiBus;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

  @Inject(method = "render", at = @At("HEAD"))
  public void render(float partialTicks, long startTime, boolean renderLevel, CallbackInfo ci) {
    GuiBus.partialTicks = partialTicks;
  }
}
