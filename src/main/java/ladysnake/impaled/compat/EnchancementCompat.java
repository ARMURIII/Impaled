/*
 * Sincere-Loyalty
 * Copyright (C) 2020 Ladysnake
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; If not, see <https://www.gnu.org/licenses>.
 */
package ladysnake.impaled.compat;

import ladysnake.impaled.common.entity.ImpaledTridentEntity;
import moriyashiine.enchancement.client.util.EnchancementClientUtil;
import moriyashiine.enchancement.common.ModConfig;
import moriyashiine.enchancement.common.component.entity.LeechComponent;
import moriyashiine.enchancement.common.component.entity.WarpComponent;
import moriyashiine.enchancement.common.init.ModEntityComponents;
import moriyashiine.enchancement.common.util.EnchancementUtil;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static ladysnake.impaled.config.ImpaledConfig.*;

public final class EnchancementCompat {
    public static final boolean enabled = FabricLoader.getInstance().isModLoaded("enchancement");

    public static void tryEnableEnchantments(ImpaledTridentEntity trident, LivingEntity user, ItemStack stack) {
        if (enabled) {
            LeechComponent.maybeSet(user, stack, trident);
            WarpComponent.maybeSet(user, stack, trident);
        }
    }

    public static boolean tryRenderLeechTrident(TridentEntity trident, MatrixStack matrices, VertexConsumerProvider vertexConsumers, Model model, Identifier texture, int light, Runnable runnable) {
        if (enabled) {
            CallbackInfo ci = new CallbackInfo("render", true); // funni mixin-based API
            EnchancementClientUtil.renderLeechTrident(trident, matrices, vertexConsumers, model, texture, light, runnable, ci);
            return ci.isCancelled();
        }
        return false;
    }

    public static boolean tryRenderLeechTridentAsItem(TridentEntity entity, MatrixStack matrices, VertexConsumerProvider vertexConsumers,ItemStack tridentStack, int light, Runnable runnable) {
        if (enabled) {
            LeechComponent leechComponent = ModEntityComponents.LEECH.get(entity);
            LivingEntity stuckEntity = leechComponent.getStuckEntity();
            if (stuckEntity != null) {
                float offsetX = MathHelper.sin(leechComponent.getRenderTicks());
                float offsetZ = MathHelper.cos(leechComponent.getRenderTicks());
                matrices.push();
                matrices.translate(offsetX, -1F, offsetZ);
                matrices.scale(1.25f,1.25f,1.25f);
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((float)(-MathHelper.wrapDegrees(MathHelper.atan2(stuckEntity.getZ() - entity.getZ() + (double)offsetZ, stuckEntity.getX() - entity.getX() + (double)offsetX) * 57.2957763671875 - 90))));
                matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(60.0F-180.0F));
                matrices.translate(0,leechComponent.getStabTicks()/2,-0.5);
                matrices.translate(0, -1, -0.25);
                MinecraftClient.getInstance().getItemRenderer().renderItem(tridentStack, ModelTransformationMode.HEAD, light, OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, entity.getWorld(), 0);
                matrices.pop();
                runnable.run();
                return true;
            }
        }
        return false;
    }

    public static boolean areTridentsLoyal() {
        if (enabled) {
            return ModConfig.allTridentsHaveLoyalty && !EnchancementUtil.isEnchantmentAllowed(Enchantments.LOYALTY);
        }
        return false;
    }

    public static float getBonusBerserkDamage(LivingEntity living, ItemStack stack) {
        if (enabled) {
            return EnchancementUtil.getBonusBerserkDamage(living, stack);
        }
        return 0f;
    }
}