package ladysnake.impaled.mixin.client;

import ladysnake.impaled.common.init.ImpaledItems;
import ladysnake.sincereloyalty.SincereLoyalty;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.TridentRiptideFeatureRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import static ladysnake.impaled.client.ImpaledClient.HELLFORK_RIPTIDE_TEXTURE;
import static ladysnake.impaled.client.ImpaledClient.SOULFORK_RIPTIDE_TEXTURE;

@Mixin(TridentRiptideFeatureRenderer.class)
public abstract class TridentRiptideFeatureRendererMixin {
    @Shadow @Final public static Identifier TEXTURE;

    @ModifyVariable(method = "render", at = @At("STORE"))
    private VertexConsumer swapHotRiptide(VertexConsumer orig, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light, LivingEntity livingEntity) {
        if (livingEntity instanceof PlayerEntity && livingEntity.isUsingRiptide() && (livingEntity.getMainHandStack().getItem() == ImpaledItems.HELLFORK || (livingEntity.getOffHandStack().getItem() == ImpaledItems.HELLFORK) && !livingEntity.getMainHandStack().isIn(SincereLoyalty.TRIDENTS))) {
            return vertexConsumerProvider.getBuffer(RenderLayer.getEntityTranslucentEmissive(HELLFORK_RIPTIDE_TEXTURE));
        } else if (livingEntity instanceof PlayerEntity && livingEntity.isUsingRiptide() && (livingEntity.getMainHandStack().getItem() == ImpaledItems.SOULFORK || (livingEntity.getOffHandStack().getItem() == ImpaledItems.SOULFORK) && !livingEntity.getMainHandStack().isIn(SincereLoyalty.TRIDENTS))) {
            return vertexConsumerProvider.getBuffer(RenderLayer.getEntityTranslucentEmissive(SOULFORK_RIPTIDE_TEXTURE));
        }
        return vertexConsumerProvider.getBuffer(RenderLayer.getEntityCutoutNoCull(TEXTURE));
    }
}