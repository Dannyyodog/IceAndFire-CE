package com.iafenvoy.iceandfire.render.entity.layer;

import com.iafenvoy.iceandfire.data.DragonColor;
import com.iafenvoy.iceandfire.entity.DragonBaseEntity;
import com.iafenvoy.uranus.client.model.TabulaModel;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class DragonMaleOverlayFeatureRenderer extends FeatureRenderer<DragonBaseEntity, TabulaModel<DragonBaseEntity>> {
    public DragonMaleOverlayFeatureRenderer(MobEntityRenderer<DragonBaseEntity, TabulaModel<DragonBaseEntity>> renderIn) {
        super(renderIn);
    }

    @Override
    public void render(MatrixStack matrixStackIn, VertexConsumerProvider bufferIn, int packedLightIn, DragonBaseEntity dragon, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (dragon.isMale() && !dragon.isSkeletal())
            this.getContextModel().render(matrixStackIn, bufferIn.getBuffer(RenderLayer.getEntityTranslucent(this.getTexture(dragon))), packedLightIn, OverlayTexture.DEFAULT_UV, -1);
    }

    @Override
    protected Identifier getTexture(DragonBaseEntity dragon) {
        return DragonColor.getById(dragon.getVariant()).getMaleOverlay();
    }
}