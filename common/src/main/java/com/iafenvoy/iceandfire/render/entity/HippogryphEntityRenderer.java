package com.iafenvoy.iceandfire.render.entity;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.entity.HippogryphEntity;
import com.iafenvoy.iceandfire.render.model.HippogryphModel;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class HippogryphEntityRenderer extends MobEntityRenderer<HippogryphEntity, HippogryphModel> {
    public HippogryphEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new HippogryphModel(), 0.8F);
        this.features.add(new LayerHippogriffSaddle(this));
    }

    @Override
    protected void scale(HippogryphEntity entity, MatrixStack matrix, float partialTickTime) {
        matrix.scale(1.2F, 1.2F, 1.2F);
    }

    @Override
    public Identifier getTexture(HippogryphEntity entity) {
        return entity.getEnumVariant().getTexture(entity.isBlinking());
    }

    private static class LayerHippogriffSaddle extends FeatureRenderer<HippogryphEntity, HippogryphModel> {
        private final RenderLayer SADDLE_TEXTURE = RenderLayer.getEntityNoOutline(Identifier.of(IceAndFire.MOD_ID, "textures/entity/hippogryph/saddle.png"));
        private final RenderLayer BRIDLE = RenderLayer.getEntityNoOutline(Identifier.of(IceAndFire.MOD_ID, "textures/entity/hippogryph/bridle.png"));
        private final RenderLayer CHEST = RenderLayer.getEntityTranslucent(Identifier.of(IceAndFire.MOD_ID, "textures/entity/hippogryph/chest.png"));
        private final RenderLayer TEXTURE_IRON = RenderLayer.getEntityNoOutline(Identifier.of(IceAndFire.MOD_ID, "textures/entity/hippogryph/armor_iron.png"));
        private final RenderLayer TEXTURE_GOLD = RenderLayer.getEntityNoOutline(Identifier.of(IceAndFire.MOD_ID, "textures/entity/hippogryph/armor_gold.png"));
        private final RenderLayer TEXTURE_DIAMOND = RenderLayer.getEntityNoOutline(Identifier.of(IceAndFire.MOD_ID, "textures/entity/hippogryph/armor_diamond.png"));
        private final RenderLayer TEXTURE_NETHERITE = RenderLayer.getEntityNoOutline(Identifier.of(IceAndFire.MOD_ID, "textures/entity/hippogryph/armor_netherite.png"));

        public LayerHippogriffSaddle(HippogryphEntityRenderer renderer) {
            super(renderer);
        }

        @Override
        public void render(MatrixStack matrixStackIn, VertexConsumerProvider bufferIn, int packedLightIn, HippogryphEntity hippo, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            if (hippo.getArmor() != 0) {
                RenderLayer type = switch (hippo.getArmor()) {
                    case 1 -> this.TEXTURE_IRON;
                    case 2 -> this.TEXTURE_GOLD;
                    case 3 -> this.TEXTURE_DIAMOND;
                    case 4 -> this.TEXTURE_NETHERITE;
                    default -> null;
                };
                VertexConsumer ivertexbuilder = bufferIn.getBuffer(type);
                this.getContextModel().render(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.DEFAULT_UV, -1);
            }
            if (hippo.isSaddled()) {
                VertexConsumer ivertexbuilder = bufferIn.getBuffer(this.SADDLE_TEXTURE);
                this.getContextModel().render(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.DEFAULT_UV, -1);
            }
            if (hippo.isSaddled() && hippo.getControllingPassenger() != null) {
                VertexConsumer ivertexbuilder = bufferIn.getBuffer(this.BRIDLE);
                this.getContextModel().render(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.DEFAULT_UV, -1);
            }
            if (hippo.isChested()) {
                VertexConsumer ivertexbuilder = bufferIn.getBuffer(this.CHEST);
                this.getContextModel().render(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.DEFAULT_UV, -1);
            }
        }
    }
}
