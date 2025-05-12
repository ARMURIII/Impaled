package ladysnake.impaled.common.entity;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;

import java.util.List;
import java.util.stream.Stream;

public class AtlanTridentEntity extends ImpaledTridentEntity{

    public AtlanTridentEntity(EntityType<? extends ImpaledTridentEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);
        ItemStack stack = this.getTridentStack();
        Stream<Enchantment> enchantments = EnchantmentHelper.get(stack).keySet().stream();
        if (this.getOwner() instanceof LivingEntity entity) {
            enchantments.forEach(enchantment -> enchantment.onTargetDamaged(entity,entityHitResult.getEntity(),EnchantmentHelper.getLevel(enchantment,stack)+1));
        }
        entityHitResult.getEntity();
    }
}
