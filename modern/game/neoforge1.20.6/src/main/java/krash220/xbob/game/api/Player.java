package krash220.xbob.game.api;

import krash220.xbob.mixin.LivingEntityAccessor;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;

public class Player {

    private static Pose lastPose;
    private static float lastHandHeight;
    private static ItemStack lastItem = ItemStack.EMPTY;
    private static int lastSlot = -1;

    public static float spinProgress(float partialTicks) {
        Minecraft mc = Minecraft.getInstance();

        if (mc.player.isAutoSpinAttack()) {
            return Mth.clamp((20 - ((LivingEntityAccessor) mc.player).getAutoSpinAttackTicks() + partialTicks) / 20.0f, 0.0f, 1.0f);
        }

        return 0.0f;
    }

    public static float sneakProgress(float partialTicks) {
        Minecraft mc = Minecraft.getInstance();
        Camera camera = mc.gameRenderer.getMainCamera();

        if (camera.getEntity() != null) {
            Entity entity = camera.getEntity();
            Pose pose = entity.getPose();

            if (pose != Pose.STANDING) {
                lastPose = pose;
            }

            if ((entity.getPose() == Pose.STANDING && lastPose == Pose.CROUCHING) || entity.getPose() == Pose.CROUCHING) {
                float standing = entity.getEyeHeight(Pose.STANDING);
                float crouching = entity.getEyeHeight(Pose.CROUCHING);
                float eyeHeight = entity.getEyeHeight(entity.getPose());

                return (eyeHeight - standing) / (crouching - standing);
            }
        }

        return 0.0f;
    }

    public static float swingProgress(float partialTicks) {
        Minecraft mc = Minecraft.getInstance();
        Camera camera = mc.gameRenderer.getMainCamera();
        LocalPlayer player = mc.player;

        if (camera.getEntity() == player && player.swinging) {
            return player.getAttackAnim(partialTicks);
        }

        return 1.0f;
    }

    public static float usingItemProgress(float partialTicks) {
        Minecraft mc = Minecraft.getInstance();
        Camera camera = mc.gameRenderer.getMainCamera();
        LocalPlayer player = mc.player;

        if (camera.getEntity() == player && player.isUsingItem()) {
            ItemStack item = player.getUseItem();
            float f = item.getUseDuration() - (player.getUseItemRemainingTicks() - partialTicks + 1.0f);
            UseAnim anim = item.getUseAnimation();

            if (anim == UseAnim.BOW) {
                return Math.min(f / 20.0f, 1.0f);
            } else if (anim == UseAnim.SPEAR) {
                return Math.min(f / 10.0f, 1.0f);
            } else {
                return f / item.getUseDuration();
            }
        }

        return 1.0f;
    }

    public static float getItemShake(float partialTicks) {
        Minecraft mc = Minecraft.getInstance();
        Camera camera = mc.gameRenderer.getMainCamera();
        LocalPlayer player = mc.player;

        if (camera.getEntity() == player && player.isUsingItem()) {
            ItemStack item = player.getUseItem();
            float f = item.getUseDuration() - (player.getUseItemRemainingTicks() - partialTicks + 1.0f);
            float f0 = 0.1f;
            UseAnim anim = item.getUseAnimation();

            if (anim == UseAnim.CROSSBOW && !CrossbowItem.isCharged(item) && player.getUseItemRemainingTicks() > 0) {
                f0 = Math.min(f / CrossbowItem.getChargeDuration(item), 1.0f);
            } else if (anim == UseAnim.BOW) {
                f0 = f / 20.0f;
                f0 = (f0 * f0 + f0 * 2.0f) / 3.0f;
                f0 = Math.min(f0, 1.0f);
            } else if (anim == UseAnim.SPEAR) {
                f0 = Math.min(f / 10.0f, 1.0f);
            } else {
                return 0.0f;
            }

            return Mth.sin((f - 0.1f) * 1.3f) * (f0 - 0.1f);
        }

        return 0.0f;
    }

    public static ItemType getUsingType() {
        Minecraft mc = Minecraft.getInstance();
        Camera camera = mc.gameRenderer.getMainCamera();
        LocalPlayer player = mc.player;

        if (camera.getEntity() == player && player.isUsingItem()) {
            ItemStack item = player.getUseItem();
            UseAnim anim = item.getUseAnimation();

            if (anim == UseAnim.EAT || anim == UseAnim.DRINK) {
                return ItemType.EATING;
            } else if (anim == UseAnim.BOW) {
                return ItemType.BOW;
            } else if (anim == UseAnim.SPEAR) {
                return ItemType.SPEAR;
            } else if (anim == UseAnim.CROSSBOW && !CrossbowItem.isCharged(item)) {
                return ItemType.CROSSBOW;
            }
        }

        return ItemType.NONE;
    }

    public static float changeItemProgress(float partialTicks) {
        Minecraft mc = Minecraft.getInstance();
        Camera camera = mc.gameRenderer.getMainCamera();
        LocalPlayer player = mc.player;

        if (camera.getEntity() == player && !player.isPassenger() && !player.isUsingItem() && !player.swinging) {
            ItemStack current = player.getMainHandItem();

            if (current.getItem() == lastItem.getItem()) {
                lastHandHeight = Math.max(lastHandHeight, 0.0f);
                return lastHandHeight;
            } else {
                lastItem = current.copy();
                lastHandHeight = 1.0f;
                return 1.0f;
            }
        }

        lastHandHeight = 1.0f;
        return 1.0f;
    }

    public enum ItemType {
        NONE,
        EATING,
        BOW,
        SPEAR,
        CROSSBOW
    }
}
