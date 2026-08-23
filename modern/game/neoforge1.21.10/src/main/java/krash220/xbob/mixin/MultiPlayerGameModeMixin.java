package krash220.xbob.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import krash220.xbob.game.api.bus.PlayerBus;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

@Mixin(MultiPlayerGameMode.class)
public class MultiPlayerGameModeMixin {

    @Inject(method = "attack", at = @At("HEAD"))
    public void onAttack(Player player, Entity target, CallbackInfo ci) {
        PlayerBus.onAttack(1.0f);
    }

    @Inject(method = "destroyBlock", at = @At("RETURN"))
    public void onBreakBlock(BlockPos pos, CallbackInfoReturnable<Boolean> ci) {
        // getReturnValueZ 直取原始 boolean：getReturnValue() 会生成 Boolean.booleanValue 拆箱调用，
        // 迫使 mixin 解析 java.lang.Boolean，而老版本 ASM 读不了 JDK 25 的 java.base class。
        if (ci.getReturnValueZ()) {
            PlayerBus.onBreakBlock();
        }
    }
}
