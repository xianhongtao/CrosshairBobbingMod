package krash220.xbob.game.api;

import krash220.xbob.game.api.math.MatrixStack;
import krash220.xbob.mixin.GameRendererAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

public class Render {

  /** cameraoverhaul 当前相机欧拉角（由 CameraOverhaulMixin 更新）：x / y / z */
  public static float[] cameraOverhaulRot = {0.0f, 0.0f, 0.0f};

  public static int getScaledWidth() {
    return MinecraftClient.getInstance().getWindow().getScaledWidth();
  }

  public static int getScaledHeight() {
    return MinecraftClient.getInstance().getWindow().getScaledHeight();
  }

  public static int getBlitOffset() {
    return 0;
  }

  public static boolean isDebugCrosshair() {
    MinecraftClient mc = MinecraftClient.getInstance();

    return mc.options.hudHidden
        || mc.inGameHud.getDebugHud().shouldShowDebugHud()
            && !mc.player.hasReducedDebugInfo()
            && !mc.options.getReducedDebugInfo().getValue();
  }

  /** 相机 bob：通过 accessor 调用原版 {@code GameRenderer.tiltViewWhenHurt/bobView}（与 26.x 自实现数学等价）。 */
  public static void bobView(MatrixStack mat, float partialTicks) {
    MinecraftClient mc = MinecraftClient.getInstance();

    ((GameRendererAccessor) mc.gameRenderer).callTiltViewWhenHurt(mat.pose(), partialTicks);
    if (mc.options.getBobView().getValue().booleanValue()) {
      ((GameRendererAccessor) mc.gameRenderer).callBobView(mat.pose(), partialTicks);
    }
  }

  public static void modCamera(MatrixStack mat) {
    float rz = cameraOverhaulRot[2];
    float rx = cameraOverhaulRot[0];
    float ry = cameraOverhaulRot[1];

    if (rz != 0.0f || rx != 0.0f || ry != 0.0f) {
      mat.rotate(rz, 0, 0, 1);
      mat.rotate(rx, 1, 0, 0);
      mat.rotate(ry, 0, 1, 0);
    }
  }

  public static void updateCameraMatrix(MatrixStack mat, float partialTicks) {
    MinecraftClient mc = MinecraftClient.getInstance();

    mat.pose()
        .multiplyPositionMatrix(
            mc.gameRenderer.getBasicProjectionMatrix(
                (float)
                    ((GameRendererAccessor) mc.gameRenderer)
                        .callGetFov(mc.gameRenderer.getCamera(), partialTicks, true)));
  }

  @SuppressWarnings("resource")
  public static float getReachDistance() {
    return 4.5f;
  }

  public static void distortion(MatrixStack mat, float tickDelta) {
    MinecraftClient mc = MinecraftClient.getInstance();

    float g = mc.options.getDistortionEffectScale().getValue().floatValue();
    float f =
        MathHelper.lerp(tickDelta, mc.player.prevNauseaIntensity, mc.player.nauseaIntensity)
            * g
            * g;
    if (f > 0.0F) {
      int i = mc.player.hasStatusEffect(StatusEffects.NAUSEA) ? 7 : 20;
      float f1 = 5.0F / (f * f + 5.0F) - f * 0.04F;
      f1 = f1 * f1;
      mat.rotate(
          (mc.player.age + tickDelta) * i,
          0.0F,
          MathHelper.SQUARE_ROOT_OF_TWO / 2.0F,
          MathHelper.SQUARE_ROOT_OF_TWO / 2.0F);
      mat.scale(1.0F / f1, 1.0F, 1.0F);
      float f2 = -(mc.player.age + tickDelta) * i;
      mat.rotate(
          f2, 0.0F, MathHelper.SQUARE_ROOT_OF_TWO / 2.0F, MathHelper.SQUARE_ROOT_OF_TWO / 2.0F);
    }
  }

  public static float getCenterDepth() {
    MinecraftClient mc = MinecraftClient.getInstance();
    Entity entity = mc.getCameraEntity();
    float partialTicks = mc.getTickDelta();

    if (entity != null && mc.world != null) {
      Vec3d vec3d = entity.getCameraPosVec(partialTicks);
      Vec3d vec3d2 = entity.getRotationVec(partialTicks);
      Vec3d vec3d3 = vec3d.add(vec3d2.x * 1000F, vec3d2.y * 1000F, vec3d2.z * 1000F);
      HitResult result =
          mc.world.raycast(
              new RaycastContext(
                  vec3d,
                  vec3d3,
                  RaycastContext.ShapeType.VISUAL,
                  RaycastContext.FluidHandling.NONE,
                  entity));

      if (result.getType() != HitResult.Type.MISS) {
        Vec3d begin = entity.getCameraPosVec(partialTicks);
        Vec3d end = result.getPos();

        return (float) end.distanceTo(begin);
      }
    }

    return 1000F;
  }
}
