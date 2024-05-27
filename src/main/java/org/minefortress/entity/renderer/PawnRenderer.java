package org.minefortress.entity.renderer;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.entity.BipedEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.EndermanEyesFeatureRenderer;
import net.minecraft.client.render.entity.model.EndermanEntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.AirBlockItem;
import net.minecraft.item.BlockItem;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Box;
import net.minecraft.world.GameMode;
import net.remmintan.mods.minefortress.core.FortressState;
import net.remmintan.mods.minefortress.core.interfaces.entities.pawns.IWarrior;
import net.remmintan.mods.minefortress.core.utils.CoreModUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.AxisAngle4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.minefortress.MineFortressMod;
import org.minefortress.entity.BasePawnEntity;
import org.minefortress.entity.renderer.models.PawnModel;

import java.util.Optional;

public class PawnRenderer extends MobEntityRenderer<BasePawnEntity, PawnModel> {

    private static final Vector3f GREEN_COLOR = new Vector3f(0f, 1f, 0f);
    private static final Vector3f YELLOW_COLOR = new Vector3f(1f, 1f, 0f);

    private static final Identifier GUY = new Identifier("minecraft", "textures/entity/enderman/enderman.png");
    private static final Identifier GUY2 = new Identifier("minecraft", "textures/entity/enderman/enderman.png");
    private static final Identifier GUY3 = new Identifier("minecraft", "textures/entity/enderman/enderman.png");
    private static final Identifier GUY4 = new Identifier("minecraft", "textures/entity/enderman/enderman.png");

    public PawnRenderer(EntityRendererFactory.Context context) {
        super(context, new PawnModel(context), 0.5f);
        this.addFeature(new EndermanPawnBlockFeatureRenderer(this, context.getBlockRenderManager()));
        this.addFeature(new ConditionalHeldItemFeatureRenderer<>(this, context.getHeldItemRenderer()));
        this.addFeature(new EndermanPawnEyesFeatureRenderer(this));
    }

    @Override
    public Identifier getTexture(BasePawnEntity pawn) {
        final var bodyTextureId = pawn.getBodyTextureId();
        return switch (bodyTextureId) {
            case 0 -> GUY;
            case 1 -> GUY2;
            case 2 -> GUY3;
            default -> GUY4;
        };
    }

    @Override
    protected boolean hasLabel(BasePawnEntity colonist) {
        return false;
    }

    @NotNull
    private static Vector3f getColorBaseOnMode(BasePawnEntity pawn) {
        final var state = CoreModUtils.getMineFortressManagersProvider().get_ClientFortressManager().getState();
        final boolean warrior = pawn instanceof IWarrior;
        final var combatState = state == FortressState.COMBAT;
        if (combatState && warrior || !combatState && !warrior)
            return new Vector3f(GREEN_COLOR);
        else
            return new Vector3f(YELLOW_COLOR);
    }

    @Override
    public void render(BasePawnEntity pawn, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        setClothesVilibility(pawn);
        if(!pawn.getMainHandStack().isEmpty()){
            if(pawn.getMainHandStack().getItem() instanceof BlockItem bi) {
                if(!(bi.asItem() instanceof AirBlockItem)) {
                    model.carryingBlock = true;
                } else {
                    model.carryingBlock = false;
                }
            } else {
                model.carryingBlock = false;

            }
        } else {
            model.carryingBlock = false;
        }
        if(pawn.isSleeping()) {
            Vector3f sleepingDir = pawn.getSleepingDirection().getUnitVector().mul(-1f);
            matrixStack.translate(sleepingDir.x(), sleepingDir.y(), sleepingDir.z());
//            matrixStack.translate(0.0, 0.0, -1.0);
        }
        super.render(pawn, f, g, matrixStack, vertexConsumerProvider, i);

        final MinecraftClient client = getClient();
        final GameMode currentGamemode = Optional
                .ofNullable(client.interactionManager)
                .map(ClientPlayerInteractionManager::getCurrentGameMode)
                .orElse(GameMode.DEFAULT);

        if(currentGamemode == MineFortressMod.FORTRESS) {
            final boolean hovering = client.crosshairTarget instanceof EntityHitResult entityHitResult && entityHitResult.getEntity() == pawn;
            final var fightSelecting = isThisPawnSelected(pawn);
            var color = getHealthFoodLevelColor(pawn);
            if(hovering || color != null || fightSelecting) {
                final VertexConsumer buffer = vertexConsumerProvider.getBuffer(RenderLayer.getLines());
                if(color == null)
                    color = getColorBaseOnMode(pawn);

                if(!hovering)
                    color.mul(0.7f);

                PawnRenderer.renderRhombus(matrixStack, buffer, pawn, color);
            }
        }
    }

