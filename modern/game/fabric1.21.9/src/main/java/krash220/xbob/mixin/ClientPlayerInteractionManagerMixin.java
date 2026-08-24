package krash220.xbob.mixin;

import krash220.xbob.game.api.bus.PlayerBus;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionManagerMixin {

  @Inject(method = "attackEntity", at = @At("HEAD"))
  public void onAttack(PlayerEntity player, Entity target, CallbackInfo ci) {
    PlayerBus.onAttack(1.0f);
  }

  @Inject(method = "breakBlock", at = @At("RETURN"))
  public void onBreakBlock(BlockPos pos, CallbackInfoReturnable<Boolean> ci) {
    if (ci.getReturnValueZ()) {
      PlayerBus.onBreakBlock();
    }
  }
}
