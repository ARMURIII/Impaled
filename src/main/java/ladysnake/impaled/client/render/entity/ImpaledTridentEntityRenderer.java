package ladysnake.impaled.client.render.entity;

import ladysnake.impaled.client.ImpaledClient;
import ladysnake.impaled.client.render.entity.model.BlankModel;
import ladysnake.impaled.client.render.entity.model.ImpaledTridentEntityModel;
import ladysnake.impaled.common.entity.HellforkEntity;
import ladysnake.impaled.common.entity.ImpaledTridentEntity;
import ladysnake.impaled.common.init.ImpaledEntityTypes;
import ladysnake.impaled.compat.EnchancementCompat;
import ladysnake.impaled.config.ImpaledConfig;
import ladysnake.impaled.mixin.TridentEntityAccessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

import static ladysnake.impaled.config.ImpaledConfig.*;

@Environment(EnvType.CLIENT)
public class ImpaledTridentEntityRenderer extends EntityRenderer<ImpaledTridentEntity> {
    private final ImpaledTridentEntityModel model;
    private final Identifier texture;
    private final ItemStack stack;

    public ImpaledTridentEntityRenderer(EntityRendererFactory.Context context, Identifier texture, EntityModelLayer modelLayer, ItemStack stack) {
        super(context);
        this.model = new ImpaledTridentEntityModel(context.getPart(modelLayer));
        this.texture = texture;
        this.stack = stack;
    }

    public void render(ImpaledTridentEntity trident, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        if (ImpaledConfig.vanillaTridentModel) {
            if (EnchancementCompat.tryRenderLeechTrident(
                    trident,
                    matrices,
                    vertexConsumers,
                    model,
                    getTexture(trident),
                    light,
                    () -> super.render(trident, yaw, tickDelta, matrices, vertexConsumers, light)
            )) return;
            matrices.push();
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(MathHelper.lerp(tickDelta, trident.prevYaw, trident.getYaw()) - 90.0F));
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(MathHelper.lerp(tickDelta, trident.prevPitch, trident.getPitch()) +90.0F));
            VertexConsumer vertexConsumer = ItemRenderer.getDirectItemGlintConsumer(vertexConsumers, this.model.getLayer(this.getTexture(trident)), false, trident.isEnchanted());
            this.model.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
            matrices.pop();
        }else {
            renderAsItem(trident,yaw,tickDelta,matrices,vertexConsumers,light);
        }
        super.render(trident, yaw, tickDelta, matrices, vertexConsumers, light);
    }

    public void renderAsItem(ImpaledTridentEntity impaledTridentEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        ItemStack tridentStack = stack.copy();
        if (impaledTridentEntity.isEnchanted()) {
            tridentStack.addEnchantment(Enchantments.IMPALING,1);
        }
        if (EnchancementCompat.tryRenderLeechTridentAsItem(
                impaledTridentEntity,
                matrixStack,
                vertexConsumerProvider,
                tridentStack,
                i,
                () -> super.render(impaledTridentEntity, f, g, matrixStack, vertexConsumerProvider, i)
        )) return;
        matrixStack.push();
        //matrixStack.translate(-0.25,0,-0.25);
        matrixStack.scale(1.25f,1.25f,1.25f);
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(MathHelper.lerp(g, impaledTridentEntity.prevYaw, impaledTridentEntity.getYaw()) - 90.0F));
        matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(MathHelper.lerp(g, impaledTridentEntity.prevPitch, impaledTridentEntity.getPitch()) - 90F));
        matrixStack.translate(0,-0.25,0);
        MinecraftClient.getInstance().getItemRenderer().renderItem(tridentStack, ModelTransformationMode.HEAD, i, OverlayTexture.DEFAULT_UV, matrixStack, vertexConsumerProvider, impaledTridentEntity.getWorld(), 0);
        matrixStack.pop();
    }

    public Identifier getTexture(ImpaledTridentEntity impaledTridentEntity) {
        return this.texture;
    }
}
