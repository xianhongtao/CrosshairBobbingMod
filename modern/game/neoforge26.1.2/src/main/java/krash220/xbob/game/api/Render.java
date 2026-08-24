package krash220.xbob.game.api;

import krash220.xbob.game.api.math.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.state.OptionsRenderState;
import net.minecraft.client.renderer.state.level.CameraEntityRenderState;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class Render {

  /** cameraoverhaul 当前相机欧拉角（由 CameraOverhaulMixin 更新）：x / y / z */
  public static float[] cameraOverhaulRot = {0.0f, 0.0f, 0.0f};

  public static int getScaledWidth() {
    return Minecraft.getInstance().getWindow().getGuiScaledWidth();
  }

  public static int getScaledHeight() {
    return Minecraft.getInstance().getWindow().getGuiScaledHeight();
  }

  public static int getBlitOffset() {
    return 0;
  }

  public static boolean isDebugCrosshair() {
    Minecraft mc = Minecraft.getInstance();

    return mc.getDebugOverlay().showDebugScreen()
        && !mc.player.isReducedDebugInfo()
        && !mc.options.reducedDebugInfo().get();
  }

  /**
   * 相机 bob：与 26.x 原版 {@code GameRenderer.bobHurt/bobView} 相同的数学， 但自实现（不调用渲染专用的私有方法，避免其操作
   * RenderSystem 全局状态）。
   */
  public static void bobView(MatrixStack mat, float partialTicks) {
    Minecraft mc = Minecraft.getInstance();
    CameraEntityRenderState entity =
        mc.gameRenderer.getGameRenderState().levelRenderState.cameraRenderState.entityRenderState;

    if (!entity.isPlayer) {
      return;
    }

    OptionsRenderState options = mc.gameRenderer.getGameRenderState().optionsRenderState;

    bobHurt(mat, entity, options);

    if (options.bobView) {
      bobWalk(mat, entity);
    }
  }

  private static void bobHurt(
      MatrixStack mat, CameraEntityRenderState entity, OptionsRenderState options) {
    float hurtTime = entity.hurtTime;

    if (entity.isDeadOrDying) {
      float deathTime = Math.min(entity.deathTime, 20.0f);
      mat.rotate(40.0f - 8000.0f / (deathTime + 200.0f), 0.0f, 0.0f, 1.0f);
    }

    if (hurtTime < 0.0f) {
      return;
    }

    hurtTime = hurtTime / (float) entity.hurtDuration;
    hurtTime = Mth.sin(hurtTime * hurtTime * hurtTime * hurtTime * Mth.PI);

    float hurtDir = entity.hurtDir;

    mat.rotate(-hurtDir, 0.0f, 1.0f, 0.0f);
    mat.rotate((float) (-hurtTime * 14.0d * options.damageTiltStrength), 0.0f, 0.0f, 1.0f);
    mat.rotate(hurtDir, 0.0f, 1.0f, 0.0f);
  }

  private static void bobWalk(MatrixStack mat, CameraEntityRenderState entity) {
    float walkDist = entity.backwardsInterpolatedWalkDistance;
    float bob = entity.bob;

    mat.translate(
        Mth.sin(walkDist * Mth.PI) * bob * 0.5, -Math.abs(Mth.cos(walkDist * Mth.PI) * bob), 0.0);
    mat.rotate(Mth.sin(walkDist * Mth.PI) * bob * 3.0f, 0.0f, 0.0f, 1.0f);
    mat.rotate(Math.abs(Mth.cos((walkDist * Mth.PI) - 0.2f) * bob) * 5.0f, 1.0f, 0.0f, 0.0f);
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
    Minecraft mc = Minecraft.getInstance();
    CameraEntityRenderState entity =
        mc.gameRenderer.getGameRenderState().levelRenderState.cameraRenderState.entityRenderState;
    // 仅玩家需要准星偏移投影（cameraoverhaul 也已基于相机，不在此处叠加）
    if (!entity.isPlayer) {
      return;
    }

    mat.pose()
        .mulPose(
            mc.gameRenderer.getGameRenderState()
                .levelRenderState
                .cameraRenderState
                .projectionMatrix);
  }

  public static float getReachDistance() {
    return 4.5f;
  }

  public static void distortion(MatrixStack mat, float tickDelta) {
    // Minecraft 26.x 将 nausea 效果移至后期着色器，此处不再进行矩阵扭曲。
  }

  public static float getCenterDepth() {
    Minecraft mc = Minecraft.getInstance();
    Entity entity = mc.getCameraEntity();
    float partialTicks = mc.getDeltaTracker().getGameTimeDeltaPartialTick(true);

    if (entity != null && mc.level != null) {
      Vec3 eye = entity.getEyePosition(partialTicks);
      Vec3 look = entity.getViewVector(partialTicks);
      Vec3 end = eye.add(look.x * 1000.0, look.y * 1000.0, look.z * 1000.0);
      HitResult result =
          mc.level.clip(
              new ClipContext(eye, end, ClipContext.Block.VISUAL, ClipContext.Fluid.NONE, entity));

      if (result.getType() != HitResult.Type.MISS) {
        return (float) result.getLocation().distanceTo(eye);
      }
    }

    return 1000.0f;
  }
}
