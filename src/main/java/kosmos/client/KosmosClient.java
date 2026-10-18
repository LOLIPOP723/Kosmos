package kosmos.client;

import kosmos.Kosmos;
import kosmos.ModEntities;
import net.minecraft.client.renderer.entity.VillagerRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid = Kosmos.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class KosmosClient {
    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.ROCKET.get(), RocketRenderer::new);
        event.registerEntityRenderer(ModEntities.MOON_BEETLE.get(), MoonBeetleRenderer::new);
        event.registerEntityRenderer(ModEntities.MOON_COLOSSUS.get(), MoonColossusRenderer::new);
        event.registerEntityRenderer(ModEntities.ACADEMY_INSTRUCTOR.get(), VillagerRenderer::new);
    }
}
