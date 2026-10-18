package kosmos.client;

import com.mojang.blaze3d.vertex.PoseStack;
import kosmos.Kosmos;
import kosmos.MoonColossus;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;

public class MoonColossusRenderer extends HumanoidMobRenderer<MoonColossus, HumanoidModel<MoonColossus>> {
    private static final ResourceLocation TEXTURE = Kosmos.id("textures/entity/moon_colossus.png");

    public MoonColossusRenderer(EntityRendererProvider.Context context) {
        super(context, new HumanoidModel<>(context.bakeLayer(ModelLayers.ZOMBIE)), 1.2F);
    }

    @Override
    public ResourceLocation getTextureLocation(MoonColossus entity) {
        return TEXTURE;
    }

    @Override
    protected void scale(MoonColossus entity, PoseStack poseStack, float partialTickTime) {
        poseStack.scale(2.2F, 2.2F, 2.2F);
    }
}
