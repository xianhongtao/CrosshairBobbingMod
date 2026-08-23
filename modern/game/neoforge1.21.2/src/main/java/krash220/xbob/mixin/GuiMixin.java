package krash220.xbob.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import krash220.xbob.game.api.bus.GuiBus;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;

@Mixin(Gui.class)
public class GuiMixin {

    @Inject(method = "renderCrosshair", at = @At("HEAD"))
    public void beforeCrosshair(GuiGraphics graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        GuiBus.pre(graphics.pose());
    }

    @Inject(method = "renderCrosshair", at = @At("RETURN"))
    public void afterCrosshair(GuiGraphics graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        GuiBus.post(graphics.pose());
    }
}
