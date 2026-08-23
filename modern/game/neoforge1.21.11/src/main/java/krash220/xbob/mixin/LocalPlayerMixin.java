package krash220.xbob.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import krash220.xbob.game.api.bus.PlayerBus;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;

@Mixin(LocalPlayer.class)
public class LocalPlayerMixin {

    @Inject(method = "crit", at = @At("HEAD"))
    public void onCrit(Entity target, CallbackInfo ci) {
        PlayerBus.onAttack(2.0f);
    }
}
