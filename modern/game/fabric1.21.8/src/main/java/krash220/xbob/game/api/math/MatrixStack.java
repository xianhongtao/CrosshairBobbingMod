package krash220.xbob.game.api.math;

import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix3x2fStack;
import org.joml.Vector3f;
import org.joml.Vector4f;

/**
 * Minecraft 1.21.x matrix abstraction.
 *
 * <p>Supports two backing stores:
 *
 * <ul>
 *   <li>{@code net.minecraft.client.util.math.MatrixStack} (3D, 4x4, yarn) - world/camera math in
 *       {@code MainMod}
 *   <li>{@link Matrix3x2fStack} (2D GUI) - crosshair transform in {@code InGameHud.renderCrosshair}
 * </ul>
 */
public class MatrixStack {

  private final net.minecraft.client.util.math.MatrixStack pose;
  private final Matrix3x2fStack gui;

  public MatrixStack() {
    this.pose = new net.minecraft.client.util.math.MatrixStack();
    this.gui = null;
  }

  public MatrixStack(net.minecraft.client.util.math.MatrixStack pose) {
    this.pose = pose;
    this.gui = null;
  }

  public MatrixStack(Matrix3x2fStack gui) {
    this.pose = null;
    this.gui = gui;
  }

  public net.minecraft.client.util.math.MatrixStack pose() {
    return this.pose;
  }

  public void push() {
    if (this.pose != null) {
      this.pose.push();
    } else {
      this.gui.pushMatrix();
    }
  }

  public void pop() {
    if (this.pose != null) {
      this.pose.pop();
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
      this.pose.multiply(RotationAxis.of(new Vector3f(x, y, z)).rotationDegrees(angle));
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
      this.pose.peek().getPositionMatrix().identity();
    } else {
      this.gui.identity();
    }
  }

  public float[] multiplyVector(float x, float y, float z, float w) {
    Vector4f vec = new Vector4f(x, y, z, w);

    vec.mul(this.pose.peek().getPositionMatrix());

    return new float[] {vec.x, vec.y, vec.z, vec.w};
  }
}
