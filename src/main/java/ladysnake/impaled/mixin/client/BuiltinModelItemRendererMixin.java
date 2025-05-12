package ladysnake.impaled.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(net.minecraft.client.render.item.BuiltinModelItemRenderer.class)
public class BuiltinModelItemRendererMixin {

    @ModifyExpressionValue(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isOf(Lnet/minecraft/item/Item;)Z",ordinal = 1))
    private boolean impaled$tridentsUseNormalModel(boolean original) {
        return false;
    }
}
