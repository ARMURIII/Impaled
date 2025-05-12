package ladysnake.impaled.mixin;

import ladysnake.impaled.common.interfaces.IPlayerTargeting;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin implements IPlayerTargeting {
    @Unique @Nullable LivingEntity lastTarget;
    @Unique int targetDecayTime;

    @Override
    public LivingEntity impaled$getLastTarget() {
        return this.lastTarget;
    }

    @Override
    public void impaled$setLastTarget(LivingEntity target) {
        this.lastTarget = target;
        this.targetDecayTime = 60;
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void impaled$decayTarget(CallbackInfo ci) {
        if (this.targetDecayTime > 0) {
            this.targetDecayTime--;
            if (this.targetDecayTime == 0) {
                this.impaled$setLastTarget(null);
            }
        }
    }
}