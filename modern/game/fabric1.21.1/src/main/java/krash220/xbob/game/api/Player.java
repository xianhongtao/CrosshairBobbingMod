package krash220.xbob.game.api;

import krash220.xbob.mixin.LivingEntityAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.MathHelper;

public class Player {

    private static EntityPose lastPose;
    private static float lastHandHeight;
    private static ItemStack lastItem = ItemStack.EMPTY;
    private static int lastSlot = -1;

    public static float spinProgress(float partialTicks) {
        MinecraftClient mc = MinecraftClient.getInstance();

        if (mc.player.isUsingRiptide()) {
            return MathHelper.clamp((20 - ((LivingEntityAccessor) mc.player).getRiptideTicks() + partialTicks) / 20.0f, 0.0f, 1.0f);
        }

        return 0.0f;
    }

    public static float sneakProgress(float partialTicks) {
        MinecraftClient mc = MinecraftClient.getInstance();
        Camera camera = mc.gameRenderer.getCamera();

        if (camera.getFocusedEntity() != null) {
            Entity entity = camera.getFocusedEntity();
            EntityPose pose = entity.getPose();

            if (pose != EntityPose.STANDING) {
                lastPose = pose;
            }

            if ((entity.getPose() == EntityPose.STANDING && lastPose == EntityPose.CROUCHING) || entity.getPose() == EntityPose.CROUCHING) {
                float standing = entity.getEyeHeight(EntityPose.STANDING);
                float crouching = entity.getEyeHeight(EntityPose.CROUCHING);
                float eyeHeight = entity.getEyeHeight(entity.getPose());

                return (eyeHeight - standing) / (crouching - standing);
            }
        }

        return 0.0f;
    }

    public static float swingProgress(float partialTicks) {
        MinecraftClient mc = MinecraftClient.getInstance();
        Camera camera = mc.gameRenderer.getCamera();
        ClientPlayerEntity player = mc.player;

        if (camera.getFocusedEntity() == player && player.handSwinging) {
            return player.getHandSwingProgress(partialTicks);
        }

        return 1.0f;
    }

    public static float usingItemProgress(float partialTicks) {
        MinecraftClient mc = MinecraftClient.getInstance();
        Camera camera = mc.gameRenderer.getCamera();
        ClientPlayerEntity player = mc.player;

        if (camera.getFocusedEntity() == player && player.isUsingItem()) {
            ItemStack item = player.getActiveItem();
            float f = item.getMaxUseTime(player) - (player.getItemUseTimeLeft() - partialTicks + 1.0f);
            UseAction anim = item.getUseAction();

            if (anim == UseAction.BOW) {
                return Math.min(f / 20.0f, 1.0f);
            } else if (anim == UseAction.SPEAR) {
                return Math.min(f / 10.0f, 1.0f);
            } else {
                return f / item.getMaxUseTime(player);
            }
        }

        return 1.0f;
    }

    public static float getItemShake(float partialTicks) {
        MinecraftClient mc = MinecraftClient.getInstance();
        Camera camera = mc.gameRenderer.getCamera();
        ClientPlayerEntity player = mc.player;

        if (camera.getFocusedEntity() == player && player.isUsingItem()) {
            ItemStack item = player.getActiveItem();
            float f = item.getMaxUseTime(player) - (player.getItemUseTimeLeft() - partialTicks + 1.0f);
            float f0 = 0.1f;
            UseAction anim = item.getUseAction();

            if (anim == UseAction.CROSSBOW && !CrossbowItem.isCharged(item) && player.getItemUseTimeLeft() > 0) {
                f0 = Math.min(f / CrossbowItem.getPullTime(item, player), 1.0f);
            } else if (anim == UseAction.BOW) {
                f0 = f / 20.0f;
                f0 = (f0 * f0 + f0 * 2.0f) / 3.0f;
                f0 = Math.min(f0, 1.0f);
            } else if (anim == UseAction.SPEAR) {
                f0 = Math.min(f / 10.0f, 1.0f);
            } else {
                return 0.0f;
            }

            return MathHelper.sin((f - 0.1f) * 1.3f) * (f0 - 0.1f);
        }

        return 0.0f;
    }

    public static ItemType getUsingType() {
        MinecraftClient mc = MinecraftClient.getInstance();
        Camera camera = mc.gameRenderer.getCamera();
        ClientPlayerEntity player = mc.player;

        if (camera.getFocusedEntity() == player && player.isUsingItem()) {
            ItemStack item = player.getActiveItem();
            UseAction anim = item.getUseAction();

            if (anim == UseAction.EAT || anim == UseAction.DRINK) {
                return ItemType.EATING;
            } else if (anim == UseAction.BOW) {
                return ItemType.BOW;
            } else if (anim == UseAction.SPEAR) {
                return ItemType.SPEAR;
            } else if (anim == UseAction.CROSSBOW && !CrossbowItem.isCharged(item)) {
                return ItemType.CROSSBOW;
            }
        }

        return ItemType.NONE;
    }

    public static float changeItemProgress(float partialTicks) {
        MinecraftClient mc = MinecraftClient.getInstance();
        Camera camera = mc.gameRenderer.getCamera();
        ClientPlayerEntity player = mc.player;

        if (camera.getFocusedEntity() == player && !player.isRiding() && !player.isUsingItem() && !player.handSwinging) {
            ItemStack current = player.getMainHandStack();

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
