package kosmos;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(Kosmos.MODID)
public class Kosmos {
    public static final String MODID = "kosmos";

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }

    public Kosmos(IEventBus modEventBus) {
        ModBlocks.BLOCKS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModArmor.MATERIALS.register(modEventBus);
        ModEntities.ENTITIES.register(modEventBus);
        ModTabs.TABS.register(modEventBus);
        modEventBus.addListener(ModEntities::registerAttributes);
        modEventBus.addListener(ModEntities::registerSpawnPlacements);
    }
}
