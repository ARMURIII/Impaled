package ladysnake.impaled.common.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import ladysnake.impaled.common.entity.ImpaledTridentEntity;
import ladysnake.impaled.compat.EnchancementCompat;
import ladysnake.sincereloyalty.LoyalTrident;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.TridentItem;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class ImpaledTridentItem extends TridentItem {
    EntityType<? extends ImpaledTridentEntity> type;
    private final Multimap<EntityAttribute, EntityAttributeModifier> attributeModifiers;

    public ImpaledTridentItem(Settings settings, EntityType<? extends ImpaledTridentEntity> entityType, double attackDamage, double attackSpeed) {
        super(settings);
        this.type = entityType;
        ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> builder = ImmutableMultimap.builder();
        if (attackDamage == 0) attackDamage = 8.0;
        if (attackSpeed == 0) attackSpeed = -2.9000000953674316;
        builder.put(EntityAttributes.GENERIC_ATTACK_DAMAGE, new EntityAttributeModifier(ATTACK_DAMAGE_MODIFIER_ID, "Tool modifier", attackDamage, EntityAttributeModifier.Operation.ADDITION));
        builder.put(EntityAttributes.GENERIC_ATTACK_SPEED, new EntityAttributeModifier(ATTACK_SPEED_MODIFIER_ID, "Tool modifier", attackSpeed, EntityAttributeModifier.Operation.ADDITION));
        this.attributeModifiers = builder.build();
    }

    public EntityType<? extends ImpaledTridentEntity> getEntityType() {
        return type;
    }

    public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot slot) {
        return slot == EquipmentSlot.MAINHAND ? this.attributeModifiers : super.getAttributeModifiers(slot);
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (user instanceof PlayerEntity player) {
            int i = this.getMaxUseTime(stack) - remainingUseTicks;
            if (i >= 10) {
                int j = EnchantmentHelper.getRiptide(stack);
                if (j <= 0 || canRiptide(player)) {
                    if (!world.isClient) {
                        stack.damage(1, player, livingEntity -> livingEntity.sendToolBreakStatus(user.getActiveHand()));
                        if (j == 0) {
                            ImpaledTridentEntity trident = createTrident(world, player, stack);
                            LoyalTrident.of(trident).loyaltrident_setReturnSlot(player.getActiveHand() == Hand.OFF_HAND ? -1 : player.getInventory().selectedSlot);

                            if (player.getAbilities().creativeMode) {
                                trident.pickupType = PersistentProjectileEntity.PickupPermission.CREATIVE_ONLY;
                            }

                            world.spawnEntity(trident);
                            world.playSoundFromEntity(null, trident, SoundEvents.ITEM_TRIDENT_THROW, SoundCategory.PLAYERS, 1.0F, 1.0F);
                            if (!player.getAbilities().creativeMode) {
                                player.getInventory().removeOne(stack);
                            }
                        }
                    }

                    player.incrementStat(Stats.USED.getOrCreateStat(this));
                    if (j > 0) {
                        float f = player.getYaw();
                        float g = player.getPitch();
                        float h = -MathHelper.sin(f * 0.017453292F) * MathHelper.cos(g * 0.017453292F);
                        float k = -MathHelper.sin(g * 0.017453292F);
                        float l = MathHelper.cos(f * 0.017453292F) * MathHelper.cos(g * 0.017453292F);
                        float m = MathHelper.sqrt(h * h + k * k + l * l);
                        float n = 3.0F * ((1.0F + (float) j) / 4.0F);
                        h *= n / m;
                        k *= n / m;
                        l *= n / m;
                        player.addVelocity(h, k, l);
                        player.useRiptide(20);
                        if (player.isOnGround()) {
                            player.move(MovementType.SELF, new Vec3d(0.0D, 1.1999999284744263D, 0.0D));
                        }

                        SoundEvent soundEvent3;
                        if (j >= 3) {
                            soundEvent3 = SoundEvents.ITEM_TRIDENT_RIPTIDE_3;
                        } else if (j == 2) {
                            soundEvent3 = SoundEvents.ITEM_TRIDENT_RIPTIDE_2;
                        } else {
                            soundEvent3 = SoundEvents.ITEM_TRIDENT_RIPTIDE_1;
                        }

                        world.playSoundFromEntity(null, player, soundEvent3, SoundCategory.PLAYERS, 1.0F, 1.0F);
                    }
                }
            }
        }
    }

    protected boolean canRiptide(PlayerEntity playerEntity) {
        return playerEntity.isTouchingWaterOrRain();
    }

    public @NotNull ImpaledTridentEntity createTrident(World world, LivingEntity user, ItemStack stack) {
        ImpaledTridentEntity impaledTridentEntity = Objects.requireNonNull(this.type.create(world));
        impaledTridentEntity.setTridentAttributes(stack);
        impaledTridentEntity.setOwner(user);
        impaledTridentEntity.setTridentStack(stack);
        impaledTridentEntity.setVelocity(user, user.getPitch(), user.getYaw(), 0.0F, 2.5F, 1.0F);
        impaledTridentEntity.updatePosition(user.getX(), user.getEyeY() - 0.1, user.getZ());
        EnchancementCompat.tryEnableEnchantments(impaledTridentEntity, user, stack);
        return impaledTridentEntity;
    }

    public float getDamage() {
        return 8f;
    }

    @Override
    public boolean damage(DamageSource source) {
        return super.damage(source);
    }
}
