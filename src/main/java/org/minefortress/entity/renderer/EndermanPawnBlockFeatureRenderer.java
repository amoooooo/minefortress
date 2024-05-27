package org.minefortress.entity.renderer;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EndermanEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.util.math.RotationAxis;
import org.minefortress.entity.BasePawnEntity;
import org.minefortress.entity.renderer.models.PawnModel;

public class EndermanPawnBlockFeatureRenderer extends FeatureRenderer<BasePawnEntity, PawnModel> {
    private final BlockRenderManager blockRenderManager;

    public EndermanPawnBlockFeatureRenderer(FeatureRendererContext<BasePawnEntity,PawnModel> context, BlockRenderManager blockRenderManager) {
        super(context);
        this.blockRenderManager = blockRenderManager;
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, BasePawnEntity entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        BlockItem blockItem = entity.getStackInHand(entity.getActiveHand()).getItem() instanceof BlockItem ? (BlockItem) entity.getStackInHand(entity.getActiveHand()).getItem() : null;
        if(blockItem == null) return;
        BlockState blockState = blockItem.getBlock().getDefaultState();
        if (blockState != null) {
            matrices.push();
            matrices.translate(0.0F, 0.6875F, -0.75F);
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(20.0F));
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(45.0F));
            matrices.translate(0.25F, 0.1875F, 0.25F);
            float m = 0.5F;
            matrices.scale(-0.5F, -0.5F, 0.5F);
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90.0F));
            this.blockRenderManager.renderBlockAsEntity(blockState, matrices, vertexConsumers, light, OverlayTexture.DEFAULT_UV);
            matrices.pop();
        }
    }
}
