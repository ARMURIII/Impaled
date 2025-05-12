package ladysnake.impaled.common.item;

import ladysnake.impaled.common.damage.ImpaledDamageTypes;
import ladysnake.impaled.common.entity.ImpaledTridentEntity;
import ladysnake.impaled.common.init.ImpaledItems;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.*;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FlintAndSteelItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class HellforkItem extends ImpaledTridentItem {
    public HellforkItem(Settings settings, EntityType<? extends ImpaledTridentEntity> entityType) {
        super(settings, entityType,0,0);
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        target.setOnFireFor(4 + attacker.getRandom().nextInt(4));
        return super.postHit(stack, target, attacker);
    }

    @Override
    protected boolean canRiptide(PlayerEntity playerEntity) {
        return playerEntity.isInLava() || playerEntity.isOnFire() || playerEntity.getMainHandStack().getItem() == ImpaledItems.SOULFORK || playerEntity.getOffHandStack().getItem() == ImpaledItems.SOULFORK;
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        super.onStoppedUsing(stack, world, user, remainingUseTicks);

        if (user instanceof PlayerEntity player && user.isUsingRiptide() && stack.getItem() == ImpaledItems.SOULFORK) {
            if (player.experienceLevel <= 0) {
                DamageSource source = world.getDamageSources().create(ImpaledDamageTypes.HELLFORK_HEAT);
                user.damage(source, 2f);
                user.playSound(SoundEvents.ENTITY_PLAYER_HURT, 1.0f, 1.0f);
            } else {
                if (player.getNextLevelExperience()<=4) player.addExperienceLevels(-1);
                else player.addExperience(-17);
            }
            if (world instanceof ServerWorld serverWorld) {
                serverWorld.spawnParticles(ParticleTypes.SOUL, user.getX(), user.getY(), user.getZ(), 20, user.getRandom().nextFloat(), user.getRandom().nextGaussian(), user.getRandom().nextFloat(), user.getRandom().nextFloat() / 10f);
            }
            user.playSound(SoundEvents.PARTICLE_SOUL_ESCAPE, 5.0f, 1.0f);
        }
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        if (itemStack.getDamage() >= itemStack.getMaxDamage() - 1) {
            return TypedActionResult.fail(itemStack);
        } else if (EnchantmentHelper.getRiptide(itemStack) > 0 && !user.isInLava() && !user.isOnFire() && !(itemStack.hasNbt() && itemStack.getItem() == ImpaledItems.SOULFORK)) {
            return TypedActionResult.fail(itemStack);
        } else {
            user.setCurrentHand(hand);
            return TypedActionResult.consume(itemStack);
        }
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        if ((context.getWorld().getBlockState(context.getBlockPos()).getBlock() == Blocks.SOUL_CAMPFIRE || context.getWorld().getBlockState(context.getBlockPos()).getBlock() == Blocks.SOUL_LANTERN || context.getWorld().getBlockState(context.getBlockPos()).getBlock() == Blocks.SOUL_TORCH || context.getWorld().getBlockState(context.getBlockPos()).getBlock() == Blocks.SOUL_WALL_TORCH) && context.getStack().getItem() == ImpaledItems.HELLFORK) {
            ItemStack soulfork = new ItemStack(ImpaledItems.SOULFORK, context.getStack().getCount());
            soulfork.setNbt(context.getStack().getNbt());
            context.getWorld().playSound(context.getPlayer().getX(), context.getPlayer().getY(), context.getPlayer().getZ(), SoundEvents.ITEM_FIRECHARGE_USE, SoundCategory.PLAYERS, 1.0f, 1.0f, false);
            context.getWorld().playSound(context.getPlayer().getX(), context.getPlayer().getY(), context.getPlayer().getZ(), SoundEvents.PARTICLE_SOUL_ESCAPE, SoundCategory.PLAYERS, 1.0f, 1.0f, false);
            context.getWorld().playSound(context.getPlayer().getX(), context.getPlayer().getY(), context.getPlayer().getZ(), SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, SoundCategory.PLAYERS, 1.0f, 1.0f, false);

            context.getPlayer().setStackInHand(context.getHand(), soulfork);

            BlockState blockState = context.getWorld().getBlockState(context.getBlockPos());
            BlockState replacedBlockState = blockState;
            if (context.getWorld().getBlockState(context.getBlockPos()).getBlock() == Blocks.SOUL_CAMPFIRE) {
                replacedBlockState = Blocks.CAMPFIRE.getDefaultState();
            } else if (context.getWorld().getBlockState(context.getBlockPos()).getBlock() == Blocks.SOUL_LANTERN) {
                replacedBlockState = Blocks.LANTERN.getDefaultState();
            } else if (context.getWorld().getBlockState(context.getBlockPos()).getBlock() == Blocks.SOUL_TORCH) {
                replacedBlockState = Blocks.TORCH.getDefaultState();
            } else if (context.getWorld().getBlockState(context.getBlockPos()).getBlock() == Blocks.SOUL_WALL_TORCH) {
                replacedBlockState = Blocks.WALL_TORCH.getDefaultState();
            }
            for (Property property : context.getWorld().getBlockState(context.getBlockPos()).getProperties()) {
                if (replacedBlockState.getProperties().contains(property)) {
                    replacedBlockState = replacedBlockState.with(property, blockState.get(property));
                }
            }
            context.getWorld().setBlockState(context.getBlockPos(), replacedBlockState);

            for (int i = 0; i < 20; i++) {
                context.getWorld().addParticle(ParticleTypes.SOUL, context.getBlockPos().getX() + .5 + context.getWorld().getRandom().nextGaussian() / 10, context.getBlockPos().getY() + .5 + context.getWorld().getRandom().nextGaussian() / 10, context.getBlockPos().getZ() + .5 + context.getWorld().getRandom().nextGaussian() / 10, 0, context.getWorld().getRandom().nextFloat() / 10, 0);
            }
            return ActionResult.SUCCESS;
        } else if ((context.getWorld().getBlockState(context.getBlockPos()).getBlock() == Blocks.CAMPFIRE || context.getWorld().getBlockState(context.getBlockPos()).getBlock() == Blocks.LANTERN || context.getWorld().getBlockState(context.getBlockPos()).getBlock() == Blocks.TORCH || context.getWorld().getBlockState(context.getBlockPos()).getBlock() == Blocks.WALL_TORCH) && context.getStack().getItem() == ImpaledItems.SOULFORK) {
            ItemStack hellfork = new ItemStack(ImpaledItems.HELLFORK, context.getStack().getCount());
            hellfork.setNbt(context.getStack().getNbt());
            context.getWorld().playSound(context.getPlayer().getX(), context.getPlayer().getY(), context.getPlayer().getZ(), SoundEvents.ITEM_FIRECHARGE_USE, SoundCategory.PLAYERS, 1.0f, 1.0f, false);
            context.getWorld().playSound(context.getPlayer().getX(), context.getPlayer().getY(), context.getPlayer().getZ(), SoundEvents.PARTICLE_SOUL_ESCAPE, SoundCategory.PLAYERS, 1.0f, 0.8f, false);
            context.getWorld().playSound(context.getPlayer().getX(), context.getPlayer().getY(), context.getPlayer().getZ(), SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, SoundCategory.PLAYERS, 1.0f, 0.8f, false);

            context.getPlayer().setStackInHand(context.getHand(), hellfork);

            BlockState blockState = context.getWorld().getBlockState(context.getBlockPos());
            BlockState replacedBlockState = blockState;
            if (context.getWorld().getBlockState(context.getBlockPos()).getBlock() == Blocks.CAMPFIRE) {
                replacedBlockState = Blocks.SOUL_CAMPFIRE.getDefaultState();
            } else if (context.getWorld().getBlockState(context.getBlockPos()).getBlock() == Blocks.LANTERN) {
                replacedBlockState = Blocks.SOUL_LANTERN.getDefaultState();
            } else if (context.getWorld().getBlockState(context.getBlockPos()).getBlock() == Blocks.TORCH) {
                replacedBlockState = Blocks.SOUL_TORCH.getDefaultState();
            } else if (context.getWorld().getBlockState(context.getBlockPos()).getBlock() == Blocks.WALL_TORCH) {
                replacedBlockState = Blocks.SOUL_WALL_TORCH.getDefaultState();
            }
            for (Property property : context.getWorld().getBlockState(context.getBlockPos()).getProperties()) {
                if (replacedBlockState.getProperties().contains(property)) {
                    replacedBlockState = replacedBlockState.with(property, blockState.get(property));
                }
            }
            context.getWorld().setBlockState(context.getBlockPos(), replacedBlockState);

            for (int i = 0; i < 20; i++) {
                context.getWorld().addParticle(ParticleTypes.SOUL_FIRE_FLAME, context.getBlockPos().getX() + .5 + context.getWorld().getRandom().nextGaussian() / 10, context.getBlockPos().getY() + .5 + context.getWorld().getRandom().nextGaussian() / 10, context.getBlockPos().getZ() + .5 + context.getWorld().getRandom().nextGaussian() / 10, 0, context.getWorld().getRandom().nextFloat() / 10, 0);
            }
            return ActionResult.SUCCESS;
        }

        if (context.getPlayer().isSneaking()) {
            return this.Fire(context);
        }

        return super.useOnBlock(context);
    }

    public ActionResult Fire(ItemUsageContext context) {
        PlayerEntity playerEntity = context.getPlayer();
        World world = context.getWorld();
        BlockPos blockPos = context.getBlockPos();
        BlockState blockState = world.getBlockState(blockPos);
        if (!CampfireBlock.canBeLit(blockState) && !CandleBlock.canBeLit(blockState) && !CandleCakeBlock.canBeLit(blockState)) {
            BlockPos blockPos2 = blockPos.offset(context.getSide());
            if (AbstractFireBlock.canPlaceAt(world, blockPos2, context.getHorizontalPlayerFacing())) {
                world.playSound(playerEntity, blockPos2, SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.0F, world.getRandom().nextFloat() * 0.4F + 0.8F);
                BlockState blockState2 = AbstractFireBlock.getState(world, blockPos2);
                world.setBlockState(blockPos2, blockState2, 11);
                world.emitGameEvent(playerEntity, GameEvent.BLOCK_PLACE, blockPos);
                ItemStack itemStack = context.getStack();
                if (playerEntity instanceof ServerPlayerEntity) {
                    Criteria.PLACED_BLOCK.trigger((ServerPlayerEntity)playerEntity, blockPos2, itemStack);
                    itemStack.damage(1, playerEntity, (p) -> {
                        p.sendToolBreakStatus(context.getHand());
                    });
                }

                return ActionResult.success(world.isClient());
            } else {
                return ActionResult.FAIL;
            }
        } else {
            world.playSound(playerEntity, blockPos, SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.0F, world.getRandom().nextFloat() * 0.4F + 0.8F);
            world.setBlockState(blockPos, (BlockState)blockState.with(Properties.LIT, true), 11);
            world.emitGameEvent(playerEntity, GameEvent.BLOCK_CHANGE, blockPos);
            if (playerEntity != null) {
                context.getStack().damage(1, playerEntity, (p) -> {
                    p.sendToolBreakStatus(context.getHand());
                });
            }

            return ActionResult.success(world.isClient());
        }
    }
}
