package ladysnake.impaled.common;

import com.terraformersmc.modmenu.api.ModMenuApi;
import eu.midnightdust.lib.config.MidnightConfig;
import ladysnake.impaled.common.init.ImpaledEntityTypes;
import ladysnake.impaled.common.init.ImpaledItems;
import ladysnake.impaled.common.interfaces.IPlayerTargeting;
import ladysnake.impaled.config.ImpaledConfig;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.util.Identifier;

public class Impaled implements ModInitializer {
    public static final String MODID = "impaled";

    private static final Identifier BASTION_TREASURE_CHEST_LOOT_TABLE_ID = new Identifier("minecraft", "chests/bastion_treasure");

    public static final Identifier targetPacket = id("target");

    public static Identifier id(String path) {
        return new Identifier(MODID, path);
    }

    @Override
    public void onInitialize() {
        ImpaledEntityTypes.init();
        ImpaledItems.init();
        registerPackets();

        // add loot to dungeons, mineshaft, jungle temples, and stronghold libraries chests loot tables
        UniformLootNumberProvider lootTableRange = UniformLootNumberProvider.create(1, 1);
        LootCondition chanceLootCondition = RandomChanceLootCondition.builder(60).build();
        LootTableEvents.MODIFY.register((resourceManager, lootManager, id, supplier, setter) -> {
            if (BASTION_TREASURE_CHEST_LOOT_TABLE_ID.equals(id)) {
                LootPool lootPool = LootPool.builder()
                        .rolls(lootTableRange)
                        .conditionally(chanceLootCondition)
                        .with(ItemEntry.builder(ImpaledItems.ANCIENT_TRIDENT).build()).build();
                supplier.pool(lootPool);
            }
        });
    }

    private void registerPackets() {
        ServerPlayNetworking.registerGlobalReceiver(targetPacket, (minecraftServer, serverPlayer, serverPlayNetworkHandler, packetByteBuf, packetSender) -> {
            int id = packetByteBuf.readInt();
            minecraftServer.execute(() -> {
                if (serverPlayer instanceof IPlayerTargeting targeting) {
                    Entity entity = serverPlayer.getWorld().getEntityById(id);
                    if (entity instanceof LivingEntity living) {
                        targeting.impaled$setLastTarget(living);
                    }
                }
            });
        });
    }
}
