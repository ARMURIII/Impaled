package ladysnake.impaled.mixin;

import ladysnake.impaled.common.item.AtlanItem;
import ladysnake.impaled.common.item.ImpaledTridentItem;
import ladysnake.impaled.compat.EnchancementCompat;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(TridentEntity.class)
public class TridentEntityMixin {

    @Shadow private ItemStack tridentStack;

    @ModifyConstant(method = "onEntityHit", constant = @Constant(floatValue = 8.0f,ordinal = 0))
    private float impaled$changeDamage(float constant) {
        if (tridentStack.getItem() instanceof ImpaledTridentItem impaledTrident) {
            if (impaledTrident instanceof AtlanItem) {
                TridentEntity entity = (TridentEntity) (Object)this;
                if (entity.getOwner() instanceof LivingEntity living) {
                    return (float) living.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE) + EnchancementCompat.getBonusBerserkDamage(living,tridentStack);
                }
            }
            return impaledTrident.getDamage();
        }
        return constant;
    }
}
