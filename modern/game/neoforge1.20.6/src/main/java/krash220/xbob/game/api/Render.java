package krash220.xbob.game.api;

import krash220.xbob.game.api.math.MatrixStack;
import krash220.xbob.mixin.GameRendererAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
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

        return mc.options.hideGui || mc.gui.getDebugOverlay().showDebugScreen() && !mc.player.isReducedDebugInfo() && !mc.options.reducedDebugInfo().get();
    }

    /**
     * 相机 bob：通过 accessor 调用原版 {@code GameRenderer.bobHurt/bobView}。
     */
    public static void bobView(MatrixStack mat, float partialTicks) {
        Minecraft mc = Minecraft.getInstance();

        ((GameRendererAccessor) mc.gameRenderer).callBobHurt(mat.pose(), partialTicks);
        if (mc.options.bobView().get().booleanValue()) {
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
        Minecraft mc = Minecraft.getInstance();

        mat.pose().mulPose(mc.gameRenderer.getProjectionMatrix(((GameRendererAccessor) mc.gameRenderer).callGetFov(mc.gameRenderer.getMainCamera(), partialTicks, true)));
    }

    public static float getReachDistance() {
        return 4.5f;
    }

    public static void distortion(MatrixStack mat, float tickDelta) {
        Minecraft mc = Minecraft.getInstance();

        float g = mc.options.screenEffectScale().get().floatValue();
        float f = Mth.lerp(tickDelta, mc.player.oSpinningEffectIntensity, mc.player.spinningEffectIntensity) * g * g;
        if (f > 0.0F) {
           int i = mc.player.hasEffect(MobEffects.CONFUSION) ? 7 : 20;
           float f1 = 5.0F / (f * f + 5.0F) - f * 0.04F;
           f1 = f1 * f1;
           mat.rotate((mc.player.tickCount + tickDelta) * i, 0.0F, Mth.SQRT_OF_TWO / 2.0F, Mth.SQRT_OF_TWO / 2.0F);
           mat.scale(1.0F / f1, 1.0F, 1.0F);
           float f2 = -(mc.player.tickCount + tickDelta) * i;
           mat.rotate(f2, 0.0F, Mth.SQRT_OF_TWO / 2.0F, Mth.SQRT_OF_TWO / 2.0F);
        }
    }

    public static float getCenterDepth() {
        Minecraft mc = Minecraft.getInstance();
        Entity entity = mc.getCameraEntity();
        float partialTicks = mc.getPartialTick();

        if (entity != null && mc.level != null) {
            Vec3 eye = entity.getEyePosition(partialTicks);
            Vec3 look = entity.getViewVector(partialTicks);
            Vec3 end = eye.add(look.x * 1000.0, look.y * 1000.0, look.z * 1000.0);
            HitResult result = mc.level.clip(new ClipContext(eye, end, ClipContext.Block.VISUAL, ClipContext.Fluid.NONE, entity));

            if (result.getType() != HitResult.Type.MISS) {
                return (float) result.getLocation().distanceTo(eye);
            }
        }

        return 1000.0f;
    }
}
