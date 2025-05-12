package ladysnake.impaled.mixin.client;

import ladysnake.impaled.common.entity.ImpaledTridentEntity;
import ladysnake.impaled.compat.EnchancementCompat;
import ladysnake.impaled.config.ImpaledConfig;
import moriyashiine.enchancement.client.util.EnchancementClientUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.TridentEntityRenderer;
import net.minecraft.client.render.entity.model.TridentEntityModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = TridentEntityRenderer.class,priority = 999)
public abstract class TridentEntityRendererMixin extends EntityRenderer<TridentEntity> {

    protected TridentEntityRendererMixin(EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Inject(
            method = {"render(Lnet/minecraft/entity/projectile/TridentEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V"},
            at = {@At("HEAD")},
            cancellable = true
    )
    private void impaled$renderAsItem(TridentEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        if (!ImpaledConfig.vanillaTridentModel) {
            renderAsItem(entity,yaw,tickDelta,matrices,vertexConsumers,light);
            ci.cancel();
        }
    }

    @Unique
    public void renderAsItem(TridentEntity TridentEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        ItemStack tridentStack = Items.TRIDENT.getDefaultStack();
        if (TridentEntity.isEnchanted()) {
            tridentStack.addEnchantment(Enchantments.IMPALING,1);
        }
        if (EnchancementCompat.tryRenderLeechTridentAsItem(
                TridentEntity,
                matrixStack,
                vertexConsumerProvider,
                tridentStack,
                i,
                () -> super.render(TridentEntity, f, g, matrixStack, vertexConsumerProvider, i)
        )) return;
        matrixStack.push();
        matrixStack.scale(1.25f,1.25f,1.25f);
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(MathHelper.lerp(g, TridentEntity.prevYaw, TridentEntity.getYaw()) - 90.0F));
        matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(MathHelper.lerp(g, TridentEntity.prevPitch, TridentEntity.getPitch()) - 90F));
        matrixStack.translate(-0.25,-0.5,0);
        MinecraftClient.getInstance().getItemRenderer().renderItem(tridentStack, ModelTransformationMode.HEAD, i, OverlayTexture.DEFAULT_UV, matrixStack, vertexConsumerProvider, TridentEntity.getWorld(), 0);
        matrixStack.pop();
    }
}
