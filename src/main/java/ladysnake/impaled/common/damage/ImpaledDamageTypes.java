package ladysnake.impaled.common.damage;

import ladysnake.impaled.common.Impaled;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageSources;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public final class ImpaledDamageTypes {
    public static final RegistryKey<DamageType> HELLFORK_HEAT = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.of(Impaled.MODID,"hellfork_heat"));
}
