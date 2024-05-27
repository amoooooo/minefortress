package org.minefortress.entity.renderer;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.ModelWithArms;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BlockItem;
import org.minefortress.entity.BasePawnEntity;

public class ConditionalHeldItemFeatureRenderer<T extends LivingEntity, M extends EntityModel<T> & ModelWithArms> extends HeldItemFeatureRenderer<T, M> {
    public ConditionalHeldItemFeatureRenderer(FeatureRendererContext<T, M> context, HeldItemRenderer heldItemRenderer) {
        super(context, heldItemRenderer);
    }

    @Override
    public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T livingEntity, float f, float g, float h, float j, float k, float l) {
        if(livingEntity instanceof BasePawnEntity pbe) {
            BlockItem blockItem = pbe.getMainHandStack().getItem() instanceof BlockItem ? (BlockItem) pbe.getMainHandStack().getItem() : null;
            if(blockItem == null) {
                matrixStack.push();
                // translate down 0.5
                matrixStack.translate(0.0F, 1.0F, 0.0F);
                super.render(matrixStack, vertexConsumerProvider, i, livingEntity, f, g, h, j, k, l);
                matrixStack.pop();
            }
        }
    }
}
