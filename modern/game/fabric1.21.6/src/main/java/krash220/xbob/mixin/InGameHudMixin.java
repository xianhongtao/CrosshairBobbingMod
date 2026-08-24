package krash220.xbob.mixin;

import krash220.xbob.game.api.bus.GuiBus;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {

  @Inject(method = "renderCrosshair", at = @At("HEAD"))
  public void beforeCrosshair(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
    GuiBus.pre(context.getMatrices());
  }

  @Inject(method = "renderCrosshair", at = @At("RETURN"))
  public void afterCrosshair(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
    GuiBus.post(context.getMatrices());
  }
}
