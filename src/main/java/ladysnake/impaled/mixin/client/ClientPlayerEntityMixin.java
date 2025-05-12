package ladysnake.impaled.mixin.client;

import ladysnake.impaled.common.Impaled;
import ladysnake.impaled.common.interfaces.IPlayerTargeting;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.PacketByteBuf;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ClientPlayerEntity.class})
public class ClientPlayerEntityMixin implements IPlayerTargeting {
    @Unique
    @Nullable LivingEntity lastTarget;
    @Unique
    int targetDecayTime;

    public ClientPlayerEntityMixin() {
    }

    public LivingEntity impaled$getLastTarget() {
        return this.lastTarget;
    }

    public void impaled$setLastTarget(LivingEntity target) {
        this.lastTarget = target;
        this.targetDecayTime = 60;
        if (target != null) {
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeInt(target.getId());
            ClientPlayNetworking.send(Impaled.targetPacket, buf);
        }

    }

    @Inject(
            method = {"tick"},
            at = {@At("TAIL")}
    )
    private void decayTarget(CallbackInfo ci) {
        if (this.targetDecayTime > 0) {
            --this.targetDecayTime;
            if (this.targetDecayTime == 0) {
                this.impaled$setLastTarget(null);
            }
        }

    }
}
