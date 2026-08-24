package krash220.xbob.mixin;

import krash220.xbob.game.api.Render;
import mirsario.cameraoverhaul.CameraSystem;
import mirsario.cameraoverhaul.utilities.Transform;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CameraSystem.class)
public class CameraOverhaulMixin {

  @Unique private double prevRotX;
  @Unique private double prevRotY;
  @Unique private double prevRotZ;

  /**
   * modifyCameraTransform 把相机世界旋转与 cameraoverhaul 的偏移旋转相加。 修改前后的差值 = cameraoverhaul
   * 添加的相对倾斜（roll/pitch 小角度）， 正是准星应跟随的倾斜量（直接使用世界旋转会锁死准星）。
   */
  @Inject(method = "modifyCameraTransform", at = @At("HEAD"))
  public void onModifyCameraTransformHead(Transform transform, CallbackInfo ci) {
    this.prevRotX = transform.eulerRot.x;
    this.prevRotY = transform.eulerRot.y;
    this.prevRotZ = transform.eulerRot.z;
  }

  @Inject(method = "modifyCameraTransform", at = @At("RETURN"))
  public void onModifyCameraTransformReturn(Transform transform, CallbackInfo ci) {
    Render.cameraOverhaulRot[0] = (float) (transform.eulerRot.x - this.prevRotX);
    Render.cameraOverhaulRot[1] = (float) (transform.eulerRot.y - this.prevRotY);
    Render.cameraOverhaulRot[2] = (float) (transform.eulerRot.z - this.prevRotZ);
  }
}
