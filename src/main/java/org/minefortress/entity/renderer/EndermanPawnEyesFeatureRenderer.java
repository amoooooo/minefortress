package org.minefortress.entity.renderer;

import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.EndermanEyesFeatureRenderer;
import net.minecraft.client.render.entity.feature.EyesFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EndermanEntityModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import org.minefortress.entity.BasePawnEntity;
import org.minefortress.entity.renderer.models.PawnModel;

public class EndermanPawnEyesFeatureRenderer extends FeatureRenderer<BasePawnEntity, PawnModel> {
    private static final RenderLayer SKIN = RenderLayer.getEyes(new Identifier("textures/entity/enderman/enderman_eyes.png"));

    public EndermanPawnEyesFeatureRenderer(FeatureRendererContext<BasePawnEntity, PawnModel> context) {
        super(context);
    }

    public RenderLayer getEyesTexture() {
        return SKIN;
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, BasePawnEntity entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(this.getEyesTexture());
        this.getContextModel().render(matrices, vertexConsumer, 15728640, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
    }
}
