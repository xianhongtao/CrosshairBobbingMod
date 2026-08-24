package krash220.xbob.game.api.math;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import org.joml.Matrix3x2fStack;
import org.joml.Vector3f;
import org.joml.Vector4f;

/**
 * Minecraft 1.21.6+ matrix abstraction（官方映射）。
 *
 * <p>Supports two backing stores:
 *
 * <ul>
 *   <li>{@link PoseStack} (3D, 4x4) - world/camera math in {@code MainMod}
 *   <li>{@link Matrix3x2fStack} (2D GUI) - crosshair transform in {@code Gui.renderCrosshair}
 * </ul>
 */
public class MatrixStack {

  private final PoseStack pose;
  private final Matrix3x2fStack gui;

  public MatrixStack() {
    this.pose = new PoseStack();
    this.gui = null;
  }

  public MatrixStack(PoseStack pose) {
    this.pose = pose;
    this.gui = null;
  }

  public MatrixStack(Matrix3x2fStack gui) {
    this.pose = null;
    this.gui = gui;
  }

  public PoseStack pose() {
    return this.pose;
  }

  public void push() {
    if (this.pose != null) {
      this.pose.pushPose();
    } else {
      this.gui.pushMatrix();
    }
  }

  public void pop() {
    if (this.pose != null) {
      this.pose.popPose();
    } else {
      this.gui.popMatrix();
    }
  }

  public void translate(double x, double y, double z) {
    if (this.pose != null) {
      this.pose.translate(x, y, z);
    } else {
      this.gui.translate((float) x, (float) y);
    }
  }

  public void rotate(float angle, float x, float y, float z) {
    if (this.pose != null) {
      this.pose.mulPose(Axis.of(new Vector3f(x, y, z)).rotationDegrees(angle));
    } else {
      this.gui.rotate((float) Math.toRadians(angle));
    }
  }

  public void scale(float x, float y, float z) {
    if (this.pose != null) {
      this.pose.scale(x, y, z);
    } else {
      this.gui.scale(x, y);
    }
  }

  public void identity() {
    if (this.pose != null) {
      this.pose.last().pose().identity();
    } else {
      this.gui.identity();
    }
  }

  public float[] multiplyVector(float x, float y, float z, float w) {
    Vector4f vec = new Vector4f(x, y, z, w);

    vec.mul(this.pose.last().pose());

    return new float[] {vec.x, vec.y, vec.z, vec.w};
  }
}
