package krash220.xbob.game.api.math;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import org.joml.Vector3f;
import org.joml.Vector4f;

/**
 * Minecraft ≤1.21.5 matrix abstraction（官方映射）。
 *
 * <p>GUI 与世界/相机共用 {@link PoseStack}（4x4）。1.21.6+ 才把 GUI 换成 Matrix3x2fStack。
 */
public class MatrixStack {

  private final PoseStack pose;

  public MatrixStack() {
    this.pose = new PoseStack();
  }

  public MatrixStack(PoseStack pose) {
    this.pose = pose;
  }

  public PoseStack pose() {
    return this.pose;
  }

  public void push() {
    this.pose.pushPose();
  }

  public void pop() {
    this.pose.popPose();
  }

  public void translate(double x, double y, double z) {
    this.pose.translate(x, y, z);
  }

  public void rotate(float angle, float x, float y, float z) {
    this.pose.mulPose(Axis.of(new Vector3f(x, y, z)).rotationDegrees(angle));
  }

  public void scale(float x, float y, float z) {
    this.pose.scale(x, y, z);
  }

  public void identity() {
    this.pose.last().pose().identity();
  }

  public float[] multiplyVector(float x, float y, float z, float w) {
    Vector4f vec = new Vector4f(x, y, z, w);

    vec.mul(this.pose.last().pose());

    return new float[] {vec.x, vec.y, vec.z, vec.w};
  }
}
