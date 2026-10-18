package kosmos.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import kosmos.ModBlocks;
import kosmos.RocketEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.state.BlockState;

/** Die Rakete wird aus Bloecken gezeichnet: Triebwerk, Rumpf, Spitze und vier Flossen. */
public class RocketRenderer extends EntityRenderer<RocketEntity> {
    public RocketRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 0.8F;
    }

    @Override
    public ResourceLocation getTextureLocation(RocketEntity entity) {
        return InventoryMenu.BLOCK_ATLAS;
    }

    @Override
    public void render(RocketEntity entity, float entityYaw, float partialTick, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight) {
        super.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight);
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(-entity.getViewYRot(partialTick)));
        poseStack.mulPose(Axis.XP.rotationDegrees(entity.getViewXRot(partialTick)));

        BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
        BlockState accent = ModBlocks.COSMIUM_BLOCK.get().defaultBlockState();
        BlockState hull = ModBlocks.HULL_PLATING.get().defaultBlockState();

        part(dispatcher, poseStack, buffer, packedLight, accent, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F);
        part(dispatcher, poseStack, buffer, packedLight, hull, 0.0F, 1.0F, 0.0F, 0.9F, 1.0F);
        part(dispatcher, poseStack, buffer, packedLight, hull, 0.0F, 2.0F, 0.0F, 0.9F, 1.0F);
        part(dispatcher, poseStack, buffer, packedLight, accent, 0.0F, 3.0F, 0.0F, 0.6F, 1.0F);
        float f = 0.7F;
        part(dispatcher, poseStack, buffer, packedLight, accent, f, 0.0F, 0.0F, 0.3F, 0.8F);
        part(dispatcher, poseStack, buffer, packedLight, accent, -f, 0.0F, 0.0F, 0.3F, 0.8F);
        part(dispatcher, poseStack, buffer, packedLight, accent, 0.0F, 0.0F, f, 0.3F, 0.8F);
        part(dispatcher, poseStack, buffer, packedLight, accent, 0.0F, 0.0F, -f, 0.3F, 0.8F);

        poseStack.popPose();
    }

    private static void part(BlockRenderDispatcher dispatcher, PoseStack poseStack, MultiBufferSource buffer,
                             int light, BlockState state, float x, float y, float z, float width, float height) {
        poseStack.pushPose();
        poseStack.translate(x - width / 2.0F, y, z - width / 2.0F);
        poseStack.scale(width, height, width);
        dispatcher.renderSingleBlock(state, poseStack, buffer, light, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
    }
}
