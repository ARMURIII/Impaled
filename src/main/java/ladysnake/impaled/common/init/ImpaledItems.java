package ladysnake.impaled.common.init;

import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import ladysnake.impaled.common.Impaled;
import ladysnake.impaled.common.entity.ImpaledTridentEntity;
import ladysnake.impaled.common.item.*;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.ProjectileDispenserBehavior;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SmithingTemplateItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.Util;
import net.minecraft.util.math.Position;
import net.minecraft.world.World;

import java.util.List;
import java.util.Objects;
import java.util.Set;

public class ImpaledItems {
    public static final Set<ImpaledTridentItem> ALL_TRIDENTS = new ReferenceOpenHashSet<>();
    public static Item ELDER_GUARDIAN_EYE;
    public static Item ANCIENT_TRIDENT;
    public static Item PITCHFORK;
    public static Item HELLFORK;
    public static Item SOULFORK;
    public static Item ELDER_TRIDENT;
    public static Item GUARDIAN_TRIDENT;
    public static Item ATLAN;
    public static Item MAELSTROM;
    public static final Item TRIDENT_UPGRADE_SMITHING_TEMPLATE = new SmithingTemplateItem(
            Text.translatable(
                    Util.createTranslationKey("item", Impaled.id("smithing_template.trident_upgrade.applies_to"))
            ).formatted(Formatting.BLUE),
            Text.translatable(
                    Util.createTranslationKey("item", Impaled.id("smithing_template.trident_upgrade.ingredients"))
            ).formatted(Formatting.BLUE),
            Text.translatable(
                    Util.createTranslationKey("upgrade", Impaled.id("trident_upgrade"))
            ).formatted(Formatting.GRAY),
            Text.translatable(
                    Util.createTranslationKey("item", Impaled.id("smithing_template.trident_upgrade.base_slot_description"))
            ),
            Text.translatable(
                    Util.createTranslationKey("item", Impaled.id("smithing_template.trident_upgrade.additions_slot_description"))
            ),
            List.of(new Identifier("item/empty_slot_sword")),
            List.of(new Identifier("item/empty_slot_amethyst_shard"))
    );

    public static void init() {
        ELDER_GUARDIAN_EYE = registerItem(new Item((new Item.Settings()).rarity(Rarity.UNCOMMON)), "elder_guardian_eye");
        ANCIENT_TRIDENT = registerItem(new Item((new Item.Settings()).rarity(Rarity.UNCOMMON).fireproof()), "ancient_trident");

        PITCHFORK = registerTrident(new PitchforkItem((new Item.Settings()).maxDamage(150), ImpaledEntityTypes.PITCHFORK), "pitchfork", true);
        HELLFORK = registerTrident(new HellforkItem((new Item.Settings()).maxDamage(325).fireproof().fireproof(), ImpaledEntityTypes.HELLFORK), "hellfork", true);
        SOULFORK = registerTrident(new HellforkItem((new Item.Settings()).maxDamage(325).fireproof().fireproof(), ImpaledEntityTypes.SOULFORK), "soulfork", true);
        ELDER_TRIDENT = registerTrident(new ElderTridentItem((new Item.Settings()).maxDamage(250), ImpaledEntityTypes.ELDER_TRIDENT), "elder_trident", true);
        GUARDIAN_TRIDENT = registerTrident(new ElderTridentItem((new Item.Settings()).maxDamage(200), ImpaledEntityTypes.GUARDIAN_TRIDENT), "guardian_trident", true);
        ATLAN = registerTrident(new AtlanItem((new Item.Settings()).maxDamage(250), ImpaledEntityTypes.ATLAN), "atlan", true);
        MAELSTROM = registerItem(new MaelstromItem((new Item.Settings()).maxDamage(80)), "maelstrom");
        registerItem(TRIDENT_UPGRADE_SMITHING_TEMPLATE, "trident_upgrade_smithing_template");
    }

    public static ImpaledTridentItem registerTrident(ImpaledTridentItem item, String name, boolean registerDispenserBehavior) {
        Registry.register(Registries.ITEM, Impaled.MODID + ":" + name, item);
        ALL_TRIDENTS.add(item);
        if (registerDispenserBehavior) {
            DispenserBlock.registerBehavior(item, new ProjectileDispenserBehavior() {
                @Override
                protected ProjectileEntity createProjectile(World world, Position position, ItemStack itemStack) {
                    ImpaledTridentEntity tridentEntity = Objects.requireNonNull(item.getEntityType().create(world));
                    tridentEntity.setPos(position.getX(), position.getY(), position.getZ());
                    itemStack.decrement(1);
                    return tridentEntity;
                }
            });
        }

        return item;
    }

    public static Item registerItem(Item item, String name) {
        Registry.register(Registries.ITEM, Impaled.MODID + ":" + name, item);
        return item;
    }
}
