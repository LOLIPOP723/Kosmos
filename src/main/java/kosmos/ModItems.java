package kosmos;

import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Kosmos.MODID);

    // Rohstoffe
    public static final DeferredItem<Item> RAW_COSMIUM = ITEMS.registerSimpleItem("raw_cosmium", new Item.Properties());
    public static final DeferredItem<Item> COSMIUM_INGOT = ITEMS.registerSimpleItem("cosmium_ingot", new Item.Properties());
    public static final DeferredItem<Item> STARLIGHT_SHARD = ITEMS.registerSimpleItem("starlight_shard", new Item.Properties());
    public static final DeferredItem<Item> COLOSSUS_CORE = ITEMS.registerSimpleItem("colossus_core",
            new Item.Properties().rarity(Rarity.EPIC).stacksTo(1));

    // Raketenteile (liegen in abgestuerzten Kapseln in der Welt)
    public static final DeferredItem<Item> ROCKET_HULL_PART = ITEMS.registerSimpleItem("rocket_hull_part", new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> ROCKET_ENGINE_PART = ITEMS.registerSimpleItem("rocket_engine_part", new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> ROCKET_COCKPIT_PART = ITEMS.registerSimpleItem("rocket_cockpit_part", new Item.Properties().stacksTo(1));

    // Rakete, Lizenz, Crew
    public static final DeferredItem<Item> ROCKET = ITEMS.register("rocket",
            () -> new RocketItem(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON)));
    public static final DeferredItem<Item> PILOT_LICENSE = ITEMS.registerSimpleItem("pilot_license",
            new Item.Properties().stacksTo(1).rarity(Rarity.RARE));
    public static final DeferredItem<Item> CREW_BADGE = ITEMS.register("crew_badge",
            () -> new CrewBadgeItem(new Item.Properties().stacksTo(16)));

    // Raumanzug
    public static final DeferredItem<Item> SPACE_HELMET = ITEMS.register("space_helmet",
            () -> new ArmorItem(ModArmor.SPACE_SUIT, ArmorItem.Type.HELMET,
                    new Item.Properties().durability(ArmorItem.Type.HELMET.getDurability(33))));
    public static final DeferredItem<Item> SPACE_CHESTPLATE = ITEMS.register("space_chestplate",
            () -> new ArmorItem(ModArmor.SPACE_SUIT, ArmorItem.Type.CHESTPLATE,
                    new Item.Properties().durability(ArmorItem.Type.CHESTPLATE.getDurability(33))));
    public static final DeferredItem<Item> SPACE_LEGGINGS = ITEMS.register("space_leggings",
            () -> new ArmorItem(ModArmor.SPACE_SUIT, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().durability(ArmorItem.Type.LEGGINGS.getDurability(33))));
    public static final DeferredItem<Item> SPACE_BOOTS = ITEMS.register("space_boots",
            () -> new ArmorItem(ModArmor.SPACE_SUIT, ArmorItem.Type.BOOTS,
                    new Item.Properties().durability(ArmorItem.Type.BOOTS.getDurability(33))));

    // Spawn-Eier
    public static final DeferredItem<Item> MOON_BEETLE_SPAWN_EGG = ITEMS.register("moon_beetle_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.MOON_BEETLE, 0x6e5aa8, 0x40e0e0, new Item.Properties()));
    public static final DeferredItem<Item> MOON_COLOSSUS_SPAWN_EGG = ITEMS.register("moon_colossus_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.MOON_COLOSSUS, 0x5f6473, 0x40e0e0, new Item.Properties()));
    public static final DeferredItem<Item> ACADEMY_INSTRUCTOR_SPAWN_EGG = ITEMS.register("academy_instructor_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.ACADEMY_INSTRUCTOR, 0xffffff, 0x2f6fff, new Item.Properties()));

    // Blockitems
    public static final DeferredItem<?> COSMIUM_ORE_ITEM = ITEMS.registerSimpleBlockItem(ModBlocks.COSMIUM_ORE);
    public static final DeferredItem<?> COSMIUM_BLOCK_ITEM = ITEMS.registerSimpleBlockItem(ModBlocks.COSMIUM_BLOCK);
    public static final DeferredItem<?> LUNAR_ROCK_ITEM = ITEMS.registerSimpleBlockItem(ModBlocks.LUNAR_ROCK);
    public static final DeferredItem<?> LUNAR_BRICKS_ITEM = ITEMS.registerSimpleBlockItem(ModBlocks.LUNAR_BRICKS);
    public static final DeferredItem<?> HULL_PLATING_ITEM = ITEMS.registerSimpleBlockItem(ModBlocks.HULL_PLATING);
    public static final DeferredItem<?> STARLIGHT_CRYSTAL_ITEM = ITEMS.registerSimpleBlockItem(ModBlocks.STARLIGHT_CRYSTAL);
}
