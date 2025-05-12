package ladysnake.impaled.client;

import eu.midnightdust.lib.config.MidnightConfig;
import ladysnake.impaled.client.render.entity.ImpaledTridentEntityRenderer;
import ladysnake.impaled.client.render.entity.model.ImpaledTridentEntityModel;
import ladysnake.impaled.common.Impaled;
import ladysnake.impaled.common.init.ImpaledEntityTypes;
import ladysnake.impaled.common.init.ImpaledItems;
import ladysnake.impaled.common.item.ImpaledTridentItem;
import ladysnake.impaled.config.ImpaledConfig;
import ladysnake.sincereloyalty.SincereLoyalty;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

public class ImpaledClient implements ClientModInitializer {
    public static final Identifier HELLFORK_RIPTIDE_TEXTURE = new Identifier(Impaled.MODID, "textures/entity/hellfork_riptide.png");
    public static final Identifier SOULFORK_RIPTIDE_TEXTURE = new Identifier(Impaled.MODID, "textures/entity/soulfork_riptide.png");
    public static final EntityModelLayer ATLAN = new EntityModelLayer(new Identifier(Impaled.MODID, "atlan"), "main");

    @Override
    public void onInitializeClient() {
        MidnightConfig.init(Impaled.MODID, ImpaledConfig.class);
        EntityModelLayerRegistry.registerModelLayer(ATLAN, ImpaledTridentEntityModel::getAtlanTexturedModelData);

        FabricLoader.getInstance().getModContainer(Impaled.MODID).ifPresent(mod ->
            ResourceManagerHelper.registerBuiltinResourcePack(Impaled.id("2d_tridents"), mod, ResourcePackActivationType.NORMAL));

        for (ImpaledTridentItem item : ImpaledItems.ALL_TRIDENTS) {
            Identifier tridentId = Registries.ITEM.getId(item);
            Identifier texture = new Identifier(tridentId.getNamespace(), "textures/entity/" + tridentId.getPath() + ".png");

            EntityModelLayer modelLayer = item == ImpaledItems.ATLAN ? ATLAN : EntityModelLayers.TRIDENT;
            EntityRendererRegistry.register(item.getEntityType(), ctx -> new ImpaledTridentEntityRenderer(ctx, texture, modelLayer, item.getDefaultStack()));

            FabricModelPredicateProviderRegistry.register(item, new Identifier("throwing"), (stack, world, entity, seed) -> entity != null && entity.isUsingItem() && entity.getActiveItem() == stack ? 1.0F : 0.0F);
        }

        // Add items to groups
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register((content) -> {
            content.add(ImpaledItems.ELDER_GUARDIAN_EYE);
            content.add(ImpaledItems.ANCIENT_TRIDENT);

            content.addAfter(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE, SincereLoyalty.LOYALTY_UPGRADE_SMITHING_TEMPLATE);
            content.addAfter(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE,ImpaledItems.TRIDENT_UPGRADE_SMITHING_TEMPLATE);
        });

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register((content) -> {
            content.addAfter(Items.TRIDENT, ImpaledItems.MAELSTROM);
            content.addAfter(Items.TRIDENT, ImpaledItems.ATLAN);
            content.addAfter(Items.TRIDENT, ImpaledItems.ELDER_TRIDENT);
            content.addAfter(Items.TRIDENT, ImpaledItems.SOULFORK);
            content.addAfter(Items.TRIDENT, ImpaledItems.HELLFORK);
            content.addAfter(Items.TRIDENT, ImpaledItems.PITCHFORK);
        });

        //EntityRendererRegistry.register(ImpaledEntityTypes.GUARDIAN_TRIDENT, ctx -> new ImpaledTridentEntityRenderer(ctx, new Identifier(Impaled.MODID, "textures/entity/guardian_trident.png"), EntityModelLayers.TRIDENT,ImpaledItems.GUARDIAN_TRIDENT.getDefaultStack()));
    }
}