    private boolean isThisPawnSelected(BasePawnEntity pawn) {
        return CoreModUtils.getMineFortressManagersProvider().get_PawnsSelectionManager().isSelected(pawn);
    }

    private float getHealthFoodLevel(BasePawnEntity colonist) {
        final var health = colonist.getHealth();
        final var foodLevel = colonist.getCurrentFoodLevel();

        return Math.min(health, foodLevel);
    }

    @Nullable
    private Vector3f getHealthFoodLevelColor(BasePawnEntity colonist) {
        final var healthFoodLevel = getHealthFoodLevel(colonist);
        final var maxLevelOfEachColor = (float)0xFF;
        if(healthFoodLevel > 10) return null;
        if(healthFoodLevel <= 10 && healthFoodLevel >= 5) {
            final var red = 0xFF / maxLevelOfEachColor;
            final var green = 0xAA / maxLevelOfEachColor;
            final var blue = 0x00 / maxLevelOfEachColor;
            return new Vector3f(red, green, blue);
        }
        final var red = 0xFF / maxLevelOfEachColor;
        final var green = 0x55 / maxLevelOfEachColor;
        final var blue = 0x55 / maxLevelOfEachColor;
        return new Vector3f(red, green, blue);
    }

    @Nullable
    @Override
    protected RenderLayer getRenderLayer(BasePawnEntity entity, boolean showBody, boolean translucent, boolean showOutline) {
        return super.getRenderLayer(entity, showBody, translucent, showOutline);
    }

    private MinecraftClient getClient() {
        return MinecraftClient.getInstance();
    }

    private void setClothesVilibility(MobEntity colonist) {
        final var colonistModel = (EndermanEntityModel<BasePawnEntity>)this.getModel();
        colonistModel.hat.visible = true;
//        colonistModel.jacket.visible = !colonist.isSleeping();
//        colonistModel.leftPants.visible = !colonist.isSleeping();
//        colonistModel.rightPants.visible = !colonist.isSleeping();
//        colonistModel.leftSleeve.visible = !colonist.isSleeping();
//        colonistModel.rightSleeve.visible = !colonist.isSleeping();
    }

    private static void renderRhombus(MatrixStack matrices, VertexConsumer vertices, Entity entity, Vector3f color) {
        Box box = entity.getBoundingBox().offset(-entity.getX(), -entity.getY(), -entity.getZ());
        if (entity instanceof LivingEntity) {
            matrices.push();
            final double xCenter = (box.minX + box.maxX) / 2;
            final double zCenter = (box.minZ + box.maxZ) / 2;
            matrices.translate(xCenter, box.maxY * 1.75, zCenter);

            float radians = (float) Math.toRadians(45);

            final Quaternionf xRotation = new Quaternionf().set(new AxisAngle4f(radians, 1, 0, 0));
            final Quaternionf yRoation = new Quaternionf().set(new AxisAngle4f(radians, 0, 1, 0));
            matrices.multiply(xRotation);
            matrices.multiply(yRoation);
            matrices.scale(0.3f, 0.3f, 0.3f);

            WorldRenderer.drawBox(matrices, vertices, -0.5f,  -0.5f, -0.5f, 0.5f,  0.5f, 0.5f, color.x(), color.y(), color.z(), 1.0f);
            matrices.pop();
        }
    }

}