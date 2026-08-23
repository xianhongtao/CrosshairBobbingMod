package krash220.xbob.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;

@Mixin(GameRenderer.class)
public interface GameRendererAccessor {

    @Invoker("tiltViewWhenHurt")
    void callTiltViewWhenHurt(MatrixStack mat, float partialTicks);

    @Invoker("bobView")
    void callBobView(MatrixStack mat, float partialTicks);

    @Invoker("getFov")
    float callGetFov(Camera camera, float tickDelta, boolean changingFov);
}
