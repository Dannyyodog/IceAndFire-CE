package com.iafenvoy.iceandfire.render.entity;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.entity.HydraEntity;
import com.iafenvoy.iceandfire.entity.StoneStatueEntity;
import com.iafenvoy.iceandfire.entity.TrollEntity;
import com.iafenvoy.iceandfire.registry.IafRenderLayers;
import com.iafenvoy.iceandfire.render.entity.feature.HydraHeadFeatureRenderer;
import com.iafenvoy.iceandfire.render.model.ICustomStatueModel;
import com.iafenvoy.iceandfire.render.model.HydraBodyModel;
import com.iafenvoy.iceandfire.render.model.StonePlayerModel;
import com.iafenvoy.uranus.client.model.AdvancedEntityModel;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.PigEntityModel;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

import java.util.HashMap;
import java.util.Map;

public class StoneStatueEntityRenderer extends EntityRenderer<StoneStatueEntity> {
    protected static final Identifier[] DESTROY_STAGES = new Identifier[]{
            Identifier.ofVanilla("textures/block/destroy_stage_0.png"),
            Identifier.of(Identifier.DEFAULT_NAMESPACE,"textures/block/destroy_stage_1.png"),
            Identifier.of(Identifier.DEFAULT_NAMESPACE,"textures/block/destroy_stage_2.png"),
            Identifier.of(Identifier.DEFAULT_NAMESPACE,"textures/block/destroy_stage_3.png"),
            Identifier.of(Identifier.DEFAULT_NAMESPACE,"textures/block/destroy_stage_4.png"),
            Identifier.of(Identifier.DEFAULT_NAMESPACE,"textures/block/destroy_stage_5.png"),
            Identifier.of(Identifier.DEFAULT_NAMESPACE,"textures/block/destroy_stage_6.png"),
            Identifier.of(Identifier.DEFAULT_NAMESPACE,"textures/block/destroy_stage_7.png"),
            Identifier.of(Identifier.DEFAULT_NAMESPACE,"textures/block/destroy_stage_8.png"),
            Identifier.of(Identifier.DEFAULT_NAMESPACE,"textures/block/destroy_stage_9.png")};
    private final Map<String, EntityModel> modelMap = new HashMap<>();
    private final Map<String, Entity> hollowEntityMap = new HashMap<>();
    private final EntityRendererFactory.Context context;

    public StoneStatueEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.context = context;
    }

    @SuppressWarnings("deprecation")
    @Override
    public Identifier getTexture(StoneStatueEntity entity) {
        return SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE;
    }

    protected void preRenderCallback(StoneStatueEntity entity, MatrixStack matrixStackIn, float partialTickTime) {
        float scale = entity.getScaleFactor() < 0.01F ? 1F : entity.getScaleFactor();
        matrixStackIn.scale(scale, scale, scale);
    }

    @Override
    public void render(StoneStatueEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, VertexConsumerProvider bufferIn, int packedLightIn) {
        EntityModel model = new PigEntityModel<>(this.context.getPart(EntityModelLayers.PIG));

        // Get the correct model
        if (this.modelMap.get(entityIn.getTrappedEntityTypeString()) != null)
            model = this.modelMap.get(entityIn.getTrappedEntityTypeString());
        else {
            EntityRenderer<?> renderer = MinecraftClient.getInstance().getEntityRenderDispatcher().renderers.get(entityIn.getTrappedEntityType());

            if (renderer instanceof FeatureRendererContext)
                model = ((FeatureRendererContext<?, ?>) renderer).getModel();
            else if (entityIn.getTrappedEntityType() == EntityType.PLAYER)
                model = new StonePlayerModel(this.context.getPart(EntityModelLayers.PLAYER));
            this.modelMap.put(entityIn.getTrappedEntityTypeString(), model);
        }
        if (model == null) return;

        Entity fakeEntity;
        if (this.hollowEntityMap.get(entityIn.getTrappedEntityTypeString()) == null) {
            fakeEntity = entityIn.getTrappedEntityType().create(MinecraftClient.getInstance().world);
            if (fakeEntity != null) {
                try {
                    fakeEntity.readNbt(entityIn.getTrappedTag());
                } catch (Exception e) {
                    IceAndFire.LOGGER.warn("Mob {} could not build statue NBT", entityIn.getTrappedEntityTypeString());
                }
                this.hollowEntityMap.putIfAbsent(entityIn.getTrappedEntityTypeString(), fakeEntity);
            }
        } else
            fakeEntity = this.hollowEntityMap.get(entityIn.getTrappedEntityTypeString());
        RenderLayer tex = IafRenderLayers.getStoneMobRenderType(200, 200);
        if (fakeEntity instanceof TrollEntity troll)
            tex = RenderLayer.getEntityCutout(troll.getTrollType().getStatueTexture());

        VertexConsumer ivertexbuilder = bufferIn.getBuffer(tex);

        matrixStackIn.push();
        float yaw = entityIn.prevYaw + (entityIn.getYaw() - entityIn.prevYaw) * partialTicks;
        model.child = entityIn.isBaby();
        model.riding = false;
        model.handSwingProgress = entityIn.getHandSwingProgress(partialTicks);
        if (model instanceof AdvancedEntityModel advancedEntityModel)
            advancedEntityModel.resetToDefaultPose();
        else if (fakeEntity != null)
            model.setAngles(fakeEntity, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F);
        this.preRenderCallback(entityIn, matrixStackIn, partialTicks);
        matrixStackIn.translate(0, 1.5F, 0);
        matrixStackIn.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180.0F));
        matrixStackIn.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(yaw));
        if (model instanceof ICustomStatueModel statueModel && fakeEntity != null) {
            statueModel.renderStatue(matrixStackIn, ivertexbuilder, packedLightIn, fakeEntity);
            if (model instanceof HydraBodyModel hydraBody && fakeEntity instanceof HydraEntity hydra)
                HydraHeadFeatureRenderer.renderHydraHeads(hydraBody, true, matrixStackIn, bufferIn, packedLightIn, hydra, 0, 0, partialTicks, 0, 0, 0);
        } else
            model.render(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.DEFAULT_UV, -1);

        matrixStackIn.pop();

        if (entityIn.getCrackAmount() >= 1) {
            int i = MathHelper.clamp(entityIn.getCrackAmount() - 1, 0, DESTROY_STAGES.length - 1);
            RenderLayer crackTex = IafRenderLayers.getStoneCrackRenderType(DESTROY_STAGES[i]);
            VertexConsumer ivertexbuilder2 = bufferIn.getBuffer(crackTex);
            matrixStackIn.push();
            matrixStackIn.push();
            this.preRenderCallback(entityIn, matrixStackIn, partialTicks);
            matrixStackIn.translate(0, 1.5F, 0);
            matrixStackIn.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180.0F));
            matrixStackIn.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(yaw));
            if (model instanceof ICustomStatueModel statueModel)
                statueModel.renderStatue(matrixStackIn, ivertexbuilder2, packedLightIn, fakeEntity);
            else
                model.render(matrixStackIn, ivertexbuilder2, packedLightIn, OverlayTexture.DEFAULT_UV, -1);
            matrixStackIn.pop();
            matrixStackIn.pop();
        }
        //super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
    }
}