package kosmos.client;

import kosmos.Kosmos;
import kosmos.MoonBeetle;
import net.minecraft.client.model.SilverfishModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class MoonBeetleRenderer extends MobRenderer<MoonBeetle, SilverfishModel<MoonBeetle>> {
    private static final ResourceLocation TEXTURE = Kosmos.id("textures/entity/moon_beetle.png");

    public MoonBeetleRenderer(EntityRendererProvider.Context context) {
        super(context, new SilverfishModel<>(context.bakeLayer(ModelLayers.SILVERFISH)), 0.3F);
    }

    @Override
    public ResourceLocation getTextureLocation(MoonBeetle entity) {
        return TEXTURE;
    }
}
