package krash220.xbob.game.api.math;

import org.joml.Vector3f;
import org.joml.Vector4f;

import net.minecraft.util.math.RotationAxis;

/**
 * Minecraft ≤1.21.5 matrix abstraction（yarn）。
 *
 * <p>GUI 与世界/相机共用 {@code net.minecraft.client.util.math.MatrixStack}（4x4）。
 * 1.21.6+ 才把 GUI 换成 {@code Matrix3x2fStack}。
 */
public class MatrixStack {

    private final net.minecraft.client.util.math.MatrixStack pose;

    public MatrixStack() {
        this.pose = new net.minecraft.client.util.math.MatrixStack();
    }

    public MatrixStack(net.minecraft.client.util.math.MatrixStack pose) {
        this.pose = pose;
    }

    public net.minecraft.client.util.math.MatrixStack pose() {
        return this.pose;
    }

    public void push() {
        this.pose.push();
    }

    public void pop() {
        this.pose.pop();
    }

    public void translate(double x, double y, double z) {
        this.pose.translate(x, y, z);
    }

    public void rotate(float angle, float x, float y, float z) {
        this.pose.multiply(RotationAxis.of(new Vector3f(x, y, z)).rotationDegrees(angle));
    }

    public void scale(float x, float y, float z) {
        this.pose.scale(x, y, z);
    }

    public void identity() {
        this.pose.peek().getPositionMatrix().identity();
    }

    public float[] multiplyVector(float x, float y, float z, float w) {
        Vector4f vec = new Vector4f(x, y, z, w);

        vec.mul(this.pose.peek().getPositionMatrix());

        return new float[] {vec.x, vec.y, vec.z, vec.w};
    }
}
