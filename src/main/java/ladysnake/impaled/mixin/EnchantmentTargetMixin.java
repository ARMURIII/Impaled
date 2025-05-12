package ladysnake.impaled.mixin;

import ladysnake.impaled.common.item.AtlanItem;
import ladysnake.impaled.common.item.MaelstromItem;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Enchantment.class)
public abstract class EnchantmentTargetMixin {

    @Shadow @Final public EnchantmentTarget target;

    @Inject(method = "isAcceptableItem", at = @At(value = "RETURN"), cancellable = true)
    public void isAcceptableItem(ItemStack itemStack, CallbackInfoReturnable<Boolean> cir) {
        Enchantment enchantment = (Enchantment)(Object)this;
        if (!cir.getReturnValue() && itemStack.getItem() instanceof AtlanItem && this.target == EnchantmentTarget.WEAPON) {
            cir.setReturnValue(true);
        }
        if (!cir.getReturnValue() && itemStack.getItem() instanceof MaelstromItem && enchantment == Enchantments.EFFICIENCY) {
            cir.setReturnValue(true);
        }
    }
}
