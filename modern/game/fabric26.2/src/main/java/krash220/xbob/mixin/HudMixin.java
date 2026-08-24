package krash220.xbob.mixin;

import krash220.xbob.game.api.bus.GuiBus;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Hud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Hud.class)
public class HudMixin {

  @Inject(method = "extractCrosshair", at = @At("HEAD"))
  public void beforeCrosshair(
      GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
    GuiBus.pre(graphics.pose());
  }

  @Inject(method = "extractCrosshair", at = @At("RETURN"))
  public void afterCrosshair(
      GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
    GuiBus.post(graphics.pose());
  }
}
