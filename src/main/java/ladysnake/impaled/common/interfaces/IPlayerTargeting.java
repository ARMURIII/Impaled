package ladysnake.impaled.common.interfaces;


import net.minecraft.entity.LivingEntity;

public interface IPlayerTargeting {
    LivingEntity impaled$getLastTarget();

    void impaled$setLastTarget(LivingEntity var1);
}
