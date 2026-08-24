package krash220.xbob.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(GameRenderer.class)
public interface GameRendererAccessor {

  @Invoker("bobHurt")
  void callBobHurt(CameraRenderState cameraRenderState, PoseStack poseStack);

  @Invoker("bobView")
  void callBobView(CameraRenderState cameraRenderState, PoseStack poseStack);
}
